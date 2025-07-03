package com.blog.afaq.service;


import com.blog.afaq.dto.request.InitiativeRequest;
import com.blog.afaq.dto.response.InitiativeResponse;
import com.blog.afaq.model.Initiative;
import com.blog.afaq.repository.InitiativeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InitiativeService {

    private final InitiativeRepository initiativeRepository;

    public List<InitiativeResponse> getAllInitiatives() {
        return initiativeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public Optional<InitiativeResponse> getInitiativeById(String id) {
        return initiativeRepository.findById(id)
                .map(this::mapToResponse);
    }

    public InitiativeResponse createInitiative(InitiativeRequest request) {
        Initiative initiative = new Initiative();
        initiative.setTitle(request.getTitle());
        initiative.setSubTitle(request.getSubTitle());
        initiative.setContent(request.getContent());
        initiative.setImageUrl(request.getImageUrl());
        initiative.setCountry(request.getCountry());
        initiative.setCreatedAt(Instant.now());

        Initiative saved = initiativeRepository.save(initiative);
        return mapToResponse(saved);
    }

    public InitiativeResponse updateInitiative(String id, InitiativeRequest request) {
        Initiative initiative = initiativeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Initiative not found"));

        initiative.setTitle(request.getTitle());
        initiative.setSubTitle(request.getSubTitle());
        initiative.setContent(request.getContent());
        initiative.setImageUrl(request.getImageUrl());
        initiative.setCountry(request.getCountry());

        Initiative updated = initiativeRepository.save(initiative);
        return mapToResponse(updated);
    }

    public void deleteInitiative(String id) {
        initiativeRepository.deleteById(id);
    }

    private InitiativeResponse mapToResponse(Initiative initiative) {
        InitiativeResponse response = new InitiativeResponse();
        response.setId(initiative.getId());
        response.setTitle(initiative.getTitle());
        response.setSubTitle(initiative.getSubTitle());
        response.setContent(initiative.getContent());
        response.setImageUrl(initiative.getImageUrl());
        response.setCountry(initiative.getCountry());
        response.setCreatedAt(initiative.getCreatedAt());
        return response;
    }
}
