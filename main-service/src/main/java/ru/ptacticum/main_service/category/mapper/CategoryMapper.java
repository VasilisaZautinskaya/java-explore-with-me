package ru.ptacticum.main_service.category.mapper;

import lombok.experimental.UtilityClass;
import ru.ptacticum.main_service.category.dto.CategoryDto;
import ru.ptacticum.main_service.category.model.Category;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CategoryMapper {
    public CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
        return categoryDto;
    }

    public Category toCategory(CategoryDto categoryDto) {
        Category category = Category.builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build();
        return category;
    }

    public List<CategoryDto> toCategoryListDto(Iterable<Category> categories) {
        List<CategoryDto> result = new ArrayList<>();

        for (Category category : categories) {
            result.add(toCategoryDto(category));
        }
        return result;
    }
}