/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.domain;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"group\"", schema = "techradar")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_id_gen")
    @SequenceGenerator(name = "group_id_gen", sequenceName = "techradar.seq_group_id", allocationSize = 1)
    @Column(name = "id")
    private Integer id;

    @Column(name = "\"name\"", nullable = false, length = 50)
    private String name;

    @Column(name = "parent_id")
    private Integer parentId;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "parent_id", insertable = false, updatable = false)
     @ToString.Exclude
     private Group parent;

     @OneToMany(mappedBy = "parent")
     @ToString.Exclude
     private List<Group> children = new ArrayList<>();
}
