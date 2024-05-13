package ru.beeline.techradar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.beeline.techradar.domain.Category;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TechDTO {

    private Integer id;

    private String label;

    private String descr;

    @JsonProperty("sector_id")
    private Integer sectorId;

    private List<TechCategoryDTO> categories;

    private String link;

    @JsonProperty("ring_id")
    private Integer ringId;
}
