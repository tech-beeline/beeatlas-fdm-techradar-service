/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.beeline.techradar.domain.Ring;
import ru.beeline.techradar.repository.RingRepository;

import java.util.List;

@Transactional
@Service
@Slf4j
public class RingService {

    private final RingRepository ringRepository;


    public RingService(RingRepository ringRepository) {
        this.ringRepository = ringRepository;

    }


    public List<Ring> getAllRings() {
        return ringRepository.findAll();
    }
}