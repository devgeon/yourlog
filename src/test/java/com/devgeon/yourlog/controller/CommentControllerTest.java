package com.devgeon.yourlog.controller;

import com.devgeon.yourlog.domain.dto.CommentDeleteRequest;
import com.devgeon.yourlog.domain.dto.CommentDto;
import com.devgeon.yourlog.domain.dto.CommentEditRequest;
import com.devgeon.yourlog.domain.dto.CommentWriteRequest;
import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.Comment;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.exception.CommentNotFoundException;
import com.devgeon.yourlog.repository.ArticleRepository;
import com.devgeon.yourlog.repository.CommentRepository;
import com.devgeon.yourlog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class CommentControllerTest {

    private final String BASE_URI = "/api/v1/article/%d/comment";
    private final String EMAIL = "test@test.com", USERNAME = "testUsername", PASSWORD = "testPassword";
    private final String TITLE = "testTitle", ARTICLE_CONTENT = "testArticleContent", COMMENT_CONTENT = "testCommentContent";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void writeSuccess() throws Exception {
        // given
        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());

        final CommentWriteRequest writeRequest = new CommentWriteRequest(EMAIL, PASSWORD, COMMENT_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(writeRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(String.format(BASE_URI, article.getId()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isCreated());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readValue(response, CommentDto.class).getCommentId();
        Comment comment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        assertThat(comment.getContent()).isEqualTo(COMMENT_CONTENT);
        assertThat(comment.getUser().getEmail()).isEqualTo(EMAIL);
        assertThat(comment.getArticle().getId()).isEqualTo(article.getId());
    }

    @Test
    public void writeFailByNullContent() throws Exception {
        // given
        final String NULL_CONTENT = null;

        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());

        final CommentWriteRequest writeRequest = new CommentWriteRequest(EMAIL, PASSWORD, NULL_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(writeRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(String.format(BASE_URI, article.getId()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void writeFailByEmptyContent() throws Exception {
        // given
        final String EMPTY_CONTENT = "";

        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());

        final CommentWriteRequest writeRequest = new CommentWriteRequest(EMAIL, PASSWORD, EMPTY_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(writeRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(String.format(BASE_URI, article.getId()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void writeFailByBlankContent() throws Exception {
        // given
        final String EMPTY_CONTENT = " ";

        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());

        final CommentWriteRequest writeRequest = new CommentWriteRequest(EMAIL, PASSWORD, EMPTY_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(writeRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(String.format(BASE_URI, article.getId()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void editSuccess() throws Exception {
        // given
        final String OLD_COMMENT_CONTENT = "testOldCommentContent", NEW_COMMENT_CONTENT = "testNewCommentContent";

        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());
        final Comment oldComment = commentRepository.save(Comment.builder().content(OLD_COMMENT_CONTENT).article(article).user(user).build());

        final CommentEditRequest editRequest = new CommentEditRequest(EMAIL, PASSWORD, NEW_COMMENT_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(String.format(BASE_URI, article.getId()) + "/" + oldComment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readValue(response, CommentDto.class).getCommentId();
        Comment newComment = commentRepository.findById(id).orElseThrow(CommentNotFoundException::new);

        assertThat(newComment.getId()).isEqualTo(oldComment.getId());
        assertThat(newComment.getContent()).isEqualTo(NEW_COMMENT_CONTENT);
        assertThat(newComment.getUser().getEmail()).isEqualTo(EMAIL);
        assertThat(newComment.getArticle().getId()).isEqualTo(article.getId());
    }

    @Test
    public void editFailByNullContent() throws Exception {
        // given
        final String NULL_CONTENT = null;

        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());
        final Comment oldComment = commentRepository.save(Comment.builder().content(COMMENT_CONTENT).article(article).user(user).build());

        final CommentEditRequest editRequest = new CommentEditRequest(EMAIL, PASSWORD, NULL_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(String.format(BASE_URI, article.getId()) + "/" + oldComment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void editFailByEmptyContent() throws Exception {
        // given
        final String EMPTY_CONTENT = "";

        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());
        final Comment oldComment = commentRepository.save(Comment.builder().content(COMMENT_CONTENT).article(article).user(user).build());

        final CommentEditRequest editRequest = new CommentEditRequest(EMAIL, PASSWORD, EMPTY_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(String.format(BASE_URI, article.getId()) + "/" + oldComment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void editFailByBlankContent() throws Exception {
        // given
        final String BLANK_CONTENT = " ";

        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());
        final Comment oldComment = commentRepository.save(Comment.builder().content(COMMENT_CONTENT).article(article).user(user).build());

        final CommentEditRequest editRequest = new CommentEditRequest(EMAIL, PASSWORD, BLANK_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(String.format(BASE_URI, article.getId()) + "/" + oldComment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void deleteSuccess() throws Exception {
        // given
        final User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        final Article article = articleRepository.save(Article.builder().title(TITLE).content(ARTICLE_CONTENT).user(user).build());
        final Comment comment = commentRepository.save(Comment.builder().content(COMMENT_CONTENT).article(article).user(user).build());

        final CommentDeleteRequest deleteRequest = new CommentDeleteRequest(EMAIL, PASSWORD);
        final String requestBody = objectMapper.writeValueAsString(deleteRequest);

        // when
        ResultActions resultActions = mockMvc.perform(delete(String.format(BASE_URI, article.getId()) + "/" + comment.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isOk());

        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }


}