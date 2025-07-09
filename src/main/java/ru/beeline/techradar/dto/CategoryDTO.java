package ru.beeline.techradar.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {
    private Integer id;
    private String name;
}