package com.blog.afaq.dto.response;

import com.blog.afaq.model.FileType;
import com.blog.afaq.model.ResourceCategory;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class RessourceResponse {
    private String id;
    private String titre;
    private String description;
    private ResourceCategory category;
    private FileType fileType;
    private long size;
    private String fileUrl;
    private Date createdAt;
    private String originalFilename;
}
