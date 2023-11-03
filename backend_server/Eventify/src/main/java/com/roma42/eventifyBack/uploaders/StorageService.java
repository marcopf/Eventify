package com.roma42.eventifyBack.uploaders;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface StorageService {

    void init();
    void upload(MultipartFile file, String name, String type);
    Path load(String filename, String type);
    Resource loadAsResource(String filename, String type);
    void deleteFile(String filename, String type);
    void deleteAllFile(List<String> filenames, String type);
    void deleteAll();
}
