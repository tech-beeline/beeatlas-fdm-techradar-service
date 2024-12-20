package ru.beeline.techradar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.client.NotificationClient;
import ru.beeline.techradar.client.ProductClient;
import ru.beeline.techradar.controller.RequestContext;
import ru.beeline.techradar.domain.*;
import ru.beeline.techradar.dto.*;
import ru.beeline.techradar.exception.ConflictException;
import ru.beeline.techradar.exception.ForbiddenException;
import ru.beeline.techradar.maper.TechMapper;
import ru.beeline.techradar.maper.TechVersionMapper;
import ru.beeline.techradar.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@Service
@Slf4j
public class TechService {
    private String techQueueName;
    private RabbitTemplate rabbitTemplate;
    private final TechRepository techRepository;
    private final TechCategoryRepository techCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SectorRepository sectorRepository;
    private final RingRepository ringRepository;
    private final BlackListRepository blackListRepository;
    private final TechBlProductRepository techBlProductRepository;
    private final TechMapper techMapper;
    private final TechVersionMapper techVersionMapper;
    private final NotificationClient notificationClient;
    private final ProductClient productClient;
    private final TechVersionRepository techVersionRepository;

    public TechService(RabbitTemplate rabbitTemplate, TechRepository techRepository, TechCategoryRepository techCategoryRepository, TechMapper techMapper, TechVersionMapper techVersionMapper, CategoryRepository categoryRepository, SectorRepository sectorRepository, RingRepository ringRepository, BlackListRepository blackListRepository, TechBlProductRepository techBlProductRepository, NotificationClient notificationClient, ProductClient productClient, @Value("${queue.tech-queue.name}") String techQueueName, TechVersionRepository techVersionRepository) {
        this.rabbitTemplate = rabbitTemplate;
        this.techRepository = techRepository;
        this.techCategoryRepository = techCategoryRepository;
        this.techMapper = techMapper;
        this.techVersionMapper = techVersionMapper;
        this.categoryRepository = categoryRepository;
        this.sectorRepository = sectorRepository;
        this.ringRepository = ringRepository;
        this.blackListRepository = blackListRepository;
        this.techBlProductRepository = techBlProductRepository;
        this.techQueueName = techQueueName;
        this.notificationClient = notificationClient;
        this.productClient = productClient;
        this.techVersionRepository = techVersionRepository;
    }

    public List<TechAdvancedDTO> getAllTech(Boolean actualTech) {
        List<Tech> techs = Boolean.TRUE.equals(actualTech) ? techRepository.findAllByDeletedDateIsNull() : techRepository.findAll();
        List<TechAdvancedDTO> result = new ArrayList<>();
        techs.forEach(tech -> {
            List<TechVersionDTO> versionsResult = new ArrayList<>();
            List<TechVersion> techVersions = techVersionRepository.findAllByTechIdAndDeletedDateIsNull(tech.getId());
            techVersions.forEach(techVersion -> {
                versionsResult.add(techVersionMapper.toTechVersionDTO(techVersion, ringRepository.findById(techVersion.getStatusId()).get()));
            });
            TechAdvancedDTO techAdvancedDTO = techMapper.toTechAdvancedDTO(tech);
            techAdvancedDTO.setVersions(versionsResult);
            result.add(techAdvancedDTO);

        });
        return result;
    }

    public List<ProductDTO> getProductTech() {
        List<ProductDTO> result = productClient.getProduct();
        result.forEach(productDTO -> {
            productDTO.getTech().forEach(techDTO -> {
                techDTO.setLabel(techRepository.findById(techDTO.getId()).get().getLabel());
            });
        });
        return result;
    }

    public void createRelations(PostProductTechDTO tech) {
        Tech techFromDb = techRepository.findByLabelAndDeletedDateIsNull(tech.getProjLang());
        if (techFromDb == null) {
            log.info("Tech not found in database for label: {}", tech.getProjLang());
            BlackList blackList = blackListRepository.findBlackListByLabel(tech.getProjLang());
            if (blackList == null) {
                log.info("BlackList entry not found for label: {}. Creating new entry.", tech.getProjLang());
                blackList = blackListRepository.save(BlackList.builder().label(tech.getProjLang()).review(false).createDate(new Date()).build());
            }
            if (!blackList.getReview()) {
                TechBlProduct techBlProduct = techBlProductRepository.findByCmdbCodeAndTechBlId(tech.getCmdbCode(), blackList.getId());
                if (techBlProduct == null) {
                    log.info("Creating new TechBlProduct entry for cmdbCode: {} and techBlId: {}", tech.getCmdbCode(), blackList.getId());
                    techBlProductRepository.save(TechBlProduct.builder().cmdbCode(tech.getCmdbCode()).techBlId(blackList.getId()).build());
                }
            }
        } else {
            productClient.postProduct(tech.getCmdbCode(), techFromDb.getId());
        }
    }

    public List<Tech> getAllTechByCategory(List<Integer> ids) {
        return techCategoryRepository.findByCategory_IdIn(ids).stream().map(TechCategory::getTech).collect(Collectors.toList());
    }

    public List<TechSubscribeDTO> getTechSubscribed() {
        List<Integer> techIds = notificationClient.getSubscribes("TECH");
        log.info("receive ids from notification server:" + techIds.toString());
        if (techIds.isEmpty()) {
            return new ArrayList<>();
        }
        return techRepository.findAllByIdInAndDeletedDateIsNull(techIds).stream().map(techMapper::toTechSubscribeDTO).collect(Collectors.toList());
    }

    public void addTech(List<PostTechDTO> techDTOs) throws JsonProcessingException {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        validatePostTechDTOFields(techDTOs);
        List<String> labels = techDTOs.stream().map(PostTechDTO::getLabel).collect(Collectors.toList());
        List<Tech> existTechList = techRepository.findAllByLabelIn(labels);
        if (!existTechList.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(existTechList.stream().map(tech -> Collections.singletonMap("label", tech.getLabel())).collect(Collectors.toList()));
            throw new ConflictException(json);
        }
        List<ObjectNode> messageList = new ArrayList<>();
        techDTOs.forEach(techDTOtoSave -> {
            Tech techForSave = techMapper.toTech(techDTOtoSave);
            Ring ring = ringRepository.findById(techDTOtoSave.getRingId()).orElseThrow(() -> new IllegalArgumentException("Ring with id=" + techDTOtoSave.getRingId() + " not found."));
            techForSave.setRing(ring);
            Sector sector = sectorRepository.findById(techDTOtoSave.getSectorId()).orElseThrow(() -> new IllegalArgumentException("Sector with id=" + techDTOtoSave.getSectorId() + " not found."));
            techForSave.setSector(sector);
            techForSave.setCreatedDate(LocalDate.now());
            techForSave.setLastModifiedDate(LocalDate.now());
            Tech savedTech = techRepository.save(techForSave);
            if (techDTOtoSave.getCategories() != null && !techDTOtoSave.getCategories().isEmpty()) {
                saveTechCategoryWithoutDuplicate(savedTech, techDTOtoSave.getCategories());
            }
            addMessage(savedTech.getId(), "CREATE", messageList, savedTech.getLabel());
        });
        sendMessageToTechCapabilityQueue(techQueueName, messageList.toString());
    }

    private void saveTechCategoryWithoutDuplicate(Tech savedTech, List<TechCategoryDTO> categories) {
        Set<TechCategory> techCategories = categories.stream().map(category -> {
            Category categoryEntity = categoryRepository.findById(category.getId()).orElseThrow(() -> new IllegalArgumentException("Category with id=" + category.getId() + " not found."));
            return TechCategory.builder().tech(savedTech).category(categoryEntity).build();
        }).collect(Collectors.toSet());

        techCategoryRepository.saveAll(techCategories);
    }

    public void patchTech(Integer id, TechDTO techDTO) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        validateTechDTOFields(techDTO);
        if (techRepository.findAllByLabelIn(Collections.singletonList(techDTO.getLabel())).get(0).getId().equals(id)) {
            throw new ConflictException("Поменять название технологии");
        }
        Tech tech = techRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tech with id=" + techDTO.getId() + " not found."));
        List<ObjectNode> messageList = new ArrayList<>();
        tech.setLastModifiedDate(LocalDate.now());
        if (techDTO.getLabel() != null) {
            tech.setLabel(techDTO.getLabel());
        }
        if (techDTO.getDescr() != null) {
            tech.setDescription(techDTO.getDescr());
        }
        if (techDTO.getRingId() != null) {
            Ring ring = ringRepository.findById(techDTO.getRingId()).orElseThrow(() -> new IllegalArgumentException("Ring with id=" + techDTO.getRingId() + " not found."));
            tech.setRing(ring);
        }
        if (techDTO.getSectorId() != null) {
            Sector sector = sectorRepository.findById(techDTO.getSectorId()).orElseThrow(() -> new IllegalArgumentException("Sector with id=" + techDTO.getSectorId() + " not found."));
            tech.setSector(sector);
        }
        if (techDTO.getLink() != null) {
            tech.setLink(techDTO.getLink());
        }
        tech.setDeletedDate(null);
        Tech savedTech = techRepository.save(tech);
        techCategoryRepository.deleteAllByTech(savedTech);
        techCategoryRepository.flush();
        if (techDTO.getCategories() != null && !techDTO.getCategories().isEmpty()) {
            saveTechCategoryWithoutDuplicate(savedTech, techDTO.getCategories());
        }
        addMessage(savedTech.getId(), "UPDATE", messageList, savedTech.getLabel());
        sendMessageToTechCapabilityQueue(techQueueName, messageList.toString());
    }

    private void addMessage(Integer id, String changeType, List<ObjectNode> messageList, String name) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            ObjectNode messagePayload = objectMapper.createObjectNode();
            messagePayload.put("entity_id", id);
            messagePayload.put("name", name);
            messagePayload.put("change_type", changeType);

            messageList.add(messagePayload);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToTechCapabilityQueue(String queue, String message) {
        rabbitTemplate.convertAndSend(queue, message, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            return messagePostProcessor;
        });
    }

    public void deleteTech(Integer id) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        Tech tech = techRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Tech with id=" + id + " not found."));
        tech.setDeletedDate(LocalDate.now());
        techRepository.save(tech);
    }


    private void validatePostTechDTOFields(List<PostTechDTO> techDTOs) {
        for (PostTechDTO dto : techDTOs) {
            if (dto.getLabel() == null || dto.getLabel().isEmpty() || dto.getRingId() == null || dto.getSectorId() == null) {
                throw new IllegalArgumentException("Bad Request: 'label', 'ring_id', or 'sector_id' is empty.");
            }
        }
    }

    private void validateTechDTOFields(TechDTO techDTO) {
        if (techDTO.getLabel() == null
                || techDTO.getLabel().isEmpty()
                || techDTO.getRingId() == null
                || techDTO.getSectorId() == null
                || techDTO.getId() == null) {
            throw new IllegalArgumentException("Bad Request: 'label', 'ring_id', 'id' or 'sector_id' is empty.");
        }
    }

    public void deleteTechVersion(Integer techId, Integer versionId) {
        if (RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        TechVersion techVersion = techVersionRepository.findByTechIdAndIdAndDeletedDateIsNull(techId, versionId);
        if (techVersion != null) {
            techVersion.setDeletedDate(LocalDateTime.now());
            techVersionRepository.save(techVersion);
        }
    }
}