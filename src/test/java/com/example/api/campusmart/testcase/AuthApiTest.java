package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.AuthApi;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.util.RandomUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 登录注册接口测试用例
 */
@Epic("CampusMart 接口自动化测试")
@Feature("登录注册模块")
public class AuthApiTest {

    @Test
    @Story("用户注册")
    @DisplayName("注册新用户成功")
    @Severity(SeverityLevel.CRITICAL)
    @Description("使用合法参数注册新用户，期望返回成功")
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequest request = RandomUtil.randomRegisterRequest();

        Result<Void> result = AuthApi.register(request);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getMessage()).isEqualTo("成功");
    }

    @Test
    @Story("用户注册")
    @DisplayName("注册失败-用户名已存在")
    @Severity(SeverityLevel.NORMAL)
    @Description("使用已存在的用户名注册，期望返回账号已存在")
    void shouldFailToRegisterWhenUsernameExists() {
        RegisterRequest request = RandomUtil.randomRegisterRequest();
        AuthApi.register(request);

        Result<Void> result = AuthApi.register(request);

        assertThat(result.getCode()).isEqualTo(301);
        assertThat(result.getMessage()).isEqualTo("账号已存在");
    }

    @Test
    @Story("用户登录")
    @DisplayName("登录成功")
    @Severity(SeverityLevel.CRITICAL)
    @Description("使用正确的用户名密码登录，期望返回 JWT token")
    void shouldLoginSuccessfully() {
        RegisterRequest registerRequest = RandomUtil.randomRegisterRequest();
        AuthApi.register(registerRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();

        Result<String> result = AuthApi.login(loginRequest);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).isNotBlank();
    }

    @Test
    @Story("用户登录")
    @DisplayName("登录失败-密码错误")
    @Severity(SeverityLevel.NORMAL)
    @Description("使用错误的密码登录，期望返回用户名或密码错误")
    void shouldFailToLoginWithWrongPassword() {
        RegisterRequest registerRequest = RandomUtil.randomRegisterRequest();
        AuthApi.register(registerRequest);

        LoginRequest loginRequest = LoginRequest.builder()
                .username(registerRequest.getUsername())
                .password("wrong_password")
                .build();

        Result<String> result = AuthApi.login(loginRequest);

        assertThat(result.getCode()).isEqualTo(307);
        assertThat(result.getMessage()).isEqualTo("用户名或密码错误");
    }

    @Test
    @Story("用户登录")
    @DisplayName("登录失败-账号不存在")
    @Severity(SeverityLevel.NORMAL)
    @Description("使用不存在的用户名登录，期望返回账号不存在")
    void shouldFailToLoginWhenUserNotExists() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username(RandomUtil.randomUsername())
                .password("any_password")
                .build();

        Result<String> result = AuthApi.login(loginRequest);

        assertThat(result.getCode()).isEqualTo(306);
        assertThat(result.getMessage()).isEqualTo("账号不存在");
    }
}
