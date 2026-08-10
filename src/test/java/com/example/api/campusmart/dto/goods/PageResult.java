package com.example.api.campusmart.dto.goods;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * 分页查询响应封装
 * 后端 PageVo 继承 MyBatis Plus 的 Page，会返回 orders 字段，测试端不需要，故忽略未知字段
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageResult<T> {

    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private Long pages;
}
