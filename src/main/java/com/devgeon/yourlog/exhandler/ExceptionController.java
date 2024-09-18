package com.devgeon.yourlog.exhandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult validationExHandle(DataIntegrityViolationException e, HttpServletRequest request) {
        log.error("validation exception: ", e);
        return new ErrorResult(LocalDateTime.now(), "BAD_REQUEST", e.getMessage(), request.getRequestURI());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult validationExHandle(Exception e, HttpServletRequest request) {
        log.error("exception: ", e);
        return new ErrorResult(LocalDateTime.now(), "INTERNAL_SERVER_ERROR", e.getMessage(), request.getRequestURI());
    }
}
