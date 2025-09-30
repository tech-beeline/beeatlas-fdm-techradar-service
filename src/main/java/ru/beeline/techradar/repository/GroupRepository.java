package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    int countByParentId(Integer parentId);
}
