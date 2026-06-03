package com.port.tracking.historique;

import com.port.tracking.fiche.FicheSuiveuse;
import com.port.tracking.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historique")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Historique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fiche_id", nullable = false)
    private FicheSuiveuse fiche;

    @ManyToOne
    @JoinColumn(name = "acteur_id")
    private User acteur;

    private String action;
    private String details;
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        this.timestamp = LocalDateTime.now();
    }
}