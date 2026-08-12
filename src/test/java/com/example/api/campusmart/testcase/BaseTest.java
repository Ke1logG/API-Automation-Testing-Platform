package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.AuthApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.context.AccountContext;
import com.example.api.campusmart.context.TestAccount;
import com.example.api.campusmart.db.MyBatisPlusConfig;
import com.example.api.campusmart.db.service.TestDataCleanupService;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.util.JwtUtil;
import com.example.api.campusmart.util.RandomUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = MyBatisPlusConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    @Autowired
    protected TestDataCleanupService testDataCleanupService;

    @BeforeAll
    void setupAccounts() {
        AccountContext.setSeller(createAccount());
        AccountContext.setBuyer(createAccount());
    }

    @AfterAll
    void cleanupAccounts() {
        TestAccount seller = AccountContext.getSeller();
        TestAccount buyer = AccountContext.getBuyer();
        if (seller != null && buyer != null) {
            List<Long> userIds = Arrays.asList(seller.getUserId(), buyer.getUserId());
            testDataCleanupService.cleanupByUserIds(userIds);
        }
        AccountContext.clear();
    }

    private TestAccount createAccount() {
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
