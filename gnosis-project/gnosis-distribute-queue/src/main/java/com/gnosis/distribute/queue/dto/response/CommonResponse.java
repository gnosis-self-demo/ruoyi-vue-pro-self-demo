package com.gnosis.distribute.queue.dto.response;

import lombok.Data;

/**
 * 通用响应对象
 */
@Data
public class CommonResponse<T> {
    /**
     * 响应状态
     */
    private String status;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    public CommonResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public CommonResponse(String status, String message) {
        this();
        this.status = status;
        this.message = message;
    }
    
    public CommonResponse(String status, String message, T data) {
        this(status, message);
        this.data = data;
    }
    
    /**
     * 通用数据类
     */
    @Data
    public static class CommonData {
        private String status;
        private String service;
        private String queueName;
        private Long timestamp;
        
        public CommonData() {
            this.timestamp = System.currentTimeMillis();
        }
    }
    
    /**
     * 创建成功响应
     */
    public static <T> CommonResponse<T> success(String message) {
        return new CommonResponse<>("success", message);
    }
    
    /**
     * 创建成功响应带数据
     */
    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>("success", message, data);
    }
    
    /**
     * 创建错误响应
     */
    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>("error", message);
    }
    
    /**
     * 创建错误响应带数据
     */
    public static <T> CommonResponse<T> error(String message, T data) {
        return new CommonResponse<>("error", message, data);
    }
}