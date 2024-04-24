package ru.beeline.techradar.dto;

import lombok.Data;

import java.util.List;

@Data
public class PutTechCategoryDTO {
    private String joinCategoryName;
    private List<Integer> joinedCategoriesId;
}
