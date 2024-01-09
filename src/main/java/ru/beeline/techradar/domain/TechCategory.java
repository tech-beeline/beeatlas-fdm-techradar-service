package ru.beeline.techradar.domain;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "techcategory")
public class TechCategory {
    @Id
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "techId")
    private Tech tech;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
}
