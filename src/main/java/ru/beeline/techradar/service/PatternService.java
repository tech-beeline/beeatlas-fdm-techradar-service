/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.domain.Group;
import ru.beeline.techradar.domain.Pattern;
import ru.beeline.techradar.domain.PatternGroup;
import ru.beeline.techradar.domain.PatternTech;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.dto.GroupDTO;
import ru.beeline.techradar.dto.IdDTO;
import ru.beeline.techradar.dto.PatchPatternDTO;
import ru.beeline.techradar.dto.PatternDTO;
import ru.beeline.techradar.dto.PatternGroupDTO;
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
        if (patternDTO.getGroups() != null && !patternDTO.getGroups().isEmpty()) {
            Set<Integer> groupsSet = new HashSet<>(patternDTO.getGroups());
            List<Group> groups = groupRepository.findAllById(new ArrayList<>(groupsSet));
            if (groupsSet.size() != groups.size()) {
                throw new IllegalArgumentException("Указаны несуществующие категории");
            }
            List<PatternGroup> patternGroups = groups.stream().map(group -> PatternGroup.builder().pattern(pattern)
                    .group(group).build()).toList();
            patternGroupRepository.saveAll(patternGroups);
        }
        return new IdDTO(pattern.getId());
    }

    private Pattern createPattern(PostPatternDTO patternDTO) {
        Pattern pattern = Pattern.builder()
                .dsl(patternDTO.getDsl())
                .description(patternDTO.getDescription())
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
        if (patternDTO.getGroups() == null) {
            errMsg.append("Отсутствует обязательный список groups");
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
        validateAdminRole(userRoles);
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
        validateAdminRole(userRoles);
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
        validateAdminRole(userRoles);
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

    public List<PatternGroupDTO> getAllPatternsGroup() {
        List<PatternGroupDTO> result = new ArrayList<>();
        List<Group> patternGroups = groupRepository.findAll();
        if (!patternGroups.isEmpty()) {
            patternGroups.forEach(patternGroup -> {
                result.add(PatternGroupDTO.builder()
                        .id(patternGroup.getId())
                        .name(patternGroup.getName()).build());
            });
        }
        return result;
    }

    public List<GroupDTO> getTreePatternsGroup() {
        List<Group> groups = groupRepository.findAll();
        Map<Integer, GroupDTO> dtoMap = groups.stream()
                .collect(Collectors.toMap(
                        Group::getId,
                        group -> GroupDTO.builder()
                                .id(group.getId())
                                .name(group.getName())
                                .children(new ArrayList<>())
                                .build()
                ));
        List<GroupDTO> result = new ArrayList<>();
        for (Group group : groups) {
            GroupDTO dto = dtoMap.get(group.getId());
            if (group.getParentId() == null) {
                result.add(dto);
            } else {
                GroupDTO parent = dtoMap.get(group.getParentId());
                if (parent != null) {
                    parent.getChildren().add(dto);
                }
            }
        }
        return result;
    }

    public void editPattern(Integer id, PatchPatternDTO patternDTO, String userRoles) {
        validateAdminRole(userRoles);
        Pattern updatePattern = patternRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Паттерн с данным id не найден"));
        boolean hasChanges = updatePattern(patternDTO, updatePattern);
        boolean updateRelationsTechs = false;
        boolean updateRelationsGroups = false;
        if (patternDTO.getRelationsTech() != null && !patternDTO.getRelationsTech().isEmpty()) {
            updateRelationsTechs = updateRelationsTechs(id, patternDTO.getRelationsTech(), updatePattern);
        }
        if (patternDTO.getGroups() != null && !patternDTO.getGroups().isEmpty()) {
            updateRelationsGroups = updateRelationsGroups(id, patternDTO.getGroups(), updatePattern);
        }
        if (hasChanges || updateRelationsGroups || updateRelationsTechs) {
            updatePattern.setUpdateDate(LocalDateTime.now());
            patternRepository.save(updatePattern);
        }
    }

    private boolean updatePattern(PatchPatternDTO patternDTO, Pattern pattern) {
        boolean updateDate = false;
        if (patternDTO.getIsAntiPattern() != null && !patternDTO.getIsAntiPattern().equals(pattern.getIsAntiPattern())) {
            pattern.setIsAntiPattern(patternDTO.getIsAntiPattern());
            updateDate = true;
        }
        if (patternDTO.getDsl() != null && !patternDTO.getDsl().equals(pattern.getDsl())) {
            pattern.setDsl(patternDTO.getDsl());
            updateDate = true;
        }
        if (patternDTO.getName() != null && !patternDTO.getName().equals(pattern.getName())) {
            pattern.setName(patternDTO.getName());
            updateDate = true;
        }
        if (patternDTO.getRule() != null && !patternDTO.getRule().equals(pattern.getRule())) {
            pattern.setRule(patternDTO.getRule());
            updateDate = true;
        }
        if (patternDTO.getDescription() != null && !patternDTO.getDescription().equals(pattern.getDescription())) {
            pattern.setDescription(patternDTO.getDescription());
            updateDate = true;
        }
        return updateDate;
    }

    private boolean updateRelationsTechs(Integer id, List<Integer> techIds, Pattern pattern) {
        boolean updateDate;
        List<Tech> techList = techRepository.findAllByIdInAndDeletedDateIsNull(techIds);
        List<Tech> filteredPatterns = techList.stream().filter(Tech::getReview).toList();
        if (techList.size() != techIds.size() || filteredPatterns.size() != techIds.size()) {
            throw new IllegalArgumentException("Указаны несуществующие технологии");
        }
        List<PatternTech> patternTeches = patternTechRepository.findAllByPatternId(id);
        updateDate = deletedPatternTeches(patternTeches, techIds);
        Set<Integer> existingTechIdsInPattern = patternTeches.stream()
                .map(pt -> pt.getTech().getId())
                .collect(Collectors.toSet());
        List<PatternTech> savePatternTeches = techIds.stream()
                .filter(techId -> !existingTechIdsInPattern.contains(techId))
                .map(techId -> {
                    Tech tech = techRepository.getReferenceById(techId);
                    return PatternTech.builder()
                            .pattern(pattern)
                            .tech(tech)
                            .build();
                })
                .collect(Collectors.toList());
        if (!savePatternTeches.isEmpty()) {
            patternTechRepository.saveAll(savePatternTeches);
            updateDate = true;
        }
        return updateDate;
    }

    private boolean deletedPatternTeches(List<PatternTech> patternTeches, List<Integer> techIds) {
        boolean updateDate = false;
        List<PatternTech> deletedPatternTeches = patternTeches.stream()
                .filter(obj -> !techIds.contains(obj.getTech().getId()))
                .toList();
        if (!deletedPatternTeches.isEmpty()) {
            patternTechRepository.deleteAll(deletedPatternTeches);
            updateDate = true;
        }
        return updateDate;
    }

    private boolean updateRelationsGroups(Integer id, List<Integer> groupIds, Pattern pattern) {
        boolean updateDate;
        List<PatternGroup> patternGroups = patternGroupRepository.findAllByPatternId(id);
        updateDate = deletedPatternGroups(patternGroups, groupIds);
        List<Integer> existingIds = groupRepository.findExistingIds(groupIds);
        if (existingIds.size() != groupIds.size()) {
            throw new IllegalArgumentException("Указаны несуществующие категории");
        }
        Set<Integer> existingGroupIdsInPattern = patternGroups.stream()
                .map(pg -> pg.getGroup().getId())
                .collect(Collectors.toSet());
        List<PatternGroup> savePatternGroups = groupIds.stream()
                .filter(groupId -> !existingGroupIdsInPattern.contains(groupId))
                .map(groupId -> {
                    Group group = groupRepository.getReferenceById(groupId);
                    return PatternGroup.builder()
                            .pattern(pattern)
                            .group(group)
                            .build();
                })
                .toList();
        if (!savePatternGroups.isEmpty()) {
            patternGroupRepository.saveAll(savePatternGroups);
            updateDate = true;
        }
        return updateDate;
    }

    private boolean deletedPatternGroups(List<PatternGroup> patternGroups, List<Integer> groupIds) {
        boolean updateDate = false;
        List<PatternGroup> deletedPatternGroups = patternGroups.stream()
                .filter(obj -> !groupIds.contains(obj.getGroup().getId()))
                .toList();
        if (!deletedPatternGroups.isEmpty()) {
            patternGroupRepository.deleteAll(deletedPatternGroups);
            updateDate = true;
        }
        return updateDate;
    }
}

