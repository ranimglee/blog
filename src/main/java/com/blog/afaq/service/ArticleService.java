package com.blog.afaq.service;


import com.blog.afaq.dto.request.ArticleRequest;
import com.blog.afaq.dto.response.ArticleResponse;
import com.blog.afaq.model.Article;
import com.blog.afaq.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final NewsletterService newsletterService;

    public ArticleResponse createArticle(ArticleRequest request) {
        Article article = Article.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .auteur(request.getAuteur())
                .type(request.getType())
                .contenu(request.getContenu())
                .createdAt(new Date())
                .imageUrl(request.getImageUrl())
                .build();

        Article saved = articleRepository.save(article);
        newsletterService.notifySubscribersAboutNewArticle(
                saved.getTitle(),
                saved.getDescription(),
                "http://localhost:3000/public/get-article-by/" + saved.getId() // adjust to your frontend
        );

        return mapToResponse(saved);
    }

    public List<ArticleResponse> getAllArticles() {
        return articleRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Optional<ArticleResponse> getArticleById(String id) {
        return articleRepository.findById(id).map(this::mapToResponse);
    }

    public ArticleResponse updateArticle(String id, ArticleRequest request) {
        return articleRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(request.getTitle());
                    existing.setDescription(request.getDescription());
                    existing.setAuteur(request.getAuteur());
                    existing.setType(request.getType());
                    existing.setContenu(request.getContenu());
                    return mapToResponse(articleRepository.save(existing));
                })
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    public void deleteArticle(String id) {
        articleRepository.deleteById(id);
    }

    private ArticleResponse mapToResponse(Article article) {
        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .description(article.getDescription())
                .auteur(article.getAuteur())
                .type(article.getType())
                .contenu(article.getContenu())
                .createdAt(article.getCreatedAt())
                .imageUrl(article.getImageUrl())
                .build();
    }
}
