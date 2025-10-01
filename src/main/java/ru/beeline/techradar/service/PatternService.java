package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.domain.Group;
import ru.beeline.techradar.domain.Pattern;
import ru.beeline.techradar.domain.PatternTech;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.IdDTO;
import ru.beeline.techradar.dto.PatternDTO;
import ru.beeline.techradar.dto.PostPatternDTO;
import ru.beeline.techradar.dto.PostPatternGroupDTO;
import ru.beeline.techradar.exception.ForbiddenException;
import ru.beeline.techradar.exception.NotFoundException;
import ru.beeline.techradar.exception.ValidationException;
import ru.beeline.techradar.maper.PatternMapper;
import ru.beeline.techradar.repository.*;

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
    private final GroupRepository groupRepository;
    private final PatternGroupRepository patternGroupRepository;

    private final PatternRepository patternRepository;

    private final PatternTechRepository patternTechRepository;

    public PatternService(PatternMapper patternMapper,
                          TechRepository techRepository,
                          PatternRepository patternRepository,
                          PatternTechRepository patternTechRepository,
                          PatternGroupRepository patternGroupRepository,
                          GroupRepository groupRepository) {
        this.patternMapper = patternMapper;
        this.techRepository = techRepository;
        this.patternRepository = patternRepository;
        this.patternTechRepository = patternTechRepository;
        this.groupRepository = groupRepository;
        this.patternGroupRepository = patternGroupRepository;
    }

    public IdDTO createPattern(PostPatternDTO patternDTO, String userRoles) {
        validatePostPatternDTO(patternDTO);
        validateAdminRole(userRoles);
        Pattern pattern = createPattern(patternDTO);
        if (patternDTO.getRelationsTech() != null && !patternDTO.getRelationsTech().isEmpty()) {
            Set<Integer> techIds = new HashSet<>(patternDTO.getRelationsTech());
            List<Tech> techList = techRepository.findByIdInAndDeletedDateIsNullAndReviewIsTrue(techIds);
            if (techIds.size() != techList.size()) {
                throw new IllegalArgumentException("Указаны несуществующие технологии");
            }
            List<PatternTech> links = techList.stream()
                    .map(tech -> PatternTech.builder().pattern(pattern).tech(tech).build())
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
                .isAntiPattern(patternDTO.getIsAntiPattern() != null && patternDTO.getIsAntiPattern())
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
        Pattern pattern = patternRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found: Pattern с данным id не найден."));
        if (pattern.getDeleteDate() == null) {
            pattern.setDeleteDate(LocalDateTime.now());
            patternRepository.save(pattern);
        }
    }

    public List<PatternDTO> getAllPatterns() {
        List<Pattern> patterns = patternRepository.findAllByDeleteDateIsNull();
        return mapPatternsToDTOs(patterns);
    }

    private List<PatternDTO> mapPatternsToDTOs(List<Pattern> patterns) {
        Set<Integer> allTechIds = patterns.stream()
                .flatMap(pattern -> pattern.getChildren().stream())
                .map(PatternTech::getTech)
                .filter(Objects::nonNull)
                .map(Tech::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<Tech> techList = techRepository.findByIdInAndDeletedDateIsNullAndReviewIsTrue(allTechIds);
        Map<Integer, Tech> techMap = techList.stream().collect(Collectors.toMap(Tech::getId, Function.identity()));
        List<PatternDTO> result = new ArrayList<>();
        for (Pattern pattern : patterns) {
            List<Tech> techs = pattern.getChildren()
                    .stream()
                    .map(PatternTech::getTech)
                    .filter(Objects::nonNull)
                    .map(tech -> techMap.get(tech.getId()))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            PatternDTO dto = patternMapper.convert(pattern, techs);
            result.add(dto);
        }
        if (!result.isEmpty()) {
            result.sort(Comparator.comparingInt(PatternDTO::getId));
        }
        return result;
    }

    public List<PatternDTO> getAllTechnologyPatterns(Integer techId) {
        techRepository.findByIdAndDeletedDateIsNullAndReviewIsTrue(techId)
                .orElseThrow(() -> new NotFoundException("Указана несуществующая технология"));
        List<Pattern> patterns = patternTechRepository.findAllByTechIdAndPatternDeleteDateIsNull(techId)
                .stream()
                .map(PatternTech::getPattern)
                .collect(Collectors.toList());
        return mapPatternsToDTOs(patterns);
    }

    public PatternDTO getPatternId(Integer id) {
        Pattern pattern = patternRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Паттерн с данным id не найден"));
        List<Tech> techList = patternTechRepository.findAllByPatternAndTech_DeletedDateIsNullAndTech_ReviewIsTrue(
                pattern).stream().map(PatternTech::getTech).collect(Collectors.toList());
        return patternMapper.convert(pattern, techList);
    }

    public List<PatternDTO> getPatternsAutoCheck() {
        return mapPatternsToDTOs(patternRepository.findAllByDeleteDateIsNullAndRuleNotNull());
    }

    public IdDTO createPatternGroup(PostPatternGroupDTO patternGroupDTO, String userRoles) {
        if (!userRoles.contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        if (patternGroupDTO.getName() == null || patternGroupDTO.getName().equals("")) {
            throw new IllegalArgumentException("name is empty");
        }
        Group parentGroup = null;
        if (patternGroupDTO.getParentId() != null) {
            parentGroup = groupRepository.findById(patternGroupDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException("Родитель с идентификатором " + patternGroupDTO.getParentId() + " не найден"));
        }
        return IdDTO.builder()
                .id(groupRepository.save(Group.builder()
                                                 .name(patternGroupDTO.getName())
                                                 .parent(parentGroup)
                                                 .parentId(patternGroupDTO.getParentId())
                                                 .build()).getId())
                .build();
    }

    public void deletePatternGroup(Integer id, String userRoles) {
        if (!userRoles.contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Группа с идентификатором " + id + " не найдена"));
        if (patternGroupRepository.countPatternGroupByGroupId(id) > 0) {
            throw new RuntimeException("Группа с идентификатором " + id + " имеет паттерны");
        }
        if (groupRepository.countByParentId(id) > 0) {
            throw new RuntimeException("Группа с идентификатором " + id + " имеет дочерние элементы");
        }
        groupRepository.delete(group);
    }

    public void editPatternGroup(Integer id, PostPatternGroupDTO patternGroupDTO, String userRoles) {
        if (!userRoles.contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Группа с идентификатором " + patternGroupDTO.getParentId() + " не найдена"));
        if (patternGroupDTO.getName() != null) {
            group.setName(patternGroupDTO.getName());
        }
        if (patternGroupDTO.getParentId() != null) {
            Group parentGroup = null;
            if (patternGroupDTO.getParentId() != null) {
                parentGroup = groupRepository.findById(patternGroupDTO.getParentId())
                        .orElseThrow(() -> new RuntimeException("Родитель с идентификатором " + patternGroupDTO.getParentId() + " не найден"));
            }

            group.setParentId(parentGroup.getId());
        }
        groupRepository.save(group);
    }
}
