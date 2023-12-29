package ru.beeline.techradar.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.service.TechService;

import java.util.List;

@RestController
@RequestMapping("/tech")
public class TechController {
    private final TechService techService;

    public TechController(TechService techService) {
        this.techService = techService;
    }

    @GetMapping
    public List<Tech> getAllTech() {
        return techService.getAllTech();
    }
}