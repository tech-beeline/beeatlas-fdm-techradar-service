package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.beeline.techradar.domain.HistoryTech;

import java.util.List;

public interface HistoryTechRepository extends JpaRepository<HistoryTech, Integer> {
    List<HistoryTech> findByRefId(Integer refId);

    List<HistoryTech> findByRefIdIn(List<Integer> techIds);
}
