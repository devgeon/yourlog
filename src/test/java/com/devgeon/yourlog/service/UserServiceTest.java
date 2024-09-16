package com.devgeon.yourlog.service;

import com.devgeon.yourlog.domain.dto.UserDeleteRequest;
import com.devgeon.yourlog.domain.dto.UserDeleteResponse;
import com.devgeon.yourlog.domain.dto.UserDto;
import com.devgeon.yourlog.domain.dto.UserJoinRequest;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.exception.UserAuthenticationException;
import com.devgeon.yourlog.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    final String EMAIL = "test@test.com", USERNAME = "testUser", PASSWORD = "testPassword";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    public void joinSuccess() {
        // given
        when(userRepository.save(any())).thenReturn(new User(ID, EMAIL, USERNAME, PASSWORD));

        // when
        UserDto userDto = userService.join(new UserJoinRequest(EMAIL, USERNAME, PASSWORD));

        // then
        assertThat(userDto.getEmail()).isEqualTo(EMAIL);
        assertThat(userDto.getUsername()).isEqualTo(USERNAME);
    }

    @Test
    public void deleteSuccess() {
        // given
        User user = new User(ID, EMAIL, USERNAME, PASSWORD);

        when(userRepository.findByEmail(any())).thenReturn(List.of(user));

        // when
        UserDeleteResponse deleteResponse = userService.delete(new UserDeleteRequest(EMAIL, PASSWORD));

        // then
        verify(userRepository).deleteById(ID);

        assertThat(deleteResponse.getEmail()).isEqualTo(EMAIL);
        // TODO: Rewrite to compare with encrypted password
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
        final String PASSWORD1 = "testPassword1", PASSWORD2 = "testPassword2";
        final User USER = new User(ID, EMAIL, USERNAME, PASSWORD1);

        when(userRepository.findByEmail(EMAIL)).thenReturn(List.of(USER));

        // when
        assertThrows(UserAuthenticationException.class, () -> userService.delete(new UserDeleteRequest(EMAIL, PASSWORD2)));

        // then
    }

    @Test
    public void verifyPasswordEncrypted() {
        // given
        when(userRepository.save(any())).thenReturn(new User(ID, EMAIL, USERNAME, PASSWORD));

        // when
        userService.join(new UserJoinRequest(EMAIL, USERNAME, PASSWORD));

        // then
        verify(userRepository).save(userCaptor.capture());
        // TODO: Rewrite to compare with encrypted password
        assertThat(userCaptor.getValue().getPassword()).isNotEqualTo(PASSWORD);
    }

}