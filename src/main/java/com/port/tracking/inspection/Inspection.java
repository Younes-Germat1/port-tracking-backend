package com.port.tracking.inspection;

import com.port.tracking.conteneur.Conteneur;
import com.port.tracking.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspections")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conteneur_id", nullable = false)
    private Conteneur conteneur;

    @ManyToOne
    @JoinColumn(name = "inspecteur_id")
    private User inspecteur;

    private String organisme;

    @Enumerated(EnumType.STRING)
    private InspectionResultat resultat;

    private LocalDateTime date;
    private String commentaire;

    @Column(name = "photo_path")
    private String photoPath;

    @Column(name = "delay_alert_sent")
    @Builder.Default
    private Boolean delayAlertSent = false;
}