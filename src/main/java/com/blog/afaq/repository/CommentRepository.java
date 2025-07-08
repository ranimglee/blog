package com.blog.afaq.repository;

import com.blog.afaq.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByArticleIdAndApprovedTrue(String articleId);
    List<Comment> findByApprovedFalse();

    long countByApprovedTrue();
}

