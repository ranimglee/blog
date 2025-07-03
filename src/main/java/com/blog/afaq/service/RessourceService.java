package com.blog.afaq.service;

import com.blog.afaq.dto.response.RessourceResponse;
import com.blog.afaq.model.FileType;
import com.blog.afaq.model.ResourceCategory;
import com.blog.afaq.model.Ressource;

import com.blog.afaq.repository.RessourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RessourceService {

    private final CloudinaryService cloudinaryService;
    private final RessourceRepository ressourceRepository;

    public RessourceResponse uploadRessource(MultipartFile file, String titre, String description,
                                             ResourceCategory category, FileType fileType) {
        String fileUrl = cloudinaryService.uploadFile(file);

        Ressource ressource = Ressource.builder()
                .titre(titre)
                .description(description)
                .category(category)
                .fileType(fileType)
                .size(file.getSize())
                .fileUrl(fileUrl)
                .createdAt(new Date())
                .build();

        ressourceRepository.save(ressource);

        return toDto(ressource);
    }
    public List<RessourceResponse> getAll() {
        return ressourceRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public RessourceResponse getById(String id) {
        Ressource ressource = ressourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ressource not found"));
        return toDto(ressource);
    }

    public void delete(String id) {
        if (!ressourceRepository.existsById(id)) {
            throw new RuntimeException("Ressource not found");
        }
        ressourceRepository.deleteById(id);
    }
    private RessourceResponse toDto(Ressource r) {
        return RessourceResponse.builder()
                .id(r.getId())
                .titre(r.getTitre())
                .description(r.getDescription())
                .category(r.getCategory())
                .fileType(r.getFileType())
                .size(r.getSize())
                .fileUrl(r.getFileUrl())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
