package com.example.api.campusmart.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 钱包流水表实体
 */
@Data
@TableName("wallet_flows")
public class WalletFlow {

    @TableId(value = "flowID", type = IdType.AUTO)
    private Long flowID;

    @TableField("userID")
    private Long userID;

    @TableField("flowType")
    private String flowType;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("balance")
    private BigDecimal balance;

    @TableField("relatedID")
    private Long relatedID;

    @TableField("remark")
    private String remark;

    @TableField("createTime")
    private LocalDateTime createTime;
}
