package ru.beeline.techradar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TechSubscribeDTO {

    private Integer id;

    private String label;

    private String desription;
}
