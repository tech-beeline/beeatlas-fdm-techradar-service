/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.maper;

import org.springframework.stereotype.Component;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.domain.Sector;
import ru.beeline.techradar.dto.RingDTO;

@Component
public class RingMapper {

    public RingDTO convert(Ring ring) {
        return RingDTO.builder()
                .id(ring.getId())
                .name(ring.getName())
                .order(ring.getOrder())
                .build();
    }

    public RingDTO convert(Sector sector) {
        return RingDTO.builder()
                .id(sector.getId())
                .name(sector.getName())
                .order(sector.getOrder())
                .build();
    }
}
