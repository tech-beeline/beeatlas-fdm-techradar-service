/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.annotation.ApiErrorCodes;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.service.RingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rings")
@Tag(name = "Кольца", description = "Справочник колец техрадара.")
public class RingController {
    private final RingService ringService;

    public RingController(RingService ringService) {
        this.ringService = ringService;
    }

    @GetMapping
    @ApiErrorCodes({500})
    @Operation(
            operationId = "listRings",
            summary = "Получить список колец",
            description = "Возвращает все доступные кольца."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Ring.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<Ring> getAllRings() {
        return ringService.getAllRings();
    }

}