package com.blog.afaq.dto.response;

import com.blog.afaq.model.Article;
import com.blog.afaq.model.Initiative;
import com.blog.afaq.model.Ressource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SiteSearchResponse {
    private List<Article> articles;
    private List<Ressource> resources;
    private List<Initiative> projects;

}
