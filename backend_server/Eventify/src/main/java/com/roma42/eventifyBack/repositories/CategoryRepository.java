package com.roma42.eventifyBack.repositories;

import com.roma42.eventifyBack.models.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryName(String categoryName);
    Set<Category> findByCategoryNameIn(Set<String> categoriesName);
    Slice<Category> findSliceBy(Pageable pageable);
    void    deleteByCategoryName(String categoryName);
}
