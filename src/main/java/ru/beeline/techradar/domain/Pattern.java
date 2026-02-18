/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pattern")
public class Pattern {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "patterns_id_gen")
    @SequenceGenerator(name = "patterns_id_gen", sequenceName = "techradar.seq_patterns_id", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    private String code;

    private String name;

    private String rule;

    private String dsl;

    @Column(name = "is_anti_pattern")
    private Boolean isAntiPattern;

    @Column(name = "create_date")
    private LocalDateTime createDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "delete_date")
    private LocalDateTime deleteDate;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "pattern")
    private List<PatternTech> children = new ArrayList<>();
}
