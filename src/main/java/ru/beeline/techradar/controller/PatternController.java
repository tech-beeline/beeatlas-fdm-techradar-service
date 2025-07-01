package ru.beeline.techradar.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.techradar.dto.PatternDTO;
import ru.beeline.techradar.dto.PostPatternDTO;
import ru.beeline.techradar.service.PatternService;

import java.util.List;

import static ru.beeline.techradar.utils.Constant.USER_ROLES_HEADER;

@RestController
@RequestMapping("/api/v1/pattern")
public class PatternController {

    private final PatternService patternService;

    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @PostMapping
    @ApiOperation(value = "Создания паттернов проектирования")
    public ResponseEntity createPattern(@RequestBody PostPatternDTO patternDTO,
                                        @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patternService.createPattern(patternDTO, userRoles));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Удаление паттерна проектирования")
    public ResponseEntity deletePattern(@PathVariable Integer id,
                                        @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.deletePattern(id, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping
    @ApiOperation(value = "Просмотр всех паттернов проектирования")
    public ResponseEntity<List<PatternDTO>> allPatterns() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatterns());
    }

    @GetMapping("/tech/{tech_id}")
    @ApiOperation(value = "Просмотр всех паттернов связанных с технологией")
    public ResponseEntity<List<PatternDTO>> getAllTechnologyPatterns(@PathVariable (name = "tech_id") Integer  techId ) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllTechnologyPatterns(techId));
    }
}
