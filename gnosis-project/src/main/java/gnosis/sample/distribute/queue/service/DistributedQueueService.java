package gnosis.sample.distribute.queue.service;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.enums.QueueMessageStatus;
import gnosis.sample.distribute.queue.exception.QueueFullException;
import gnosis.sample.distribute.queue.model.QueueMessage;
import gnosis.sample.distribute.queue.model.QueueResult;
import gnosis.sample.distribute.queue.util.IdGenerator;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式队列核心服务
 * 通过工厂创建实例，非单例模式
 */
@Slf4j
public class DistributedQueueService {

    private DataSource dataSource;

    private RuntimeQueueConfig runtimeConfig;

    // 结果缓存，用于同步等待结果
    private final ConcurrentHashMap<String, QueueResult> resultCache = new ConcurrentHashMap<>();
    
    // 等待者映射，用于阻塞等待通知
    private final ConcurrentHashMap<String, Object> waiters = new ConcurrentHashMap<>();

    // setter方法，用于工厂注入
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setRuntimeConfig(RuntimeQueueConfig runtimeConfig) {
        this.runtimeConfig = runtimeConfig;
    }

    /**
     * 异步入队
     * @param queueName 队列名称
     * @param messageBody 消息体
     * @throws SQLException 数据库异常
     * @throws QueueFullException 队列满异常
     */
    public void enqueue(String queueName, String messageBody) throws SQLException {
        Objects.requireNonNull(queueName, "queueName cannot be null");
        Objects.requireNonNull(messageBody, "messageBody cannot be null");

        // 检查队列长度限制
        int maxLen = runtimeConfig.getMaxQueueLength(queueName);
        if (maxLen > 0) {
            int currentLen = getCurrentQueueLength(queueName);
            if (currentLen >= maxLen) {
                throw new QueueFullException(queueName, currentLen, maxLen);
            }
        }

        // 生成唯一ID并入队
        long id = IdGenerator.nextId();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO sys_distributed_queue (id, queue_name, message_body, status) VALUES (?, ?, ?, ?)");
            ps.setLong(1, id);
            ps.setString(2, queueName);
            ps.setString(3, messageBody);
            ps.setString(4, QueueMessageStatus.PENDING.getValue());
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 同步入队（带结果返回）
     * @param queueName 队列名称
     * @param payload 业务负载
     * @return 请求ID
     * @throws SQLException 数据库异常
     * @throws QueueFullException 队列满异常
     */
    public String enqueueWithResult(String queueName, String payload) throws SQLException {
        String requestId = UUID.randomUUID().toString().replace("-", "");
        String wrappedBody = "{\"requestId\":\"" + requestId + "\",\"payload\":" + payload + "}";
        enqueue(queueName, wrappedBody);
        return requestId;
    }

    /**
     * 阻塞等待处理结果
     * @param requestId 请求ID
     * @param timeoutMs 超时时间（毫秒）
     * @return 处理结果，超时返回null
     * @throws InterruptedException 中断异常
     */
    public QueueResult waitForResult(String requestId, long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        long remaining = timeoutMs;

        // 先检查缓存中是否已有结果
        QueueResult cached = resultCache.get(requestId);
        if (cached != null) {
            return cached;
        }

        // 注册等待者
        Object lock = new Object();
        waiters.put(requestId, lock);

        try {
            while (remaining > 0) {
                synchronized (lock) {
                    cached = resultCache.get(requestId);
                    if (cached != null) {
                        return cached;
                    }
                    lock.wait(remaining);
                    remaining = timeoutMs - (System.currentTimeMillis() - start);
                }
            }
            return null; // 超时
        } finally {
            waiters.remove(requestId);
        }
    }

    /**
     * 保存处理结果
     * @param requestId 请求ID
     * @param success 是否成功
     * @param resultData 结果数据
     * @param errorMessage 错误信息
     * @throws SQLException 数据库异常
     */
    public void saveResult(String requestId, boolean success, String resultData, String errorMessage) throws SQLException {
        QueueResult result = new QueueResult(requestId, success, resultData, errorMessage);
        resultCache.put(requestId, result);

        // 通知等待者
        Object lock = waiters.get(requestId);
        if (lock != null) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        // 持久化到数据库
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO sys_distributed_queue_result (request_id, result_status, result_data, error_message) VALUES (?, ?, ?, ?)");
            ps.setString(1, requestId);
            ps.setString(2, success ? "SUCCESS" : "FAILED");
            ps.setString(3, resultData);
            ps.setString(4, errorMessage);
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 出队消息
     * @param queueName 队列名称
     * @param consumerId 消费者ID
     * @return 队列消息，无消息返回null
     * @throws SQLException 数据库异常
     */
    public QueueMessage dequeue(String queueName, String consumerId) throws SQLException {
        Objects.requireNonNull(queueName, "queueName cannot be null");
        Objects.requireNonNull(consumerId, "consumerId cannot be null");

        Long candidateId = null;
        String body = null;
        int attempts = 0;

        // 查找待处理的消息
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "SELECT id, message_body, attempt_count FROM sys_distributed_queue " +
                "WHERE queue_name = ? AND status = ? ORDER BY id ASC LIMIT 1");
            ps.setString(1, queueName);
            ps.setString(2, QueueMessageStatus.PENDING.getValue());
            rs = ps.executeQuery();
            if (rs.next()) {
                candidateId = rs.getLong("id");
                body = rs.getString("message_body");
                attempts = rs.getInt("attempt_count");
            } else {
                return null;
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }

        if (candidateId == null) {
            return null;
        }

        // 更新消息状态为处理中
        conn = null;
        ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "UPDATE sys_distributed_queue SET status = ?, consumer_id = ?, " +
                "attempt_count = attempt_count + 1, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND status = ?");
            ps.setString(1, QueueMessageStatus.PROCESSING.getValue());
            ps.setString(2, consumerId);
            ps.setLong(3, candidateId);
            ps.setString(4, QueueMessageStatus.PENDING.getValue());
            return ps.executeUpdate() == 1 ? new QueueMessage(candidateId, queueName, body, attempts + 1) : null;
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 确认消息处理完成
     * @param messageId 消息ID
     * @param consumerId 消费者ID
     * @throws SQLException 数据库异常
     */
    public void ack(long messageId, String consumerId) throws SQLException {
        executeUpdate(
            "UPDATE sys_distributed_queue SET status = ?, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = ? AND consumer_id = ?",
            QueueMessageStatus.DONE.getValue(), messageId, consumerId);
    }

    /**
     * 拒绝消息处理（重新入队）
     * @param messageId 消息ID
     * @param consumerId 消费者ID
     * @throws SQLException 数据库异常
     */
    public void nack(long messageId, String consumerId) throws SQLException {
        executeUpdate(
            "UPDATE sys_distributed_queue SET status = ?, consumer_id = NULL, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = ? AND consumer_id = ?",
            QueueMessageStatus.PENDING.getValue(), messageId, consumerId);
    }

    /**
     * 清理已完成的消息
     * @param keepDays 保留天数
     * @throws SQLException 数据库异常
     */
    public void cleanupDoneMessages(int keepDays) throws SQLException {
        Timestamp cutoff = new Timestamp(System.currentTimeMillis() - (long) keepDays * 24 * 3600 * 1000L);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "DELETE FROM sys_distributed_queue WHERE status = ? AND updated_at < ?");
            ps.setString(1, QueueMessageStatus.DONE.getValue());
            ps.setTimestamp(2, cutoff);
            int deleted = ps.executeUpdate();
            log.info("清理了 {} 条超过 {} 天的已完成消息", deleted, keepDays);
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 获取空队列轮询间隔
     * @return 轮询间隔毫秒数
     */
    public long getEmptyPollIntervalMs() {
        return runtimeConfig.getEmptyPollIntervalMs();
    }

    // ==================== 私有工具方法 ====================

    /**
     * 获取队列当前长度
     */
    private int getCurrentQueueLength(String queueName) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM sys_distributed_queue WHERE queue_name = ? AND status IN (?, ?)");
            ps.setString(1, queueName);
            ps.setString(2, QueueMessageStatus.PENDING.getValue());
            ps.setString(3, QueueMessageStatus.PROCESSING.getValue());
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 执行更新操作
     */
    private void executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof Long) {
                    ps.setLong(i + 1, (Long) param);
                } else if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Timestamp) {
                    ps.setTimestamp(i + 1, (Timestamp) param);
                }
            }
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 静默关闭Statement
     */
    private void closeQuietly(java.sql.Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * 静默关闭Connection
     */
    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    /**
     * 静默关闭ResultSet
     */
    private void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
    }
}