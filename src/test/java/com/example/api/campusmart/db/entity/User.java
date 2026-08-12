package com.example.api.campusmart.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户表实体
 */
@Data
@TableName("user")
public class User {

    @TableId(value = "userID", type = IdType.NONE)
    private Long userID;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("Nickname")
    private String nickname;

    @TableField("email")
    private String email;

    @TableField("phone")
    private Long phone;

    @TableField("profileSignature")
    private String profileSignature;

    @TableField("schoolName")
    private String schoolName;

    @TableField("studentID")
    private Long studentID;

    @TableField("avatarURL")
    private String avatarURL;
}
