package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.AuthApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.context.AccountContext;
import com.example.api.campusmart.context.TestAccount;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.util.JwtUtil;
import com.example.api.campusmart.util.RandomUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 测试基类：每个继承它的测试类在 @BeforeAll 阶段创建一组新的买家和卖家账号
 * 账号信息通过 ThreadLocal 在类内共享，不同测试类之间账号互相隔离
 */
public abstract class BaseTest {

    @BeforeAll
    static void setupAccounts() {
        AccountContext.setSeller(createAccount());
        AccountContext.setBuyer(createAccount());
    }

    @AfterAll
    static void cleanupAccounts() {
        AccountContext.clear();
    }

    private static TestAccount createAccount() {
        RegisterRequest registerRequest = RandomUtil.randomRegisterRequest();

        Result<Void> registerResult = AuthApi.register(registerRequest);
        assertThat(registerResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(registerResult.getMessage()).isEqualTo(ResultCode.SUCCESS.getMessage());

        LoginRequest loginRequest = LoginRequest.builder()
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .build();

        Result<String> loginResult = AuthApi.login(loginRequest);
        assertThat(loginResult.getCode()).isEqualTo(ResultCode.SUCCESS.getCode());
        assertThat(loginResult.getData()).isNotBlank();

        Long userId = JwtUtil.parseUserId(loginResult.getData());

        return TestAccount.builder()
                .userId(userId)
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .token(loginResult.getData())
                .build();
    }
}
