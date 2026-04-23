/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.TechBlProduct;

@Repository
public interface TechBlProductRepository extends JpaRepository<TechBlProduct, Integer> {
    TechBlProduct findByCmdbCodeAndTechBlId(String cmdbCode, Integer techBlId);

}
