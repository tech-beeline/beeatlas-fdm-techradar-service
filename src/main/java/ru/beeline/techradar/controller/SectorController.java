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
import ru.beeline.techradar.domain.Sector;
import ru.beeline.techradar.service.SectorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sectors")
@Tag(name = "Сектора", description = "Справочник секторов техрадара.")
public class SectorController {

    private final SectorService sectorService;

    public SectorController(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    @GetMapping
    @Operation(
            operationId = "listSectors",
            summary = "Получить список секторов",
            description = "Возвращает все доступные сектора."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = Sector.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<Sector> getAllSectors() {
        return sectorService.getAllSectors();
    }
}
