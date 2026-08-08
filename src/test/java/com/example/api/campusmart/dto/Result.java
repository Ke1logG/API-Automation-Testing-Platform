package com.example.api.campusmart.dto;

import lombok.Data;

/**
 * 后端统一响应结果封装
 */
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
