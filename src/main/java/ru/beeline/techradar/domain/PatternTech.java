/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "pattern_tech")
public class PatternTech {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pattern_tech_id_gen")
    @SequenceGenerator(name = "pattern_tech_id_gen", sequenceName = "techradar.seq_patterns_tech_id", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "pattern_id")
    private Pattern pattern;

    @ManyToOne
    @JoinColumn(name = "tech_id")
    private Tech tech;
}
