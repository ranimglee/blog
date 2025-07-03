package com.blog.afaq.repository;

import com.blog.afaq.model.Ressource;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RessourceRepository extends MongoRepository<Ressource, String> {
}
