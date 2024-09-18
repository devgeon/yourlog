package com.devgeon.yourlog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserJoinRequest {

    private String email;
    private String username;
    private String password;
}
