package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Pattern;

@Repository
public interface PatternRepository extends JpaRepository<Pattern, Integer> {
}
