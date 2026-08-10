package com.example.api.campusmart.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    ACCOUNT_EXIST_ERROR(301, "账号已存在"),
    ACCOUNT_NOT_EXIST_ERROR(306, "账号不存在"),
    ACCOUNT_ERROR(307, "用户名或密码错误"),

    GOODS_NOT_FOUND(401, "商品不存在"),
    GOODS_SELF_PURCHASE(402, "不能购买自己发布的商品"),
    GOODS_PRICE_ILLEGAL(403, "商品价格小于或等于零"),
    GOODS_TITLE_EMPTY(404, "商品标题为空"),

    ORDER_NOT_FOUND(701, "订单不存在"),
    ORDER_STATUS_ERROR(702, "订单状态不正确"),
    ORDER_CANCEL_NOT_ALLOWED(703, "只能取消未支付的订单"),
    ORDER_CONFIRM_NOT_ALLOWED(704, "只能确认已支付的订单"),
    ORDER_NO_PERMISSION(705, "无权操作该订单"),

    TOKEN_EXPIRED(601, "token过期"),
    TOKEN_INVALID(602, "token非法"),

    PAYMENT_NOT_FOUND(801, "支付单不存在"),
    PAYMENT_STATUS_ERROR(802, "支付单状态不正确"),
    PAYMENT_NO_PERMISSION(803, "无权操作该支付单");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
