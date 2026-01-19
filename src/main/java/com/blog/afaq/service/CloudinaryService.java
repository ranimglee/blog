package com.blog.afaq.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "articles",
                        "access_mode", "public"  // make sure it's public
                ));
        return uploadResult.get("secure_url").toString();
    }

    public String uploadFile(MultipartFile file, String fileType) {
        try {
            Map uploadOptions = ObjectUtils.emptyMap();
            if ("PDF".equalsIgnoreCase(fileType)) {
                // For PDFs, upload as raw file
                uploadOptions = ObjectUtils.asMap("resource_type", "raw", "access_mode", "public");
            } else {
                // For images or others, upload as image
                uploadOptions = ObjectUtils.asMap("access_mode", "public");
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Cloudinary upload failed", e);
        }
    }



}

