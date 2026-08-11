package com.example.api.campusmart.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表实体
 */
@Data
@TableName("orders")
public class Order {

    @TableId(value = "orderID", type = IdType.AUTO)
    private Long orderID;

    @TableField("goodID")
    private Long goodID;

    @TableField("buyerID")
    private Long buyerID;

    @TableField("sellerID")
    private Long sellerID;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("status")
    private String status;

    @TableField("payTimeout")
    private LocalDateTime payTimeout;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;
}
