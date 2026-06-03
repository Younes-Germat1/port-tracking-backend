package com.port.tracking.conteneur;

import com.port.tracking.fiche.FicheSuiveuse;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conteneurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conteneur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fiche_id", nullable = false)
    private FicheSuiveuse fiche;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConteneurStatut statut = ConteneurStatut.ARRIVE;

    private String zone;
    private String rangee;
    private String position;
    private String quai;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @PrePersist
    public void prePersist() {
        this.arrivedAt = LocalDateTime.now();
    }
}