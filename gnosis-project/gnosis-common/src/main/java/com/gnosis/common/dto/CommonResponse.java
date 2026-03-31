package com.gnosis.common.dto;

import java.io.Serializable;

public class CommonResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;

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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
