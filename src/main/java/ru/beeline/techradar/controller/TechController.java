package ru.beeline.techradar.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.*;
import ru.beeline.techradar.service.TechService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tech")
public class TechController {
    private final TechService techService;

    public TechController(TechService techService) {
        this.techService = techService;
    }

    @GetMapping
    public List<TechAdvancedDTO> getAllTech(@RequestParam(required = false) Boolean actualTech) {
        return techService.getAllTech(actualTech);
    }

    @GetMapping("/subscribed")
    public List<TechSubscribeDTO> getSubscribed() {
        return techService.getTechSubscribed();
    }

    @GetMapping("/product-tech")
    public ResponseEntity<List<ProductDTO>> createRelations() {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getProductTech());
    }

    @PostMapping("/product-relation")
    public ResponseEntity createRelations(@RequestBody PostProductTechDTO tech) {
        techService.createRelations(tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
    public ResponseEntity addTech(@Valid @RequestBody List<PostTechDTO> techs) throws JsonProcessingException {
        techService.addTech(techs);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchTech(@PathVariable Integer id,
                                    @RequestBody TechDTO tech) {
        techService.patchTech(id, tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTech(@PathVariable Integer id) {
        techService.deleteTech(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{tech_id}/version/{version_id}")
    public ResponseEntity deleteTechVersion(@PathVariable(name = "tech_id") Integer techId
            , @PathVariable(name = "version_id") Integer versionId) {
        techService.deleteTechVersion(techId, versionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}