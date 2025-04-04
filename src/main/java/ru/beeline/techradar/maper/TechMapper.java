package ru.beeline.techradar.maper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.PostTechDTO;
import ru.beeline.techradar.dto.TechAdvancedDTO;
import ru.beeline.techradar.dto.TechSubscribeDTO;

@Mapper
public interface TechMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(source = "descr", target = "description")
    @Mapping(source = "review", target = "review")
    Tech toTech(PostTechDTO techDTO);

    TechSubscribeDTO toTechSubscribeDTO(Tech tech);

    @Mapping(source = "ring", target = "ring")
    @Mapping(source = "review", target = "review")
    TechAdvancedDTO toTechAdvancedDTO(Tech tech);
}