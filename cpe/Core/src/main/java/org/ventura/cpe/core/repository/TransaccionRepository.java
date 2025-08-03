package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ventura.cpe.core.domain.Transaccion;

import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, String> {

    @Query(value = "SELECT t FROM Transaccion t WHERE t.fEEstado IN ('R','P','S') ORDER BY t.fEMaxSalto ASC")
    List<Transaccion> findPendientes();

    @Query(value = "SELECT t FROM Transaccion t WHERE t.fEEstado IN ('N','E','C','G','W') ORDER BY t.fEMaxSalto ASC")
    List<Transaccion> findDisponibles();
}
