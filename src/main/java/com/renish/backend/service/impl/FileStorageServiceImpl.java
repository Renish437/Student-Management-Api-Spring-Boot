package com.renish.backend.service.impl;

import com.renish.backend.service.FileStorageService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;


    @Override
    public String storeFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = "file";
        }
        originalFileName = StringUtils.cleanPath(originalFileName);
        String fileExtension="";
        int dotIndex = originalFileName.lastIndexOf(".");
        if(dotIndex>0){
            fileExtension = originalFileName.substring(dotIndex);
        }
            String fileName = UUID.randomUUID()+fileExtension;
            Path targetLocation = uploadPath.resolve(fileName);
            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

        return fileName;
    }

    @Override
    public Resource loadFileAsResource(String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = uploadPath.resolve(fileName).normalize();
        if (!filePath.startsWith(uploadPath)) {
            throw new IOException("Invalid file name " + fileName);
        }
        Resource resource = new UrlResource(filePath.toUri());

        if(resource.exists() && resource.isReadable()){
            return resource;
        }
        throw new IOException("File not found or not readable "+fileName);
    }

    @Override
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path filePath = uploadPath.resolve(fileName).normalize();
        if (!filePath.startsWith(uploadPath)) {
            log.warn("Refusing to delete file outside upload dir: {}", fileName);
            return;
        }
        try {
            if (Files.deleteIfExists(filePath)) {
                log.debug("Deleted stored file: {}", fileName);
            }
        } catch (IOException e) {
            log.warn("Failed to delete stored file: {}", fileName, e);
        }
    }
}
