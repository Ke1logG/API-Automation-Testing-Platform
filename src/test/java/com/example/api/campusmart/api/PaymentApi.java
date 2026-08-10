package com.example.api.campusmart.api;

import com.example.api.campusmart.config.TestConfig;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.dto.trade.PaymentVo;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;

/**
 * 支付单相关接口封装
 */
public class PaymentApi {

    private PaymentApi() {
    }

    public static Result<PaymentVo> createPayment(String token, Long orderId) {
        return TestConfig.givenWithToken(token)
                    .queryParam("orderId", orderId)
                .when()
                    .post("/app/payments/create")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<PaymentVo>>() {
                    });
    }

    public static Result<PaymentVo> getPaymentByOrderId(String token, Long orderId) {
        return TestConfig.givenWithToken(token)
                .when()
                    .get("/app/payments/order/{orderId}", orderId)
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<PaymentVo>>() {
                    });
    }

    /**
     * 用于异常场景：返回原始 Response，方便断言 HTTP 状态码
     */
    public static Response createPaymentRaw(String token, Long orderId) {
        return TestConfig.givenWithToken(token)
                    .queryParam("orderId", orderId)
                .when()
                    .post("/app/payments/create");
    }
}
