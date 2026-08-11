package com.example.api.campusmart.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付单表实体
 */
@Data
@TableName("payments")
public class Payment {

    @TableId(value = "paymentID", type = IdType.AUTO)
    private Long paymentID;

    @TableField("orderID")
    private Long orderID;

    @TableField("userID")
    private Long userID;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("status")
    private String status;

    @TableField("alipayTradeNo")
    private String alipayTradeNo;

    @TableField("payTime")
    private LocalDateTime payTime;

    @TableField("expireTime")
    private LocalDateTime expireTime;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;
}
