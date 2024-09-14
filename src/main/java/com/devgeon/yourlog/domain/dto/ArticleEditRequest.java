package com.devgeon.yourlog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArticleEditRequest {

    private String email;
    private String password;
    private String title;
    private String content;
}
