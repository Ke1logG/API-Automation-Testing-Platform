package com.example.api.campusmart.dto.trade;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单信息
 */
@Data
public class OrderVo {

    private Long orderID;
    private Long goodID;
    private String goodTitle;
    private String goodPictureURL;
    private Long buyerID;
    private Long sellerID;
    private BigDecimal amount;
    private String status;
    private Date payTimeout;
    private Date createTime;
}
