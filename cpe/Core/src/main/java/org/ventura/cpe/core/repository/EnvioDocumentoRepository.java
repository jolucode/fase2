package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.EnvioDocumento;

import java.util.Optional;

public interface EnvioDocumentoRepository extends JpaRepository<EnvioDocumento, String> {

    Optional<EnvioDocumento> findByIdDocumentoAndKeySociedad(String feId, String keySociedad);
}
