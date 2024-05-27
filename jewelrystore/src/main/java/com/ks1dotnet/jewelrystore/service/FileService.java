package com.ks1dotnet.jewelrystore.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.service.serviceImp.IFileService;

import jakarta.annotation.PostConstruct;

@Service

public class FileService implements IFileService {

    @Value("${fileUpload.rootPath}")
    private String rootPath;
    private Path root;

    @PostConstruct
    private void init() {
        root = Paths.get(rootPath);
        if (Files.notExists(root)) {
            try {
                Files.createDirectories(root);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error create folder root " + e.getMessage());
            }
        }
    }

    @Override
    public boolean savefile(MultipartFile file) {
        try {
            Files.copy(file.getInputStream(), root.resolve(file.getOriginalFilename()),
                    StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            System.out.println("Error save file root " + e.getMessage());

            return false;
        }
    }

    @Override
    public Resource loadFile(String fileName) {
        try {
            Path file = root.resolve(fileName);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        } catch (Exception e) {
            System.out.println("Error load file root " + e.getMessage());
        }
        return null;
    }

}
