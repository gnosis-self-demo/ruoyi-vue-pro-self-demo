package com.gnosis.distribute.queue.dto;

import lombok.Data;

/**
 * 业务处理器结果对象
 */
@Data
public class BusinessProcessResult {
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 结果数据
     */
    private Object resultData;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 处理时间戳
     */
    private long processTime;
    
    public BusinessProcessResult() {
        this.processTime = System.currentTimeMillis();
    }
    
    public BusinessProcessResult(String requestId, boolean success, Object resultData, String errorMessage) {
        this();
        this.requestId = requestId;
        this.success = success;
        this.resultData = resultData;
        this.errorMessage = errorMessage;
    }
    
    /**
     * 创建成功结果
     */
    public static BusinessProcessResult success(String requestId, Object resultData) {
        return new BusinessProcessResult(requestId, true, resultData, null);
    }
    
    /**
     * 创建失败结果
     */
    public static BusinessProcessResult failure(String requestId, String errorMessage) {
        return new BusinessProcessResult(requestId, false, null, errorMessage);
    }
}