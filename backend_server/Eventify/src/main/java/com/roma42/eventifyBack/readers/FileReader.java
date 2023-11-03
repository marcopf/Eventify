package com.roma42.eventifyBack.readers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileReader {

    static public String readFile(String filename) throws IOException {
        Resource resource = new ClassPathResource("newPassword.html");
        String newPasswordHtml;
        try (InputStream inputStream = resource.getInputStream()) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (inputStream.read(buffer) != -1) {
                result.write(buffer);
            }
            newPasswordHtml = result.toString();
        }
        return newPasswordHtml;
    }
}
