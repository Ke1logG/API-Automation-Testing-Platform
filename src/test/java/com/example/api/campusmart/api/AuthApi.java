package com.example.api.campusmart.api;

import com.example.api.campusmart.config.TestConfig;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import io.qameta.allure.Step;
import io.restassured.common.mapper.TypeRef;

/**
 * 登录注册相关接口封装层
 */
public class AuthApi {

    @Step("调用登录接口：username={request.username}")
    public static Result<String> login(LoginRequest request) {
        return TestConfig.given()
                    .body(request)
                .when()
                    .post("/app/login")
                .then()
                    .extract()
                    .as(new TypeRef<Result<String>>() {});
    }

    @Step("调用注册接口：username={request.username}")
    public static Result<Void> register(RegisterRequest request) {
        return TestConfig.given()
                    .body(request)
                .when()
                    .post("/app/register")
                .then()
                    .extract()
                    .as(new TypeRef<Result<Void>>() {});
    }
}
