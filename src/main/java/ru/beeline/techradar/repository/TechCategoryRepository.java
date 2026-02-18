/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.domain.TechCategory;
import java.util.List;

public interface TechCategoryRepository extends JpaRepository<TechCategory, Long> {
    List<TechCategory> findByCategory_IdIn(List<Integer> ids);

    void deleteAllByTech(Tech tech);
}
