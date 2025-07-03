package com.blog.afaq.controller;


import com.blog.afaq.dto.request.InitiativeRequest;
import com.blog.afaq.dto.response.InitiativeResponse;
import com.blog.afaq.service.InitiativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/initiatives")
@RequiredArgsConstructor
public class InitiativeController {

    private final InitiativeService initiativeService;

    @GetMapping("/get-all-initiatives")
    public ResponseEntity<List<InitiativeResponse>> getAll() {
        return ResponseEntity.ok(initiativeService.getAllInitiatives());
    }

    @GetMapping("/get-initiative-by/{id}")
    public ResponseEntity<InitiativeResponse> getById(@PathVariable String id) {
        return initiativeService.getInitiativeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create-new-initiative")
    public ResponseEntity<InitiativeResponse> create(@RequestBody InitiativeRequest request) {
        return ResponseEntity.ok(initiativeService.createInitiative(request));
    }

    @PutMapping("/update-initiative/{id}")
    public ResponseEntity<InitiativeResponse> update(@PathVariable String id, @RequestBody InitiativeRequest request) {
        return ResponseEntity.ok(initiativeService.updateInitiative(id, request));
    }

    @DeleteMapping("/delete-initiative/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        initiativeService.deleteInitiative(id);
        return ResponseEntity.noContent().build();
    }
}
