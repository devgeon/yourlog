package com.devgeon.yourlog.repository;

import com.devgeon.yourlog.domain.entity.Article;
import com.devgeon.yourlog.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ArticleRepository extends JpaRepository<Article, Long> {

    void deleteByUser(User user);
}
