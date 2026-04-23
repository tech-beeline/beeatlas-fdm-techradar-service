/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TechCategory that = (TechCategory) o;
        return Objects.equals(tech, that.tech) &&
                Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tech, category);
    }
}
