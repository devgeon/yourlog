package com.devgeon.yourlog.controller;

import com.devgeon.yourlog.domain.dto.*;
import com.devgeon.yourlog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    public ResponseEntity<ArticleDto> write(@RequestBody @Validated ArticleWriteRequest writeRequest) {

        ArticleDto articleDto = articleService.write(writeRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(articleDto);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ArticleDto> edit(@PathVariable("id") Long id, @RequestBody @Validated ArticleEditRequest editRequest) {

        ArticleDto articleDto = articleService.edit(id, editRequest);

        return ResponseEntity.status(HttpStatus.OK).body(articleDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ArticleDeleteResponse> delete(@PathVariable("id") Long id, @RequestBody @Validated ArticleDeleteRequest deleteRequest) {

        ArticleDeleteResponse deleteResponse = articleService.delete(id, deleteRequest);

        return ResponseEntity.status(HttpStatus.OK).body(deleteResponse);
    }

}
