/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.domain.Sector;
import ru.beeline.techradar.service.SectorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sectors")
public class SectorController {

    private final SectorService sectorService;

    public SectorController(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    @GetMapping
    @ApiOperation(value = "get all Sectors")
    public List<Sector> getAllTech() {
        return sectorService.getAllSectors();
    }
}
