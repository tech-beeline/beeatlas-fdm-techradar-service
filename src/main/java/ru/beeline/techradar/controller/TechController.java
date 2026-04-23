/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "get all Tech")
    public List<TechAdvancedDTO> getAllTech(@RequestParam(required = false) Boolean actualTech) {
        return techService.getAllTech(actualTech);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение технологии и истории статусов по id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    public ResponseEntity<HistoryTechDTO> getTechById(@PathVariable Integer id) {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getTechById(id));
    }

    @GetMapping("/by-ids")
    @Operation(summary = "Получить технологии по ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<List<TechAdvancedGetDTO>> getTechById(
            @Parameter(description = "Список ID", in = ParameterIn.QUERY, name = "ids")
            @RequestParam(required = true) List<Integer> ids) {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getTechByIds(ids));
    }

    @GetMapping("/subscribed")
    @Operation(summary = "get all Subscribed")
    public List<TechSubscribeDTO> getSubscribed() {
        return techService.getTechSubscribed();
    }

    @GetMapping("/product-tech")
    @Operation(summary = "get Product")
    public ResponseEntity<List<ProductDTO>> createRelations() {
        return ResponseEntity.status(HttpStatus.OK).body(techService.getProductTech());
    }

    @PostMapping("/product-relation")
    @Operation(summary = "Add relation")
    public ResponseEntity<Void> createRelations(@RequestBody PostProductTechDTO tech) {
        techService.createRelations(tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping
    @Operation(summary = "Add tech")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Version updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<IdDTO>> addTech(@Valid @RequestBody List<PostTechDTO> techs) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(techService.addTech(techs));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update tech")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Version updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> patchTech(@PathVariable Integer id,
                                    @RequestBody TechDTO tech) {
        techService.patchTech(id, tech);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tech")
    public ResponseEntity<Void> deleteTech(@PathVariable Integer id) {
        techService.deleteTech(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{tech_id}/version/{version_id}")
    @Operation(summary = "Delete tech version")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Version updated successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteTechVersion(@PathVariable(name = "tech_id") Integer techId
            , @PathVariable(name = "version_id") Integer versionId) {
        techService.deleteTechVersion(techId, versionId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/{tech_id}/version")
    @Operation(summary = "Update tech version")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Version updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request – validation error (missing/invalid fields, version format, overlapping ranges, etc.)"),
            @ApiResponse(responseCode = "403", description = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(responseCode = "404", description = "Not found – tech_id or id_version does not exist"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> createTechVersion(@RequestBody List<PostTechVersionDTO> postTechVersionDTOS,
                                            @PathVariable(name = "tech_id") Integer techId) {
        techService.createTechVersion(postTechVersionDTOS, techId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{tech_id}/version/{id_version}")
    @Operation(summary = "Update tech version")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Version updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request – validation error (missing/invalid fields, version format, overlapping ranges, etc.)"),
            @ApiResponse(responseCode = "403", description = "Forbidden – missing required ADMINISTRATOR role or authorization header"),
            @ApiResponse(responseCode = "404", description = "Not found – tech_id or id_version does not exist"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> patchTechVersion(@RequestBody PostTechVersionDTO postTechVersionDTO,
                                           @PathVariable(name = "tech_id") Integer techId,
                                           @PathVariable(name = "id_version") Integer idVersion) {
        techService.patchTechVersion(postTechVersionDTO, techId, idVersion);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/export/{doc_id}")
    @Operation(summary = "Export document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Export created successfully",
                    content = @Content(schema = @Schema(implementation = TechExportDTO.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable – DB access problem or export error"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TechExportDTO> postTechVersion(@PathVariable(name = "doc_id") Integer docId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(techService.export(docId));
    }
}
