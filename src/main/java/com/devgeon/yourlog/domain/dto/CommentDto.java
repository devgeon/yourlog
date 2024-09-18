package com.devgeon.yourlog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommentDto {

    private Long commentId;
    private String email;
    private String content;
}
