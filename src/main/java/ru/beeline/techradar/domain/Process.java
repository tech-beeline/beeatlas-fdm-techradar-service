/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.domain;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "process")
public class Process {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "process_id_gen")
    @SequenceGenerator(name = "process_id_gen", sequenceName = "techradar.seq_process_id")
    @Column(name = "id")
    private Integer id;

    @Column(name = "name_process", nullable = false)
    private String nameProcess;

    @ManyToOne
    @JoinColumn(name = "tech_id")
    private Tech tech;

    @Column(name = "created_date", nullable = false)
    private Timestamp createdDate;

    @Column(name = "deleted_date")
    private Timestamp deletedDate;
}