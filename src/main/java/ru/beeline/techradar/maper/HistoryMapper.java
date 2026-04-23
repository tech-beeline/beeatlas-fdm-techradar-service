/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.maper;

import org.springframework.stereotype.Component;
import ru.beeline.techradar.domain.HistoryTech;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.dto.HistoryDTO;
import ru.beeline.techradar.dto.RingDTO;
import ru.beeline.techradar.exception.NotFoundException;

@Component
public class HistoryMapper {

    private final RingMapper ringMapper;

    public HistoryMapper(RingMapper ringMapper) {
        this.ringMapper = ringMapper;
    }

    public HistoryDTO toHistoryDTO(HistoryTech historyTech) {
        Ring historyRing = historyTech.getRing();
        if (historyRing == null) {
            throw new NotFoundException("Ring не найден для HistoryTech id=" + historyTech.getId());
        }
        RingDTO historyRingDTO = ringMapper.convert(historyRing);
        return HistoryDTO.builder()
                .version(historyTech.getVersion())
                .createdDate(historyTech.getCreatedDate())
                .ring(historyRingDTO)
                .build();
    }
}