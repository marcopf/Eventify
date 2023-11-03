package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.exception.CategoryNotFoundException;
import com.roma42.eventifyBack.models.CategoriesList;
import com.roma42.eventifyBack.repositories.CategoryRepository;
import com.roma42.eventifyBack.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryService {

    final private CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAllCategory() {
        return this.categoryRepository.findAll();
    }

    public Slice<Category> findSliceBy(Pageable pageable) {
        return this.categoryRepository.findSliceBy(pageable);
    }

    public Category findCategoryById(Long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("not found"));
    }

    public Category findCategoryByName(String categoryName) {
        return this.categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("not found"));
    }

    public Set<Category> findCategoriesByNames(Set<String> categoriesName) {
        Set<Category> categories = this.categoryRepository.findByCategoryNameIn(categoriesName);
        // this check could be deleted if the event should be created even if a category doesn't exist
        if (categories.size() != categoriesName.size())
            throw new CategoryNotFoundException("category not found");
        return categories;
    }

    public Category addCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    public List<Category> addCategories(List<String> categoriesName) {
        List<Category> categories = new ArrayList<>();
        for (String categoryName : categoriesName) {
            if (this.categoryRepository.findByCategoryName(categoryName).isEmpty()) {
                categories.add(this.categoryRepository
                        .save(new Category(null, categoryName, new HashSet<>())));
            }
        }
        return categories;
    }

    public Category updateCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        this.categoryRepository.deleteById(id);
    }

    public void deleteCategoryByName(String categoryName) {
        this.categoryRepository.deleteByCategoryName(categoryName);
    }

    public CategoriesList sliceToCategoriesNameList(Slice<Category> categories) {
        List<String> categoriesName = new ArrayList<>();
        for (Category category : categories)
            categoriesName.add(category.getCategoryName());
        return new CategoriesList(categoriesName);
    }
}
