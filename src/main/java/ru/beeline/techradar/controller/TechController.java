package ru.beeline.techradar.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.PostTechDTO;
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
    public ResponseEntity addTech(@RequestBody List<PostTechDTO> tech) {
        techService.addTech(tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping
    public ResponseEntity patchTech(@RequestBody List<TechDTO> tech) {
        techService.patchTech(tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTech(@PathVariable Integer id) {
        techService.deleteTech(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}