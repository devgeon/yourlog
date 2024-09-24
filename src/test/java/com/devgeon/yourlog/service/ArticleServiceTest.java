package com.devgeon.yourlog.service;

import com.devgeon.yourlog.domain.dto.*;
import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.exception.UserAuthenticationException;
import com.devgeon.yourlog.repository.ArticleRepository;
import com.devgeon.yourlog.repository.CommentRepository;
import com.devgeon.yourlog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    final Long USER_ID = 1L, ARTICLE_ID = 1L, COMMENT_ID = 1L;
    final String EMAIL = "test@test.com", USERNAME = "testUsername", PASSWORD = "testPassword",
            ARTICLE_TITLE = "testArticleTitle", ARTICLE_CONTENT = "testArticleContent", COMMENT_CONTENT = "testCommentContent";
    final String OTHER_EMAIL = "other@test.com", WRONG_PASSWORD = "wrongPassword", NEW_ARTICLE_TITLE = "newArticleTitle", NEW_ARTICLE_CONTENT = "newArticleContent";

    final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD);
    final Article ARTICLE = new Article(ARTICLE_ID, ARTICLE_TITLE, ARTICLE_CONTENT, USER);

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private ArticleService articleService;

    private User getTestUser() {
        return new User(USER_ID, EMAIL, USERNAME, bCryptPasswordEncoder.encode(PASSWORD));
    }

    private User getOtherUser() {
        return new User(USER_ID + 1, OTHER_EMAIL, USERNAME, bCryptPasswordEncoder.encode(PASSWORD));
    }

    @Test
    public void writeSuccess() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(articleRepository.save(any())).thenReturn(new Article(ARTICLE_ID, ARTICLE_TITLE, ARTICLE_CONTENT, USER));

        // when
        ArticleDto articleDto = articleService.write(new ArticleWriteRequest(EMAIL, PASSWORD, ARTICLE_TITLE, ARTICLE_CONTENT));

        // then
        assertThat(articleDto.getArticleId()).isNotNull();
        assertThat(articleDto.getEmail()).isEqualTo(EMAIL);
        assertThat(articleDto.getTitle()).isEqualTo(ARTICLE_TITLE);
        assertThat(articleDto.getContent()).isEqualTo(ARTICLE_CONTENT);
    }

    @Test
    public void writeFailByWrongEmail() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Collections.emptyList());

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.write(new ArticleWriteRequest(EMAIL, PASSWORD, ARTICLE_TITLE, ARTICLE_CONTENT)));

        // then
    }

    @Test
    public void writeFailByWrongPassword() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.write(new ArticleWriteRequest(EMAIL, WRONG_PASSWORD, ARTICLE_TITLE, ARTICLE_CONTENT)));

        // then
    }

    @Test
    public void editTitleSuccess() {
        // given
        final User USER = getTestUser();

        Article article = new Article(ARTICLE_ID, ARTICLE_TITLE, ARTICLE_CONTENT, USER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));

        // when
        articleService.edit(ARTICLE_ID, new ArticleEditRequest(EMAIL, PASSWORD, NEW_ARTICLE_TITLE, ARTICLE_CONTENT));

        // then
        assertThat(article.getId()).isEqualTo(ARTICLE_ID);
        assertThat(article.getTitle()).isEqualTo(NEW_ARTICLE_TITLE);
        assertThat(article.getContent()).isEqualTo(ARTICLE_CONTENT);
        assertThat(article.getUser()).isEqualTo(USER);
    }

    @Test
    public void editContentSuccess() {
        // given
        final User USER = getTestUser();

        Article article = new Article(ARTICLE_ID, ARTICLE_TITLE, ARTICLE_CONTENT, USER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));

        // when
        articleService.edit(ARTICLE_ID, new ArticleEditRequest(EMAIL, PASSWORD, ARTICLE_TITLE, NEW_ARTICLE_CONTENT));

        // then
        assertThat(article.getId()).isEqualTo(ARTICLE_ID);
        assertThat(article.getTitle()).isEqualTo(ARTICLE_TITLE);
        assertThat(article.getContent()).isEqualTo(NEW_ARTICLE_CONTENT);
        assertThat(article.getUser()).isEqualTo(USER);
    }

    @Test
    public void editFailByWrongEmail() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Collections.emptyList());

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.edit(ARTICLE_ID, new ArticleEditRequest(EMAIL, PASSWORD, ARTICLE_TITLE, ARTICLE_CONTENT)));

        // then
    }

    @Test
    public void editFailByWrongPassword() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.edit(ARTICLE_ID, new ArticleEditRequest(EMAIL, WRONG_PASSWORD, ARTICLE_TITLE, ARTICLE_CONTENT)));

        // then
    }

    @Test
    public void editFailByWrongAccount() {
        // given
        final User OTHER_USER = getOtherUser();

        when(userRepository.findByEmail(OTHER_EMAIL)).thenReturn(List.of(OTHER_USER));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(ARTICLE));

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.edit(ARTICLE_ID, new ArticleEditRequest(OTHER_EMAIL, PASSWORD, ARTICLE_TITLE, ARTICLE_CONTENT)));

        // then
    }

    @Test
    public void deleteSuccess() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(ARTICLE));

        // when
        ArticleDeleteResponse deleteResponse = articleService.delete(ARTICLE_ID, new ArticleDeleteRequest(EMAIL, PASSWORD));

        // then
        verify(articleRepository).deleteById(ARTICLE_ID);
        verify(commentRepository).deleteByArticle(ARTICLE);

        assertThat(deleteResponse.getEmail()).isEqualTo(EMAIL);
        assertThat(deleteResponse.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    public void deleteFailByWrongEmail() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Collections.emptyList());

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.delete(ARTICLE_ID, new ArticleDeleteRequest(EMAIL, PASSWORD)));

        // then
    }

    @Test
    public void deleteFailByWrongPassword() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.delete(ARTICLE_ID, new ArticleDeleteRequest(EMAIL, WRONG_PASSWORD)));

        // then
    }

    @Test
    public void deleteFailByWrongAccount() {
        // given
        final User OTHER_USER = getOtherUser();

        when(userRepository.findByEmail(OTHER_EMAIL)).thenReturn(List.of(OTHER_USER));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(ARTICLE));

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.delete(ARTICLE_ID, new ArticleDeleteRequest(OTHER_EMAIL, PASSWORD)));

        // then
    }

}
