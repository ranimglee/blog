package com.blog.afaq.model;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("ressources")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ressource {

    @Id
    private String id;

    private String titre;
    private String description;
    private ResourceCategory category;
    private FileType fileType;
    private long size; // en octets
    private String fileUrl;
    private Date createdAt;
}
