package com.roma42.eventifyBack.validators;

import com.roma42.eventifyBack.models.Picture;
import com.roma42.eventifyBack.exception.StorageException;
import com.roma42.eventifyBack.exception.UploadException;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public class ImageValidator {

    static private boolean extensionChecker(MultipartFile image) {
        String extension = FilenameUtils.getExtension(image.getOriginalFilename());
        return extension != null && (
            extension.equals("jpg")
            || extension.equals("jpeg")
            || extension.equals("png")
            || extension.equals("heic"));
    }
    static private boolean typeChecker(MultipartFile image) throws IOException {
        String type = new Tika().detect(image.getBytes());
        return (type.equals("image/png")
                || type.equals("image/jpeg")
                || type.equals("image/jpg")
                || type.equals("image/heif"));
    }

    static public void validate(MultipartFile image) {
        try {
            if (!extensionChecker(image) || !typeChecker(image))
                throw new UploadException("Unsupported type");
        }
        catch (IOException e) {
            throw new UploadException("Unsupported type");
        }
    }

    static public void validatePicturesList(List<Picture> pictures, Integer imageNum) {
        if (pictures.isEmpty())
            throw new StorageException("cannot load images: empty gallery");
        else if (imageNum < 0 || imageNum > pictures.size() - 1)
            throw new StorageException("out of bounds");
    }
}