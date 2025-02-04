package ru.beeline.techradar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class HistoryTechDTO {

    private Integer id;
    private String label;
    private String description;
    private RingDTO sector;
    private List<TechCategoryAdvancedDTO> category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate lastModifiedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate deletedDate;
    private String link;
    private RingDTO ring;
    @JsonProperty("current_version")
    private Integer currentVersion;
    private List<HistoryDTO> history;
}
