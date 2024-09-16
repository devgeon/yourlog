package com.devgeon.yourlog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDeleteResponse {

    private String email;
    private String password;
}
