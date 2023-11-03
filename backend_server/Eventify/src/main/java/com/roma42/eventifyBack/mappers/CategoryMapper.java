package com.roma42.eventifyBack.mappers;

import com.roma42.eventifyBack.dto.CategoryDto;
import com.roma42.eventifyBack.models.Category;

public class CategoryMapper {
    static public CategoryDto ToMinimalDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryName(category.getCategoryName());
        categoryDto.setEvents(null);
        return categoryDto;
    }

    static public Category ToCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setCategoryName(categoryDto.getCategoryName());
        return category;
    }
}
