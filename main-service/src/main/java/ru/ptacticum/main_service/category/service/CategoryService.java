package ru.ptacticum.main_service.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ptacticum.main_service.UnionService;
import ru.ptacticum.main_service.category.model.Category;
import ru.ptacticum.main_service.category.repository.CategoryRepository;
import ru.ptacticum.main_service.event.repository.EventRepository;
import ru.ptacticum.main_service.exception.ConflictException;

import java.util.List;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UnionService unionService;


    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }


    public Category updateCategory(Category newCategory, Long categoryId) {

        Category category = unionService.getCategoryOrNotFound(categoryId);
        category.setName(category.getName());
        categoryRepository.save(category);

        return category;
    }


    public void deleteCategory(Long categoryId) {

        unionService.getCategoryOrNotFound(categoryId);

        if (!eventRepository.findByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException(String.format("This category id %s is used and cannot be deleted", categoryId));
        }

        categoryRepository.deleteById(categoryId);
    }


    public List<Category> getCategories(Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return categoryRepository.findAll(pageRequest).toList();
    }

    public Category getCategoryById(Long categoryId) {

        return unionService.getCategoryOrNotFound(categoryId);
    }
}
