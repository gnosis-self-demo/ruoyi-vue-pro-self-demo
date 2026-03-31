package com.gnosis.distribute.queue.processor;

import com.gnosis.distribute.queue.dto.BusinessProcessRequest;
import com.gnosis.distribute.queue.dto.BusinessProcessResult;

/**
 * 分布式队列业务处理器抽象基类
 * 定义统一的消息处理规范
 */
public abstract class AbstractDistributeQueueBusinessProcessor {
    
    /**
     * 处理消息
     * @param request 处理请求对象
     * @return 处理结果对象
     * @throws Exception 处理异常
     */
    public abstract BusinessProcessResult process(BusinessProcessRequest request) throws Exception;
    
    /**
     * 获取处理器支持的队列类型
     * @return 队列类型标识
     */
    public abstract String getSupportedQueueType();
    
    /**
     * 获取处理器名称
     * @return 处理器名称
     */
    public abstract String getName();
}