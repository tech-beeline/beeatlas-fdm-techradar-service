package ru.beeline.techradar.service;

import org.springframework.stereotype.Service;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.domain.TechCategory;
import ru.beeline.techradar.repository.TechCategoryRepository;
import ru.beeline.techradar.repository.TechRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TechService {
    private final TechRepository techRepository;
    private final TechCategoryRepository techCategoryRepository;

    public TechService(TechRepository techRepository, TechCategoryRepository techCategoryRepository) {
        this.techRepository = techRepository;
        this.techCategoryRepository = techCategoryRepository;
    }

    public List<Tech> getAllTech() {
        return techRepository.findAll();
    }

    public List<Tech> getAllTechByCategory(List<Integer> ids) {
        return techCategoryRepository.findByCategory_IdIn(ids)
                .stream().map(TechCategory::getTech).collect(Collectors.toList());
    }
}