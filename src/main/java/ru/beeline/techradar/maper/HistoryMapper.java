package ru.beeline.techradar.maper;

import org.springframework.stereotype.Component;
import ru.beeline.techradar.domain.HistoryTech;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.dto.HistoryDTO;
import ru.beeline.techradar.dto.RingDTO;
import ru.beeline.techradar.exception.NotFoundException;
import ru.beeline.techradar.repository.RingRepository;

@Component
public class HistoryMapper {

    private final RingMapper ringMapper;
    private final RingRepository ringRepository;

    public HistoryMapper(RingMapper ringMapper, RingRepository ringRepository) {
        this.ringMapper = ringMapper;
        this.ringRepository = ringRepository;
    }

    public HistoryDTO toHistoryDTO(HistoryTech historyTech) {
        Ring historyRing = ringRepository.findById(historyTech.getRingId())
                .orElseThrow(() -> new NotFoundException("Запись в таблице Ring не найдена"));
        RingDTO historyRingDTO = ringMapper.convert(historyRing);

        return HistoryDTO.builder()
                .version(historyTech.getVersion())
                .createdDate(historyTech.getCreatedDate())
                .ring(historyRingDTO)
                .build();
    }
}