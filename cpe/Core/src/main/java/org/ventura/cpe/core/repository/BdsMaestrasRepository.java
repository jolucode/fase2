package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.ventura.cpe.core.domain.BdsMaestras;
import org.ventura.cpe.core.domain.PublicardocWs;

import java.util.List;

public interface BdsMaestrasRepository extends JpaRepository<BdsMaestras, String> {

    @Query(value = "SELECT b FROM BdsMaestras b")
    List<BdsMaestras> listarSociedades();
}
