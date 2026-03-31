package com.gnosis.distribute.queue.dto.request;

import lombok.Data;

/**
 * 任务提交请求对象
 */
@Data
public class TaskSubmitRequest {
    /**
     * 队列名称
     */
    private String queueName;
    
    /**
     * 任务负载数据
     */
    private String payload;
    
    /**
     * 超时时间(毫秒)，仅同步提交时使用
     */
    private Long timeoutMs;
    
    public TaskSubmitRequest() {}
    
    public TaskSubmitRequest(String queueName, String payload) {
        this.queueName = queueName;
        this.payload = payload;
    }
    
    public TaskSubmitRequest(String queueName, String payload, Long timeoutMs) {
        this.queueName = queueName;
        this.payload = payload;
        this.timeoutMs = timeoutMs;
    }
}