package gnosis.sample.distribute.queue.service;

import gnosis.sample.distribute.queue.enums.QueueMessageStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private JdbcTemplate jdbcTemplate;

    // 默认超时时间：30分钟
    private static final long DEFAULT_PROCESSING_TIMEOUT_MS = 30 * 60 * 1000L;
    
    // 最大重试次数
    private static final int MAX_RETRY_ATTEMPTS = 3;

    /**
     * 每5分钟检查一次超时的任务
     */
    @Scheduled(fixedDelay = 5 * 60 * 1000) // 5分钟执行一次
    public void checkProcessingTimeout() {
        try {
            log.debug("开始检查超时处理中的任务...");
            
            Timestamp timeoutThreshold = Timestamp.valueOf(
                LocalDateTime.now().minusMinutes(30)); // 30分钟前
            
            // 查找超时的处理中任务
            List<Map<String, Object>> timeoutMessages = jdbcTemplate.queryForList(
                "SELECT id, queue_name, message_body, attempt_count, consumer_id, created_at " +
                "FROM sys_distributed_queue " +
                "WHERE status = ? AND updated_at < ?",
                QueueMessageStatus.PROCESSING.getValue(), timeoutThreshold);
            
            int movedCount = 0;
            for (Map<String, Object> message : timeoutMessages) {
                Long messageId = ((Number) message.get("id")).longValue();
                String queueName = (String) message.get("queue_name");
                Integer attemptCount = ((Number) message.get("attempt_count")).intValue();
                
                if (attemptCount < MAX_RETRY_ATTEMPTS) {
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
                log.info("本次共处理 {} 个超时任务", movedCount);
            }
            
        } catch (Exception e) {
            log.error("检查超时任务时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 每小时检查失败的任务
     */
    @Scheduled(cron = "0 0 * * * ?") // 每小时执行
    public void checkFailedTasks() {
        try {
            log.debug("开始检查失败的任务...");
            
            // 查找标记为失败但还未处理的任务
            List<Map<String, Object>> failedMessages = jdbcTemplate.queryForList(
                "SELECT id, queue_name, message_body, attempt_count, consumer_id, created_at " +
                "FROM sys_distributed_queue " +
                "WHERE status = ? AND updated_at < ?",
                QueueMessageStatus.FAILED.getValue(), 
                Timestamp.valueOf(LocalDateTime.now().minusHours(1))); // 1小时前
            
            int processedCount = 0;
            for (Map<String, Object> message : failedMessages) {
                Long messageId = ((Number) message.get("id")).longValue();
                String queueName = (String) message.get("queue_name");
                Integer attemptCount = ((Number) message.get("attempt_count")).intValue();
                
                if (attemptCount < MAX_RETRY_ATTEMPTS) {
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
                log.info("本次共处理 {} 个失败任务", processedCount);
            }
            
        } catch (Exception e) {
            log.error("检查失败任务时发生错误: {}", e.getMessage(), e);
        }
    }

    /**
     * 将消息重新设置为待处理状态
     */
    private void moveToPending(Long messageId, String queueName, int newAttemptCount) {
        try {
            int updated = jdbcTemplate.update(
                "UPDATE sys_distributed_queue SET status = ?, consumer_id = NULL, " +
                "attempt_count = ?, updated_at = ? WHERE id = ?",
                QueueMessageStatus.PENDING.getValue(), newAttemptCount, 
                new Timestamp(System.currentTimeMillis()), messageId);
            
            if (updated == 0) {
                log.warn("未能更新消息状态: messageId={}", messageId);
            }
        } catch (Exception e) {
            log.error("移动消息到待处理状态失败: messageId={}, error={}", messageId, e.getMessage(), e);
        }
    }

    /**
     * 将消息移至死信队列
     */
    private void moveToDeadLetter(Long messageId, String queueName, int attemptCount, String reason) {
        try {
            // 更新原消息状态为死信
            int updated = jdbcTemplate.update(
                "UPDATE sys_distributed_queue SET status = ?, updated_at = ?, " +
                "error_message = ? WHERE id = ?",
                QueueMessageStatus.DEAD_LETTER.getValue(), 
                new Timestamp(System.currentTimeMillis()), 
                String.format("失败原因: %s, 重试次数: %d", reason, attemptCount),
                messageId);
            
            if (updated > 0) {
                log.info("消息已移至死信队列: messageId={}, queue={}, reason={}", 
                    messageId, queueName, reason);
            }
        } catch (Exception e) {
            log.error("移动消息到死信队列失败: messageId={}, error={}", messageId, e.getMessage(), e);
        }
    }

    /**
     * 获取死信队列统计信息
     */
    public Map<String, Object> getDeadLetterStats() {
        try {
            Integer totalDeadLetters = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sys_distributed_queue WHERE status = ?",
                Integer.class, QueueMessageStatus.DEAD_LETTER.getValue());
            
            Map<String, Object> stats = new java.util.HashMap<>();
            stats.put("deadLetterCount", totalDeadLetters != null ? totalDeadLetters : 0);
            stats.put("lastCheckTime", System.currentTimeMillis());
            return stats;
        } catch (Exception e) {
            log.error("获取死信队列统计失败: {}", e.getMessage(), e);
            Map<String, Object> errorStats = new java.util.HashMap<>();
            errorStats.put("error", e.getMessage());
            return errorStats;
        }
    }

    /**
     * 清理历史死信消息
     * @param daysToKeep 保留天数
     * @return 清理的记录数
     */
    public int cleanupOldDeadLetters(int daysToKeep) {
        try {
            int deleted = jdbcTemplate.update(
                "DELETE FROM sys_distributed_queue WHERE status = ? AND updated_at < ?",
                QueueMessageStatus.DEAD_LETTER.getValue(),
                Timestamp.valueOf(LocalDateTime.now().minusDays(daysToKeep)));
            
            log.info("清理了 {} 条超过 {} 天的死信消息", deleted, daysToKeep);
            return deleted;
        } catch (Exception e) {
            log.error("清理死信消息失败: {}", e.getMessage(), e);
            return 0;
        }
    }

    /**
     * 手动重试死信消息
     * @param messageId 消息ID
     * @return 是否成功
     */
    public boolean retryDeadLetterMessage(Long messageId) {
        try {
            int updated = jdbcTemplate.update(
                "UPDATE sys_distributed_queue SET status = ?, attempt_count = 1, " +
                "consumer_id = NULL, updated_at = ? WHERE id = ? AND status = ?",
                QueueMessageStatus.PENDING.getValue(), 
                new Timestamp(System.currentTimeMillis()),
                messageId, QueueMessageStatus.DEAD_LETTER.getValue());
            
            if (updated > 0) {
                log.info("死信消息已重新入队: messageId={}", messageId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("重试死信消息失败: messageId={}, error={}", messageId, e.getMessage(), e);
            return false;
        }
    }
}