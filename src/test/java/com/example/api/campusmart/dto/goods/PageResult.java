package com.example.api.campusmart.dto.goods;

import lombok.Data;

import java.util.List;

/**
 * 分页查询响应封装
 */
@Data
public class PageResult<T> {

    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private Long pages;
}
