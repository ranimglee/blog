package com.blog.afaq.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@AllArgsConstructor
@Getter
@Setter
@Document(collection = "access_logs")
public class AccessLog {
    @Id
    private String id;

    private String userEmail;
    private Instant accessTime;


}
