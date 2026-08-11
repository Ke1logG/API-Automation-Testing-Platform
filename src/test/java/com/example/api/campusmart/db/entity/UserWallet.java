package com.example.api.campusmart.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户钱包表实体
 */
@Data
@TableName("user_wallets")
public class UserWallet {

    @TableId(value = "walletID", type = IdType.AUTO)
    private Long walletID;

    @TableField("userID")
    private Long userID;

    @TableField("balance")
    private BigDecimal balance;

    @TableField("version")
    private Long version;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;
}
