package com.blog.afaq.controller;


import com.blog.afaq.dto.request.ArticleRequest;
import com.blog.afaq.dto.response.ArticleResponse;
import com.blog.afaq.dto.response.ImageUploadResponse;
import com.blog.afaq.service.ArticleService;
import com.blog.afaq.service.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final CloudinaryService cloudinaryService;


    @PostMapping
    public ResponseEntity<ArticleResponse> create( @RequestBody ArticleRequest request) {
        return ResponseEntity.ok(articleService.createArticle(request));
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> getAll() {
        return ResponseEntity.ok(articleService.getAllArticles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> getById(@PathVariable String id) {
        return articleService.getArticleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> update(@PathVariable String id, @Valid @RequestBody ArticleRequest request) {
        return ResponseEntity.ok(articleService.updateArticle(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        articleService.deleteArticle(id);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/upload-image")
    public ResponseEntity<ImageUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(new ImageUploadResponse(imageUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping(params = "lang")
    public List<ArticleResponse> getArticlesByLanguage(@RequestParam String lang) {
        return articleService.getArticlesByLanguage(lang);
    }
}
