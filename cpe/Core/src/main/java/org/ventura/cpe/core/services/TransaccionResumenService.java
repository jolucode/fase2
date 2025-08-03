package org.ventura.cpe.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ventura.cpe.core.domain.TransaccionResumen;
import org.ventura.cpe.core.repository.TransaccionResumenRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransaccionResumenService {

    private final TransaccionResumenRepository resumenRepository;

    public TransaccionResumen saveResumen(TransaccionResumen transaccionResumen) {
        return resumenRepository.saveAndFlush(transaccionResumen);
    }

    @Transactional
    public List<TransaccionResumen> findPendientes() {
        return resumenRepository.findPendientes();
    }

    @Transactional
    public void deleteTransaccionResumen(TransaccionResumen transaccionResumen) {
        resumenRepository.deleteById(transaccionResumen.getIdTransaccion());
    }

    @Transactional
    public void marcarEnviado(TransaccionResumen transaccionResumen) {
        transaccionResumen.setEstado("K");
        resumenRepository.saveAndFlush(transaccionResumen);
    }
}
