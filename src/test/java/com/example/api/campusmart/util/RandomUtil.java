package com.example.api.campusmart.util;

import com.example.api.campusmart.dto.RegisterRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 测试数据随机生成工具
 */
public class RandomUtil {

    public static String randomUsername() {
        return "test_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"))
                + "_" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    public static Long randomPhone() {
        long suffix = ThreadLocalRandom.current().nextLong(100000000L, 999999999L);
        return Long.parseLong("13" + suffix);
    }

    public static RegisterRequest randomRegisterRequest() {
        return RegisterRequest.builder()
                .username(randomUsername())
                .password("123456")
                .phone(randomPhone())
                .schoolName("Test University")
                .studentID(ThreadLocalRandom.current().nextLong(10000000L, 99999999L))
                .build();
    }
}
