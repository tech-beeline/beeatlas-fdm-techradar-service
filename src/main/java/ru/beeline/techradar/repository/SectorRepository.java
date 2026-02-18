/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Sector;

import java.util.Optional;

@Repository
public interface SectorRepository extends JpaRepository<Sector, Integer> {
    Optional<Sector> findByOrder(Integer ring);
}
