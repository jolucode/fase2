package org.ventura.cpe.extractor.rsocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.stereotype.Controller;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.erp.sapbo.SAPBOService;
import org.ventura.cpe.core.repository.TransaccionRepository;
import org.ventura.cpe.core.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
public class ClientsController {

    private final SAPBOService sapboService;

    private final TransaccionRepository transaccionRepository;

    @Autowired
    public ClientsController(SAPBOService sapboService, TransaccionRepository transaccionRepository) {
        this.sapboService = sapboService;
        this.transaccionRepository = transaccionRepository;
    }

    @ConnectMapping("connect")
    public void setup(RSocketRequester requester, @Payload String component) {
        log.info("@ConnectMapping(connect), component:{}", component);
        requester.rsocket()
                .onClose()
                .doFinally(f -> ClientMaps.getReqesterMap().remove(component, requester));
        ClientMaps.getReqesterMap().put(component, requester);
        log.info("Enviando estado al cliente...");
        requester.route("status").data("component:" + component + " is connected!")
                .retrieveMono(String.class)
                .subscribe(
                        data -> log.info("received data from the client: {}", data),
                        error -> log.error(error.getLocalizedMessage(), error),
                        () -> log.info("done"));
    }

    @MessageMapping(AppURL.SOCIEDADES)
    public CompanyResponse sociedades() {
        List<ConnectedCompany> sociedadesConectadas = SAPBOService.getSociedadesConectadas();
        return new CompanyResponse(sociedadesConectadas);
    }

    @MessageMapping(AppURL.CAPTURAR_CODIGO)
    public void capturarCodigo(@Payload CodigoBarra codigoBarra) {
        log.info("Obteniendo Codigo [{}] desde el Resquest", codigoBarra.getDocumentoId());
        Optional<Transaccion> optional = transaccionRepository.findById(codigoBarra.getDocumentoId());
        optional.ifPresent(transaccion -> {
            log.info("Guardando Digest Value [{}] y Codigo de Barra del Documento [{}]", codigoBarra.getDigestValue(), codigoBarra.getDocumentoId());
            sapboService.capturarCodigo(transaccion, codigoBarra.getBarcodeValue(), codigoBarra.getDigestValue());
        });
    }

    @MessageMapping(AppURL.GUARDAR_RESPUESTA)
    public void guardarRespuesta(@Payload TransactionResponse transactionResponse) {
        log.info("Obteniendo Codigo [{}] desde el Resquest", transactionResponse.getDocumentoId());
        Optional<Transaccion> optional = transaccionRepository.findById(transactionResponse.getDocumentoId());
        optional.ifPresent(transaccion -> {
            log.info("Mostrando respuesta del Documento [{}]", transactionResponse.getDocumentoId());
            log.info(transactionResponse.getMessage());
            sapboService.actualizarMensaje(transaccion, transactionResponse.getEstado(), transactionResponse.getMessage());
        });
    }

    @MessageMapping(AppURL.ANEXAR_DOCUMENTOS)
    public void anexarDocumentos(@Payload AnexoDocumento anexoDocumento) {
        log.info("Obteniendo los anexos del documento [{}] desde el Resquest", anexoDocumento.getDocumentoId());
        Optional<Transaccion> optional = transaccionRepository.findById(anexoDocumento.getDocumentoId());
        optional.ifPresent(transaccion -> {
            log.info("Anexando Documento Impreso de [{}] en : {}", anexoDocumento.getDocumentoId(), anexoDocumento.getPdfPath());
            log.info("Anexando XML de [{}] en : {}", anexoDocumento.getDocumentoId(), anexoDocumento.getXmlPath());
            log.info("Anexando La Respuesta CDR de [{}] en : {}", anexoDocumento.getDocumentoId(), anexoDocumento.getCdrPath());
            Path pdfPath = Paths.get(anexoDocumento.getPdfPath());
            sapboService.agregarAnexo(transaccion, pdfPath, false);
            Path xmlPath = Paths.get(anexoDocumento.getXmlPath());
            sapboService.agregarAnexo(transaccion, xmlPath, false);
            Path cdrPath = Paths.get(anexoDocumento.getCdrPath());
            sapboService.agregarAnexo(transaccion, cdrPath, false);
        });
    }
}
