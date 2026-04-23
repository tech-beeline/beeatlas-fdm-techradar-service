/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.dto.ProcessDTO;
import ru.beeline.techradar.maper.ProcessMapper;
import ru.beeline.techradar.repository.ProcessRepository;

import java.util.List;

@Transactional
@Service
@Slf4j
public class ProcessService {

    private final ProcessRepository processRepository;
    private final ProcessMapper processMapper;

    public ProcessService(ProcessRepository processRepository,
                          ProcessMapper processMapper) {
        this.processRepository = processRepository;
        this.processMapper = processMapper;
    }

    public List<ProcessDTO> getAllProcess() {
        return processMapper.convert(processRepository.findAll());
    }
}
