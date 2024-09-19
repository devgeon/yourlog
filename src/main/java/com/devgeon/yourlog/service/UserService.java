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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDto join(UserJoinRequest joinRequest) {

        User user = userRepository.save(User.builder()
                .email(joinRequest.getEmail())
                .username(joinRequest.getUsername())
                .password(bCryptPasswordEncoder.encode(joinRequest.getPassword()))
                .build());

        return new UserDto(user.getEmail(), user.getUsername());
    }

    public UserDeleteResponse delete(UserDeleteRequest deleteRequest) {

        User user = getUserOrThrow(deleteRequest.getEmail(), deleteRequest.getPassword());

        commentRepository.deleteByUser(user);
        articleRepository.deleteByUser(user);
        userRepository.deleteById(user.getId());

        return new UserDeleteResponse(deleteRequest.getEmail(), deleteRequest.getPassword());
    }

    private User getUserOrThrow(String email, String password) {

        List<User> userList = userRepository.findByEmail(email);
        if (userList.isEmpty() || !bCryptPasswordEncoder.matches(password, userList.getFirst().getPassword())) {
            throw new UserAuthenticationException();
        }

        return userList.getFirst();
    }

}
