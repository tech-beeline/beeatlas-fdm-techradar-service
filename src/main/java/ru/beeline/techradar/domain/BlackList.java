/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

package ru.beeline.techradar.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;


@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "black_list")
public class BlackList {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_black_list_id")
    @SequenceGenerator(name = "seq_black_list_id", sequenceName = "seq_black_list_id", allocationSize = 1)
    private Integer id;

    @Column(name = "label", nullable = false, length = 80)
    private String label;

    @Column(name = "review", nullable = false)
    private Boolean review;

    @Column(name = "create_date", nullable = false)
    private Date createDate;

    @Column(name = "delete_date")
    private Date deleteDate;

    @Override
    public String toString() {
        return "BlackList{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", review=" + review +
                ", createDate=" + createDate +
                ", deleteDate=" + deleteDate +
                '}';
    }
}