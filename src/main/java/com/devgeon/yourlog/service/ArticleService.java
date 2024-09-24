package com.devgeon.yourlog.service;

import com.devgeon.yourlog.domain.dto.*;
import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.exception.ArticleNotFoundException;
import com.devgeon.yourlog.exception.UserAuthenticationException;
import com.devgeon.yourlog.repository.ArticleRepository;
import com.devgeon.yourlog.repository.CommentRepository;
import com.devgeon.yourlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArticleService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ArticleDto write(ArticleWriteRequest writeRequest) {

        User user = getUserOrThrow(writeRequest.getEmail(), writeRequest.getPassword());

        Article article = articleRepository.save(Article.builder()
                .title(writeRequest.getTitle())
                .content(writeRequest.getContent())
                .user(user)
                .build());

        return new ArticleDto(article.getId(), article.getUser().getEmail(), article.getTitle(), article.getContent());
    }

    public ArticleDto edit(Long id, ArticleEditRequest editRequest) {

        User user = getUserOrThrow(editRequest.getEmail(), editRequest.getPassword());
        Article article = articleRepository.findById(id).orElseThrow(ArticleNotFoundException::new);
        if (!user.getId().equals(article.getUser().getId())) {
            throw new UserAuthenticationException();
        }

        article.update(editRequest.getTitle(), editRequest.getContent());

        return new ArticleDto(article.getId(), article.getUser().getEmail(), article.getTitle(), article.getContent());
    }

    public ArticleDeleteResponse delete(Long id, ArticleDeleteRequest deleteRequest) {

        User user = getUserOrThrow(deleteRequest.getEmail(), deleteRequest.getPassword());
        Article article = articleRepository.findById(id).orElseThrow(ArticleNotFoundException::new);
        if (!user.getId().equals(article.getUser().getId())) {
            throw new UserAuthenticationException();
        }

        commentRepository.deleteByArticle(article);
        articleRepository.deleteById(id);

        return new ArticleDeleteResponse(deleteRequest.getEmail(), deleteRequest.getPassword());
    }

    private User getUserOrThrow(String email, String password) {

        List<User> userList = userRepository.findByEmail(email);
        if (userList.isEmpty() || !bCryptPasswordEncoder.matches(password, userList.getFirst().getPassword())) {
            throw new UserAuthenticationException();
        }

        return userList.getFirst();
    }

}
