package com.port.tracking.historique;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    List<Historique> findByFicheIdOrderByTimestampDesc(Long ficheId);
}