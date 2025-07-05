package com.blog.afaq.service;

import com.blog.afaq.dto.response.RessourceResponse;
import com.blog.afaq.exception.ResourceNotFoundException;
import com.blog.afaq.model.FileType;
import com.blog.afaq.model.ResourceCategory;
import com.blog.afaq.model.Ressource;

import com.blog.afaq.model.Subscriber;
import com.blog.afaq.repository.RessourceRepository;
import com.blog.afaq.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RessourceService {

    private final CloudinaryService cloudinaryService;
    private final RessourceRepository ressourceRepository;
    private final NewsletterService newsletterService;


    public RessourceResponse uploadRessource(MultipartFile file, String titre, String description,
                                             ResourceCategory category, FileType fileType) {
        String fileUrl = cloudinaryService.uploadFile(file, fileType.name());  // upload sur Cloudinary

        Ressource ressource = Ressource.builder()
                .titre(titre)
                .description(description)
                .category(category)
                .fileType(fileType)
                .size(file.getSize())
                .fileUrl(fileUrl)
                .originalFilename(file.getOriginalFilename()) // <--- stocker le nom original ici
                .createdAt(new Date())
                .build();

        ressourceRepository.save(ressource);
        newsletterService.notifySubscribersAboutNewArticle(
                ressource.getTitre(),
                ressource.getDescription(),
                "http://localhost:3000/article/" + ressource.getId() // adjust to your frontend
        );

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
                .originalFilename(r.getOriginalFilename())
                .build();
    }


    public Ressource getRessourceById(String id) {
        return ressourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ressource non trouvée avec l'ID : " + id));
    }


}
