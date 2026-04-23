/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.maper;

import org.springframework.stereotype.Component;
import ru.beeline.techradar.dto.ProcessDTO;
import ru.beeline.techradar.domain.Process;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProcessMapper {

    public List<ProcessDTO> convert(List<Process> processes) {
        return processes.stream().map(process -> convert(process)).collect(Collectors.toList());
    }

    public ProcessDTO convert(Process process) {
        return ProcessDTO.builder()
                .process(process.getNameProcess())
                .techName(process.getTech().getLabel())
                .build();
    }
}
