package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.Transaccion;

public interface GuiaRemisionRepository extends JpaRepository<Transaccion, String> {

}
