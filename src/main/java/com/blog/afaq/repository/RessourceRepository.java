package com.blog.afaq.repository;

import com.blog.afaq.model.Ressource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RessourceRepository extends MongoRepository<Ressource, String> {
    @Query("{ $or: [ " +
            "{ 'titre': { $regex: ?0, $options: 'i' } }, " +
            "{ 'description': { $regex: ?0, $options: 'i' } } " +
            "] }")
    List<Ressource> searchByTitreOrDescription(String query);
}
