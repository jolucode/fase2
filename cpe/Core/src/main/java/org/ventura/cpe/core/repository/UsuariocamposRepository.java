package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.Usuariocampos;

import java.util.Optional;

public interface UsuariocamposRepository extends JpaRepository<Usuariocampos, Integer> {

    Optional<Usuariocampos> findByNombre(String nombre);

}
