package com.devgeon.yourlog.service;

import com.devgeon.yourlog.domain.dto.UserDeleteRequest;
import com.devgeon.yourlog.domain.dto.UserDeleteResponse;
import com.devgeon.yourlog.domain.dto.UserDto;
import com.devgeon.yourlog.domain.dto.UserJoinRequest;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.exception.UserAuthenticationException;
import com.devgeon.yourlog.repository.ArticleRepository;
import com.devgeon.yourlog.repository.CommentRepository;
import com.devgeon.yourlog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    final Long ID = 1L;
    final String EMAIL = "test@test.com", USERNAME = "testUser", PASSWORD = "testPassword", WRONG_PASSWORD = "wrongPassword";

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    private User getTestUser() {
        return new User(ID, EMAIL, USERNAME, bCryptPasswordEncoder.encode(PASSWORD));
    }

    @Test
    public void joinSuccess() {
        // given
        final User USER = getTestUser();

        when(userRepository.save(any())).thenReturn(USER);

        // when
        UserDto userDto = userService.join(new UserJoinRequest(EMAIL, USERNAME, PASSWORD));

        // then
        assertThat(userDto.getEmail()).isEqualTo(EMAIL);
        assertThat(userDto.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    public void deleteSuccess() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(any())).thenReturn(List.of(USER));

        // when
        UserDeleteResponse deleteResponse = userService.delete(new UserDeleteRequest(EMAIL, PASSWORD));

        // then
        verify(commentRepository).deleteByUser(USER);
        verify(articleRepository).deleteByUser(USER);
        verify(userRepository).deleteById(ID);

        assertThat(deleteResponse.getEmail()).isEqualTo(EMAIL);
        assertThat(deleteResponse.getPassword()).isEqualTo(PASSWORD);
    }

    @Test
    public void deleteFailByWrongEmail() {
        // given
        when(userRepository.findByEmail(EMAIL)).thenReturn(Collections.emptyList());

        // when
        assertThrows(UserAuthenticationException.class, () -> userService.delete(new UserDeleteRequest(EMAIL, PASSWORD)));

        // then
    }

    @Test
    public void deleteFailByWrongPassword() {
        // given
        final User USER = getTestUser();

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> userService.delete(new UserDeleteRequest(EMAIL, WRONG_PASSWORD)));

        // then
    }

    @Test
    public void verifyPasswordEncrypted() {
        // given
        final User USER = getTestUser();

        when(userRepository.save(any())).thenReturn(USER);

        // when
        userService.join(new UserJoinRequest(EMAIL, USERNAME, PASSWORD));

        // then
        verify(userRepository).save(userCaptor.capture());
        String encodePassword = userCaptor.getValue().getPassword();
        assertThat(bCryptPasswordEncoder.matches(PASSWORD, encodePassword)).isTrue();
    }

}
