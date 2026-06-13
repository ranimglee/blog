package com.blog.afaq.service;

import com.blog.afaq.model.Initiative;
import com.blog.afaq.repository.InitiativeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
@Component
public class InitiativeSlugMigration {

    private final InitiativeRepository initiativeRepository;

    // ✅ Guard to ensure migration runs only once per app startup
    private static final AtomicBoolean MIGRATED = new AtomicBoolean(false);

    public InitiativeSlugMigration(InitiativeRepository initiativeRepository) {
        this.initiativeRepository = initiativeRepository;
    }

    @PostConstruct
    public void migrateSlugs() {

        // ✅ safety guard
        if (!MIGRATED.compareAndSet(false, true)) {
            return;
        }

        List<Initiative> initiatives = initiativeRepository.findAll();
        List<Initiative> toUpdate = new ArrayList<>();

        for (Initiative initiative : initiatives) {
            String slug = initiative.getSlug();

            if (slug == null
                    || slug.isBlank()
                    || slug.equalsIgnoreCase("null")) {

                initiative.setSlug(generateUniqueSlug(initiative.getTitle()));
                toUpdate.add(initiative);
            }
        }

        if (!toUpdate.isEmpty()) {
            initiativeRepository.saveAll(toUpdate);
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

        while (initiativeRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }
}
