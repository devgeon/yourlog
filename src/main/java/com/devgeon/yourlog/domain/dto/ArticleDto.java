package com.devgeon.yourlog.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ArticleDto {

    private Long articleId;
    private String email;
    private String title;
    private String content;
}
