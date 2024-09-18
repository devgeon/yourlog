package com.devgeon.yourlog.service;

import com.devgeon.yourlog.domain.dto.*;
import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.Comment;
import com.devgeon.yourlog.domain.entity.User;
import com.devgeon.yourlog.exception.ArticleNotFoundException;
import com.devgeon.yourlog.exception.CommentNotFoundException;
import com.devgeon.yourlog.exception.UserAuthenticationException;
import com.devgeon.yourlog.repository.ArticleRepository;
import com.devgeon.yourlog.repository.CommentRepository;
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
public class CommentService {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public CommentDto write(Long articleId, CommentWriteRequest writeRequest) {

        User user = getUserOrThrow(writeRequest.getEmail(), writeRequest.getPassword());
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);

        Comment comment = commentRepository.save(Comment.builder()
                .content(writeRequest.getContent())
                .article(article)
                .user(user)
                .build());

        return new CommentDto(comment.getId(), comment.getUser().getEmail(), comment.getContent());
    }

    public CommentDto edit(Long commentId, CommentEditRequest editRequest) {

        getUserOrThrow(editRequest.getEmail(), editRequest.getPassword());
        Comment comment = commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);

        comment.update(editRequest.getContent());

        return new CommentDto(comment.getId(), comment.getUser().getEmail(), comment.getContent());
    }

    public CommentDeleteResponse delete(Long commentId, CommentDeleteRequest deleteRequest) {

        getUserOrThrow(deleteRequest.getEmail(), deleteRequest.getPassword());

        commentRepository.deleteById(commentId);

        return new CommentDeleteResponse(deleteRequest.getEmail(), deleteRequest.getPassword());
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
