/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.service.RingService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rings")
public class RingController {
    private final RingService ringService;

    public RingController(RingService ringService) {
        this.ringService = ringService;
    }

    @GetMapping
    @Operation(summary = "get all Tech")
    public List<Ring> getAllTech() {
        return ringService.getAllRings();
    }

}