package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.exception.PictureNotFoundException;
import com.roma42.eventifyBack.models.Picture;
import com.roma42.eventifyBack.repositories.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PictureService {

    final private PictureRepository pictureRepository;

    @Autowired
    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public List<Picture> findAllPicture() {
        return this.pictureRepository.findAll();
    }

    public Picture findPictureById(Long id) {
        return this.pictureRepository.findById(id)
                .orElseThrow(() -> new PictureNotFoundException("Picture not found"));
    }

    public Picture addPicture(Picture picture) {
        return this.pictureRepository.save(picture);
    }

    public Picture updatePicture(Picture picture) {
        return this.pictureRepository.save(picture);
    }

    public void deletePicture(Long id) {
        this.pictureRepository.deleteById(id);
    }
}
