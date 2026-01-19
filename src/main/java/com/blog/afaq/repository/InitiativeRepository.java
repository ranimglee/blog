package com.blog.afaq.repository;

import com.blog.afaq.model.Initiative;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface InitiativeRepository extends MongoRepository<Initiative, String> {
    @Query("{ $or: [ { 'title': { $regex: ?0, $options: 'i' } }, { 'description': { $regex: ?0, $options: 'i' } } ] }")
    List<Initiative> searchByTitleOrDescription(String query);
}
