package ru.ptacticum.main_service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.ptacticum.main_service.category.dto.CategoryDto;
import ru.ptacticum.main_service.category.mapper.CategoryMapper;
import ru.ptacticum.main_service.category.model.Category;
import ru.ptacticum.main_service.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody Category category) {

        log.info("Add Category {} ", category.getName());
        return CategoryMapper.toCategoryDto(categoryService.addCategory(category));
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.OK)
    public CategoryDto updateCategory(@Valid @RequestBody Category category,
                                      @PathVariable("catId") Long categoryId) {

        log.info("Update Category {} ", category.getName());
        return CategoryMapper.toCategoryDto(categoryService.updateCategory(category, categoryId));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long categoryId) {

        log.info("Delete Category {} ", categoryId);
        categoryService.deleteCategory(categoryId);
    }
}