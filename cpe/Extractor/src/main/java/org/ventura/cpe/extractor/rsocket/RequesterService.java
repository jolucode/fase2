package org.ventura.cpe.extractor.rsocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.util.AppURL;

import java.util.Optional;

@Slf4j
@Service
public class RequesterService {

    public void sendTransaction(Transaccion transaccion) {
        Optional.ofNullable(ClientMaps.getReqesterMap().get("request")).ifPresent(socketRequester -> {
            log.info("Enviando transaccion al request: {}", transaccion.getFEId());
            socketRequester.route(AppURL.SEND_TRANSACTION)
                    .data(transaccion.getFEId())
                    .send()
                    .block();
        });
    }
}
