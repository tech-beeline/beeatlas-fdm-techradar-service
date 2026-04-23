/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Post category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public Category addCategory(@RequestBody PostCategoryDTO category) {
        return categoryService.addCategory(category);
    }

    @PutMapping("/join")
    @Operation(summary = "Put category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category edit successfully",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<Void> putCategory(@RequestBody PutTechCategoryDTO category) {
        categoryService.putCategory(category);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    public ResponseEntity<Void> patchCategory(@RequestBody PatchCategoryDTO category, @PathVariable String id) {
        categoryService.patchCategory(id, category);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @GetMapping
    @Operation(summary = "get All Categories")
    public List<Category> getAllCategories() {
        return categoryService.getAll();
    }


    @GetMapping("/tech")
    @Operation(summary = "get Tech By Categories")
    public List<TechExtensionDTO> getTechByCategories(@RequestParam("id_category") List<Integer> idCategory) {
        return techService.getAllTechByCategory(idCategory);
    }
}
