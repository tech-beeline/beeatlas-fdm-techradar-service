package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
