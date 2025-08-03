package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionAnticipo;
import org.ventura.cpe.core.domain.TransaccionAnticipoPK;

public interface TransaccionAnticipoRepository extends JpaRepository<TransaccionAnticipo, TransaccionAnticipoPK> {

}
