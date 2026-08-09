package com.example.api.campusmart.dto.goods;

import lombok.Data;

/**
 * 商品列表/搜索结果项
 */
@Data
public class GoodsVo {

    private String pictureURL;
    private Long goodID;
    private Long publishUserID;
    private String avatarURL;
    private String nickname;
    private String title;
    private String appearance;
    private String itemDescription;
    private Long price;
}
