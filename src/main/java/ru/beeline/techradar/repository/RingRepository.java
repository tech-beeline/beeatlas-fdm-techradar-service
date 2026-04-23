/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Ring;

import java.util.Optional;

@Repository
public interface RingRepository extends JpaRepository<Ring, Integer> {
    Optional<Ring> findByOrder(Integer ring);
}
