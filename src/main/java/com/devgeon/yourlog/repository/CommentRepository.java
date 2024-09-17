package com.devgeon.yourlog.repository;

import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> deleteByArticle(Article article);
}
