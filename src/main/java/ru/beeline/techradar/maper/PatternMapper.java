/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.maper;

import org.springframework.stereotype.Component;
import ru.beeline.techradar.domain.Group;
import ru.beeline.techradar.domain.Pattern;
import ru.beeline.techradar.domain.PatternGroup;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.PatternDTO;
import ru.beeline.techradar.dto.PatternGroupDTO;
import ru.beeline.techradar.dto.TechnologyDTO;
import ru.beeline.techradar.repository.GroupRepository;
import ru.beeline.techradar.repository.PatternGroupRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PatternMapper {

    private final PatternGroupRepository patternGroupRepository;

    public PatternMapper(PatternGroupRepository patternGroupRepository, GroupRepository groupRepository) {
        this.patternGroupRepository = patternGroupRepository;
    }

    public PatternDTO convert(Pattern pattern, List<Tech> techs) {
        return PatternDTO.builder()
                .id(pattern.getId())
                .code(pattern.getCode())
                .name(pattern.getName())
                .groups(buildPatternGroupDTOList(pattern))
                .description(pattern.getDescription())
                .rule(pattern.getRule())
                .dsl(pattern.getDsl())
                .isAntiPattern(pattern.getIsAntiPattern())
                .createDate(pattern.getCreateDate())
                .updateDate(pattern.getUpdateDate())
                .deleteDate(pattern.getDeleteDate())
                .technologies(
                        techs.stream()
                                .map(tech -> TechnologyDTO.builder()
                                        .id(tech.getId())
                                        .label(tech.getLabel())
                                        .ring(tech.getRing())
                                        .sector(tech.getSector())
                                        .isCritical(tech.getIsCritical())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    private List<PatternGroupDTO> buildPatternGroupDTOList(Pattern pattern) {
        List<PatternGroupDTO> result = new ArrayList<>();
        List<PatternGroup> patternGroups = patternGroupRepository.findAllByPatternId(pattern.getId());
        List<Group> groups = patternGroups.stream().map(PatternGroup::getGroup).toList();
        groups.forEach(group -> {
            result.add(PatternGroupDTO.builder()
                    .id(group.getId())
                    .name(group.getName())
                    .build());
        });
        return result;
    }
}
