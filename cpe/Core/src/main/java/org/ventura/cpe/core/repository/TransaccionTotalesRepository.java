package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionTotales;
import org.ventura.cpe.core.domain.TransaccionTotalesPK;

public interface TransaccionTotalesRepository extends JpaRepository<TransaccionTotales, TransaccionTotalesPK> {

}
