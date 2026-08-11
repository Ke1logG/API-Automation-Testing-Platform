package com.example.api.campusmart.api;

import com.example.api.campusmart.config.TestConfig;
import com.example.api.campusmart.db.entity.UserWallet;
import com.example.api.campusmart.db.entity.WalletFlow;
import com.example.api.campusmart.dto.Result;
import io.restassured.common.mapper.TypeRef;

import java.math.BigDecimal;
import java.util.List;

/**
 * 钱包相关接口封装
 */
public class WalletApi {

    private WalletApi() {
    }

    public static Result<UserWallet> getWallet(String token) {
        return TestConfig.givenWithToken(token)
                .when()
                    .get("/app/wallet")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<UserWallet>>() {
                    });
    }

    public static Result<List<WalletFlow>> listFlows(String token) {
        return TestConfig.givenWithToken(token)
                .when()
                    .get("/app/wallet/flows")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<List<WalletFlow>>>() {
                    });
    }

    public static Result<Boolean> withdraw(String token, BigDecimal amount, String alipayAccount) {
        return TestConfig.givenWithToken(token)
                    .queryParam("amount", amount)
                    .queryParam("alipayAccount", alipayAccount)
                .when()
                    .post("/app/wallet/withdraw")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<Boolean>>() {
                    });
    }
}
