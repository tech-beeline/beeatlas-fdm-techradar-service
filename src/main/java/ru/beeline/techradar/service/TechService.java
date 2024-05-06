package ru.beeline.techradar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.controller.RequestContext;
import ru.beeline.techradar.domain.Category;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.domain.TechCategory;
import ru.beeline.techradar.dto.PostTechDTO;
import ru.beeline.techradar.dto.TechDTO;
import ru.beeline.techradar.exception.ConflictException;
import ru.beeline.techradar.exception.ForbiddenException;
import ru.beeline.techradar.maper.TechMapper;
import ru.beeline.techradar.repository.CategoryRepository;
import ru.beeline.techradar.repository.RingRepository;
import ru.beeline.techradar.repository.SectorRepository;
import ru.beeline.techradar.repository.TechCategoryRepository;
import ru.beeline.techradar.repository.TechRepository;

import java.time.LocalDate;
import java.util.Collections;
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

    public void addTech(List<PostTechDTO> techDTOs) throws JsonProcessingException {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        validateFields(techDTOs);
        List<String> labels = techDTOs.stream().map(PostTechDTO::getLabel).collect(Collectors.toList());
        List<Tech> existTechList = techRepository.findAllByLabelIn(labels);
        if (!existTechList.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(
                    existTechList.stream().map(tech -> Collections.singletonMap("label", tech.getLabel()))
                            .collect(Collectors.toList())
            );
            throw new ConflictException(json);
        }

        techDTOs.forEach(techDTOtoSave -> {
            Tech techForsave = techMapper.toTech(techDTOtoSave);
            techForsave.setRing(ringRepository.findById(techDTOtoSave.getRingId()).get());
            techForsave.setSector(sectorRepository.findById(techDTOtoSave.getSectorId()).get());
            techForsave.setCreatedDate(LocalDate.now());
            techForsave.setLastModifiedDate(LocalDate.now());
            Tech savedTech = techRepository.save(techForsave);
            techDTOtoSave.getCategories().forEach(category -> {
                Category categoryEntity = categoryRepository.findById((category.getId())).get();
                techCategoryRepository.save(TechCategory.builder().tech(savedTech).category(categoryEntity).build());
            });
        });
    }

    private void validateFields(List<PostTechDTO> techDTOs) {
        for (PostTechDTO dto : techDTOs) {
            if (dto.getLabel() == null || dto.getLabel().isEmpty() ||
                    dto.getRingId() == null ||
                    dto.getSectorId() == null) {
                throw new IllegalArgumentException("Bad Request: 'label', 'ring_id', or 'sector_id' is empty.");
            }
        }
    }

    public void patchTech(List<TechDTO> techDTOS) {
        List<Integer> ids = techDTOS.stream().map(TechDTO::getId).collect(Collectors.toList());
        List<Tech> existTechList = techRepository.findAllByIdIn(ids);

        existTechList.forEach(techDTOtoPatch -> {
            TechDTO donor = techDTOS.stream().filter(techDTO -> techDTO.getId().equals(techDTOtoPatch.getId())).findFirst().get();
            techDTOtoPatch.setLastModifiedDate(LocalDate.now());
            if (donor.getLabel() != null) {
                techDTOtoPatch.setLabel(donor.getLabel());
            }
            if (donor.getDescr() != null) {
                techDTOtoPatch.setDescription(donor.getDescr());
            }
            if (donor.getRingId() != null) {
                techDTOtoPatch.setRing(ringRepository.findById(donor.getRingId()).get());
            }
            if (donor.getSectorId() != null) {
                techDTOtoPatch.setSector(sectorRepository.findById(donor.getSectorId()).get());
            }
            if (donor.getLink() != null) {
                techDTOtoPatch.setLink(donor.getLink());
            }

            Tech savedTech = techRepository.save(techDTOtoPatch);
            techCategoryRepository.deleteAllByTech(savedTech);
            techCategoryRepository.flush();
            donor.getCategories().forEach(category -> {
                Category categoryEntity = categoryRepository.findById((category.getId())).get();
                techCategoryRepository.save(TechCategory.builder().tech(savedTech).category(categoryEntity).build());
            });
        });
    }

    public void deleteTech(Integer id) {
        Tech tech = techRepository.findById(id).get();
        tech.setDeletedDate(LocalDate.now());
        techRepository.save(tech);
    }
}