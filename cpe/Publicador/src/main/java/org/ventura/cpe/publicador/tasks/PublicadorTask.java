package org.ventura.cpe.publicador.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.ventura.cpe.core.domain.BdsMaestras;
import org.ventura.cpe.core.domain.PublicardocWs;
import org.ventura.cpe.core.repository.BdsMaestrasRepository;
import org.ventura.cpe.core.repository.PublicardocRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class PublicadorTask implements Runnable {

    private final PublicardocRepository publicardocRepository;
    private final BdsMaestrasRepository bdsMaestrasRepository;
    private final ServicePublicador publicadorService;

    @Override
    public void run() {
        LocalTime ahora = LocalTime.now();
        log.info("Hora Actual Publicacion: {}", ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        List<PublicardocWs> listaPublicacion = publicardocRepository.listarHabilitadas('A');
        List<BdsMaestras> listarSociedades = bdsMaestrasRepository.listarSociedades();
        boolean hasDocuments = false;
        for (PublicardocWs publicardocWs : listaPublicacion) {
            log.info("Publicando Documento [{}] con fecha: {}", publicardocWs.getFEId(), publicardocWs.getDOCFechaEmision());
            publicadorService.publicar(publicardocWs, listarSociedades);
            hasDocuments = true;
        }
        if (!hasDocuments) {
            System.out.println();
        }
    }
}
