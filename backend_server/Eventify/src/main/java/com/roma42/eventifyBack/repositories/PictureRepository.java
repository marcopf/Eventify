package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.Picture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PictureRepository extends JpaRepository<Picture, Long> {
}
