package ru.beeline.techradar.dto;

import lombok.Data;

import java.util.List;

@Data
public class TechAdvancedDTO {
    private Integer id;
    private String label;
    private String descr;
    private String link;
    private String createdDate;
    private String deletedDate;
    private String lastModifiedDate;
    private RingDTO ring;
    private RingDTO sector;
    private List<TechCategoryAdvancedDTO> categories;
    private List<TechVersionDTO> versions;
}
