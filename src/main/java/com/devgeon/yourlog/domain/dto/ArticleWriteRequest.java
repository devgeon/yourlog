package com.devgeon.yourlog.domain.dto;

import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
public class ArticleWriteRequest {

    private String email;
    private String password;
    private String title;
    private String content;
}
