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
     private Group parent;

     @OneToMany(mappedBy = "parent")
     private List<Group> children = new ArrayList<>();
}
