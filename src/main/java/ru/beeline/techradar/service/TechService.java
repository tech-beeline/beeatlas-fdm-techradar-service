package ru.beeline.techradar.service;

import org.springframework.stereotype.Service;
import ru.beeline.techradar.domain.Tech;
import ru.beeline.techradar.repository.TechRepository;

import java.util.List;

@Service
public class TechService {
    private final TechRepository techRepository;

    public TechService(TechRepository techRepository) {
        this.techRepository = techRepository;
    }

    public List<Tech> getAllTech() {
        return techRepository.findAll();
    }
}