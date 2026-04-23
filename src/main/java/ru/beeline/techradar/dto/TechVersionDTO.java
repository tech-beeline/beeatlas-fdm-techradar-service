/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TechVersionDTO {
    private Integer id;
    private String versionStart;
    private String versionEnd;
    private String createdDate;
    private String deletedDate;
    private String lastModifiedDate;
    private RingDTO ring;
}
