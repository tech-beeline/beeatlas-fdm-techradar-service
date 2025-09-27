package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Group;
import ru.beeline.techradar.domain.PatternGroup;

import java.util.List;

@Repository
public interface PatternGroupRepository extends JpaRepository<PatternGroup, Integer> {
    int countPatternGroupByGroup(Group group);

}
