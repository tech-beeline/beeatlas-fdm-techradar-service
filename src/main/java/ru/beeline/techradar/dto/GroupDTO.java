package ru.beeline.techradar.dto;

import lombok.*;
import ru.beeline.techradar.domain.Group;

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
