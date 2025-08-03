package org.ventura.cpe.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ventura.cpe.core.domain.TransaccionResumenLineaAnexo;
import org.ventura.cpe.core.domain.TransaccionResumenLineaAnexoPK;
import org.ventura.cpe.core.repository.TransaccionResumenLineaAnexoRepository;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class TransaccionResumenLineaAnexoService {

    private final TransaccionResumenLineaAnexoRepository lineaAnexoRepository;

    @Transactional
    public TransaccionResumenLineaAnexo findTransaccionResumenLineaAnexo(TransaccionResumenLineaAnexoPK transaccionResumenLineaAnexoPK) {
        return lineaAnexoRepository.findById(transaccionResumenLineaAnexoPK).orElseThrow(() -> new EntityNotFoundException("No se encontro"));
    }

    @Transactional
    public void destroy(TransaccionResumenLineaAnexoPK transaccionResumenLineaAnexoPK) {
        lineaAnexoRepository.deleteById(transaccionResumenLineaAnexoPK);
    }
}
