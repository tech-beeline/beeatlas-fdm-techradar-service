/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
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
    private Boolean isCritical;
    private List<TechCategoryAdvancedDTO> category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime lastModifiedDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime deletedDate;
    private String link;
    private RingDTO ring;
    private Integer currentVersion;
    private List<HistoryDTO> history;
    private List<TechVersionDTO> versions;
}
