package com.newbusiness.one4all.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "image/jpg",
        "image/gif",
        "image/webp",
        "image/bmp"
    );

    @Value("${upload.dir:uploads}") // configurable via application.properties
    private String uploadDir;

    public String uploadProofFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only image files and PDFs are allowed.");
        }

        // Ensure directory exists
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Use UUID to avoid filename conflicts
        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String newFileName = UUID.randomUUID() + "." + extension;
        Path destination = uploadPath.resolve(newFileName);

        // Save file
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        // Return accessible proof URL (adjust if you serve from a static base path)
        return "/uploads/" + newFileName;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("File name has no extension.");
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
