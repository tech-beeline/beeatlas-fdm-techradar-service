/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Group;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    int countByParentId(Integer parentId);

    @Query("SELECT g.id FROM Group g WHERE g.id IN :ids")
    List<Integer> findExistingIds(@Param("ids") List<Integer> ids);
}
