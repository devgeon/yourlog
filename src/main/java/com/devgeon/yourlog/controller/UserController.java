package com.devgeon.yourlog.controller;

import com.devgeon.yourlog.domain.dto.UserDeleteRequest;
import com.devgeon.yourlog.domain.dto.UserDeleteResponse;
import com.devgeon.yourlog.domain.dto.UserDto;
import com.devgeon.yourlog.domain.dto.UserJoinRequest;
import com.devgeon.yourlog.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> join(@RequestBody @Validated UserJoinRequest joinRequest) {

        UserDto userDto = userService.join(joinRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }

    @DeleteMapping
    public ResponseEntity<UserDeleteResponse> delete(@RequestBody @Validated UserDeleteRequest deleteRequest) {

        UserDeleteResponse deleteResponse = userService.delete(deleteRequest);

        return ResponseEntity.status(HttpStatus.OK).body(deleteResponse);
    }

}
