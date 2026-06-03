package com.port.tracking.fiche;

import com.port.tracking.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FicheRepository extends JpaRepository<FicheSuiveuse, Long> {
    List<FicheSuiveuse> findByImportateur(User importateur);
    List<FicheSuiveuse> findByStatut(FicheStatut statut);
}