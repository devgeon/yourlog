package com.devgeon.yourlog.service;

import com.devgeon.yourlog.domain.dto.UserDeleteRequest;
import com.devgeon.yourlog.domain.dto.UserDeleteResponse;
import com.devgeon.yourlog.domain.dto.UserDto;
import com.devgeon.yourlog.domain.dto.UserJoinRequest;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.exception.UserAuthenticationException;
import com.devgeon.yourlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto join(UserJoinRequest joinRequest) {

        User user = userRepository.save(User.builder()
                .email(joinRequest.getEmail())
                .username(joinRequest.getUsername())
                // TODO: encrypt password
                .password(joinRequest.getPassword())
                .build());

        return new UserDto(user.getEmail(), user.getUsername());
    }

    public UserDeleteResponse delete(UserDeleteRequest deleteRequest) {

        User user = getUserOrThrow(deleteRequest.getEmail(), deleteRequest.getPassword());

        userRepository.deleteById(user.getId());

        return new UserDeleteResponse(deleteRequest.getEmail(), deleteRequest.getPassword());
    }

    private User getUserOrThrow(String email, String password) {

        List<User> userList = userRepository.findByEmail(email);
        // TODO: encrypt password
        if (userList.isEmpty() || !userList.getFirst().getPassword().equals(password)) {
            throw new UserAuthenticationException();
        }

        return userList.getFirst();
    }

}
