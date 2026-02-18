/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchPatternDTO {

    private String name;
    private String description;
    private String rule;
    private String dsl;
    private Boolean isAntiPattern;
    private List<Integer> groups;
    private List<Integer> relationsTech;
}
