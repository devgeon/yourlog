package com.devgeon.yourlog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommentEditRequest {

    private String email;
    private String password;

    @NotNull
    @NotEmpty
    @NotBlank
    private String content;
}
