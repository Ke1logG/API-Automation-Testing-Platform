package com.example.api.campusmart.testcase;

import com.example.api.campusmart.api.AuthApi;
import com.example.api.campusmart.common.ResultCode;
import com.example.api.campusmart.datadriven.model.LoginFailedCase;
import com.example.api.campusmart.dto.LoginRequest;
import com.example.api.campusmart.dto.RegisterRequest;
import com.example.api.campusmart.dto.Result;
import com.example.api.campusmart.util.RandomUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


@Epic("用户模块")
@Feature("登录注册")
@DisplayName("登录注册数据驱动测试")
public class AuthDataDrivenTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @ParameterizedTest(name = "{0}")
    @MethodSource("loadLoginFailedCases")
    @Story("用户登录")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("登录失败场景-JSON数据驱动")
    @Description("从 JSON 文件读取登录失败用例数据，验证不同异常输入返回对应错误码")
    void shouldFailToLogin(LoginFailedCase testCase) {
        String username = resolveUsername(testCase.getUserStatus());

        LoginRequest loginRequest = LoginRequest.builder()
                .username(username)
                .password(testCase.getPassword())
                .build();

        Result<String> result = AuthApi.login(loginRequest);

        try {
            // 将用例中的字符串转成枚举类对象
            ResultCode expectedCode = ResultCode.valueOf(testCase.getExpectedCode());
            assertThat(result.getCode()).isEqualTo(expectedCode.getCode());
            assertThat(result.getMessage()).isEqualTo(expectedCode.getMessage());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("测试数据中的 expectedCode 不合法: " + testCase.getExpectedCode(), e);
        }
    }

    private static String resolveUsername(String userstatus) {
        if ("REGISTERED".equals(userstatus)) {
            RegisterRequest request = RandomUtil.randomRegisterRequest();
            AuthApi.register(request);
            return request.getUsername();
        }
        return RandomUtil.randomUsername();
    }

    static Stream<LoginFailedCase> loadLoginFailedCases() throws IOException {
        try (InputStream is = AuthDataDrivenTest.class.getResourceAsStream("/cases/auth/login_failed_cases.json")) {
            List<LoginFailedCase> cases = OBJECT_MAPPER.readValue(is, new TypeReference<>() {
            });
            return cases.stream();
        }
    }
}

