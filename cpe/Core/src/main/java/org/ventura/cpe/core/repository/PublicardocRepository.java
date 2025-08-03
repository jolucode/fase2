package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ventura.cpe.core.domain.PublicardocWs;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface PublicardocRepository extends JpaRepository<PublicardocWs, String> {

    @Query(value = "SELECT p FROM PublicardocWs p WHERE p.estadoPublicacion =:estadoPublicacion")
    List<PublicardocWs> listarHabilitadas(Character estadoPublicacion);
}
