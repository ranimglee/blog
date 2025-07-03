package com.blog.afaq.repository;

import com.blog.afaq.model.Initiative;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InitiativeRepository extends MongoRepository<Initiative, String> {
}
