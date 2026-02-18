/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.maper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.domain.Sector;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Mapper(componentModel = "spring")
public interface TechMapper {

    @Mapping(target = "category", ignore = true)
    @Mapping(source = "descr", target = "description")
    @Mapping(source = "review", target = "review")
    @Mapping(source = "isCritical", target = "isCritical", defaultValue = "false")
    Tech toTech(PostTechDTO techDTO);

    TechSubscribeDTO toTechSubscribeDTO(Tech tech);

    @Mapping(source = "ring", target = "ring")
    @Mapping(source = "review", target = "review")
    @Mapping(source = "isCritical", target = "isCritical")
    TechAdvancedDTO toTechAdvancedDTO(Tech tech);

    @Mapping(source = "ring", target = "ring")
    @Mapping(source = "review", target = "review")
    @Mapping(source = "isCritical", target = "isCritical")
    TechAdvancedGetDTO toTechAdvancedGetDTO(Tech tech);

    @Mapping(source = "createdDate", target = "createdDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "deletedDate", target = "deletedDate", qualifiedByName = "localDateTimeToString")
    @Mapping(source = "lastModifiedDate", target = "lastModifiedDate", qualifiedByName = "localDateTimeToString")
    TechExtensionDTO toTechDTO(Tech tech);

    List<TechExtensionDTO> toTechDTOList(List<Tech> techList);

    CategoryDTO toCategoryDTO(Category category);

    List<CategoryDTO> toCategoryDTOList(List<Category> categories);

    SectorDTO toSectorDTO(Sector sector);

    RingDTO toRingDTO(Ring ring);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}