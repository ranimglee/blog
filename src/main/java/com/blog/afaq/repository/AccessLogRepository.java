package com.blog.afaq.repository;

import com.blog.afaq.model.AccessLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;

public interface AccessLogRepository extends MongoRepository<AccessLog, String>, CustomAccessLogRepository {
    long countByAccessTimeBetween(Instant start, Instant end);

}
