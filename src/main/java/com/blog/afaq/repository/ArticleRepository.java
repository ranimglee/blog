package com.blog.afaq.repository;

import com.blog.afaq.model.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ArticleRepository extends MongoRepository<Article, String> {

    @Query("{ $or: [ " +
            "{ 'title': { $regex: ?0, $options: 'i' } }, " +
            "{ 'content': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<Article> searchByTitleOrContent(String query);
}
