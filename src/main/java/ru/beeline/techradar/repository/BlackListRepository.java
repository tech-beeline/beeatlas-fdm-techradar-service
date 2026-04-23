/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.BlackList;

@Repository
public interface BlackListRepository extends JpaRepository<BlackList, Integer> {
    BlackList findBlackListByLabel(String label);
}
