package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionDocrefers;
import org.ventura.cpe.core.domain.TransaccionDocrefersPK;

public interface TransaccionDocrefersRepository extends JpaRepository<TransaccionDocrefers, TransaccionDocrefersPK> {

}
