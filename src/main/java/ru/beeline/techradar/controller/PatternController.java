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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.techradar.annotation.CustomHeaders;
import ru.beeline.techradar.dto.*;
import ru.beeline.techradar.exception.ChapterNotFoundException;
import ru.beeline.techradar.exception.ProductServiceUnavailableException;
import ru.beeline.techradar.service.PatternService;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.beeline.techradar.utils.Constant.USER_ROLES_HEADER;

@RestController
@RequestMapping("/api/v1")
public class PatternController {

    private final PatternService patternService;

    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @GetMapping("/pattern/by-ids")
    @Operation(summary = "Получить список паттернов по списку id из query (ids через запятую; дубликаты отбрасываются)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatternByIdsItemDTO.class)))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class),
                            examples = @ExampleObject(value = "{\"errorMessage\": \"Массив идентификаторов не передан или пустой\"}"))),
            @ApiResponse(responseCode = "404", description = "Not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class),
                            examples = @ExampleObject(value = "{\"errorMessage\": \"Не для каждого идентификатора существует паттерн\"}")))
    })
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
    @Operation(summary = "Просмотр всех паттернов проектирования")
    public ResponseEntity<List<PatternDTO>> allPatterns() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatterns());
    }

    @GetMapping("/patterns/tech/{tech_id}")
    @Operation(summary = "Просмотр всех паттернов связанных с технологией")
    public ResponseEntity<List<PatternDTO>> getAllTechnologyPatterns(@PathVariable(name = "tech_id") Integer techId) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllTechnologyPatterns(techId));
    }

    @GetMapping("/pattern/{id}")
    @Operation(summary = "Просмотр паттерна по id ")
    public ResponseEntity<PatternDTO> getPatternId(@Parameter(description = "ID Паттерна")
                                                   @PathVariable(name = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternId(id));
    }

    @GetMapping("/patterns/auto-check")
    @Operation(summary = "Просмотр паттернов принятых в компании, для которых есть правило автоматической проверки")
    public ResponseEntity<List<PatternDTO>> getPatternsAutoCheck() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternsAutoCheck());
    }

    @GetMapping("/pattern/group")
    @Operation(summary = "Просмотр групп паттернов проектирования")
    public ResponseEntity<List<PatternGroupDTO>> getAllPatternsGroup() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatternsGroup());
    }

    @GetMapping("/pattern/group/tree")
    @Operation(summary = "Просмотр дерева групп паттернов проектирования")
    public ResponseEntity<List<GroupDTO>> getTreePatternsGroup() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getTreePatternsGroup());
    }

    @CustomHeaders
    @PostMapping("/pattern")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание паттернов проектирования")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Паттерн успешно создан",
                    content = @Content(schema = @Schema(implementation = IdDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректное тело запроса",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessageDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Несуществующие технологии",
                                            summary = "Указаны несуществующие технологии",
                                            value = "{\"errorMessage\": \"Указаны несуществующие технологии\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Несуществующие категории",
                                            summary = "Указаны несуществующие категории",
                                            value = "{\"errorMessage\": \"Указаны несуществующие категории\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden — недостаточно прав для выполнения операции",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessageDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"errorMessage\": \"Forbidden\"}"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт — отсутствуют обязательные поля",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessageDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Отсутствует name",
                                            summary = "Отсутствует обязательное поле name",
                                            value = "{\"errorMessage\": \"Отсутствует обязательное поле name\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Отсутствует groups",
                                            summary = "Отсутствует обязательный список groups",
                                            value = "{\"errorMessage\": \"Отсутствует обязательный список groups\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Не все переданные идентификаторы соответствуют существующим требованиям",
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessageDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"errorMessage\": \"Не все переданные идентификаторы соответствуют существующим требованиям\"}"
                            )
                    )
            )
    })
    public ResponseEntity<IdDTO> createPattern(@RequestBody PostPatternDTO patternDTO,
                                               @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patternService.createPattern(patternDTO, userRoles));
    }


    @CustomHeaders
    @PostMapping("/pattern/group")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создание групп паттернов проектирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group created successfully",
                    content = @Content(schema = @Schema(implementation = IdDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request – invalid data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<IdDTO> createPatternGroup(@RequestBody PostPatternGroupDTO patternGroupDTO,
                                                    @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patternService.createPatternGroup(patternGroupDTO, userRoles));
    }

    @CustomHeaders
    @PatchMapping("/pattern/group/{id}")
    @Operation(summary = "Редактирование групп паттернов проектирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request – invalid data"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Group not found")
    })
    public ResponseEntity<Void> editPatternGroup(@PathVariable Integer id,
                                                 @RequestBody PostPatternGroupDTO patternGroupDTO,
                                                 @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.editPatternGroup(id, patternGroupDTO, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @CustomHeaders
    @PatchMapping("/pattern/{id}")
    @Operation(summary = "Обновление паттерна проектирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pattern updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Pattern not found"),
            @ApiResponse(responseCode = "500", description = "Ошибка вызова product при связи NFR",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class),
                            examples = @ExampleObject(
                                    value = "{\"errorMessage\": \"Ошибка при обращении к product сервису, паттерн не создан\"}"))),
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
    @Operation(summary = "Удаление паттерна проектирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pattern deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Pattern not found"),
    })
    public ResponseEntity<Void> deletePattern(@PathVariable Integer id,
                                              @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.deletePattern(id, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @CustomHeaders
    @DeleteMapping("/pattern/group/{id}")
    @Operation(summary = "Удаление групп паттернов проектирования")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Group deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Group not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deletePatternGroup(@PathVariable Integer id,
                                                   @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.deletePatternGroup(id, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/pattern/availability")
    @Operation(summary = "Проверка доступности паттернов по списку id")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Массив идентификаторов паттернов",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Integer.class))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = AvailabilityDTO.class))),
            @ApiResponse(responseCode = "400",
                    description =
                            "Некорректное тело запроса\n \n"
                                    + "Не передан массив идентификаторов паттернов или передан пустой\n"
                                    ,
                    content = @Content(
                            schema = @Schema(implementation = ErrorMessageDTO.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Некорректное тело запроса",
                                            summary = "Некорректное тело запроса",
                                            value = "{\"message\":\"Некорректное тело запроса\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Не передан массив идентификаторов паттернов или передан пустой",
                                            summary = "Не передан массив идентификаторов паттернов или передан пустой",
                                            value = "{\"message\":\"Не передан массив идентификаторов паттернов или передан пустой\"}"
                                    )
                            }
                    )),
    })
    public ResponseEntity<AvailabilityDTO> patternAvailability(@RequestBody(required = false) String body) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.patternAvailabilityFromRequest(body));
    }

    @GetMapping("/pattern/chapter/{id}")
    @Operation(summary = "Получить паттерны по главе (chapter) из product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ChapterPatternDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Chapter not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class))),
            @ApiResponse(responseCode = "500", description = "Product service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDTO.class)))
    })
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
