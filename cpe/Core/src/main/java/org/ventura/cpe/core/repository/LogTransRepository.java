package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.LogTrans;

public interface LogTransRepository extends JpaRepository<LogTrans, Integer> {

}
