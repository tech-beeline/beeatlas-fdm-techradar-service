/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Категории", description = "Операции управления категориями и связями категория↔технология.")
public class CategoryController {

    private final CategoryService categoryService;
    private final TechService techService;

    public CategoryController(CategoryService categoryService, TechService techService) {
        this.categoryService = categoryService;
        this.techService = techService;
    }

    @PostMapping
    @Operation(
            operationId = "createCategory",
            summary = "Создать категорию",
            description = "Создаёт новую категорию. Возвращает созданную сущность категории."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Категория успешно создана",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции")
    })
    public Category addCategory(@RequestBody PostCategoryDTO category) {
        return categoryService.addCategory(category);
    }

    @PutMapping("/join")
    @Operation(
            operationId = "joinTechToCategory",
            summary = "Связать технологию с категорией",
            description = "Создаёт/обновляет связь технологии с категорией. Тело запроса содержит идентификаторы сущностей."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Связь успешно сохранена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Категория или технология не найдены")
    })
    public ResponseEntity<Void> putCategory(@RequestBody PutTechCategoryDTO category) {
        categoryService.putCategory(category);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/{id}")
    @Operation(
            operationId = "updateCategory",
            summary = "Обновить категорию",
            description = "Частично обновляет поля категории по идентификатору."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена")
    })
    public ResponseEntity<Void> patchCategory(
            @RequestBody PatchCategoryDTO category,
            @Parameter(description = "Идентификатор категории", example = "10")
            @PathVariable String id
    ) {
        categoryService.patchCategory(id, category);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @DeleteMapping("/{id}")
    @Operation(
            operationId = "deleteCategory",
            summary = "Удалить категорию",
            description = "Удаляет категорию по идентификатору."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Категория успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Категория не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "Идентификатор категории", example = "10")
            @PathVariable String id
    ) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).build();

    }

    @GetMapping
    @Operation(
            operationId = "listCategories",
            summary = "Получить список категорий",
            description = "Возвращает все категории."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Category.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<Category> getAllCategories() {
        return categoryService.getAll();
    }


    @GetMapping("/tech")
    @Operation(
            operationId = "listTechByCategoryIds",
            summary = "Получить технологии по категориям",
            description = "Возвращает список технологий, относящихся к переданным категориям."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TechExtensionDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<TechExtensionDTO> getTechByCategories(
            @Parameter(description = "Список идентификаторов категорий", example = "1,2,3")
            @RequestParam("id_category") List<Integer> idCategory
    ) {
        return techService.getAllTechByCategory(idCategory);
    }
}
