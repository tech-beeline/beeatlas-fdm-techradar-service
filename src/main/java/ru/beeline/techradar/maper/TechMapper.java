package ru.beeline.techradar.maper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.PostTechDTO;
import ru.beeline.techradar.dto.TechDTO;

@Mapper
public interface TechMapper {
    TechMapper INSTANCE = Mappers.getMapper(TechMapper.class);

    @Mapping(target = "category", ignore = true)
    @Mapping(source = "descr", target = "description")
    Tech toTech(PostTechDTO techDTO);

    TechDTO toDTO(Tech tech);
}