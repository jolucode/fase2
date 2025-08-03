package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionComprobantePago;
import org.ventura.cpe.core.domain.TransaccionComprobantePagoPK;

public interface TransaccionComprobantePagoRepository extends JpaRepository<TransaccionComprobantePago, TransaccionComprobantePagoPK> {

}
