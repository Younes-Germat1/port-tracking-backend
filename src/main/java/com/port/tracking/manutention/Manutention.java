package com.port.tracking.manutention;

import com.port.tracking.conteneur.Conteneur;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "manutentions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Manutention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conteneur_id", nullable = false)
    private Conteneur conteneur;

    @Enumerated(EnumType.STRING)
    private ManutentionType type;

    private LocalDate datePrevue;
    private LocalTime heurePrevue;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}