/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TechDTO {

    private Integer id;

    private String label;

    private String descr;

    @JsonProperty("sector_id")
    private Integer sectorId;

    private List<TechCategoryDTO> categories;

    private String link;

    @JsonProperty("ring_id")
    private Integer ringId;

    private Boolean review;

    private Boolean isCritical;
}
