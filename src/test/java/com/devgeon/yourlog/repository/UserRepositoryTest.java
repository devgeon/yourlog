package com.devgeon.yourlog.repository;

import com.devgeon.yourlog.domain.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Transactional
@SpringBootTest
class UserRepositoryTest {

    final String USERNAME = "testUser", EMAIL = "test@test.com", PASSWORD = "testPassword";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    void userSaveSuccess() {
        // given

        // when
        User user = userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());

        // then
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo(EMAIL);
        assertThat(user.getUsername()).isEqualTo(USERNAME);
        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
        assertThat(bCryptPasswordEncoder.matches(PASSWORD, user.getPassword())).isTrue();
    }

    @Test
    void userSaveFailByDuplicatedEmail() {
        // given

        // when
        userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build());

        // then
        assertThrows(DataIntegrityViolationException.class, () ->
                userRepository.save(User.builder().email(EMAIL).username(USERNAME).password(bCryptPasswordEncoder.encode(PASSWORD)).build()));
    }

}
