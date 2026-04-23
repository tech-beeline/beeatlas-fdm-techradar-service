/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.techradar.domain.TechVersion;

import java.util.List;

public interface TechVersionRepository extends JpaRepository<TechVersion, Integer> {
    TechVersion findByTechIdAndIdAndDeletedDateIsNull(Integer techId, Integer id);
    List<TechVersion> findAllByTechIdAndDeletedDateIsNull(Integer techId);
}
