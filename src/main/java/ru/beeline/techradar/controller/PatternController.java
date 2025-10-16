package ru.beeline.techradar.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.techradar.dto.PatternDTO;
import ru.beeline.techradar.dto.PatternGroupDTO;
import ru.beeline.techradar.dto.PostPatternDTO;
import ru.beeline.techradar.dto.PostPatternGroupDTO;
import ru.beeline.techradar.service.PatternService;

import java.util.List;

import static ru.beeline.techradar.utils.Constant.USER_ROLES_HEADER;

@RestController
@RequestMapping("/api/v1")
public class PatternController {

    private final PatternService patternService;

    public PatternController(PatternService patternService) {
        this.patternService = patternService;
    }

    @PostMapping("/pattern")
    @ApiOperation(value = "Создание паттернов проектирования")
    public ResponseEntity createPattern(@RequestBody PostPatternDTO patternDTO,
                                        @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patternService.createPattern(patternDTO, userRoles));
    }

    @PostMapping("/pattern/group")
    @ApiOperation(value = "Создание групп паттернов проектирования")
    public ResponseEntity createPatternGroup(@RequestBody PostPatternGroupDTO patternGroupDTO,
                                             @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(patternService.createPatternGroup(patternGroupDTO, userRoles));
    }

    @PatchMapping("/pattern/group/{id}")
    @ApiOperation(value = "Редактирование групп паттернов проектирования")
    public ResponseEntity editPatternGroup(@PathVariable Integer id,
                                           @RequestBody PostPatternGroupDTO patternGroupDTO,
                                           @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.editPatternGroup(id, patternGroupDTO, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/pattern/{id}")
    @ApiOperation(value = "Удаление паттерна проектирования")
    public ResponseEntity deletePattern(@PathVariable Integer id,
                                        @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.deletePattern(id, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/pattern/group/{id}")
    @ApiOperation(value = "Удаление групп паттернов проектирования")
    public ResponseEntity deletePatternGroup(@PathVariable Integer id,
                                             @RequestHeader(value = USER_ROLES_HEADER, required = false) String userRoles) {
        patternService.deletePatternGroup(id, userRoles);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/patterns")
    @ApiOperation(value = "Просмотр всех паттернов проектирования")
    public ResponseEntity<List<PatternDTO>> allPatterns() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatterns());
    }

    @GetMapping("/patterns/tech/{tech_id}")
    @ApiOperation(value = "Просмотр всех паттернов связанных с технологией")
    public ResponseEntity<List<PatternDTO>> getAllTechnologyPatterns(@PathVariable(name = "tech_id") Integer techId) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllTechnologyPatterns(techId));
    }

    @GetMapping("/pattern/{id}")
    @ApiOperation(value = "Просмотр паттерна по id ")
    public ResponseEntity<PatternDTO> getPatternId(@PathVariable(name = "id") Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternId(id));
    }

    @GetMapping("/patterns/auto-check")
    @ApiOperation(value = "Просмотр паттернов принятых в компании, для которых есть правило автоматической проверки")
    public ResponseEntity<List<PatternDTO>> getPatternsAutoCheck() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getPatternsAutoCheck());
    }

    @GetMapping("/pattern/group")
    @ApiOperation(value = "Просмотр групп паттернов проектирования")
    public ResponseEntity<List<PatternGroupDTO>> getAllPatternsGroup() {
        return ResponseEntity.status(HttpStatus.OK).body(patternService.getAllPatternsGroup());
    }
}
