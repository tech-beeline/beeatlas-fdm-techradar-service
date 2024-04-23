package ru.beeline.techradar.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.TechDTO;
import ru.beeline.techradar.service.TechService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tech")
public class TechController {
    private final TechService techService;

    public TechController(TechService techService) {
        this.techService = techService;
    }

    @GetMapping
    public List<Tech> getAllTech() {
        return techService.getAllTech();
    }

    @PostMapping
    public ResponseEntity addTech(@RequestBody List<TechDTO> tech) {
        techService.addTech(tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}