/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.techradar.annotation.ApiErrorCodes;
import ru.beeline.techradar.annotation.CustomHeaders;
import ru.beeline.techradar.dto.*;
import ru.beeline.techradar.exception.ChapterNotFoundException;
import ru.beeline.techradar.exception.ProductServiceUnavailableException;
import ru.beeline.techradar.service.PatternService;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1")
@Tag(name = "Паттерны", description = "Операции чтения и управления паттернами проектирования и их группами.")
public class PatternController {

    private final PatternService patternService;

    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @GetMapping("/pattern/by-ids")
    @ApiErrorCodes({400, 404, 500})
    @Operation(summary = "Получить список паттернов по списку id из query (ids через запятую; дубликаты отбрасываются)")
    public ResponseEntity<?> getPatternsByIds(
            @Parameter(description = "Идентификаторы паттернов через запятую, обязательный, не пустой", example = "1,2,3")
            @RequestParam(name = "ids", required = false) String ids) {
        List<Integer> uniqueIds = parseAndValidateIds(ids);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternsByIds(uniqueIds));
        } catch (ru.beeline.techradar.exception.NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorMessageDTO.builder().errorMessage("Не для каждого идентификатора существует паттерн").build());
        }
    }

    @GetMapping("/patterns")
    @ApiErrorCodes({500})
    @Operation(summary = "Просмотр всех паттернов проектирования")
    public ResponseEntity<List<PatternDTO>> allPatterns() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatterns());
    }

    @GetMapping("/patterns/tech/{tech_id}")
    @ApiErrorCodes({500})
    @Operation(summary = "Просмотр всех паттернов связанных с технологией")
    public ResponseEntity<List<PatternDTO>> getAllTechnologyPatterns(@PathVariable(name = "tech_id") Integer techId) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllTechnologyPatterns(techId));
    }

    @GetMapping("/pattern/{id}")
    @ApiErrorCodes({500})
    @Operation(summary = "Просмотр паттерна по id ")
    public ResponseEntity<PatternDTO> getPatternId(@Parameter(description = "ID Паттерна")
                                                   @PathVariable(name = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternId(id));
    }

    @GetMapping("/patterns/auto-check")
    @ApiErrorCodes({500})
    @Operation(summary = "Просмотр паттернов принятых в компании, для которых есть правило автоматической проверки")
    public ResponseEntity<List<PatternDTO>> getPatternsAutoCheck() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternsAutoCheck());
    }

    @GetMapping("/pattern/group")
    @ApiErrorCodes({500})
    @Operation(summary = "Просмотр групп паттернов проектирования")
    public ResponseEntity<List<PatternGroupDTO>> getAllPatternsGroup() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatternsGroup());
    }

    @GetMapping("/pattern/group/tree")
    @ApiErrorCodes({500})
    @Operation(summary = "Просмотр дерева групп паттернов проектирования")
    public ResponseEntity<List<GroupDTO>> getTreePatternsGroup() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getTreePatternsGroup());
    }

    @CustomHeaders
    @PostMapping("/pattern")
    @ApiErrorCodes({400, 403, 409, 500})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание паттернов проектирования")
    public ResponseEntity<IdDTO> createPattern(@RequestBody PostPatternDTO patternDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patternService.creatingPattern(patternDTO));
    }


    @CustomHeaders
    @PostMapping("/pattern/group")
    @ApiErrorCodes({400, 403, 500})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание групп паттернов проектирования")
    public ResponseEntity<IdDTO> createPatternGroup(@RequestBody PostPatternGroupDTO patternGroupDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patternService.createPatternGroup(patternGroupDTO));
    }

    @CustomHeaders
    @PatchMapping("/pattern/group/{id}")
    @ApiErrorCodes({400, 403, 404, 500})
    @Operation(summary = "Редактирование групп паттернов проектирования")
    public ResponseEntity<Void> editPatternGroup(@PathVariable Integer id,
                                                 @RequestBody PostPatternGroupDTO patternGroupDTO) {
        patternService.editPatternGroup(id, patternGroupDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @CustomHeaders
    @PatchMapping("/pattern/{id}")
    @ApiErrorCodes({403, 404, 500})
    @Operation(summary = "Обновление паттерна проектирования")
    public ResponseEntity<Void> editPattern(@Parameter(description = "ID Паттерна")
                                            @PathVariable Integer id,
                                            @RequestBody PatchPatternDTO patternDTO) {
        patternService.editPattern(id, patternDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @CustomHeaders
    @DeleteMapping("/pattern/{id}")
    @ApiErrorCodes({403, 404, 500})
    @Operation(summary = "Удаление паттерна проектирования")
    public ResponseEntity<Void> deletePattern(@PathVariable Integer id) {
        patternService.deletePattern(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @CustomHeaders
    @DeleteMapping("/pattern/group/{id}")
    @ApiErrorCodes({403, 404, 500})
    @Operation(summary = "Удаление групп паттернов проектирования")
    public ResponseEntity<Void> deletePatternGroup(@PathVariable Integer id) {
        patternService.deletePatternGroup(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/pattern/availability")
    @ApiErrorCodes({400, 500})
    @Operation(summary = "Проверка доступности паттернов по списку id")
    public ResponseEntity<AvailabilityDTO> patternAvailability(@RequestBody(required = false) String body) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.patternAvailabilityFromRequest(body));
    }

    @GetMapping("/pattern/chapter/{id}")
    @ApiErrorCodes({404, 500})
    @Operation(summary = "Получить паттерны по главе (chapter) из product")
    public ResponseEntity<?> getPatternsByChapter(@PathVariable(name = "id") Integer chapterId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternsByChapter(chapterId));
        } catch (ChapterNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ErrorMessageDTO.builder().errorMessage("Chapter с таким id не существует").build());
        } catch (ProductServiceUnavailableException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ErrorMessageDTO.builder().errorMessage("Сервис product недоступен").build());
        }
    }

    private List<Integer> parseAndValidateIds(String ids) {
        if (ids == null || ids.isBlank()) {
            throw new IllegalArgumentException("Массив идентификаторов не передан или пустой");
        }
        String[] raw = ids.split(",");
        Set<Integer> unique = new LinkedHashSet<>();
        for (String part : raw) {
            String trimmed = part == null ? "" : part.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                unique.add(Integer.parseInt(trimmed));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Массив идентификаторов не передан или пустой");
            }
        }
        if (unique.isEmpty()) {
            throw new IllegalArgumentException("Массив идентификаторов не передан или пустой");
        }
        return unique.stream().collect(Collectors.toList());
    }
}
