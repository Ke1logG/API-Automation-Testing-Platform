package com.example.api.campusmart.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 商品表实体
 */
@Data
@TableName("goods")
public class Goods {

    @TableId("goodID")
    private Long goodID;

    @TableField("publishUserID")
    private Long publishUserID;

    @TableField("title")
    private String title;

    @TableField("appearance")
    private String appearance;

    @TableField("itemDescription")
    private String itemDescription;

    @TableField("price")
    private Long price;

    @TableField("publishTime")
    private Date publishTime;
}
