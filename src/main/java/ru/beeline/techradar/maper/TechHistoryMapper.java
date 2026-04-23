/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.maper;

import org.springframework.stereotype.Component;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.domain.Sector;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.HistoryTechDTO;
import ru.beeline.techradar.dto.RingDTO;
import ru.beeline.techradar.dto.TechCategoryAdvancedDTO;

import java.util.List;

@Component
public class TechHistoryMapper {
    private final RingMapper ringMapper;

    public TechHistoryMapper(RingMapper ringMapper) {
        this.ringMapper = ringMapper;
    }

    public HistoryTechDTO toHistoryTechDTO(Tech tech, Sector sector, Ring ring, List<TechCategoryAdvancedDTO> categoriesResult) {
        RingDTO ringDTO = ringMapper.convert(ring);
        RingDTO sectorDTO = ringMapper.convert(sector);

        return HistoryTechDTO.builder()
                .id(tech.getId())
                .label(tech.getLabel())
                .description(tech.getDescription())
                .sector(sectorDTO)
                .currentVersion(1)
                .link(tech.getLink())
                .createdDate(tech.getCreatedDate())
                .lastModifiedDate(tech.getLastModifiedDate())
                .deletedDate(tech.getDeletedDate())
                .ring(ringDTO)
                .isCritical(tech.getIsCritical())
                .category(categoriesResult)
                .build();
    }
}
