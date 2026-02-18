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
public class PostPatternDTO {

    private String dsl;
    private String name;
    private String rule;
    private String description;
    private Boolean isAntiPattern;
    private List<Integer> relationsTech;
    private List<Integer> groups;
}
