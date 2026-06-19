package com.port.tracking.marchandise;

import com.port.tracking.fiche.FicheSuiveuse;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "marchandises")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Marchandise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fiche_id", nullable = false)
    private FicheSuiveuse fiche;

    private String description;

    private String classification;

    @Column(name = "code_sh")
    private String codeSH;

    private Double poids;
    private Double volume;
}