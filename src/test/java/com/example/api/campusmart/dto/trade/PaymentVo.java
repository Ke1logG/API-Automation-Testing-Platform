package com.example.api.campusmart.dto.trade;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 支付单信息
 */
@Data
public class PaymentVo {

    private Long paymentID;
    private Long orderID;
    private Long userID;
    private BigDecimal amount;
    private String status;
    private String alipayTradeNo;
    private Date payTime;
    private Date expireTime;
    private Date createTime;
    private Date updateTime;
}
