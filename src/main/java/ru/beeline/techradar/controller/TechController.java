/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.techradar.annotation.ApiErrorCodes;
import ru.beeline.techradar.dto.*;
import ru.beeline.techradar.service.TechService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tech")
@Tag(name = "Технологии", description = "Операции чтения и управления технологиями, версиями и связями с продуктами.")
public class TechController {
    private final TechService techService;

    public TechController(TechService techService) {
        this.techService = techService;
    }

    @GetMapping
    @ApiErrorCodes({400, 500})
    @Operation(
            operationId = "listTech",
            summary = "Получить список технологий",
            description = "Возвращает список технологий. При наличии параметра actualTech может фильтровать по актуальности."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TechAdvancedDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<TechAdvancedDTO> getAllTech(
            @Parameter(description = "Фильтр по актуальности технологии. Если не задан — возвращаются все.",
                    example = "true")
            @RequestParam(required = false) Boolean actualTech
    ) {
        return techService.getAllTech(actualTech);
    }

    @GetMapping("/{id}")
    @ApiErrorCodes({404, 500})
    @Operation(
            operationId = "getTechByIdWithHistory",
            summary = "Получить технологию по ID (с историей статусов)",
            description = "Возвращает технологию и историю изменения её статусов по идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = HistoryTechDTO.class))),
            @ApiResponse(responseCode = "404", description = "Технология не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<HistoryTechDTO> getTechById(
            @Parameter(description = "Идентификатор технологии", example = "123")
            @PathVariable Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getTechById(id));
    }

    @GetMapping("/by-ids")
    @ApiErrorCodes({400, 500})
    @Operation(
            operationId = "getTechByIds",
            summary = "Получить технологии по списку ID",
            description = "Возвращает список технологий по переданным идентификаторам (query-параметр ids)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TechAdvancedGetDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<TechAdvancedGetDTO>> getTechByIds(
            @Parameter(description = "Список ID", in = ParameterIn.QUERY, name = "ids")
            @RequestParam(required = true) List<Integer> ids) {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getTechByIds(ids));
    }

    @GetMapping("/subscribed")
    @ApiErrorCodes({500})
    @Operation(
            operationId = "listSubscribedTech",
            summary = "Получить список подписок на технологии",
            description = "Возвращает технологии, на которые оформлена подписка."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TechSubscribeDTO.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<TechSubscribeDTO> getSubscribed() {
        return techService.getTechSubscribed();
    }

    @GetMapping("/product-tech")
    @ApiErrorCodes({500})
    @Operation(
            operationId = "listProductTechRelations",
            summary = "Получить связи продукт↔технология",
            description = "Возвращает список продуктов и их связей с технологиями."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<ProductDTO>> getProductTech() {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getProductTech());
    }

    @PostMapping("/product-relation")
    @ApiErrorCodes({400, 403, 404, 500})
    @Operation(
            operationId = "createProductTechRelation",
            summary = "Создать связь продукт↔технология",
            description = "Создаёт связь технологии с продуктом/продуктами на основании тела запроса."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Связь успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Продукт или технология не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> createProductTechRelation(@RequestBody PostProductTechDTO tech) {
        techService.createRelations(tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
    @ApiErrorCodes({400, 403, 500})
    @Operation(
            operationId = "createTech",
            summary = "Создать технологии",
            description = "Создаёт одну или несколько технологий. Возвращает список идентификаторов созданных сущностей."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Технологии успешно созданы",
                    content = @Content(schema = @Schema(implementation = IdDTO.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<List<IdDTO>> addTech(@Valid @RequestBody List<PostTechDTO> techs) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(techService.addTech(techs));
    }

    @PatchMapping("/{id}")
    @ApiErrorCodes({400, 403, 404, 500})
    @Operation(
            operationId = "updateTech",
            summary = "Обновить технологию",
            description = "Частично обновляет технологию по идентификатору."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Технология успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные в запросе"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Технология не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> patchTech(@PathVariable Integer id,
                                          @RequestBody TechDTO tech) {
        techService.patchTech(id, tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    @ApiErrorCodes({403, 404, 500})
    @Operation(
            operationId = "deleteTech",
            summary = "Удалить технологию",
            description = "Удаляет технологию по идентификатору."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Технология успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Технология не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> deleteTech(
            @Parameter(description = "Идентификатор технологии", example = "123")
            @PathVariable Integer id
    ) {
        techService.deleteTech(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{tech_id}/version/{version_id}")
    @ApiErrorCodes({403, 404, 500})
    @Operation(
            operationId = "deleteTechVersion",
            summary = "Удалить версию технологии",
            description = "Удаляет конкретную версию технологии по идентификаторам технологии и версии."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Версия успешно удалена"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Технология или версия не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> deleteTechVersion(
            @Parameter(description = "Идентификатор технологии", example = "123")
            @PathVariable(name = "tech_id") Integer techId,
            @Parameter(description = "Идентификатор версии", example = "5")
            @PathVariable(name = "version_id") Integer versionId
    ) {
        techService.deleteTechVersion(techId, versionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{tech_id}/version")
    @ApiErrorCodes({400, 403, 404, 500})
    @Operation(
            operationId = "createTechVersion",
            summary = "Создать версии технологии",
            description = "Создаёт одну или несколько версий для указанной технологии."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Версии успешно созданы"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации тела запроса (формат версии, обязательные поля, пересечения диапазонов и т. п.)"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Технология не найдена"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> createTechVersion(@RequestBody List<PostTechVersionDTO> postTechVersionDTOS,
                                                  @PathVariable(name = "tech_id") Integer techId) {
        techService.createTechVersion(postTechVersionDTOS, techId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{tech_id}/version/{id_version}")
    @ApiErrorCodes({400, 403, 404, 500})
    @Operation(
            operationId = "updateTechVersion",
            summary = "Обновить версию технологии",
            description = "Обновляет версию технологии по идентификаторам технологии и версии."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Версия успешно обновлена"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации тела запроса"),
            @ApiResponse(responseCode = "403", description = "Недостаточно прав для выполнения операции"),
            @ApiResponse(responseCode = "404", description = "Технология или версия не найдены"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public ResponseEntity<Void> patchTechVersion(@RequestBody PostTechVersionDTO postTechVersionDTO,
                                                 @PathVariable(name = "tech_id") Integer techId,
                                                 @PathVariable(name = "id_version") Integer idVersion) {
        techService.patchTechVersion(postTechVersionDTO, techId, idVersion);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/export/{doc_id}")
    @ApiErrorCodes({500, 503})
    @Operation(
            operationId = "exportTechRadarDocument",
            summary = "Экспорт документа",
            description = "Формирует экспорт по идентификатору документа и возвращает метаданные экспорта."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Export created successfully",
                    content = @Content(schema = @Schema(implementation = TechExportDTO.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable – DB access problem or export error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TechExportDTO> export(@PathVariable(name = "doc_id") Integer docId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(techService.export(docId));
    }
}
