package com.example.api.campusmart.context;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestAccount {

    private Long userId;
    private String username;
    private String password;
    private String token;
}
