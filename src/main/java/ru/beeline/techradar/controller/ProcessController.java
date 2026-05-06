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
import ru.beeline.techradar.dto.ProcessDTO;
import ru.beeline.techradar.service.ProcessService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/processes")
@Tag(name = "Процессы", description = "Справочник процессов/доменов, используемых в техрадаре.")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping
    @Operation(
            operationId = "listProcesses",
            summary = "Получить список процессов",
            description = "Возвращает все процессы."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = ProcessDTO.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<ProcessDTO> getAllProcesses() {
        return processService.getAllProcess();
    }
}
