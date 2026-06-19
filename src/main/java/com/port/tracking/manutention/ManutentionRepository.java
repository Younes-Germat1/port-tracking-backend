package com.port.tracking.manutention;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ManutentionRepository extends JpaRepository<Manutention, Long> {
    List<Manutention> findByConteneurIdOrderByDatePrevueAscHeurePrevueAsc(Long conteneurId);
}