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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "techcategory_id_generator")
    @SequenceGenerator(name = "techcategory_id_generator", sequenceName = "techcategory_id_seq", allocationSize = 1)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "techId")
    private Tech tech;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
}
