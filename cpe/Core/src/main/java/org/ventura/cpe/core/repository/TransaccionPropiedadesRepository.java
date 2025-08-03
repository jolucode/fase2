package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionPropiedades;
import org.ventura.cpe.core.domain.TransaccionPropiedadesPK;

public interface TransaccionPropiedadesRepository extends JpaRepository<TransaccionPropiedades, TransaccionPropiedadesPK> {

}
