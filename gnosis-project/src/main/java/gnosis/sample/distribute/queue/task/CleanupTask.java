package gnosis.sample.distribute.queue.task;

import gnosis.sample.distribute.queue.service.DistributedQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时清理任务
 * 定期清理已完成的队列消息，释放存储空间
 */
@Component
public class CleanupTask {

    @Autowired
    private DistributedQueueService queueService;

    // 默认保留7天的数据
    private static final int DEFAULT_KEEP_DAYS = 7;

    /**
     * 每天凌晨2点执行清理任务
     * cron表达式: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyCleanup() {
        try {
            System.out.println("Starting daily cleanup task...");
            queueService.cleanupDoneMessages(DEFAULT_KEEP_DAYS);
            System.out.println("Daily cleanup task completed successfully.");
        } catch (Exception e) {
            System.err.println("Error during daily cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 每周日凌晨3点执行深度清理（保留更长时间的数据）
     */
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void weeklyDeepCleanup() {
        try {
            System.out.println("Starting weekly deep cleanup task...");
            // 保留30天的数据进行深度清理
            queueService.cleanupDoneMessages(30);
            System.out.println("Weekly deep cleanup task completed successfully.");
        } catch (Exception e) {
            System.err.println("Error during weekly deep cleanup: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 每小时检查一次系统状态并执行轻量级清理
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void hourlyLightCleanup() {
        try {
            // 轻量级清理：只清理1天前的完成消息
            queueService.cleanupDoneMessages(1);
        } catch (Exception e) {
            System.err.println("Error during hourly light cleanup: " + e.getMessage());
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
            System.out.println("Starting manual cleanup for " + keepDays + " days...");
            queueService.cleanupDoneMessages(keepDays);
            System.out.println("Manual cleanup completed successfully.");
        } catch (Exception e) {
            System.err.println("Error during manual cleanup: " + e.getMessage());
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