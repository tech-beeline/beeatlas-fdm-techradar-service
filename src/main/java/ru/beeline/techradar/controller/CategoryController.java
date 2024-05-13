package ru.beeline.techradar.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.PatchCategoryDTO;
import ru.beeline.techradar.dto.PostCategoryDTO;
import ru.beeline.techradar.dto.PutTechCategoryDTO;
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

    @PostMapping
    public Category addCategory(@RequestBody PostCategoryDTO category) {
        return categoryService.addCategory(category);
    }

    @PutMapping("/join")
    public ResponseEntity putCategory(@RequestBody PutTechCategoryDTO category) {
        categoryService.putCategory(category);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity patchCategory(@RequestBody PatchCategoryDTO category,
                                        @PathVariable String id) {
        categoryService.patchCategory(id, category);
        return ResponseEntity.status(HttpStatus.OK).build();

    }
    @DeleteMapping("/{id}")
    public ResponseEntity deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();

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
