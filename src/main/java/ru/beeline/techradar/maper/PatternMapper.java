package ru.beeline.techradar.maper;

import org.springframework.stereotype.Component;
import ru.beeline.techradar.domain.Pattern;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.PatternDTO;
import ru.beeline.techradar.dto.TechnologyDTO;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PatternMapper {

    public PatternDTO convert(Pattern pattern, List<Tech> techs) {
        return PatternDTO.builder()
                .id(pattern.getId())
                .code(pattern.getCode())
                .name(pattern.getName())
                .rule(pattern.getRule())
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
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
}
