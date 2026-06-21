package com.port.tracking.inspection;

import com.port.tracking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    List<Inspection> findByInspecteur(User inspecteur);
    List<Inspection> findByConteneurId(Long conteneurId);
    List<Inspection> findByResultatIsNull();
}