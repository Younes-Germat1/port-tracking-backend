package com.port.tracking.fiche;

import com.port.tracking.marchandise.Marchandise;
import com.port.tracking.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private String importateurNom;
    private String importateurAdresse;
    private String importateurContact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FicheStatut statut = FicheStatut.EN_ATTENTE;

    @OneToMany(mappedBy = "fiche", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Marchandise> marchandises = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "fiche_organismes", joinColumns = @JoinColumn(name = "fiche_id"))
    @Column(name = "organisme")
    @Builder.Default
    private List<String> organismes = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "delay_alert_sent")
    @Builder.Default
    private Boolean delayAlertSent = false;

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