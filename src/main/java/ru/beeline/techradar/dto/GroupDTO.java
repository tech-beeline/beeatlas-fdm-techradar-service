/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {

    private Integer id;
    private String name;
    private List<GroupDTO> children;
}
