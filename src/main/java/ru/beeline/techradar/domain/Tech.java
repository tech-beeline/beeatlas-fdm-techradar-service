package ru.beeline.techradar.domain;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDate;
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
    private LocalDate createdDate;

    @Column(name = "last_modified_date")
    private LocalDate lastModifiedDate;

    @Column(name = "deleted_date")
    private LocalDate deletedDate;

    @Column(name = "link")
    private String link;

    @ManyToOne
    @JoinColumn(name = "ringId")
    private Ring ring;
}
