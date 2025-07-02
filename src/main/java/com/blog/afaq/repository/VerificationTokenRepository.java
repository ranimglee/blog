package com.blog.afaq.repository;

import com.blog.afaq.model.User;
import com.blog.afaq.model.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);

    VerificationToken save(VerificationToken token);

    void deleteById(String id);

    void delete(VerificationToken token);
}
