/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.dto.product.GetProductTechDto;
import ru.beeline.techradar.dto.product.GetProductsDTO;
import ru.beeline.techradar.client.DocumentClient;
import ru.beeline.techradar.client.NotificationClient;
import ru.beeline.techradar.client.ProductClient;
import ru.beeline.techradar.controller.RequestContext;
import ru.beeline.techradar.domain.Process;
import ru.beeline.techradar.domain.*;
import ru.beeline.techradar.dto.*;
import ru.beeline.techradar.exception.ConflictException;
import ru.beeline.techradar.exception.ForbiddenException;
import ru.beeline.techradar.exception.NotFoundException;
import ru.beeline.techradar.maper.HistoryMapper;
import ru.beeline.techradar.maper.TechHistoryMapper;
import ru.beeline.techradar.maper.TechMapper;
import ru.beeline.techradar.maper.TechVersionMapper;
import ru.beeline.techradar.repository.*;
import ru.beeline.techradar.tree.IntervalTree;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
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
    private final ProcessRepository processRepository;
    private final BlackListRepository blackListRepository;
    private final TechBlProductRepository techBlProductRepository;
    private final TechMapper techMapper;
    private final TechVersionMapper techVersionMapper;
    private final NotificationClient notificationClient;
    private final ProductClient productClient;
    private final TechVersionRepository techVersionRepository;
    private final HistoryTechRepository historyTechRepository;
    private final TechHistoryMapper techHistoryMapper;
    private final HistoryMapper historyMapper;
    private final ExcelExporterService excelExporterService;
    private final DocumentClient documentClient;

    public TechService(ExcelExporterService excelExporterService,
                       TechRepository techRepository,
                       TechCategoryRepository techCategoryRepository,
                       TechMapper techMapper,
                       TechVersionMapper techVersionMapper,
                       CategoryRepository categoryRepository,
                       SectorRepository sectorRepository,
                       RingRepository ringRepository,
                       BlackListRepository blackListRepository,
                       TechBlProductRepository techBlProductRepository,
                       NotificationClient notificationClient,
                       ProductClient productClient,
                       TechVersionRepository techVersionRepository,
                       HistoryTechRepository historyTechRepository,
                       TechHistoryMapper techHistoryMapper,
                       HistoryMapper historyMapper,
                       DocumentClient documentClient,
                       ProcessRepository processRepository) {
        this.techRepository = techRepository;
        this.techCategoryRepository = techCategoryRepository;
        this.techMapper = techMapper;
        this.techVersionMapper = techVersionMapper;
        this.categoryRepository = categoryRepository;
        this.sectorRepository = sectorRepository;
        this.ringRepository = ringRepository;
        this.blackListRepository = blackListRepository;
        this.techBlProductRepository = techBlProductRepository;
        this.notificationClient = notificationClient;
        this.productClient = productClient;
        this.techVersionRepository = techVersionRepository;
        this.historyTechRepository = historyTechRepository;
        this.techHistoryMapper = techHistoryMapper;
        this.historyMapper = historyMapper;
        this.excelExporterService = excelExporterService;
        this.documentClient = documentClient;
        this.processRepository = processRepository;
    }

    public List<TechAdvancedDTO> getAllTech(Boolean actualTech) {
        List<Tech> techs;
        if (actualTech == null || actualTech) {
            techs = techRepository.findAllByDeletedDateIsNullAndReviewIsTrue();
        } else {
            techs = techRepository.findAll();
        }
        List<TechAdvancedDTO> result = new ArrayList<>();
        Map<Integer, List<HistoryTech>> allHistoryTech = getAllTechHistories(techs);
        techs.forEach(tech -> {
            TechAdvancedDTO techAdvancedDTO = techMapper.toTechAdvancedDTO(tech);
            techAdvancedDTO.setVersions(getTechVersion(tech));
            List<HistoryTech> history = allHistoryTech.getOrDefault(tech.getId(), Collections.emptyList());
            techAdvancedDTO.setHistory(mapToHistoryDTOList(history));
            result.add(techAdvancedDTO);
        });
        result.sort(Comparator.comparing(TechAdvancedDTO::getId));
        return result;
    }

    private Map<Integer, List<HistoryTech>> getAllTechHistories(List<Tech> techs) {
        List<Integer> techIds = techs.stream()
                .map(Tech::getId)
                .collect(Collectors.toList());
        List<HistoryTech> allHistories = historyTechRepository.findByRefIdIn(techIds);
        return allHistories.stream()
                .collect(Collectors.groupingBy(HistoryTech::getRefId));
    }

    private List<TechVersionDTO> getTechVersion(Tech tech) {
        List<TechVersion> techVersions = techVersionRepository.findAllByTechIdAndDeletedDateIsNull(tech.getId());
        return techVersions.stream()
                .map(version -> techVersionMapper.toTechVersionDTO(
                        version, ringRepository.findById(version.getStatusId())
                                .orElseThrow(() -> new RuntimeException("Ring not found with id: " + version.getStatusId()))
                ))
                .collect(Collectors.toList());
    }


    public HistoryTechDTO getTechById(Integer id) {
        Tech tech = techRepository.findById(id).orElseThrow(() -> new NotFoundException("Запись не найдена"));
        List<HistoryTech> historyTechList = historyTechRepository.findByRefId(id);
        Sector sector = tech.getSector();
        if (sector == null) {
            throw new NotFoundException("Запись в таблице Sector с данным id не найдена");
        }
        Ring ring = tech.getRing();
        if (ring == null) {
            throw new NotFoundException("Запись в таблице Ring с данным id не найдена");
        }
        List<TechCategoryAdvancedDTO> categoriesResult = tech.getCategory().stream()
                .map(category -> new TechCategoryAdvancedDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
        HistoryTechDTO result = techHistoryMapper.toHistoryTechDTO(tech, sector, ring, categoriesResult);
        result.setVersions(getTechVersion(tech));
        if (!historyTechList.isEmpty()) {
            List<HistoryDTO> historyDTOList = mapToHistoryDTOList(historyTechList);
            Integer maxVersion = historyTechList.stream()
                    .map(HistoryTech::getVersion)
                    .max(Integer::compareTo)
                    .orElse(0);
            result.setHistory(historyDTOList);
            result.setCurrentVersion(maxVersion + 1);
        }
        return result;
    }

    private List<HistoryDTO> mapToHistoryDTOList(List<HistoryTech> historyTechList) {
        return historyTechList.stream()
                .map(historyMapper::toHistoryDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTO> getProductTech() {
        log.info("start getProductTech");
        List<ProductDTO> result = productClient.getProduct();
        log.info("Product size is" + result.size());
        if (result.size() < 50) {
            result.forEach(productDTO -> {
                Iterator<ProductTechDTO> iterator = productDTO.getTech().iterator();
                while (iterator.hasNext()) {
                    ProductTechDTO techDTO = iterator.next();
                    Optional<Tech> optionalTech = techRepository.findByIdAndDeletedDateIsNullAndReviewIsTrue(techDTO.getId());
                    if (optionalTech.isPresent()) {
                        techDTO.setLabel(optionalTech.get().getLabel());
                    } else {
                        iterator.remove();
                    }
                }
            });
        } else {
            Set<Integer> ids = result.stream()
                    .flatMap(productDTO -> productDTO.getTech().stream())
                    .map(ProductTechDTO::getId)
                    .collect(Collectors.toSet());
            log.info("get teches");
            List<Tech> teches = techRepository.findByIdInAndDeletedDateIsNullAndReviewIsTrue(ids);
            log.info("teches received");
            Map<Integer, String> techMap = teches.stream().collect(Collectors.toMap(Tech::getId, Tech::getLabel));
            result.forEach(productDTO -> {
                Iterator<ProductTechDTO> iterator = productDTO.getTech().iterator();
                while (iterator.hasNext()) {
                    ProductTechDTO techDTO = iterator.next();
                    Optional<Tech> optionalTech = techRepository.findByIdAndDeletedDateIsNullAndReviewIsTrue(techDTO.getId());
                    if (optionalTech.isPresent()) {
                        techDTO.setLabel(techMap.get(techDTO.getId()));
                    } else {
                        iterator.remove();
                    }
                }
            });
        }
        return result.stream().distinct().collect(Collectors.toList());
    }

    public void createRelations(PostProductTechDTO tech) {
        Tech techFromDb = techRepository.findByLabelIgnoreCase(tech.getProjLang());
        Integer id;
        if (techFromDb != null) {
            id = techFromDb.getId();
        } else {
            Process process = processRepository.findByNameProcessIgnoreCase(tech.getProjLang());
            if (process != null) {
                id = process.getTech().getId();
            } else {
                log.info("Tech not found in database for label: {}", tech.getProjLang());
                log.info("BlackList entry not found for label: {}. Creating new entry.", tech.getProjLang());
                id = createNewTech(tech);
            }
        }
        productClient.postProduct(tech.getCmdbCode(), id);
    }

    private Integer createNewTech(PostProductTechDTO tech) {
        Tech newTech = Tech.builder()
                .label(tech.getProjLang())
                .description("")
                .sector(sectorRepository.findById(0)
                        .orElseThrow(() -> new IllegalStateException("Default sector not found")))
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .ring(ringRepository.findById(0).orElseThrow(() -> new IllegalStateException("Default ring not found")))
                .review(false)
                .build();
        return techRepository.save(newTech).getId();
    }

    public List<TechExtensionDTO> getAllTechByCategory(List<Integer> ids) {
        List<Tech> techList = techCategoryRepository.findByCategory_IdIn(ids)
                .stream()
                .map(TechCategory::getTech)
                .distinct()
                .collect(Collectors.toList());

        return techMapper.toTechDTOList(techList);
    }

    public List<TechSubscribeDTO> getTechSubscribed() {
        List<Integer> techIds = notificationClient.getSubscribes("TECH");
        log.info("receive ids from notification server:" + techIds.toString());
        if (techIds.isEmpty()) {
            return new ArrayList<>();
        }
        return techRepository.findAllByIdInAndDeletedDateIsNull(techIds)
                .stream()
                .map(techMapper::toTechSubscribeDTO)
                .collect(Collectors.toList());
    }

    public List<IdDTO> addTech(List<PostTechDTO> techDTOs) throws JsonProcessingException {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        validatePostTechDTOFields(techDTOs);
        List<String> labels = techDTOs.stream().map(PostTechDTO::getLabel).collect(Collectors.toList());
        List<Tech> existTechList = techRepository.findAllByLabelIn(labels);
        if (!existTechList.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(existTechList.stream()
                    .map(tech -> Collections.singletonMap("label",
                            tech.getLabel()))
                    .collect(Collectors.toList()));
            throw new ConflictException(json);
        }
        List<IdDTO> savedTechIds = new ArrayList<>();
        techDTOs.forEach(techDTOtoSave -> {
            Tech techForSave = techMapper.toTech(techDTOtoSave);
            if (techForSave.getReview() == null) {
                log.info("tech: " + techDTOtoSave.getLabel() + ", Review - false");
                techForSave.setReview(false);
            }
            Ring ring = ringRepository.findById(techDTOtoSave.getRingId())
                    .orElseThrow(() -> new IllegalArgumentException("Ring with id=" + techDTOtoSave.getRingId() + " not found."));
            techForSave.setRing(ring);
            Sector sector = sectorRepository.findById(techDTOtoSave.getSectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector with id=" + techDTOtoSave.getSectorId() + " not found."));
            techForSave.setSector(sector);
            techForSave.setCreatedDate(LocalDateTime.now());
            Tech savedTech = techRepository.save(techForSave);
            if (techDTOtoSave.getCategories() != null && !techDTOtoSave.getCategories().isEmpty()) {
                saveTechCategoryWithoutDuplicate(savedTech, techDTOtoSave.getCategories());
            }
            savedTechIds.add(IdDTO.builder().id(savedTech.getId()).build());
            log.info("tech save: " + techDTOtoSave.getLabel() + "tech review: " + savedTech.getReview());
        });
        return savedTechIds;
    }

    private void saveTechCategoryWithoutDuplicate(Tech savedTech, List<TechCategoryDTO> categories) {
        Set<TechCategory> techCategories = categories.stream().map(category -> {
            Category categoryEntity = categoryRepository.findById(category.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category with id=" + category.getId() + " not found."));
            return TechCategory.builder().tech(savedTech).category(categoryEntity).build();
        }).collect(Collectors.toSet());

        techCategoryRepository.saveAll(techCategories);
    }

    public void patchTech(Integer id, TechDTO techDTO) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        validateTechDTOFields(techDTO);
        List<Tech> existingTechs = techRepository.findAllByLabelIn(Collections.singletonList(techDTO.getLabel()));
        for (Tech existingTech : existingTechs) {
            if (!existingTech.getId().equals(id)) {
                throw new ConflictException("Поменять название технологии");
            }
        }
        Tech tech = techRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tech with id=" + id + " not found."));
        saveHistoryTech(tech);
        tech.setLastModifiedDate(LocalDateTime.now());
        if (techDTO.getLabel() != null) {
            tech.setLabel(techDTO.getLabel());
        }
        if (techDTO.getDescr() != null) {
            tech.setDescription(techDTO.getDescr());
        }
        if (techDTO.getRingId() != null) {
            Ring ring = ringRepository.findById(techDTO.getRingId())
                    .orElseThrow(() -> new IllegalArgumentException("Ring with id=" + techDTO.getRingId() + " not found."));
            tech.setRing(ring);
        }
        if (techDTO.getSectorId() != null) {
            Sector sector = sectorRepository.findById(techDTO.getSectorId())
                    .orElseThrow(() -> new IllegalArgumentException("Sector with id=" + techDTO.getSectorId() + " not found."));
            tech.setSector(sector);
        }
        if (techDTO.getLink() != null) {
            tech.setLink(techDTO.getLink());
        }
        if (techDTO.getReview() != null) {
            tech.setReview(techDTO.getReview());
        }
        if (techDTO.getIsCritical() != null) {
            tech.setIsCritical(techDTO.getIsCritical());
        }
        tech.setDeletedDate(null);
        Tech savedTech = techRepository.save(tech);
        techCategoryRepository.deleteAllByTech(savedTech);
        techCategoryRepository.flush();
        if (techDTO.getCategories() != null && !techDTO.getCategories().isEmpty()) {
            saveTechCategoryWithoutDuplicate(savedTech, techDTO.getCategories());
        }
    }

    private void saveHistoryTech(Tech tech) {
        List<HistoryTech> historyTechList = historyTechRepository.findByRefId(tech.getId());
        Integer maxVersion = 0;
        if (!historyTechList.isEmpty()) {
            maxVersion = historyTechList.stream().map(HistoryTech::getVersion).max(Integer::compareTo).get();
        }
        historyTechRepository.save(HistoryTech.builder()
                .refId(tech.getId())
                .version(maxVersion + 1)
                .label(tech.getLabel())
                .description(tech.getDescription())
                .sector(tech.getSector())
                .ring(tech.getRing())
                .link(tech.getLink())
                .createdDate(tech.getLastModifiedDate() == null ? tech.getCreatedDate() : tech.getLastModifiedDate())
                .build());
    }

    public void deleteTech(Integer id) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        Tech tech = techRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tech with id=" + id + " not found."));
        tech.setDeletedDate(LocalDateTime.now());
        tech.setReview(true);
        techRepository.save(tech);
    }

    private void validatePostTechDTOFields(List<PostTechDTO> techDTOs) {
        for (PostTechDTO dto : techDTOs) {
            if (dto.getLabel() == null || dto.getLabel()
                    .isEmpty() || dto.getRingId() == null || dto.getSectorId() == null) {
                throw new IllegalArgumentException("Bad Request: 'label', 'ring_id', or 'sector_id' is empty.");
            }
        }
    }

    private void validateTechDTOFields(TechDTO techDTO) {
        if (techDTO.getLabel() == null || techDTO.getLabel()
                .isEmpty() || techDTO.getRingId() == null || techDTO.getSectorId() == null) {
            throw new IllegalArgumentException("Bad Request: 'label', 'ring_id', 'id' or 'sector_id' is empty.");
        }
    }

    public TechExportDTO export(Integer docId) {
        List<Tech> techList = techRepository.findAllByReviewIsTrueAndDeletedDateIsNull();
        List<GetProductTechDto> techProducts = productClient.getTechProducts();
        Map<Tech, List<GetProductsDTO>> techProductsMap = createTechMap(techList, techProducts);
        String fileName = "export_technology_" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
        File tempFile = excelExporterService.exportTechnologies(techProductsMap, fileName);
        System.gc();
        if (tempFile != null) {
            documentClient.updateDocument(docId, tempFile, fileName);
            tempFile.delete();
        }
        return TechExportDTO.builder().docId(docId).build();
    }

    private Map<Tech, List<GetProductsDTO>> createTechMap(List<Tech> techList, List<GetProductTechDto> techProducts) {
        Map<Integer, Tech> techById = techList.stream().collect(Collectors.toMap(Tech::getId, Function.identity()));
        Map<Tech, List<GetProductsDTO>> techMap = new HashMap<>();
        for (Tech tech : techList) {
            techMap.put(tech, new ArrayList<>());
        }
        for (GetProductTechDto techProduct : techProducts) {
            Tech tech = techById.get(techProduct.getTechId());
            if (tech != null) {
                techMap.get(tech).addAll(techProduct.getProducts());
            }
        }
        return techMap;
    }

    public void deleteTechVersion(Integer techId, Integer versionId) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        TechVersion techVersion = techVersionRepository.findByTechIdAndIdAndDeletedDateIsNull(techId, versionId);
        if (techVersion != null) {
            techVersion.setDeletedDate(LocalDateTime.now());
            techVersionRepository.save(techVersion);
        }
    }

    public void createTechVersion(List<PostTechVersionDTO> postTechVersionDTOS, Integer techId) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        techRepository.findById(techId)
                .orElseThrow(() -> new NotFoundException("Not found: Tech с данным id не найден."));
        List<TechVersion> result = new ArrayList<>();
        IntervalTree newIntervalTree = new IntervalTree();

        for (PostTechVersionDTO postTechVersion : postTechVersionDTOS) {
            validatePostTechVersionDTO(postTechVersion);
            ringRepository.findById(postTechVersion.getStatusId())
                    .orElseThrow(() -> new IllegalArgumentException("Bad Request: Not found record in the ring table"));
            if (postTechVersion.getVersionStart() == null || postTechVersion.getVersionStart().isEmpty()) {
                postTechVersion.setVersionStart("0.0.0");
            } else {
                postTechVersion.setVersionStart(validateStringVersion(postTechVersion.getVersionStart()));
            }
            if (postTechVersion.getVersionEnd() == null || postTechVersion.getVersionEnd().isEmpty()) {
                postTechVersion.setVersionEnd("9999.9999.9999");
            } else {
                postTechVersion.setVersionEnd(validateStringVersion(postTechVersion.getVersionEnd()));
            }
            postTechVersion.setVersionStart(removeLeadingZeros(postTechVersion.getVersionStart()));
            postTechVersion.setVersionEnd(removeLeadingZeros(postTechVersion.getVersionEnd()));
            validateVersions(postTechVersion.getVersionStart(), postTechVersion.getVersionEnd());
            TechVersion versionRange = techVersionMapper.toTechVersion(postTechVersion, techId);
            if (newIntervalTree.overlaps(versionRange)) {
                throw new IllegalArgumentException("Bad Request: Пересечение диапозонов версий в теле запроса.");
            }
            newIntervalTree.insert(versionRange);
            result.add(versionRange);
        }
        List<TechVersion> existingtechVersionList = techVersionRepository.findAllByTechIdAndDeletedDateIsNull(techId);
        IntervalTree existingIntervalTree = new IntervalTree();
        for (TechVersion existingVersion : existingtechVersionList) {
            existingIntervalTree.insert(existingVersion);
        }
        for (TechVersion newRange : result) {
            if (existingIntervalTree.overlaps(newRange)) {
                throw new IllegalArgumentException("Bad Request: Новые версии пересекаются с существующими.");
            }
        }
        techVersionRepository.saveAll(result);
    }

    private String removeLeadingZeros(String version) {
        String[] parts = version.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Bad Request: Версия должна состоять из трех частей, разделенных точками.");
        }
        String major = Long.toString(Long.parseLong(parts[0]));
        String minor = Long.toString(Long.parseLong(parts[1]));
        String patch = Long.toString(Long.parseLong(parts[2]));
        return major + "." + minor + "." + patch;
    }

    private void validateVersions(String startVersion, String endVersion) {
        long startVersionLong = convertVersionStringToLong(startVersion);
        long endVersionLong = convertVersionStringToLong(endVersion);
        if (startVersionLong >= endVersionLong) {
            throw new IllegalArgumentException("Bad Request: 'startVersion' должно быть меньше, чем 'endVersion'.");
        }
    }

    private long convertVersionStringToLong(String versionString) {
        String[] parts = versionString.split("\\.");
        for (String part : parts) {
            int partInt = Integer.parseInt(part);
            if (partInt < 0 || partInt > 9999) {
                throw new IllegalArgumentException(
                        "Bad Request: Каждая часть версии должна быть в диапазоне от 0 до 9999.");
            }
        }
        long result = 0;
        for (String part : parts) {
            result = result * 10000 + Integer.parseInt(part);
        }
        return result;
    }

    private String validateStringVersion(String version) {
        String regex = "^\\d+\\.\\d+\\.\\d+$";
        String regex1 = "^\\d+$";
        String regex2 = "^\\d+\\.\\d+$";
        if (version.matches(regex)) {
            return version;
        } else if (version.matches(regex1)) {
            return version + ".0.0";
        } else if (version.matches(regex2)) {
            return version + ".0";
        } else {
            throw new IllegalArgumentException("Строка не соответствует формату");
        }
    }

    private void validatePostTechVersionDTO(PostTechVersionDTO postTechVersionDTO) {
        if ((postTechVersionDTO.getVersionStart() == null || postTechVersionDTO.getVersionStart()
                .isEmpty()) && (postTechVersionDTO.getVersionEnd() == null || postTechVersionDTO.getVersionEnd()
                .isEmpty())) {
            throw new IllegalArgumentException("Bad Request: 'VersionStart' и 'VersionEnd' не заполнены.");
        }
        if (postTechVersionDTO.getStatusId() == null) {
            throw new IllegalArgumentException("Bad Request: поле 'statusId' не должно быть пустым.");
        }
    }

    public void patchTechVersion(PostTechVersionDTO postTechVersionDTO, Integer techId, Integer idVersion) {
        if (!RequestContext.getRoles().contains("ADMINISTRATOR")) {
            throw new ForbiddenException("403 Forbidden.");
        }
        techRepository.findById(techId)
                .orElseThrow(() -> new NotFoundException("Not found: Tech с данным id не найден."));
        TechVersion currentVersion = techVersionRepository.findById(idVersion)
                .orElseThrow(() -> new NotFoundException(
                        "Not found: запись в таблице 'tech_version' с данным id не найден."));
        validatePostTechVersionDTO(postTechVersionDTO);
        ringRepository.findById(postTechVersionDTO.getStatusId())
                .orElseThrow(() -> new IllegalArgumentException("Bad Request: Not found record in the ring table"));
        if (postTechVersionDTO.getVersionStart() == null || postTechVersionDTO.getVersionStart().isEmpty()) {
            postTechVersionDTO.setVersionStart("0.0.0");
        } else {
            postTechVersionDTO.setVersionStart(validateStringVersion(postTechVersionDTO.getVersionStart()));
        }
        if (postTechVersionDTO.getVersionEnd() == null || postTechVersionDTO.getVersionEnd().isEmpty()) {
            postTechVersionDTO.setVersionEnd("9999.9999.9999");
        } else {
            postTechVersionDTO.setVersionEnd(validateStringVersion(postTechVersionDTO.getVersionEnd()));
        }
        postTechVersionDTO.setVersionStart(removeLeadingZeros(postTechVersionDTO.getVersionStart()));
        postTechVersionDTO.setVersionEnd(removeLeadingZeros(postTechVersionDTO.getVersionEnd()));
        PostTechVersionDTO currentTechVersionDTO = PostTechVersionDTO.builder()
                .versionStart(currentVersion.getVersionStart())
                .versionEnd(currentVersion.getVersionEnd())
                .statusId(currentVersion.getStatusId())
                .build();
        if (!currentTechVersionDTO.equals(postTechVersionDTO)) {
            validateVersions(postTechVersionDTO.getVersionStart(), postTechVersionDTO.getVersionEnd());
            List<TechVersion> existingtechVersionList = techVersionRepository.findAllByTechIdAndDeletedDateIsNull(techId);
            IntervalTree existingIntervalTree = new IntervalTree();
            for (TechVersion existingVersion : existingtechVersionList) {
                if (existingVersion.getId().equals(idVersion)) {
                    continue;
                }
                existingIntervalTree.insert(existingVersion);
            }
            TechVersion updatedVersion = techVersionMapper.toTechVersion(postTechVersionDTO, techId);
            updatedVersion.setId(currentVersion.getId());
            updatedVersion.setLastModifiedDate(LocalDateTime.now());

            if (existingIntervalTree.overlaps(updatedVersion)) {
                throw new IllegalArgumentException("Bad Request: Новая версия пересекается с существующими.");
            }

            techVersionRepository.save(updatedVersion);
        }
    }

    public List<TechAdvancedGetDTO> getTechByIds(List<Integer> ids) {
        return techRepository.findByIdInAndDeletedDateIsNullAndReviewIsTrue(ids)
                .stream()
                .map(techMapper::toTechAdvancedGetDTO)
                .collect(Collectors.toList());
    }
}