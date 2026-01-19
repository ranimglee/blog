package com.blog.afaq.controller;

import com.blog.afaq.dto.response.RessourceResponse;
import com.blog.afaq.model.FileType;
import com.blog.afaq.model.Language;
import com.blog.afaq.model.ResourceCategory;
import com.blog.afaq.service.RessourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/api/ressources")
@RequiredArgsConstructor
@Slf4j
public class RessourceController {

    private final RessourceService ressourceService;

    @PostMapping("/upload")
    public ResponseEntity<RessourceResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("category") ResourceCategory category,
            @RequestParam("fileType") FileType fileType,
            @RequestParam("language") Language language
    ) {
        RessourceResponse response = ressourceService.uploadRessource(file, titre, description, category, fileType, language);
        log.info("📤 Ressource uploaded: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadById(@PathVariable String id) {
        try {
            log.info("🟢 Début téléchargement ressource avec id = {}", id);

            RessourceResponse ressource = ressourceService.getById(id);
            log.info("📄 Récupération ressource : {}", ressource);

            String fileUrl = ressource.getFileUrl();
            String originalFilename = ressource.getOriginalFilename();

            log.info("🌐 URL du fichier : {}", fileUrl);
            log.info("📛 Nom original fichier : {}", originalFilename);

            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String contentType = connection.getContentType();
            log.info("🔍 Content-Type reçu : {}", contentType);

            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
                log.warn("⚠️ Content-Type absent, valeur par défaut appliquée : {}", contentType);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                int totalRead = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
                log.info("📥 Nombre d'octets lus : {}", totalRead);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            // Sécuriser le nom de fichier
            String safeFilename = originalFilename != null ? originalFilename.replace("\"", "'") : "fichier.pdf";
            log.info("📂 Nom de fichier sécurisé pour Content-Disposition : {}", safeFilename);

            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"");
            headers.set("Access-Control-Expose-Headers", "Content-Disposition");

            log.info("✅ Réponse prête avec headers et contenu");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("❌ Erreur lors du téléchargement de la ressource id={}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RessourceResponse>> getAll() {
        List<RessourceResponse> ressources = ressourceService.getAll();
        log.info("📚 Ressources récupérées : count={}", ressources.size());
        return ResponseEntity.ok(ressources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RessourceResponse> getById(@PathVariable String id) {
        RessourceResponse ressource = ressourceService.getById(id);
        log.info("🔎 Ressource récupérée par id {} : {}", id, ressource);
        return ResponseEntity.ok(ressource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        ressourceService.delete(id);
        log.info("🗑 Ressource supprimée id={}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(params = "lang")
    public List<RessourceResponse> getResourcesByLanguage(@RequestParam String lang) {
        List<RessourceResponse> ressources = ressourceService.getResourcesByLanguage(lang);
        log.info("🌐 Ressources récupérées pour langue '{}': count={}", lang, ressources.size());
        return ressources;
    }
}
