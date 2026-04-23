/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tech")
public class Tech {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tech_id_generator")
    @SequenceGenerator(name = "tech_id_generator", sequenceName = "tech_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @Column(name = "label")
    private String label;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "sectorId")
    private Sector sector;

    @ManyToMany
    @JoinTable(
            name = "techcategory",
            joinColumns = @JoinColumn(name = "techid"),
            inverseJoinColumns = @JoinColumn(name = "categoryid"))
    private List<Category> category;

    @Column(name = "created_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime lastModifiedDate;

    @Column(name = "deleted_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deletedDate;

    @Column(name = "link")
    private String link;

    @ManyToOne
    @JoinColumn(name = "ringId")
    private Ring ring;

    @Column(name = "review")
    private Boolean review;

    @Column(name = "is_critical ")
    private Boolean isCritical;

    @OneToMany(mappedBy = "tech")
    private List<PatternTech> children = new ArrayList<>();
}
