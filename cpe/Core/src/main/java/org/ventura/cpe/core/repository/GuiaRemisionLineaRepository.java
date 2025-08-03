package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.GuiaRemisionLinea;
import org.ventura.cpe.core.domain.GuiaRemisionLineaPK;

public interface GuiaRemisionLineaRepository extends JpaRepository<GuiaRemisionLinea, GuiaRemisionLineaPK> {

}
