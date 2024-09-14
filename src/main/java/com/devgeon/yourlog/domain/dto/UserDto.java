package com.devgeon.yourlog.domain.dto;

import com.devgeon.yourlog.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserDto {

    private String email;
    private String username;
}
