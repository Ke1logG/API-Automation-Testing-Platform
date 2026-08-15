package com.example.api.campusmart.datadriven.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString(of = "name")
public class LoginFailedCase {

    private String name;
    private String userStatus;
    private String password;
    private String expectedCode;
}
