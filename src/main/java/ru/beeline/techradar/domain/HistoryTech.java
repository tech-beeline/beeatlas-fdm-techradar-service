package ru.beeline.techradar.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "history_tech")
public class HistoryTech {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_history_tech_id")
    @SequenceGenerator(name = "seq_history_tech_id", sequenceName = "seq_history_tech_id", allocationSize = 1)
    private Integer id;

    @Column(name = "ref_id")
    private Integer refId;

    @Column(name = "label")
    private String label;

    @Column(name = "description")
    private String description;

    @Column(name = "sector_id")
    private Integer sectorId;

    @Column(name = "ring_id")
    private Integer ringId;

    @Column(name = "version")
    private Integer version;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "link")
    private String link;
}
