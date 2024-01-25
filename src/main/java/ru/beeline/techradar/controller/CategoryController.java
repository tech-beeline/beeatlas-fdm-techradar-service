package ru.beeline.techradar.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.service.CategoryService;
import ru.beeline.techradar.service.TechService;
import java.util.List;

@RestController
@RequestMapping("/api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final TechService techService;

    public CategoryController(CategoryService categoryService, TechService techService) {
        this.categoryService = categoryService;
        this.techService = techService;
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAll();
    }


    @GetMapping("/tech")
    public List<Tech> getTechByCategories(@RequestParam("id_category") List<Integer> idCategory) {
        return techService.getAllTechByCategory(idCategory);
    }
}
