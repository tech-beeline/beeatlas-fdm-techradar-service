package ru.beeline.techradar.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.beeline.techradar.domain.Tech;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechRepository extends JpaRepository<Tech, Integer> {
    Tech findByLabel(String label);

    List<Tech> findAllByLabelIn(List<String> labels);

    List<Tech> findAllByDeletedDateIsNullAndReviewIsTrue();

    List<Tech> findAllByIdInAndDeletedDateIsNull(List<Integer> ids);

    List<Tech> findAllByReviewIsTrueAndDeletedDateIsNull();

    Optional <Tech> findByIdAndDeletedDateIsNullAndReviewIsTrue(Integer id);

}
