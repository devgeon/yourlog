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
class CommentServiceTest {

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
    private CommentService commentService;

    @Test
    public void writeSuccess() {
        // given
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
        final String PASSWORD1 = "testPassword1", PASSWORD2 = "testPassword2";
        final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD1);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.write(ARTICLE_ID, new CommentWriteRequest(EMAIL, PASSWORD2, COMMENT_CONTENT)));

        // then
    }

    @Test
    public void editSuccess() {
        // given
        final String COMMENT1_CONTENT = "testComment1Content", COMMENT2_CONTENT = "testComment2Content";

        Comment comment = new Comment(COMMENT_ID, COMMENT1_CONTENT, ARTICLE, USER);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        // when
        commentService.edit(COMMENT_ID, new CommentEditRequest(EMAIL, PASSWORD, COMMENT2_CONTENT));

        // then
        assertThat(comment.getId()).isEqualTo(COMMENT_ID);
        assertThat(comment.getContent()).isEqualTo(COMMENT2_CONTENT);
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
        final String PASSWORD1 = "testPassword1", PASSWORD2 = "testPassword2";
        final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD1);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.edit(COMMENT_ID, new CommentEditRequest(EMAIL, PASSWORD2, COMMENT_CONTENT)));

        // then
    }

    @Test
    public void deleteSuccess() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

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
        final String PASSWORD1 = "testPassword1", PASSWORD2 = "testPassword2";
        final User USER = new User(USER_ID, EMAIL, USERNAME, PASSWORD1);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> commentService.delete(COMMENT_ID, new CommentDeleteRequest(EMAIL, PASSWORD2)));

        // then
    }

}