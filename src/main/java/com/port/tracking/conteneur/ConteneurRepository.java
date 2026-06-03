package com.port.tracking.conteneur;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConteneurRepository extends JpaRepository<Conteneur, Long> {
    List<Conteneur> findByFicheId(Long ficheId);
    List<Conteneur> findByStatut(ConteneurStatut statut);
}