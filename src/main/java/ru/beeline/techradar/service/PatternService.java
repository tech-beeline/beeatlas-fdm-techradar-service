package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.domain.Pattern;
import ru.beeline.techradar.domain.PatternTech;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.IdDTO;
import ru.beeline.techradar.dto.PostPatternDTO;
import ru.beeline.techradar.exception.ForbiddenException;
import ru.beeline.techradar.exception.ValidationException;
import ru.beeline.techradar.repository.PatternRepository;
import ru.beeline.techradar.repository.PatternTechRepository;
import ru.beeline.techradar.repository.TechRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class PatternService {

    private final TechRepository techRepository;

    private final PatternRepository patternRepository;

    private final PatternTechRepository patternTechRepository;

    public PatternService(TechRepository techRepository, PatternRepository patternRepository, PatternTechRepository patternTechRepository) {
        this.techRepository = techRepository;
        this.patternRepository = patternRepository;
        this.patternTechRepository = patternTechRepository;
    }

    public IdDTO createPattern(PostPatternDTO patternDTO, String userRoles) {
        validatePostPatternDTO(patternDTO);
        if (userRoles != null && !userRoles.contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        Pattern pattern = createPattern(patternDTO);
        if (!patternDTO.getRelationsTech().isEmpty()) {
            Set<Integer> techIds = new HashSet<>(patternDTO.getRelationsTech());
            List<Tech> techList = techRepository.findByIdInAndDeletedDateIsNullAndReviewIsTrue(techIds);
            if (techIds.size() != techList.size()) {
                throw new IllegalArgumentException("Указаны несуществующие технологии");
            }
            List<PatternTech> links = techList.stream()
                    .map(tech -> PatternTech.builder()
                            .pattern(pattern)
                            .tech(tech)
                            .build())
                    .collect(Collectors.toList());
            patternTechRepository.saveAll(links);
        }
        return new IdDTO(pattern.getId());
    }

    private Pattern createPattern(PostPatternDTO patternDTO) {
        Pattern pattern = Pattern.builder()
                .code("")
                .name(patternDTO.getName())
                .rule(patternDTO.getRule())
                .isAntiPattern(patternDTO.getIsAntiPattern())
                .createDate(LocalDateTime.now())
                .build();
        Pattern saved = patternRepository.saveAndFlush(pattern);
        String formattedCode = "PATTERN." + String.format("%06d", saved.getId());
        saved.setCode(formattedCode);
        return patternRepository.save(saved);
    }

    private void validatePostPatternDTO(PostPatternDTO patternDTO) {
        StringBuilder errMsg = new StringBuilder();
        if (patternDTO.getName() == null || patternDTO.getName().equals("")) {
            errMsg.append("Отсутствует обязательное поле name");
        }
        if (!errMsg.toString().isEmpty()) {
            throw new ValidationException(errMsg.toString());
        }
    }
}
