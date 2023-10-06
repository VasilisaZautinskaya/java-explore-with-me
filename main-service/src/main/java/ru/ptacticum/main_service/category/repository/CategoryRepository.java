package ru.ptacticum.main_service.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ptacticum.main_service.category.model.Category;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category addCategory(Category category);

    Category updateCategory(Category category, Long categoryId);

    void deleteCategory(Long categoryId);

    List<Category> getCategories(Integer from, Integer size);

    Category getCategoryById(Long categoryId);
}
