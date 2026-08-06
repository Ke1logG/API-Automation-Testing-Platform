package com.example.api;

import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 示例接口测试类
 */
public class SampleApiTest {

   /* @BeforeAll
    static void setup() {
        io.restassured.RestAssured.baseURI = "https://jsonplaceholder.typicode.com";
    }*/

    @Test
    void testGet() {
        String str = given()
                //.header("Content-Type", "application/json")
                //.param("phone",123455)
                .header("Hello","kl")

        .when()
                .get("https://httpbin.ceshiren.com/get")
        .then()
                .statusCode(200)
                //.body("origin",equalTo("14.127.39.122, 182.92.156.22"))
                .log().all()
                .extract().jsonPath().getString("headers.Hello");

        System.out.println(str);

        assertEquals("kl",str);

    }
    /*@Test
    void testPost() {
        String str = "{\"hellow\":\"123\"}";
        given()
                //.header("Content-Type", "application/json")
                .contentType("application/json")
                .body(str)
                .log().headers()
                .log().body()

                .when()
                .get("https://httpbin.ceshiren.com/get")
                .then()
                .statusCode(200)
                .body("origin",equalTo("14.127.39.122, 182.92.156.22"));
                //.log().all();

    }*/
}
