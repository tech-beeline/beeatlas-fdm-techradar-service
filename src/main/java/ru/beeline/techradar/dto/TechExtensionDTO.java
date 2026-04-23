/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TechExtensionDTO {
    private Integer id;
    private String label;
    private String description;
    private List<CategoryDTO> category;
    private String createdDate;
    private String lastModifiedDate;
    private String deletedDate;
    private String link;
    private RingDTO ring;
    private SectorDTO sector;
}
