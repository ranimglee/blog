package com.blog.afaq.controller;

import com.blog.afaq.dto.request.CommentRequest;
import com.blog.afaq.dto.response.CommentResponse;
import com.blog.afaq.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> addComment(@RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.addComment(request));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentResponse>> getApprovedComments(@PathVariable String articleId) {
        return ResponseEntity.ok(commentService.getApprovedCommentsForArticle(articleId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<CommentResponse>> getPendingComments() {
        return ResponseEntity.ok(commentService.getPendingComments());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/approve/{id}")
    public ResponseEntity<Void> approveComment(@PathVariable String id) {
        commentService.approveComment(id);
        return ResponseEntity.ok().build();
    }
}
