package com.example.api.campusmart.db;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("com.example.api.campusmart.db")
@MapperScan("com.example.api.campusmart.db.mapper")
public class MyBatisPlusConfig {
}
