package com.example.api.campusmart.dto.goods;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发布商品请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsAddRequest {

    private Long publishUserID;
    private String title;
    private String appearance;
    private String itemDescription;
    private Long price;
}
