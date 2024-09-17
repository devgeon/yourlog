package com.devgeon.yourlog.controller;

import com.devgeon.yourlog.domain.dto.UserDeleteRequest;
import com.devgeon.yourlog.domain.dto.UserJoinRequest;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class UserControllerTest {

    private final String BASE_URI = "/api/v1/user";
    private final String EMAIL = "test@test.com", USERNAME = "testUsername", PASSWORD = "testPassword";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void joinSuccess() throws Exception {
        // given
        final UserJoinRequest joinRequest = new UserJoinRequest(EMAIL, USERNAME, PASSWORD);
        final String requestBody = objectMapper.writeValueAsString(joinRequest);

        // when
        ResultActions resultActions = mockMvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isCreated());

        List<User> users = userRepository.findByEmail(EMAIL);

        assertThat(users.size()).isEqualTo(1);
        assertThat(users.getFirst().getEmail()).isEqualTo(EMAIL);
        assertThat(users.getFirst().getUsername()).isEqualTo(USERNAME);
        // TODO: Rewrite to compare with encrypted password
        assertThat(users.getFirst().getPassword()).isNotEqualTo(PASSWORD);
    }

    @Test
    public void deleteSuccess() throws Exception {
        // given
        final UserDeleteRequest deleteRequest = new UserDeleteRequest(EMAIL, PASSWORD);
        final String requestBody = objectMapper.writeValueAsString(deleteRequest);

        userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(PASSWORD).build());

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        // then
        resultActions.andExpect(status().isOk());

        List<User> users = userRepository.findByEmail(EMAIL);

        assertThat(users).isEmpty();
    }

}