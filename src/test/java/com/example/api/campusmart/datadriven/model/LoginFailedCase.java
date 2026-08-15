package com.example.api.campusmart.datadriven.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 登录失败场景数据驱动用例模型
 * <p>
 * 对应 JSON 数据文件中的单条用例，描述一次登录失败测试的输入与预期。
 */
@Getter
@Setter
@ToString(of = "name")
public class LoginFailedCase {

    private String name;
    private String userStatus;
    private String password;
    private String expectedCode;
}
