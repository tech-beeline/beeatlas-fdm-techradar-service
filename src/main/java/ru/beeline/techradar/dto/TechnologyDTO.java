/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.domain.Sector;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TechnologyDTO {

    private Integer id;
    private String label;
    private Ring ring;
    private Sector sector;
    private Boolean isCritical;
}
