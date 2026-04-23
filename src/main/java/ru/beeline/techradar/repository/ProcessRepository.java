/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Process;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Integer> {

    Process findByNameProcessIgnoreCase(String projLang);
}
