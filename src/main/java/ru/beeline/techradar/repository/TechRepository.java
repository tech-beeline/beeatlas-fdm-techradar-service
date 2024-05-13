package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Tech;

import java.util.List;

@Repository
public interface TechRepository extends JpaRepository<Tech, Integer> {
    List<Tech> findAllByLabelIn(List<String> labels);

    List<Tech> findAllByDeletedDateIsEmpty();
}
