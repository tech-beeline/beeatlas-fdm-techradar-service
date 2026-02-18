/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.dto.ProcessDTO;
import ru.beeline.techradar.service.ProcessService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/processes")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping
    @ApiOperation(value = "get all processes")
    public List<ProcessDTO> getAllTech() {
        return processService.getAllProcess();
    }
}
