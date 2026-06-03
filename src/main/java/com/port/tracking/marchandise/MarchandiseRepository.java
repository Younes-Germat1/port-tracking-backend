package com.port.tracking.marchandise;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MarchandiseRepository extends JpaRepository<Marchandise, Long> {
    List<Marchandise> findByFicheId(Long ficheId);
}