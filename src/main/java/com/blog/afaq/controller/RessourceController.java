package com.blog.afaq.controller;

import com.blog.afaq.dto.response.RessourceResponse;

import com.blog.afaq.model.FileType;
import com.blog.afaq.model.ResourceCategory;
import com.blog.afaq.service.RessourceService;
import lombok.RequiredArgsConstructor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadById(@PathVariable String id) {
        try {
            System.out.println("🟢 Début téléchargement ressource avec id = " + id);

            RessourceResponse ressource = ressourceService.getById(id);
            System.out.println("📄 Récupération ressource : " + ressource);

            String fileUrl = ressource.getFileUrl();
            String originalFilename = ressource.getOriginalFilename();

            System.out.println("🌐 URL du fichier : " + fileUrl);
            System.out.println("📛 Nom original fichier : " + originalFilename);

            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            String contentType = connection.getContentType();
            System.out.println("🔍 Content-Type reçu : " + contentType);

            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
                System.out.println("⚠️ Content-Type absent, valeur par défaut appliquée : " + contentType);
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
                System.out.println("📥 Nombre d'octets lus : " + totalRead);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));

            // Sécuriser le nom de fichier
            String safeFilename = originalFilename != null ? originalFilename.replace("\"", "'") : "fichier.pdf";
            System.out.println("📂 Nom de fichier sécurisé pour Content-Disposition : " + safeFilename);

            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"");
            headers.set("Access-Control-Expose-Headers", "Content-Disposition");

            System.out.println("✅ Réponse prête avec headers et contenu");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("❌ Erreur lors du téléchargement : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
