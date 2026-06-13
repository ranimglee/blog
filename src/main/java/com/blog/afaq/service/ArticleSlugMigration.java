package com.blog.afaq.service;

import com.blog.afaq.model.Article;
import com.blog.afaq.repository.ArticleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class ArticleSlugMigration {

    private final ArticleRepository articleRepository;

    // ✅ Guard to ensure migration runs only once per app startup
    private static final AtomicBoolean MIGRATED = new AtomicBoolean(false);

    public ArticleSlugMigration(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @PostConstruct
    public void migrateSlugs() {

        // ✅ safety guard
        if (!MIGRATED.compareAndSet(false, true)) {
            return;
        }

        List<Article> articles = articleRepository.findAll();
        List<Article> toUpdate = new ArrayList<>();

        for (Article article : articles) {
            String slug = article.getSlug();

            if (slug == null
                    || slug.isBlank()
                    || slug.equals("-")
                    || slug.trim().length() < 3
                    || slug.equalsIgnoreCase("null")) {

                article.setSlug(generateUniqueSlug(article.getTitle()));
                toUpdate.add(article);
            }
        }

        if (!toUpdate.isEmpty()) {
            articleRepository.saveAll(toUpdate);
        }

        System.out.println("✅ Slug migration completed successfully");
    }

    String generateUniqueSlug(String title) {
        String baseSlug = title.toLowerCase()
                .trim()
                .replaceAll("[^\\p{L}0-9\\s]", "")   // supports Arabic, French, etc.
                .replaceAll("\\s+", "-");

        String slug = baseSlug;
        int counter = 1;

        while (articleRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}