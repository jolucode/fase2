package org.ventura.cpe.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ventura.cpe.core.domain.TransaccionComprobantepagoUsuario;
import org.ventura.cpe.core.domain.TransaccionComprobantepagoUsuarioPK;

public interface TransaccionComprobantepagoUsuarioRepository extends JpaRepository<TransaccionComprobantepagoUsuario, TransaccionComprobantepagoUsuarioPK> {

}
