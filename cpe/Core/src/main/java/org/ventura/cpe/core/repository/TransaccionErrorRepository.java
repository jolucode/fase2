package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionError;

public interface TransaccionErrorRepository extends JpaRepository<TransaccionError, String> {

}
