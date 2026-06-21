package com.port.tracking.conteneur;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConteneurRepository extends JpaRepository<Conteneur, Long> {
    List<Conteneur> findByFicheId(Long ficheId);
    List<Conteneur> findByStatut(ConteneurStatut statut);
    Optional<Conteneur> findByZoneAndRangeeAndPosition(String zone, String rangee, String position);
}