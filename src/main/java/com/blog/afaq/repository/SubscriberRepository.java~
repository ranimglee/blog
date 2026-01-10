package com.blog.afaq.repository;

import com.blog.afaq.model.Subscriber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriberRepository extends MongoRepository<Subscriber, String> {
    Subscriber save(Subscriber subscriber);

    Optional<Subscriber> findByEmail(String email);
    Optional<Subscriber> findByConfirmationToken(String token);

    void delete(Subscriber subscriber);

    boolean existsByEmail(String email);
}
