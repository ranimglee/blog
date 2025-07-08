package com.blog.afaq.service;

import com.blog.afaq.dto.request.CommentRequest;
import com.blog.afaq.dto.response.CommentResponse;
import com.blog.afaq.model.Comment;
import com.blog.afaq.model.User;
import com.blog.afaq.repository.CommentRepository;
import com.blog.afaq.repository.UserRepository;
import com.blog.afaq.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final HttpServletRequest request;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    public CommentResponse addComment(CommentRequest requestDto) {
        // 1. Extraire JWT depuis header
        String jwt = extractJwtFromRequest(request);
        if (jwt == null || !jwtTokenProvider.validateAccessToken(jwt)) {
            throw new RuntimeException("Invalid or missing JWT");
        }

        // 2. Extraire l'ID utilisateur
        String userId = jwtTokenProvider.extractUserIdFromAccessToken(jwt);

        // 3. Charger l'utilisateur depuis la base de données
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. Créer et sauvegarder le commentaire
        Comment comment = Comment.builder()
                .articleId(requestDto.getArticleId())
                .content(requestDto.getContent())
                .author(user.getFirstname()+" " +user.getLastname())
                .approved(false)
                .createdAt(new Date())
                .build();

        commentRepository.save(comment);

        return toDto(comment);
    }


    public List<CommentResponse> getApprovedCommentsForArticle(String articleId) {
        return commentRepository.findByArticleIdAndApprovedTrue(articleId)
                .stream().map(this::toDto).toList();
    }

    public List<CommentResponse> getPendingComments() {
        return commentRepository.findByApprovedFalse()
                .stream().map(this::toDto).toList();
    }

    public void approveComment(String id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setApproved(true);
        commentRepository.save(comment);
    }

    private CommentResponse toDto(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .author(comment.getAuthor())
                .approved(comment.isApproved())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public Map<String, Long> getCommentStats() {
        long total = commentRepository.count();
        long approved = commentRepository.countByApprovedTrue();
        long notApproved = total - approved;

        Map<String, Long> stats = new HashMap<>();
        stats.put("totalComments", total);
        stats.put("approvedComments", approved);
        stats.put("pendingComments", notApproved);

        return stats;
    }

}

