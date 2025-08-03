package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionLineaImpuestos;
import org.ventura.cpe.core.domain.TransaccionLineaImpuestosPK;

public interface TransaccionLineaImpuestosRepository extends JpaRepository<TransaccionLineaImpuestos, TransaccionLineaImpuestosPK> {

}
