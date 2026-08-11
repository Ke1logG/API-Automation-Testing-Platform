package com.example.api.campusmart.api;

import com.example.api.campusmart.config.TestConfig;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.dto.trade.OrderVo;
import io.restassured.common.mapper.TypeRef;

import java.util.List;

/**
 * 订单相关接口封装
 */
public class OrderApi {

    private OrderApi() {
    }

    public static Result<OrderVo> createOrder(String token, Long goodId) {
        return TestConfig.givenWithToken(token)
                    .queryParam("goodId", goodId)
                .when()
                    .post("/app/orders/create")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<OrderVo>>() {
                    });
    }

    public static Result<OrderVo> getOrderDetail(String token, Long orderId) {
        return TestConfig.givenWithToken(token)
                .when()
                    .get("/app/orders/{orderId}", orderId)
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<OrderVo>>() {
                    });
    }

    public static Result<Boolean> cancelOrder(String token, Long orderId) {
        return TestConfig.givenWithToken(token)
                .when()
                    .post("/app/orders/{orderId}/cancel", orderId)
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<Boolean>>() {
                    });
    }

    public static Result<List<OrderVo>> listBuyerOrders(String token) {
        return TestConfig.givenWithToken(token)
                .when()
                    .get("/app/orders/buyer/list")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<List<OrderVo>>>() {
                    });
    }

    public static Result<List<OrderVo>> listSellerOrders(String token) {
        return TestConfig.givenWithToken(token)
                .when()
                    .get("/app/orders/seller/list")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<List<OrderVo>>>() {
                    });
    }

}
