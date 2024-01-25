package ru.beeline.techradar.service;

import org.springframework.stereotype.Service;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.repository.CategoryRepository;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll(){
        return categoryRepository.findAll();
    }
}
