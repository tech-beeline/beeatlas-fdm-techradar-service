package ru.beeline.techradar.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ProductTechDTO {

    private Integer id;
    private String label;
}
