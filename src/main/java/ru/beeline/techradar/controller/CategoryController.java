/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.dto.PatchCategoryDTO;
import ru.beeline.techradar.dto.PostCategoryDTO;
import ru.beeline.techradar.dto.PutTechCategoryDTO;
import ru.beeline.techradar.dto.TechExtensionDTO;
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
    @ApiOperation(value = "Post category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Category created successfully",
                    response = Category.class),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    public Category addCategory(@RequestBody PostCategoryDTO category) {
        return categoryService.addCategory(category);
    }

    @PutMapping("/join")
    @ApiOperation(value = "Put category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Category edit successfully",
                    response = Category.class),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    public ResponseEntity<Void> putCategory(@RequestBody PutTechCategoryDTO category) {
        categoryService.putCategory(category);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}")
    @ApiOperation(value = "Patch category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Category created successfully",
                    response = Category.class),
            @ApiResponse(code = 403, message = "Forbidden"),
    })
    public ResponseEntity<Void> patchCategory(@RequestBody PatchCategoryDTO category, @PathVariable String id) {
        categoryService.patchCategory(id, category);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete category")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Category created successfully",
                    response = Category.class),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @GetMapping
    @ApiOperation(value = "get All Categories")
    public List<Category> getAllCategories() {
        return categoryService.getAll();
    }


    @GetMapping("/tech")
    @ApiOperation(value = "get Tech By Categories")
    public List<TechExtensionDTO> getTechByCategories(@RequestParam("id_category") List<Integer> idCategory) {
        return techService.getAllTechByCategory(idCategory);
    }
}
