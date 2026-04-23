/*
 * Copyright (c) 2024 PJSC VimpelCom
 */

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
@Table(name = "tech_version", schema = "techradar")
public class TechVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tech_version")
    @SequenceGenerator(name = "seq_tech_version", sequenceName = "seq_tech_version", allocationSize = 1)
    private Integer id;

    @Column(name = "version_start", length = 50)
    private String versionStart;

    @Column(name = "version_end", length = 50)
    private String versionEnd;

    @Column(name = "status_id", nullable = false)
    private Integer statusId;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "deleted_date")
    private LocalDateTime deletedDate;

    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;

    @Column(name = "tech_id", nullable = false)
    private Integer techId;

    @Override
    public String toString() {
        return "TechVersion{" +
                "id=" + id +
                ", versionStart='" + versionStart + '\'' +
                ", versionEnd='" + versionEnd + '\'' +
                ", statusId=" + statusId +
                ", createdDate=" + createdDate +
                ", deletedDate=" + deletedDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", techId=" + techId +
                '}';
    }
}