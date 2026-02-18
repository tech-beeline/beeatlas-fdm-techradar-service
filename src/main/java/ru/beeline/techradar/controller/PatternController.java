/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.techradar.annotation.CustomHeaders;
import ru.beeline.techradar.dto.*;
import ru.beeline.techradar.service.PatternService;

import java.util.List;

import static ru.beeline.techradar.utils.Constant.USER_ROLES_HEADER;

@RestController
@RequestMapping("/api/v1")
public class PatternController {

    private final PatternService patternService;

    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @GetMapping("/patterns")
    @ApiOperation(value = "Просмотр всех паттернов проектирования")
    public ResponseEntity<List<PatternDTO>> allPatterns() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatterns());
    }

    @GetMapping("/patterns/tech/{tech_id}")
    @ApiOperation(value = "Просмотр всех паттернов связанных с технологией")
    public ResponseEntity<List<PatternDTO>> getAllTechnologyPatterns(@PathVariable(name = "tech_id") Integer techId) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllTechnologyPatterns(techId));
    }

    @GetMapping("/pattern/{id}")
    @ApiOperation(value = "Просмотр паттерна по id ")
    public ResponseEntity<PatternDTO> getPatternId(@Parameter(description = "ID Паттерна")
                                                   @PathVariable(name = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternId(id));
    }

    @GetMapping("/patterns/auto-check")
    @ApiOperation(value = "Просмотр паттернов принятых в компании, для которых есть правило автоматической проверки")
    public ResponseEntity<List<PatternDTO>> getPatternsAutoCheck() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternsAutoCheck());
    }

    @GetMapping("/pattern/group")
    @ApiOperation(value = "Просмотр групп паттернов проектирования")
    public ResponseEntity<List<PatternGroupDTO>> getAllPatternsGroup() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatternsGroup());
    }

    @GetMapping("/pattern/group/tree")
    @ApiOperation(value = "Просмотр дерева групп паттернов проектирования")
    public ResponseEntity<List<GroupDTO>> getTreePatternsGroup() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getTreePatternsGroup());
    }

    @CustomHeaders
    @PostMapping("/pattern")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Создание паттернов проектирования")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pattern created successfully",
                    response = IdDTO.class),
            @ApiResponse(code = 400, message = "Bad request – invalid data"),
            @ApiResponse(code = 403, message = "Forbidden")
    })
    public ResponseEntity<IdDTO> createPattern(@RequestBody PostPatternDTO patternDTO,
                                               @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patternService.createPattern(patternDTO, userRoles));
    }


    @CustomHeaders
    @PostMapping("/pattern/group")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Создание групп паттернов проектирования")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Group created successfully", response = IdDTO.class), @ApiResponse(code = 400, message = "Bad request – invalid data"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 500, message = "Internal server error")})
    public ResponseEntity<IdDTO> createPatternGroup(@RequestBody PostPatternGroupDTO patternGroupDTO,
                                                    @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patternService.createPatternGroup(patternGroupDTO, userRoles));
    }

    @CustomHeaders
    @PatchMapping("/pattern/group/{id}")
    @ApiOperation(value = "Редактирование групп паттернов проектирования")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Group updated successfully"),
            @ApiResponse(code = 400, message = "Bad request – invalid data"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Group not found")
    })
    public ResponseEntity<Void> editPatternGroup(@PathVariable Integer id,
                                                 @RequestBody PostPatternGroupDTO patternGroupDTO,
                                                 @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.editPatternGroup(id, patternGroupDTO, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @CustomHeaders
    @PatchMapping("/pattern/{id}")
    @ApiOperation(value = "Обновление паттерна проектирования")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pattern updated successfully"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Pattern not found"),
    })
    public ResponseEntity<Void> editPattern(@Parameter(description = "ID Паттерна")
                                            @PathVariable Integer id,
                                            @RequestBody PatchPatternDTO patternDTO,
                                            @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.editPattern(id, patternDTO, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @CustomHeaders
    @DeleteMapping("/pattern/{id}")
    @ApiOperation(value = "Удаление паттерна проектирования")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Pattern deleted successfully"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Pattern not found"),
    })
    public ResponseEntity<Void> deletePattern(@PathVariable Integer id,
                                              @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.deletePattern(id, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @CustomHeaders
    @DeleteMapping("/pattern/group/{id}")
    @ApiOperation(value = "Удаление групп паттернов проектирования")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Group deleted successfully"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Group not found"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> deletePatternGroup(@PathVariable Integer id,
                                                   @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.deletePatternGroup(id, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
