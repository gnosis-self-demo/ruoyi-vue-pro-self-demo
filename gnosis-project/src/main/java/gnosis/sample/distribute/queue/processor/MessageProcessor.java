package gnosis.sample.distribute.queue.processor;

/**
 * 业务处理器策略接口
 * 定义统一的消息处理规范
 */
public interface MessageProcessor {
    
    /**
     * 处理消息
     * @param message 消息内容
     * @return 处理结果
     * @throws Exception 处理异常
     */
    String process(String message) throws Exception;
    
    /**
     * 获取处理器支持的队列类型
     * @return 队列类型标识
     */
    String getSupportedQueueType();
    
    /**
     * 获取处理器名称
     * @return 处理器名称
     */
    String getName();
}