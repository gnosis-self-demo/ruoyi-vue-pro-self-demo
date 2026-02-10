package gnosis.sample.distribute.queue.config.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 队列启动配置实体类
 * 用于从配置文件加载队列初始配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "distributed.queue")
public class QueueStartupConfig {
    
    /**
     * 默认队列配置
     */
    private Map<String, QueueConfig> queues = new HashMap<>();
    
    /**
     * 默认配置值
     */
    private Defaults defaults = new Defaults();
    
    @Data
    public static class QueueConfig {
        private Integer maxLength;
        private Integer maxQps;
        private Long pollInterval;
        private Boolean enabled = true;
    }
    
    @Data
    public static class Defaults {
        private Integer maxLength = 1000;
        private Integer maxQps = 50;
        private Long pollInterval = 1000L;
    }
}