package ru.beeline.techradar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RingDTO {
    private Integer id;
    private String name;
    private Integer order;
}
