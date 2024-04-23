package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.domain.TechCategory;
import ru.beeline.techradar.dto.TechDTO;
import ru.beeline.techradar.maper.TechMapper;
import ru.beeline.techradar.repository.CategoryRepository;
import ru.beeline.techradar.repository.RingRepository;
import ru.beeline.techradar.repository.SectorRepository;
import ru.beeline.techradar.repository.TechCategoryRepository;
import ru.beeline.techradar.repository.TechRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class TechService {
    private final TechRepository techRepository;
    private final TechCategoryRepository techCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SectorRepository sectorRepository;
    private final RingRepository ringRepository;

    private final TechMapper techMapper;

    public TechService(TechRepository techRepository,
                       TechCategoryRepository techCategoryRepository,
                       TechMapper techMapper,
                       CategoryRepository categoryRepository,
                       SectorRepository sectorRepository,
                       RingRepository ringRepository) {
        this.techRepository = techRepository;
        this.techCategoryRepository = techCategoryRepository;
        this.techMapper = techMapper;
        this.categoryRepository = categoryRepository;
        this.sectorRepository = sectorRepository;
        this.ringRepository = ringRepository;
    }

    public List<Tech> getAllTech() {
        return techRepository.findAll();
    }

    public List<Tech> getAllTechByCategory(List<Integer> ids) {
        return techCategoryRepository.findByCategory_IdIn(ids)
                .stream().map(TechCategory::getTech).collect(Collectors.toList());
    }

    public void addTech(List<TechDTO> techDTOs) {
        List<String> labels = techDTOs.stream().map(TechDTO::getLabel).collect(Collectors.toList());
        List<Tech> existTechList = techRepository.findAllByLabelIn(labels);
        List<String> noExistTechLabelList = existTechList.stream().map(Tech::getLabel).collect(Collectors.toList());

        List<TechDTO> techDTOsToSave = techDTOs.stream()
                .filter(t -> !noExistTechLabelList.contains(t.getLabel()))
                .collect(Collectors.toList());

        techDTOsToSave.forEach(techDTOtoSave -> {
            Tech techForsave = techMapper.toTech(techDTOtoSave);
            techForsave.setRing(ringRepository.findById(techDTOtoSave.getRingId()).get());
            techForsave.setSector(sectorRepository.findById(techDTOtoSave.getSectorId()).get());
            Tech savedTech = techRepository.save(techForsave);
            techDTOtoSave.getCategories().forEach(category -> {
                Category categoryEntity = categoryRepository.findById((category.getId())).get();
                techCategoryRepository.save(TechCategory.builder().tech(savedTech).category(categoryEntity).build());
            });
        });
    }
}