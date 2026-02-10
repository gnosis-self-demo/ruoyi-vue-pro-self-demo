package gnosis.sample.distribute.queue.task;

import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时清理任务
 * 定期清理已完成的队列消息，释放存储空间
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CleanupTask {

    private final DistributedQueueFactory queueFactory;

    // 默认保留7天的数据
    private static final int DEFAULT_KEEP_DAYS = 7;

    /**
     * 每天凌晨2点执行清理任务
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyCleanup() {
        try {
            log.info("开始每日清理任务...");
            // 获取任意一个队列实例进行清理
            java.util.Optional<String> firstQueue = queueFactory.getAllQueueNames().stream().findFirst();
            if (firstQueue.isPresent()) {
                String queueName = firstQueue.get();
                queueFactory.getQueueInstance(queueName).getService()
                    .cleanupDoneMessages(DEFAULT_KEEP_DAYS);
                log.info("每日清理任务完成");
            }
        } catch (Exception e) {
            log.error("每日清理任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每周日凌晨3点执行深度清理（保留更长时间的数据）
     */
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void weeklyDeepCleanup() {
        try {
            log.info("开始每周深度清理任务...");
            // 保留30天的数据进行深度清理
            java.util.Optional<String> firstQueue = queueFactory.getAllQueueNames().stream().findFirst();
            if (firstQueue.isPresent()) {
                String queueName = firstQueue.get();
                queueFactory.getQueueInstance(queueName).getService()
                    .cleanupDoneMessages(30);
                log.info("每周深度清理任务完成");
            }
        } catch (Exception e) {
            log.error("每周深度清理任务执行失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 每小时检查一次系统状态并执行轻量级清理
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyLightCleanup() {
        try {
            // 轻量级清理：只清理1天前的完成消息
            java.util.Optional<String> firstQueue = queueFactory.getAllQueueNames().stream().findFirst();
            if (firstQueue.isPresent()) {
                String queueName = firstQueue.get();
                queueFactory.getQueueInstance(queueName).getService()
                    .cleanupDoneMessages(1);
            }
        } catch (Exception e) {
            log.warn("每小时轻量级清理任务执行失败: {}", e.getMessage());
            // 轻量级清理失败不影响主要业务
        }
    }

    /**
     * 手动触发清理任务（可通过管理接口调用）
     * @param keepDays 保留天数
     */
    public void manualCleanup(int keepDays) {
        if (keepDays <= 0) {
            throw new IllegalArgumentException("keepDays must be positive");
        }
        
        try {
            log.info("开始手动清理 {} 天前的数据...", keepDays);
            // 获取任意一个队列实例进行清理
            java.util.Optional<String> firstQueue = queueFactory.getAllQueueNames().stream().findFirst();
            if (firstQueue.isPresent()) {
                String queueName = firstQueue.get();
                queueFactory.getQueueInstance(queueName).getService()
                    .cleanupDoneMessages(keepDays);
                log.info("手动清理完成");
            }
        } catch (Exception e) {
            log.error("手动清理任务异常: {}", e.getMessage(), e);
            throw new RuntimeException("Manual cleanup failed", e);
        }
    }

    /**
     * 获取默认保留天数
     * @return 保留天数
     */
    public int getDefaultKeepDays() {
        return DEFAULT_KEEP_DAYS;
    }
}