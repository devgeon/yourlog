package com.devgeon.yourlog.controller;

import com.devgeon.yourlog.domain.dto.ArticleDto;
import com.devgeon.yourlog.domain.dto.ArticleEditRequest;
import com.devgeon.yourlog.domain.dto.ArticleWriteRequest;
import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.exception.ArticleNotFoundException;
import com.devgeon.yourlog.repository.ArticleRepository;
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
class ArticleControllerTest {

    private final String BASE_URI = "/api/v1/article";
    private final String EMAIL = "test@test.com", USERNAME = "testUsername", PASSWORD = "testPassword", TITLE = "testTitle", CONTENT = "testContent";

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
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void writeSuccess() throws Exception {
        // given
        final ArticleWriteRequest writeRequest = new ArticleWriteRequest(EMAIL, PASSWORD, TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(writeRequest);

        userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isCreated());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readValue(response, ArticleDto.class).getArticleId();
        Article article = articleRepository.findById(id).orElseThrow(ArticleNotFoundException::new);

        assertThat(article.getUser().getEmail()).isEqualTo(EMAIL);
        assertThat(article.getTitle()).isEqualTo(TITLE);
        assertThat(article.getContent()).isEqualTo(CONTENT);
    }

    @Test
    public void writeFailByNullTitle() throws Exception {
        // given
        final String NULL_TITLE = null;

        final ArticleWriteRequest writeRequest = new ArticleWriteRequest(EMAIL, PASSWORD, NULL_TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(writeRequest);

        userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void writeFailByEmptyTitle() throws Exception {
        // given
        final String EMPTY_TITLE = "";

        final ArticleWriteRequest writeRequest = new ArticleWriteRequest(EMAIL, PASSWORD, EMPTY_TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(writeRequest);

        userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void writeFailByBlankTitle() throws Exception {
        // given
        final String BLANK_TITLE = " ";

        final ArticleWriteRequest writeRequest = new ArticleWriteRequest(EMAIL, PASSWORD, BLANK_TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(writeRequest);

        userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void editTitleSuccess() throws Exception {
        // given;
        final String OLD_TITLE = "testOldTitle", NEW_TITLE = "testNewTitle";

        final ArticleEditRequest editRequest = new ArticleEditRequest(EMAIL, PASSWORD, NEW_TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article oldArticle = articleRepository.save(Article.builder().title(OLD_TITLE).content(CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI + "/" + oldArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readValue(response, ArticleDto.class).getArticleId();
        Article newArticle = articleRepository.findById(id).orElseThrow(ArticleNotFoundException::new);

        assertThat(newArticle.getId()).isEqualTo(oldArticle.getId());
        assertThat(newArticle.getUser().getEmail()).isEqualTo(EMAIL);
        assertThat(newArticle.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(newArticle.getContent()).isEqualTo(CONTENT);
    }

    @Test
    public void editContentSuccess() throws Exception {
        // given;
        final String OLD_CONTENT = "testOldContent", NEW_CONTENT = "testNewContent";

        final ArticleEditRequest editRequest = new ArticleEditRequest(EMAIL, PASSWORD, TITLE, NEW_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article oldArticle = articleRepository.save(Article.builder().title(TITLE).content(OLD_CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI + "/" + oldArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isOk());

        String response = resultActions.andReturn().getResponse().getContentAsString();
        Long id = objectMapper.readValue(response, ArticleDto.class).getArticleId();
        Article newArticle = articleRepository.findById(id).orElseThrow(ArticleNotFoundException::new);

        assertThat(newArticle.getId()).isEqualTo(oldArticle.getId());
        assertThat(newArticle.getUser().getEmail()).isEqualTo(EMAIL);
        assertThat(newArticle.getTitle()).isEqualTo(TITLE);
        assertThat(newArticle.getContent()).isEqualTo(NEW_CONTENT);
    }

    @Test
    public void editFailByNullTitle() throws Exception {
        // given
        final String NULL_TITLE = null;

        final ArticleEditRequest editRequest = new ArticleEditRequest(EMAIL, PASSWORD, NULL_TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article oldArticle = articleRepository.save(Article.builder().title(TITLE).content(CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI + "/" + oldArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void editFailByEmptyTitle() throws Exception {
        // given
        final String EMPTY_TITLE = "";

        final ArticleEditRequest editRequest = new ArticleEditRequest(EMAIL, PASSWORD, EMPTY_TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article oldArticle = articleRepository.save(Article.builder().title(TITLE).content(CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI + "/" + oldArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void editFailByBlankTitle() throws Exception {
        // given
        final String BLANK_TITLE = " ";

        final ArticleEditRequest editRequest = new ArticleEditRequest(EMAIL, PASSWORD, BLANK_TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article oldArticle = articleRepository.save(Article.builder().title(TITLE).content(CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI + "/" + oldArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void editFailByNullContent() throws Exception {
        // given
        final String NULL_CONTENT = null;

        final ArticleEditRequest editRequest = new ArticleEditRequest(EMAIL, PASSWORD, TITLE, NULL_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article oldArticle = articleRepository.save(Article.builder().title(TITLE).content(CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI + "/" + oldArticle.getId())
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

        final ArticleEditRequest editRequest = new ArticleEditRequest(EMAIL, PASSWORD, TITLE, EMPTY_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article oldArticle = articleRepository.save(Article.builder().title(TITLE).content(CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI + "/" + oldArticle.getId())
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

        final ArticleEditRequest editRequest = new ArticleEditRequest(EMAIL, PASSWORD, TITLE, BLANK_CONTENT);
        final String requestBody = objectMapper.writeValueAsString(editRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article oldArticle = articleRepository.save(Article.builder().title(TITLE).content(CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI + "/" + oldArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isBadRequest());

        Exception resolvedException = resultActions.andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(MethodArgumentNotValidException.class);
    }

    @Test
    public void deleteSuccess() throws Exception {
        // given;
        final ArticleEditRequest deleteRequest = new ArticleEditRequest(EMAIL, PASSWORD, TITLE, CONTENT);
        final String requestBody = objectMapper.writeValueAsString(deleteRequest);

        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());
        Article article = articleRepository.save(Article.builder().title(TITLE).content(CONTENT).user(user).build());

        // when
        ResultActions resultActions = mockMvc.perform(delete(BASE_URI + "/" + article.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isOk());

        assertThat(articleRepository.findById(article.getId())).isEmpty();
    }

}
