package com.renish.backend.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String storeFile(MultipartFile file) throws IOException;
    Resource loadFileAsResource(String fileName) throws IOException;

    /**
     * Deletes a previously stored file. Missing files are ignored.
     */
    void deleteFile(String fileName);
}
