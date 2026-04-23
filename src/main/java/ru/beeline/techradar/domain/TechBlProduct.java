/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.domain;

import lombok.*;

import javax.persistence.*;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tech_bl_product")
public class TechBlProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tech_bl_product_id")
    @SequenceGenerator(name = "seq_tech_bl_product_id", sequenceName = "seq_tech_bl_product_id", allocationSize = 1)
    private Integer id;

    @Column(name = "cmdb_code", nullable = false, length = 80)
    private String cmdbCode;

    @Column(name = "tech_bl_id", nullable = false)
    private Integer techBlId;

    @Override
    public String toString() {
        return "TechBlProduct{" +
                "id=" + id +
                ", cmdbCode='" + cmdbCode + '\'' +
                ", techBlId=" + techBlId +
                '}';
    }
}