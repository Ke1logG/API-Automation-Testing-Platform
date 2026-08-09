package com.example.api.campusmart.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    ACCOUNT_EXIST_ERROR(301, "账号已存在"),
    ACCOUNT_NOT_EXIST_ERROR(306, "账号不存在"),
    ACCOUNT_ERROR(307, "用户名或密码错误");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
