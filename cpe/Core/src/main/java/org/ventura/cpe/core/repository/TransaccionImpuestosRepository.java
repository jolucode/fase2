package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionImpuestos;
import org.ventura.cpe.core.domain.TransaccionImpuestosPK;

public interface TransaccionImpuestosRepository extends JpaRepository<TransaccionImpuestos, TransaccionImpuestosPK> {

}
