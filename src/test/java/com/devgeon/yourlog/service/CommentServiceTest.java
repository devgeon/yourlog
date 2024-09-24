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
class CommentServiceTest {

    final Long USER_ID = 1L, ARTICLE_ID = 1L, COMMENT_ID = 1L;
    final String EMAIL = "test@test.com", USERNAME = "testUsername", PASSWORD = "testPassword",
            ARTICLE_TITLE = "testArticleTitle", ARTICLE_CONTENT = "testArticleContent", COMMENT_CONTENT = "testCommentContent";
    final String OTHER_EMAIL = "other@test.com", WRONG_PASSWORD = "wrongPassword", NEW_COMMENT_CONTENT = "newCommentContent";

    final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD);
    final Article ARTICLE = new Article(ARTICLE_ID, ARTICLE_TITLE, ARTICLE_CONTENT, USER);
    final Comment COMMENT = new Comment(COMMENT_ID, COMMENT_CONTENT, ARTICLE, USER);

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private CommentService commentService;

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
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(ARTICLE));
        when(commentRepository.save(any())).thenReturn(COMMENT);

        // when
        CommentDto commentDto = commentService.write(ARTICLE_ID, new CommentWriteRequest(EMAIL, PASSWORD, COMMENT_CONTENT));

        // then
        assertThat(commentDto.getCommentId()).isNotNull();
        assertThat(commentDto.getEmail()).isEqualTo(EMAIL);
        assertThat(commentDto.getContent()).isEqualTo(COMMENT_CONTENT);
    }

    @Test
    public void writeFailByWrongEmail() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Collections.emptyList());

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.write(ARTICLE_ID, new CommentWriteRequest(EMAIL, PASSWORD, COMMENT_CONTENT)));

        // then
    }

    @Test
    public void writeFailByWrongPassword() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.write(ARTICLE_ID, new CommentWriteRequest(EMAIL, WRONG_PASSWORD, COMMENT_CONTENT)));

        // then
    }

    @Test
    public void editSuccess() {
        // given
        final User USER = getTestUser();

        Comment comment = new Comment(COMMENT_ID, COMMENT_CONTENT, ARTICLE, USER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        // when
        commentService.edit(COMMENT_ID, new CommentEditRequest(EMAIL, PASSWORD, NEW_COMMENT_CONTENT));

        // then
        assertThat(comment.getId()).isEqualTo(COMMENT_ID);
        assertThat(comment.getContent()).isEqualTo(NEW_COMMENT_CONTENT);
        assertThat(comment.getArticle()).isEqualTo(ARTICLE);
        assertThat(comment.getUser()).isEqualTo(USER);
    }

    @Test
    public void editFailByWrongEmail() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Collections.emptyList());

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.edit(COMMENT_ID, new CommentEditRequest(EMAIL, PASSWORD, COMMENT_CONTENT)));

        // then
    }

    @Test
    public void editFailByWrongPassword() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.edit(COMMENT_ID, new CommentEditRequest(EMAIL, WRONG_PASSWORD, COMMENT_CONTENT)));

        // then
    }

    @Test
    public void editFailByWrongAccount() {
        // given
        final User OTHER_USER = getOtherUser();

        when(userRepository.findByEmail(OTHER_EMAIL)).thenReturn(List.of(OTHER_USER));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(COMMENT));

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.edit(COMMENT_ID, new CommentEditRequest(OTHER_EMAIL, PASSWORD, COMMENT_CONTENT)));

        // then
    }

    @Test
    public void deleteSuccess() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(COMMENT));

        // when
        CommentDeleteResponse deleteResponse = commentService.delete(COMMENT_ID, new CommentDeleteRequest(EMAIL, PASSWORD));

        // then
        verify(commentRepository).deleteById(COMMENT_ID);

        assertThat(deleteResponse.getEmail()).isEqualTo(EMAIL);
        assertThat(deleteResponse.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    public void deleteFailByWrongEmail() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Collections.emptyList());

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.delete(COMMENT_ID, new CommentDeleteRequest(EMAIL, PASSWORD)));

        // then
    }

    @Test
    public void deleteFailByWrongPassword() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.delete(COMMENT_ID, new CommentDeleteRequest(EMAIL, WRONG_PASSWORD)));

        // then
    }

    @Test
    public void deleteFailByWrongAccount() {
        // given
        final User OTHER_USER = getOtherUser();

        when(userRepository.findByEmail(OTHER_EMAIL)).thenReturn(List.of(OTHER_USER));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(COMMENT));

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.delete(COMMENT_ID, new CommentDeleteRequest(OTHER_EMAIL, PASSWORD)));

        // then
    }

}
