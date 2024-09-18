package com.devgeon.yourlog.controller;

import com.devgeon.yourlog.domain.dto.*;
import com.devgeon.yourlog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article/{article_id}/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> write(@PathVariable("article_id") Long articleId, @RequestBody @Validated CommentWriteRequest writeRequest) {

        CommentDto commentDto = commentService.write(articleId, writeRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
    }

    @PostMapping("/{comment_id}")
    public ResponseEntity<CommentDto> edit(@PathVariable("comment_id") Long commentId, @RequestBody @Validated CommentEditRequest editRequest) {

        CommentDto commentDto = commentService.edit(commentId, editRequest);

        return ResponseEntity.status(HttpStatus.OK).body(commentDto);
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<CommentDeleteResponse> delete(@PathVariable("comment_id") Long commentId, @RequestBody @Validated CommentDeleteRequest deleteRequest) {

        CommentDeleteResponse deleteResponse = commentService.delete(commentId, deleteRequest);

        return ResponseEntity.status(HttpStatus.OK).body(deleteResponse);
    }

}
