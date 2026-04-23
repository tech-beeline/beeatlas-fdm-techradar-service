/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Pattern;
import ru.beeline.techradar.domain.PatternTech;

import java.util.List;

@Repository
public interface PatternTechRepository extends JpaRepository<PatternTech, Integer> {

    List<PatternTech> findAllByTechIdAndPatternDeleteDateIsNull(Integer techId);

    List<PatternTech> findAllByPatternAndTech_DeletedDateIsNullAndTech_ReviewIsTrue(Pattern pattern);

    List<PatternTech> findAllByPatternId(Integer patternId);
}
