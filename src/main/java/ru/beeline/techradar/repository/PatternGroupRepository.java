/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.PatternGroup;

import java.util.List;

@Repository
public interface PatternGroupRepository extends JpaRepository<PatternGroup, Integer> {
    int countPatternGroupByGroupId(Integer groupId);

    List<PatternGroup> findAllByPatternId(Integer id);
}
