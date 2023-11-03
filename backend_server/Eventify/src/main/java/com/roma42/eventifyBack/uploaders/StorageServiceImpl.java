package com.roma42.eventifyBack.uploaders;

import com.roma42.eventifyBack.exception.StorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocation;
    private final Path eventRootLocation;

    @Autowired
    public StorageServiceImpl(StorageProperties storageProperties) {
        this.rootLocation = Paths.get(storageProperties.getLocation());
        this.eventRootLocation = Paths.get(storageProperties.getEventLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(this.rootLocation);
            Files.createDirectories(this.eventRootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Cannot create root directory");
        }
    }

    @Override
    public void upload(MultipartFile file, String name, String type) {
        Path root;
        // substitute with an enum
        if (type.equals("user"))
            root = this.rootLocation;
        else
            root = this.eventRootLocation;
        Path destinationFile = root;
        try {
            if (file.isEmpty())
                throw new StorageException("cannot upload empty file");
            destinationFile = root.resolve(
                    Paths.get(Objects.requireNonNull(name)))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(root.toAbsolutePath())) {
                throw new StorageException("cannot store file outside current directory");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file: " + destinationFile
                    + "\nMessage error: " + e.getMessage());
        }
    }

    public void uploadFiles(MultipartFile[] files, String[] names, String[] types) {
        for (int i = 0; i < files.length; i++) {
            upload(files[i], names[i], types[i]);
        }
    }

    @Override
    public Path load(String filename, String type) {
        if (type.equals("user"))
            return this.rootLocation.resolve(filename);
        else
            return this.eventRootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename, String type) {
        try {
            Path file = load(filename, type);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageException("Cannot read file");
            }
        }
        catch (MalformedURLException e) {
            throw new StorageException("Cannot read file");
        }
    }


    @Override
    public void deleteFile(String filename, String type) {
        Path root;
        if (type.equals("user"))
            root = this.rootLocation;
        else
            root = this.eventRootLocation;
        File image = new File(root.resolve(filename).toString());
        if (!image.delete())
            throw new StorageException("cannot delete file");
    }

    @Override
    public void deleteAllFile(List<String> filenames, String type) {
        for (String filename : filenames) {
            try {
                deleteFile(filename, type);
            } catch (StorageException ignore) {}
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
        FileSystemUtils.deleteRecursively(eventRootLocation.toFile());
    }
}
