package com.softwareengineering.team.campussecondhand.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private final Path fileStorageLocation;
    
    // 更改URL前缀，避免与控制器路径冲突
    private static final String UPLOAD_URL_PREFIX = "/files/";

    public FileStorageUtil() {
        String currentDir = System.getProperty("user.dir");
        System.out.println("当前工作目录: " + currentDir);
        
        this.fileStorageLocation = Paths.get(currentDir, "uploads").toAbsolutePath().normalize();
        System.out.println("文件存储位置: " + this.fileStorageLocation);
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            System.out.println("上传目录已创建或已存在");
        } catch (Exception ex) {
            System.err.println("创建上传目录失败: " + ex.getMessage());
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // 文件名使用UUID避免重名
        String fileName = UUID.randomUUID().toString() + getFileExtension(file.getOriginalFilename());
        
        try {
            // 检查文件名是否包含无效字符
            if(fileName.contains("..")) {
                throw new RuntimeException("文件名包含无效路径序列 " + fileName);
            }
            
            // 将文件复制到目标位置并替换同名文件
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 返回可访问的URL路径，使用新的前缀
            String fileUrl = UPLOAD_URL_PREFIX + fileName;
            System.out.println("保存文件URL: " + fileUrl);
            return fileUrl;
        } catch (IOException ex) {
            throw new RuntimeException("无法存储文件 " + fileName, ex);
        }
    }

    public String store(MultipartFile file) {
        return storeFile(file);
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex);
    }
}