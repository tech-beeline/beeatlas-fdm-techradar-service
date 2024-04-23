package ru.beeline.techradar.service;

import org.springframework.stereotype.Service;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category addCategory(Category category) {
        Optional<Category> categoryOptional = categoryRepository.findByName(category.getName());
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        } else {
            return categoryRepository.save(category);
        }
    }
}
