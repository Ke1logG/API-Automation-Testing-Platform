package com.example.api.campusmart.dto.goods;

import lombok.Data;

import java.util.Date;

/**
 * 商品详情
 */
@Data
public class GoodsDetail {

    private Long goodID;
    private Long publishUserID;
    private String title;
    private String appearance;
    private String itemDescription;
    private Long price;
    private Date publishTime;
}
