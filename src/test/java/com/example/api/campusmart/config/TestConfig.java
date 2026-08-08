package com.example.api.campusmart.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 测试环境配置：加载 application.properties 并初始化 RestAssured 全局配置
 */
public class TestConfig {

    private static final Properties PROPS = new Properties();
    private static final String BASE_URL;

    static {
        try (InputStream is = TestConfig.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new IllegalStateException("未找到 application.properties");
            }
            PROPS.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("加载 application.properties 失败", e);
        }

        BASE_URL = PROPS.getProperty("base.url", "http://localhost:8080");
        RestAssured.baseURI = BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        RestAssured.config = RestAssured.config()
                .objectMapperConfig(new ObjectMapperConfig(ObjectMapperType.JACKSON_2));
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static RequestSpecification given() {
        return RestAssured.given()
                .contentType("application/json")
                .accept("application/json");
    }
}
