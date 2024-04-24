package ru.beeline.techradar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.domain.TechCategory;
import ru.beeline.techradar.dto.PutTechCategoryDTO;
import ru.beeline.techradar.repository.CategoryRepository;
import ru.beeline.techradar.repository.TechCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TechCategoryRepository techCategoryRepository;


    public CategoryService(CategoryRepository categoryRepository, TechCategoryRepository techCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.techCategoryRepository = techCategoryRepository;
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

    public void putCategory(PutTechCategoryDTO category) {
        Category savedEntity = null;
        Optional<Category> entityOptional = categoryRepository.findByName(category.getJoinCategoryName());
        if (entityOptional.isPresent()) {
            savedEntity = entityOptional.get();
        } else {
            savedEntity = addCategory(Category.builder().name(category.getJoinCategoryName()).build());
        }
        List<TechCategory> techCategories = techCategoryRepository.findByCategory_IdIn(category.getJoinedCategoriesId());
        Category finalSavedEntity = savedEntity;
        techCategories.forEach(techCategory -> techCategory.setCategory(finalSavedEntity));
        techCategoryRepository.saveAll(techCategories);
        categoryRepository.deleteAllByIdIn(category.getJoinedCategoriesId());
    }
}
