package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.AuthApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.util.RandomUtil;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 登录注册接口测试用例
 */
@Epic("用户模块")
@Feature("登录注册")
public class AuthApiTest {

    @Test
    @Story("用户注册")
    @DisplayName("注册新用户成功")
    @Severity(SeverityLevel.CRITICAL)
    @Description("使用合法参数注册新用户，期望返回成功")
    void shouldRegisterNewUserSuccessfully() {
        RegisterRequest request = RandomUtil.randomRegisterRequest();

        Result<Void> result = AuthApi.register(request);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(ResultCode.SUCCESS.getMessage());
    }

    @Test
    @Story("用户注册")
    @DisplayName("注册失败-用户名已存在")
    @Severity(SeverityLevel.NORMAL)
    @Description("使用已存在的用户名注册，期望返回账号已存在")
    void shouldFailToRegisterWhenUsernameExists() {
        RegisterRequest request = RandomUtil.randomRegisterRequest();

        Result<Void> firstRegisterResult = AuthApi.register(request);
        assertThat(firstRegisterResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(firstRegisterResult.getMessage()).isEqualTo(ResultCode.SUCCESS.getMessage());

        Result<Void> secondRegisterResult = AuthApi.register(request);

        assertThat(secondRegisterResult.getCode()).isEqualTo(ResultCode.ACCOUNT_EXIST_ERROR.getCode());
        assertThat(secondRegisterResult.getMessage()).isEqualTo(ResultCode.ACCOUNT_EXIST_ERROR.getMessage());
    }

    @Test
    @Story("用户登录")
    @DisplayName("登录成功")
    @Severity(SeverityLevel.CRITICAL)
    @Description("使用正确的用户名密码登录，期望返回 JWT token")
    void shouldLoginSuccessfully() {
        RegisterRequest registerRequest = RandomUtil.randomRegisterRequest();

        Result<Void> registerResult = AuthApi.register(registerRequest);
        assertThat(registerResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(registerResult.getMessage()).isEqualTo(ResultCode.SUCCESS.getMessage());

        LoginRequest loginRequest = LoginRequest.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();

        Result<String> result = AuthApi.login(loginRequest);

        assertThat(result.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(result.getData()).isNotBlank();
    }

    @ParameterizedTest(name = "{3}")
    @CsvSource(delimiter = '|', value = {
            "registered | wrong_password | ACCOUNT_ERROR | 登录失败-密码错误",
            "unregistered | any_password | ACCOUNT_NOT_EXIST_ERROR | 登录失败-账号不存在"
    })
    @Story("用户登录")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("登录失败场景")
    @Description("使用不同异常输入登录，期望返回对应错误码")
    void shouldFailToLogin(String failedCase, String password, ResultCode expectedCode, String description) {
        String username;
        if ("registered".equals(failedCase)) {
            RegisterRequest registerRequest = RandomUtil.randomRegisterRequest();
            AuthApi.register(registerRequest);
            username = registerRequest.getUsername();
        } else {
            username = RandomUtil.randomUsername();
        }

        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        Result<String> result = AuthApi.login(loginRequest);

        assertThat(result.getCode()).isEqualTo(expectedCode.getCode());
        assertThat(result.getMessage()).isEqualTo(expectedCode.getMessage());
    }
}
