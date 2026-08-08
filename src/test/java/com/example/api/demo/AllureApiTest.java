package com.example.api.demo;

import io.qameta.allure.*;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Allure 典型 API 运用示例
 */
@Epic("接口自动化测试")
@Feature("用户管理模块")
public class AllureApiTest {

    @BeforeEach
    void setup() {
        io.restassured.RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
        RestAssured.filters(new AllureRestAssured());
    }

    @Test
    @Story("查询帖子详情")
    @DisplayName("根据ID查询帖子")
    @Description("验证 GET /posts/{id} 接口能正确返回帖子信息")
    @Severity(SeverityLevel.CRITICAL)
    void shouldGetPostById() {
        int postId = 1;
        Allure.parameter("帖子ID", postId);

        given()
            //.filter(new AllureRestAssured())
            .pathParam("id", postId)
        .when()
            .get("/posts/{id}")
        .then()
            .statusCode(200)
            .body("id", equalTo(postId))
            .body("userId", equalTo(1));

        attachResponseSummary(postId);
    }
    /* 
    @Test
    @Story("查询帖子列表")
    @DisplayName("查询所有帖子")
    @Severity(SeverityLevel.NORMAL)
    void shouldGetAllPosts() {
        Allure.step("发送 GET /posts 请求", () -> {
            given()
                //.filter(new AllureRestAssured())
            .when()
                .get("/posts")
            .then()
                .statusCode(200);
        });

        Allure.addAttachment("测试说明", "text/plain", "该接口返回所有帖子列表");
    }*/

    @Step("记录帖子 {postId} 的查询摘要")
    private void attachResponseSummary(int postId) {
        Allure.addAttachment("查询摘要", "text/plain",
                "成功查询帖子，ID = " + postId);
    }
}
