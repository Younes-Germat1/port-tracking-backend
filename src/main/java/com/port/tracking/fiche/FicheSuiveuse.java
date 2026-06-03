package com.port.tracking.fiche;

import com.port.tracking.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fiches_suiveuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FicheSuiveuse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "importateur_id", nullable = false)
    private User importateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FicheStatut statut = FicheStatut.EN_ATTENTE;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}