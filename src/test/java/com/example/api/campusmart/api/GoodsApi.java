package com.example.api.campusmart.api;

import com.example.api.campusmart.config.TestConfig;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.dto.goods.GoodsAddRequest;
import com.example.api.campusmart.dto.goods.GoodsDetail;
import com.example.api.campusmart.dto.goods.GoodsVo;
import com.example.api.campusmart.dto.goods.PageResult;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * 商品相关接口封装
 */
public class GoodsApi {

    private GoodsApi() {
    }

    public static Result<Long> addGoods(String token, GoodsAddRequest request) {
        return TestConfig.givenWithToken(token)
                    .body(request)
                .when()
                    .post("/app/goods/add")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<Long>>() {
                    });
    }

    public static Result<GoodsDetail> getGoodsById(String token, Long id) {
        return TestConfig.givenWithToken(token)
                    .queryParam("id", id)
                .when()
                    .get("/app/goods/selectById")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<GoodsDetail>>() {
                    });
    }

    public static Result<PageResult<GoodsVo>> pageGoods(String token, long current, long size) {
        return TestConfig.givenWithToken(token)
                    .queryParam("current", current)
                    .queryParam("size", size)
                .when()
                    .get("/app/goods/page")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<PageResult<GoodsVo>>>() {
                    });
    }

    public static Result<PageResult<GoodsVo>> searchGoodsByTitle(String token, long current, long size, String titleKeyword) {
        return TestConfig.givenWithToken(token)
                    .queryParam("current", current)
                    .queryParam("size", size)
                    .queryParam("titleKeyword", titleKeyword)
                .when()
                    .get("/app/goods/search")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<PageResult<GoodsVo>>>() {
                    });
    }

    public static Result<PageResult<GoodsVo>> getGoodsByPoster(String token, long current, long size, Long posterId) {
        return TestConfig.givenWithToken(token)
                    .queryParam("current", current)
                    .queryParam("size", size)
                    .queryParam("posterId", posterId)
                .when()
                    .get("/app/goods/poster")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<PageResult<GoodsVo>>>() {
                    });
    }

    public static Result<Boolean> deleteGoodsById(String token, Long id) {
        return TestConfig.givenWithToken(token)
                    .queryParam("id", id)
                .when()
                    .put("/app/goods/deleteById")
                .then()
                    .statusCode(200)
                    .extract()
                    .as(new TypeRef<Result<Boolean>>() {
                    });
    }

    /**
     * 用于异常场景：返回原始 Response，方便断言 HTTP 状态码
     */
    public static Response addGoodsRaw(String token, GoodsAddRequest request) {
        return TestConfig.givenWithToken(token)
                    .body(request)
                .when()
                    .post("/app/goods/add");
    }

    /**
     * 用于未登录场景：不带 token 发送请求
     */
    public static Response getGoodsByIdWithoutToken(Long id) {
        return TestConfig.given()
                    .queryParam("id", id)
                .when()
                    .get("/app/goods/selectById");
    }
}
