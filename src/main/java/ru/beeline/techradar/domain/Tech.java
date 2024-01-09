package ru.beeline.techradar.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tech")
public class Tech {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "label")
    private String label;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "sectorId")
    private Sector sector;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @Column(name = "link")
    private String link;

    @ManyToOne
    @JoinColumn(name = "ringId")
    private Ring ring;
}
