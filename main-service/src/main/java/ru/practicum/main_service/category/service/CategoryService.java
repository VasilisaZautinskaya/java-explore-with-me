package ru.practicum.main_service.category.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.UnionService;
import ru.practicum.main_service.category.repository.CategoryRepository;
import ru.practicum.main_service.exception.ConflictException;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.event.repository.EventRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UnionService unionService;

    @Transactional
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(String name, Long categoryId) {

        Category category = unionService.getCategoryOrNotFound(categoryId);
        category.setName(name);
        categoryRepository.save(category);

        return category;
    }

    @Transactional
    public void deleteCategory(Long categoryId) {

        unionService.getCategoryOrNotFound(categoryId);

        if (!eventRepository.findByCategoryId(categoryId).isEmpty()) {
            throw new ConflictException(String.format("Этот идентификатор категории %s используется, поэтому не удалось удалить категорию   ", categoryId));
        }

        categoryRepository.deleteById(categoryId);
    }

    public List<Category> getCategories(Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        return categoryRepository.findAll(pageRequest).toList();
    }

    @Transactional
    public Category getCategoryById(Long categoryId) {

        return unionService.getCategoryOrNotFound(categoryId);
    }
}
