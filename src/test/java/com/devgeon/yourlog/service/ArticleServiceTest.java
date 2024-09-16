package com.devgeon.yourlog.service;

import com.devgeon.yourlog.domain.dto.*;
import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.Comment;
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
import org.mockito.junit.jupiter.MockitoExtension;

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
            ARTICLE_TITLE = "testTitle", ARTICLE_CONTENT = "testArticleContent", COMMENT_CONTENT = "testCommentContent";
    final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD);
    final Article ARTICLE = new Article(ARTICLE_ID, ARTICLE_TITLE, ARTICLE_CONTENT, USER);
    final Comment COMMENT = new Comment(COMMENT_ID, COMMENT_CONTENT, ARTICLE, USER);

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ArticleService articleService;

    @Test
    public void writeSuccess() {
        // given
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
        final String PASSWORD1 = "testPassword1", PASSWORD2 = "testPassword2";
        final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD1);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.write(new ArticleWriteRequest(EMAIL, PASSWORD2, ARTICLE_TITLE, ARTICLE_CONTENT)));

        // then
    }

    @Test
    public void editTitleSuccess() {
        // given
        final String TITLE1 = "testTitle1", TITLE2 = "testTitle2";

        Article article = new Article(ARTICLE_ID, TITLE1, ARTICLE_CONTENT, USER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));

        // when
        articleService.edit(ARTICLE_ID, new ArticleEditRequest(EMAIL, PASSWORD, TITLE2, ARTICLE_CONTENT));

        // then
        assertThat(article.getId()).isEqualTo(ARTICLE_ID);
        assertThat(article.getTitle()).isEqualTo(TITLE2);
        assertThat(article.getContent()).isEqualTo(ARTICLE_CONTENT);
        assertThat(article.getUser()).isEqualTo(USER);
    }

    @Test
    public void editContentSuccess() {
        // given
        final String CONTENT1 = "testContent1", CONTENT2 = "testContent2";

        Article article = new Article(ARTICLE_ID, ARTICLE_TITLE, CONTENT1, USER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(article));

        // when
        articleService.edit(ARTICLE_ID, new ArticleEditRequest(EMAIL, PASSWORD, ARTICLE_TITLE, CONTENT2));

        // then
        assertThat(article.getId()).isEqualTo(ARTICLE_ID);
        assertThat(article.getTitle()).isEqualTo(ARTICLE_TITLE);
        assertThat(article.getContent()).isEqualTo(CONTENT2);
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
        final String PASSWORD1 = "testPassword1", PASSWORD2 = "testPassword2";
        final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD1);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.edit(ARTICLE_ID, new ArticleEditRequest(EMAIL, PASSWORD2, ARTICLE_TITLE, ARTICLE_CONTENT)));

        // then
    }

    @Test
    public void deleteSuccess() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(ARTICLE));
        when(commentRepository.deleteByArticle(ARTICLE)).thenReturn(List.of(COMMENT));

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
        final String PASSWORD1 = "testPassword1", PASSWORD2 = "testPassword2";
        final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD1);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> articleService.delete(ARTICLE_ID, new ArticleDeleteRequest(EMAIL, PASSWORD2)));

        // then
    }

}
