package gnosis.sample.distribute.queue.service;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.enums.QueueMessageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 死信队列服务
 * 处理超时未完成的任务、失败的任务等
 */
@Slf4j
@Service
public class DeadLetterQueueService {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private RuntimeQueueConfig runtimeConfig;

    // 默认配置常量
    private static final int DEFAULT_MAX_RETRY_ATTEMPTS = 3;
    private static final long DEFAULT_PROCESSING_TIMEOUT_MS = 30 * 60 * 1000L; // 30分钟
    public static final long DEFAULT_CHECK_INTERVAL_MS = 5 * 60 * 1000L; // 5分钟
    
    private ScheduledExecutorService scheduler;

    @PostConstruct
    public void init() {
        // 初始化调度器，用于处理启动延迟
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "DeadLetterQueueScheduler");
            t.setDaemon(true);
            return t;
        });
        
        // 启动后1分钟执行首次检查
        scheduler.schedule(this::checkAllQueuesProcessingTimeout, 60, TimeUnit.SECONDS);
        scheduler.schedule(this::checkAllQueuesFailedTasks, 60, TimeUnit.SECONDS);
    }

    /**
     * 批量检查所有队列的超时任务
     * 使用配置的超时时间和检查间隔
     */
    @Scheduled(fixedDelayString = "#{T(gnosis.sample.distribute.queue.service.DeadLetterQueueService).DEFAULT_CHECK_INTERVAL_MS}")
    public void checkAllQueuesProcessingTimeout() {
        // 批量处理所有队列的超时任务
        log.debug("开始批量检查所有队列的超时任务");
        // TODO: 获取所有队列名称并遍历检查
        // 从数据库查询所有存在的队列名称
        List<String> queueNames = getQueueNamesWithStatus("processing", "pending");
        
        if (queueNames.isEmpty()) {
            log.debug("没有找到需要检查的队列");
            return;
        }
        
        log.debug("发现 {} 个队列需要检查超时任务: {}", queueNames.size(), queueNames);
        for (String queueName : queueNames) {
            try {
                checkProcessingTimeout(queueName);
            } catch (Exception e) {
                log.error("检查队列 {} 超时任务时发生异常", queueName, e);
            }
        }
        log.debug("批量检查所有队列的超时任务完成");
    }

    public void checkProcessingTimeout(String queueName) {
        try {
            long timeoutMs = runtimeConfig.getProcessingTimeoutMs(queueName);
            Timestamp timeoutThreshold = Timestamp.valueOf(
                LocalDateTime.now().minusSeconds(timeoutMs / 1000));
            
            int maxRetryAttempts = runtimeConfig.getMaxRetryAttempts(queueName);
            
            // 查找超时的处理中任务
            List<Map<String, Object>> timeoutMessages = getTimeoutMessages(queueName, QueueMessageStatus.PROCESSING.getValue(), timeoutThreshold);
            
            int movedCount = 0;
            for (Map<String, Object> message : timeoutMessages) {
                Long messageId = ((Number) message.get("id")).longValue();
                Integer attemptCount = ((Number) message.get("attempt_count")).intValue();
                
                if (attemptCount < maxRetryAttempts) {
                    // 重试次数未达到上限，重新入队
                    moveToPending(messageId, queueName, attemptCount + 1);
                    log.info("将超时任务重新入队: messageId={}, queue={}, attempt={}", 
                        messageId, queueName, attemptCount + 1);
                } else {
                    // 达到最大重试次数，移至死信队列
                    moveToDeadLetter(messageId, queueName, attemptCount, "处理超时");
                    log.warn("将超时任务移至死信队列: messageId={}, queue={}, attempts={}", 
                        messageId, queueName, attemptCount);
                }
                movedCount++;
            }
            
            if (movedCount > 0) {
                log.info("队列 {} 本次共处理 {} 个超时任务", queueName, movedCount);
            }
            
        } catch (Exception e) {
            log.error("检查队列 {} 超时任务时发生错误: {}", queueName, e.getMessage(), e);
        }
    }

    /**
     * 检查失败的任务
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行
    public void checkAllQueuesFailedTasks() {
        // 批量处理所有队列的失败任务
        log.debug("开始批量检查所有队列的失败任务");
        // TODO: 获取所有队列名称并遍历检查
        // 从数据库查询所有存在的队列名称（包含失败状态的任务）
        List<String> queueNames = getQueueNamesWithStatus("failed");
        
        if (queueNames.isEmpty()) {
            log.debug("没有找到需要检查的失败任务队列");
            return;
        }
        
        log.debug("发现 {} 个队列需要检查失败任务: {}", queueNames.size(), queueNames);
        for (String queueName : queueNames) {
            try {
                checkFailedTasks(queueName);
            } catch (Exception e) {
                log.error("检查队列 {} 失败任务时发生异常", queueName, e);
            }
        }
        log.debug("批量检查所有队列的失败任务完成");
    }

    public void checkFailedTasks(String queueName) {
        try {
            int maxRetryAttempts = runtimeConfig.getMaxRetryAttempts(queueName);
            
            // 查找标记为失败但还未处理的任务（1小时前）
            List<Map<String, Object>> failedMessages = getFailedMessages(queueName, 
                Timestamp.valueOf(LocalDateTime.now().minusHours(1)));
            
            int processedCount = 0;
            for (Map<String, Object> message : failedMessages) {
                Long messageId = ((Number) message.get("id")).longValue();
                Integer attemptCount = ((Number) message.get("attempt_count")).intValue();
                
                if (attemptCount < maxRetryAttempts) {
                    // 重新入队尝试
                    moveToPending(messageId, queueName, attemptCount + 1);
                    log.info("将失败任务重新入队: messageId={}, queue={}, attempt={}", 
                        messageId, queueName, attemptCount + 1);
                } else {
                    // 移至死信队列
                    moveToDeadLetter(messageId, queueName, attemptCount, "处理失败");
                    log.warn("将失败任务移至死信队列: messageId={}, queue={}, attempts={}", 
                        messageId, queueName, attemptCount);
                }
                processedCount++;
            }
            
            if (processedCount > 0) {
                log.info("队列 {} 本次共处理 {} 个失败任务", queueName, processedCount);
            }
            
        } catch (Exception e) {
            log.error("检查失败任务时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 将消息重新设置为待处理状态
     */
    private void moveToPending(Long messageId, String queueName, int newAttemptCount) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "UPDATE sys_distributed_queue SET status = ?, consumer_id = NULL, " +
                "attempt_count = ?, updated_at = ? WHERE id = ?");
            ps.setString(1, QueueMessageStatus.PENDING.getValue());
            ps.setInt(2, newAttemptCount);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            ps.setLong(4, messageId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("移动消息到待处理状态失败: messageId={}, error={}", messageId, e.getMessage(), e);
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 将消息移至死信队列
     */
    private void moveToDeadLetter(Long messageId, String queueName, int attemptCount, String reason) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "UPDATE sys_distributed_queue SET status = ?, updated_at = ?, " +
                "error_message = ? WHERE id = ?");
            ps.setString(1, QueueMessageStatus.DEAD_LETTER.getValue());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setString(3, String.format("失败原因: %s, 重试次数: %d", reason, attemptCount));
            ps.setLong(4, messageId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("移动消息到死信队列失败: messageId={}, error={}", messageId, e.getMessage(), e);
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 获取死信队列统计信息
     */
    public Map<String, Object> getDeadLetterStats() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM sys_distributed_queue WHERE status = ?");
            ps.setString(1, QueueMessageStatus.DEAD_LETTER.getValue());
            rs = ps.executeQuery();
            Integer totalDeadLetters = 0;
            if (rs.next()) {
                totalDeadLetters = rs.getInt(1);
            }
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("deadLetterCount", totalDeadLetters != null ? totalDeadLetters : 0);
            stats.put("lastCheckTime", System.currentTimeMillis());
            return stats;
        } catch (SQLException e) {
            log.error("获取死信队列统计失败: {}", e.getMessage(), e);
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", e.getMessage());
            return errorStats;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 清理历史死信消息
     * @param daysToKeep 保留天数
     * @return 清理的记录数
     */
    public int cleanupOldDeadLetters(int daysToKeep) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "DELETE FROM sys_distributed_queue WHERE status = ? AND updated_at < ?");
            ps.setString(1, QueueMessageStatus.DEAD_LETTER.getValue());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now().minusDays(daysToKeep)));
            int deleted = ps.executeUpdate();
            
            log.info("清理了 {} 条超过 {} 天的死信消息", deleted, daysToKeep);
            return deleted;
        } catch (SQLException e) {
            log.error("清理死信消息失败: {}", e.getMessage(), e);
            return 0;
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    /**
     * 手动重试死信消息
     * @param messageId 消息ID
     * @return 是否成功
     */
    public boolean retryDeadLetterMessage(Long messageId) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "UPDATE sys_distributed_queue SET status = ?, attempt_count = 1, " +
                "consumer_id = NULL, updated_at = ? WHERE id = ? AND status = ?");
            ps.setString(1, QueueMessageStatus.PENDING.getValue());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setLong(3, messageId);
            ps.setString(4, QueueMessageStatus.DEAD_LETTER.getValue());
            int updated = ps.executeUpdate();
            
            if (updated > 0) {
                log.info("死信消息已重新入队: messageId={}", messageId);
                return true;
            }
            return false;
        } catch (SQLException e) {
            log.error("重试死信消息失败: messageId={}, error={}", messageId, e.getMessage(), e);
            return false;
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    
    /**
     * 获取队列的死信配置信息
     * @param queueName 队列名称
     * @return 配置信息
     */
    public Map<String, Object> getDeadLetterConfig(String queueName) {
        Map<String, Object> config = new HashMap<>();
        config.put("maxRetryAttempts", runtimeConfig.getMaxRetryAttempts(queueName));
        config.put("processingTimeoutMs", runtimeConfig.getProcessingTimeoutMs(queueName));
        config.put("checkIntervalMs", runtimeConfig.getDeadLetterCheckIntervalMs(queueName));
        return config;
    }
    
    /**
     * 更新队列的死信配置
     * @param queueName 队列名称
     * @param maxRetryAttempts 最大重试次数
     * @param processingTimeoutMs 处理超时时间(毫秒)
     * @param checkIntervalMs 检查间隔(毫秒)
     */
    public void updateDeadLetterConfig(String queueName, Integer maxRetryAttempts, 
                                     Long processingTimeoutMs, Long checkIntervalMs) {
        if (maxRetryAttempts != null) {
            runtimeConfig.setMaxRetryAttempts(queueName, maxRetryAttempts);
        }
        if (processingTimeoutMs != null) {
            runtimeConfig.setProcessingTimeoutMs(queueName, processingTimeoutMs);
        }
        if (checkIntervalMs != null) {
            runtimeConfig.setDeadLetterCheckIntervalMs(queueName, checkIntervalMs);
        }
        log.info("更新队列 {} 的死信配置完成", queueName);
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 查询指定状态的队列名称列表
     */
    private List<String> getQueueNamesWithStatus(String... statuses) {
        if (statuses == null || statuses.length == 0) {
            return new ArrayList<>();
        }
        
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            StringBuilder sql = new StringBuilder("SELECT DISTINCT queue_name FROM sys_distributed_queue WHERE status IN (");
            for (int i = 0; i < statuses.length; i++) {
                if (i > 0) sql.append(", ");
                sql.append("?");
            }
            sql.append(")");
            
            ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < statuses.length; i++) {
                ps.setString(i + 1, statuses[i]);
            }
            
            rs = ps.executeQuery();
            List<String> queueNames = new ArrayList<>();
            while (rs.next()) {
                queueNames.add(rs.getString("queue_name"));
            }
            return queueNames;
        } catch (SQLException e) {
            log.error("查询队列名称失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    
    /**
     * 查询超时消息
     */
    private List<Map<String, Object>> getTimeoutMessages(String queueName, String status, Timestamp timeoutThreshold) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "SELECT id, queue_name, message_body, attempt_count, consumer_id, created_at " +
                "FROM sys_distributed_queue " +
                "WHERE queue_name = ? AND status = ? AND updated_at < ?");
            ps.setString(1, queueName);
            ps.setString(2, status);
            ps.setTimestamp(3, timeoutThreshold);
            
            rs = ps.executeQuery();
            List<Map<String, Object>> messages = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> message = new HashMap<>();
                message.put("id", rs.getLong("id"));
                message.put("queue_name", rs.getString("queue_name"));
                message.put("message_body", rs.getString("message_body"));
                message.put("attempt_count", rs.getInt("attempt_count"));
                message.put("consumer_id", rs.getString("consumer_id"));
                message.put("created_at", rs.getTimestamp("created_at"));
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            log.error("查询超时消息失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    
    /**
     * 查询失败消息
     */
    private List<Map<String, Object>> getFailedMessages(String queueName, Timestamp beforeTime) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "SELECT id, queue_name, message_body, attempt_count, consumer_id, created_at " +
                "FROM sys_distributed_queue " +
                "WHERE queue_name = ? AND status = ? AND updated_at < ?");
            ps.setString(1, queueName);
            ps.setString(2, QueueMessageStatus.FAILED.getValue());
            ps.setTimestamp(3, beforeTime);
            
            rs = ps.executeQuery();
            List<Map<String, Object>> messages = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> message = new HashMap<>();
                message.put("id", rs.getLong("id"));
                message.put("queue_name", rs.getString("queue_name"));
                message.put("message_body", rs.getString("message_body"));
                message.put("attempt_count", rs.getInt("attempt_count"));
                message.put("consumer_id", rs.getString("consumer_id"));
                message.put("created_at", rs.getTimestamp("created_at"));
                messages.add(message);
            }
            return messages;
        } catch (SQLException e) {
            log.error("查询失败消息失败: {}", e.getMessage(), e);
            return new ArrayList<>();
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }
    
    /**
     * 静默关闭资源
     */
    private void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }
}