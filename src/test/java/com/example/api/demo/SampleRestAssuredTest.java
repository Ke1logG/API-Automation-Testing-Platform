package com.example.api.demo;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 示例接口测试类
 */
public class SampleRestAssuredTest {

    @Test
    void testGet() {
        String str = given()
                .header("Hello","kl")

        .when()
                .get("https://httpbin.ceshiren.com/get")
        .then()
                .statusCode(200)
                .log().all()
                .extract().jsonPath().getString("headers.Hello");

        System.out.println(str);

        assertEquals("kl",str);

    }
}
