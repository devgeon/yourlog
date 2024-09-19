package com.devgeon.yourlog.exhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResult {

    private LocalDateTime time;
    private String status;
    private String message;
    private String requestURI;
}
