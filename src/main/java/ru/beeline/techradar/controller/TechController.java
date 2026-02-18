/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    @ApiOperation(value = "get all Tech")
    public List<TechAdvancedDTO> getAllTech(@RequestParam(required = false) Boolean actualTech) {
        return techService.getAllTech(actualTech);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Получение технологии и истории статусов по id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Not found")
    })
    public ResponseEntity<HistoryTechDTO> getTechById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getTechById(id));
    }

    @GetMapping("/by-ids")
    @ApiOperation(value = "Получить технологии по ID")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad Request")
    })
    @ApiImplicitParam(name = "ids", value = "Список ID", dataType = "integer", paramType = "query")
    public ResponseEntity<List<TechAdvancedGetDTO>> getTechById(@RequestParam(required = true) List<Integer> ids) {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getTechByIds(ids));
    }

    @GetMapping("/subscribed")
    @ApiOperation(value = "get all Subscribed")
    public List<TechSubscribeDTO> getSubscribed() {
        return techService.getTechSubscribed();
    }

    @GetMapping("/product-tech")
    @ApiOperation(value = "get Product")
    public ResponseEntity<List<ProductDTO>> createRelations() {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getProductTech());
    }

    @PostMapping("/product-relation")
    @ApiOperation(value = "Add relation")
    public ResponseEntity<Void> createRelations(@RequestBody PostProductTechDTO tech) {
        techService.createRelations(tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
    @ApiOperation(value = "Add tech")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Version updated successfully"),
            @ApiResponse(code = 403, message = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<List<IdDTO>> addTech(@Valid @RequestBody List<PostTechDTO> techs) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(techService.addTech(techs));
    }

    @PatchMapping("/{id}")
    @ApiOperation(value = "Update tech")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Version updated successfully"),
            @ApiResponse(code = 403, message = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> patchTech(@PathVariable Integer id,
                                    @RequestBody TechDTO tech) {
        techService.patchTech(id, tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete tech")
    public ResponseEntity<Void> deleteTech(@PathVariable Integer id) {
        techService.deleteTech(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{tech_id}/version/{version_id}")
    @ApiOperation(value = "Delete tech version")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Version updated successfully"),
            @ApiResponse(code = 403, message = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(code = 500, message = "Internal server error")
    })    public ResponseEntity<Void> deleteTechVersion(@PathVariable(name = "tech_id") Integer techId
            , @PathVariable(name = "version_id") Integer versionId) {
        techService.deleteTechVersion(techId, versionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{tech_id}/version")
    @ApiOperation(value = "Update tech version")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Version updated successfully"),
            @ApiResponse(code = 400, message = "Bad request – validation error (missing/invalid fields, version format, overlapping ranges, etc.)"),
            @ApiResponse(code = 403, message = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(code = 404, message = "Not found – tech_id or id_version does not exist"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> createTechVersion(@RequestBody List<PostTechVersionDTO> postTechVersionDTOS,
                                            @PathVariable(name = "tech_id") Integer techId) {
        techService.createTechVersion(postTechVersionDTOS, techId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{tech_id}/version/{id_version}")
    @ApiOperation(value = "Update tech version")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Version updated successfully"),
            @ApiResponse(code = 400, message = "Bad request – validation error (missing/invalid fields, version format, overlapping ranges, etc.)"),
            @ApiResponse(code = 403, message = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(code = 404, message = "Not found – tech_id or id_version does not exist"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<Void> patchTechVersion(@RequestBody PostTechVersionDTO postTechVersionDTO,
                                           @PathVariable(name = "tech_id") Integer techId,
                                           @PathVariable(name = "id_version") Integer idVersion) {
        techService.patchTechVersion(postTechVersionDTO, techId, idVersion);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/export/{doc_id}")
    @ApiOperation(value = "Export document")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Export created successfully",
                    response = TechExportDTO.class),   // doc_id returned inside DTO
            @ApiResponse(code = 503, message = "Service unavailable – DB access problem or export error"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    public ResponseEntity<TechExportDTO> postTechVersion(@PathVariable(name = "doc_id") Integer docId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(techService.export(docId));
    }
}