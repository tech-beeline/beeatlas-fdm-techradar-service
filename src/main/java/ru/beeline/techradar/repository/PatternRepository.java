/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Pattern;

import java.util.List;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, Integer> {

    List<Pattern> findAllByDeleteDateIsNull();

    List<Pattern> findAllByDeleteDateIsNullAndRuleNotNull();
}
