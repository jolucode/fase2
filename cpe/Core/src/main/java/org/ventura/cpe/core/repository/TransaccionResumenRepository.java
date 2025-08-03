package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ventura.cpe.core.domain.TransaccionResumen;

import java.util.List;

public interface TransaccionResumenRepository extends JpaRepository<TransaccionResumen, String> {

    @Query(value = "SELECT t FROM TransaccionResumen t WHERE t.estado IN('G','K','D','A')")
    List<TransaccionResumen> findPendientes();

    List<TransaccionResumen> findByEstado(String estado);
}
