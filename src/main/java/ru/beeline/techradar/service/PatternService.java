package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.domain.Pattern;
import ru.beeline.techradar.domain.PatternTech;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.IdDTO;
import ru.beeline.techradar.dto.PatternDTO;
import ru.beeline.techradar.dto.PostPatternDTO;
import ru.beeline.techradar.exception.ForbiddenException;
import ru.beeline.techradar.exception.NotFoundException;
import ru.beeline.techradar.exception.ValidationException;
import ru.beeline.techradar.maper.PatternMapper;
import ru.beeline.techradar.repository.PatternRepository;
import ru.beeline.techradar.repository.PatternTechRepository;
import ru.beeline.techradar.repository.TechRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class PatternService {

    private final PatternMapper patternMapper;
    private final TechRepository techRepository;

    private final PatternRepository patternRepository;

    private final PatternTechRepository patternTechRepository;

    public PatternService(PatternMapper patternMapper, TechRepository techRepository, PatternRepository patternRepository,
                          PatternTechRepository patternTechRepository) {
        this.patternMapper = patternMapper;
        this.techRepository = techRepository;
        this.patternRepository = patternRepository;
        this.patternTechRepository = patternTechRepository;
    }

    public IdDTO createPattern(PostPatternDTO patternDTO, String userRoles) {
        validatePostPatternDTO(patternDTO);
        validateAdminRole(userRoles);
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

    private void validateAdminRole(String userRoles) {
        if (userRoles != null && !userRoles.contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
    }

    public void deletePattern(Integer id, String userRoles) {
        validateAdminRole(userRoles);
        Pattern pattern = patternRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found: Pattern с данным id не найден."));
        if (pattern.getDeleteDate() == null) {
            pattern.setDeleteDate(LocalDateTime.now());
            patternRepository.save(pattern);
        }
    }

    public List<PatternDTO> getAllPatterns() {
        List<Pattern> patterns = patternRepository.findAllByDeleteDateIsNull();
        Map<Integer, List<Integer>> patternIdToTechIds = new HashMap<>();
        for (Pattern pattern : patterns) {
            Integer patternId = pattern.getId();
            List<Integer> techIds = pattern.getChildren().stream()
                    .map(pt -> pt.getTech().getId())
                    .collect(Collectors.toList());
            patternIdToTechIds.put(patternId, techIds);
        }
        Set<Integer> allTechIds = patternIdToTechIds.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<Tech> techList = techRepository.findByIdInAndDeletedDateIsNullAndReviewIsTrue(allTechIds);
        Map<Integer, Tech> techMap = techList.stream()
                .collect(Collectors.toMap(Tech::getId, Function.identity()));
        List<PatternDTO> result = new ArrayList<>();
        for (Pattern pattern : patterns) {
            List<Integer> techIds = patternIdToTechIds.getOrDefault(pattern.getId(), Collections.emptyList());
            List<Tech> techs = techIds.stream()
                    .map(techMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            result.add(patternMapper.convert(pattern, techs));
        }
        result.sort(Comparator.comparingInt(PatternDTO::getId));
        return result;
    }
}
