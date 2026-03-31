package com.gnosis.openplat.dto;

import lombok.Data;

/**
 * 统一响应结果
 */
@Data
public class CommonResponse<T> {
    
    /**
     * 响应码
     */
    private Integer code;
    
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
    
    public static <T> CommonResponse<T> success(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }
    
    public static <T> CommonResponse<T> success() {
        return success(null);
    }
    
    public static <T> CommonResponse<T> error(String message) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(500);
        response.setMessage(message);
        return response;
    }
    
    public static <T> CommonResponse<T> error(Integer code, String message) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}
