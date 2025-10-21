package com.softwareengineering.team.campussecondhand.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;

@Component
public class FileStorageUtil {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        Path uploadPath = Paths.get(System.getProperty("user.dir"), uploadDir);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = System.currentTimeMillis() + "-" + java.util.UUID.randomUUID() + ext;
        Path target = uploadPath.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        // Return URL path mapped by WebMvcConfig
        return "/uploads/" + filename;
    }
}
