package com.blog.afaq.controller;

import com.blog.afaq.dto.response.RessourceResponse;

import com.blog.afaq.model.FileType;
import com.blog.afaq.model.ResourceCategory;
import com.blog.afaq.service.RessourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/ressources")
@RequiredArgsConstructor
public class RessourceController {

    private final RessourceService ressourceService;

    @PostMapping("/upload")
    public ResponseEntity<RessourceResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("category") ResourceCategory category,
            @RequestParam("fileType") FileType fileType
    ) {
        RessourceResponse response = ressourceService.uploadRessource(file, titre, description, category, fileType);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<RessourceResponse>> getAll() {
        return ResponseEntity.ok(ressourceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RessourceResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(ressourceService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ressourceService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
