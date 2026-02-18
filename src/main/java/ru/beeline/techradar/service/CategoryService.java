/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.controller.RequestContext;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.domain.TechCategory;
import ru.beeline.techradar.dto.PatchCategoryDTO;
import ru.beeline.techradar.dto.PostCategoryDTO;
import ru.beeline.techradar.dto.PutTechCategoryDTO;
import ru.beeline.techradar.exception.ForbiddenException;
import ru.beeline.techradar.repository.CategoryRepository;
import ru.beeline.techradar.repository.TechCategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Category addCategory(PostCategoryDTO category) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        return categoryRepository.findByName(category.getName())
                .orElseGet(() -> categoryRepository.save(Category.builder().name(category.getName()).build()));
    }

    public void putCategory(PutTechCategoryDTO category) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        Category savedEntity = categoryRepository.findByName(category.getJoinCategoryName())
                .orElseGet(() -> categoryRepository.save(Category.builder().name(category.getJoinCategoryName()).build()));
        List<TechCategory> techCategories = techCategoryRepository.findByCategory_IdIn(category.getJoinedCategoriesId());
        Category finalSavedEntity = savedEntity;
        Set<Tech> techSet = techCategories.stream().map(TechCategory::getTech).collect(Collectors.toSet());
        List<TechCategory> newTechCategories = techSet.stream().map(tech -> TechCategory.builder().tech(tech).category(finalSavedEntity)
        .build()).collect(Collectors.toList());
        techCategoryRepository.deleteAllInBatch(techCategories);
        techCategoryRepository.saveAll(newTechCategories);
        category.getJoinedCategoriesId().remove(Integer.valueOf(finalSavedEntity.getId()));
        categoryRepository.deleteAllByIdIn(category.getJoinedCategoriesId());
    }

    public void patchCategory(String id, PatchCategoryDTO category) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        Category entity = categoryRepository.findById(Integer.parseInt(id))
                .orElseThrow(() -> new IllegalArgumentException("Category with id=" + id + " not found."));
        entity.setName(category.getName());
        categoryRepository.save(entity);
    }

    public void deleteCategory(String id) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        List<TechCategory> techCategories = techCategoryRepository.findByCategory_IdIn(new ArrayList<>(Integer.parseInt(id)));
        if (techCategories == null || techCategories.isEmpty()) ;
        {
            categoryRepository.deleteById(Integer.parseInt(id));
        }
        new RuntimeException("table tech_category have category_id = " + id);
    }
}
