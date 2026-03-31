package com.gnosis.distribute.queue.dto;

import lombok.Data;

/**
 * 业务处理器请求对象
 */
@Data
public class BusinessProcessRequest {
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 业务数据
     */
    private Object businessData;
    
    /**
     * 队列名称
     */
    private String queueName;
    
    /**
     * 时间戳
     */
    private long timestamp;
    
    public BusinessProcessRequest() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public BusinessProcessRequest(String requestId, Object businessData, String queueName) {
        this();
        this.requestId = requestId;
        this.businessData = businessData;
        this.queueName = queueName;
    }
}