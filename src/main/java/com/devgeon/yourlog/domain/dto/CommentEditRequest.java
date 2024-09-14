package com.devgeon.yourlog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentEditRequest {

    private Long commentId;
    private String email;
    private String content;
}
