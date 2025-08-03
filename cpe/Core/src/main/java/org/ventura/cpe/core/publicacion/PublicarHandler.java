package org.ventura.cpe.core.publicacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.domain.PublicardocWs;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.erp.sapbo.SAPBOService;
import org.ventura.cpe.core.repository.PublicardocRepository;
import org.ventura.cpe.core.util.DocumentoPublicado;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicarHandler {

    private final SAPBOService sapboService;

    private final AppProperties properties;

    private final PublicardocRepository publicardocRepository;

    private boolean isSuccess = false;

    public void publicarDocumento(Transaccion transaccion, HashMap<String, Path> archivos) {
        HashMap<String, String> nameSociedades = sapboService.getNameSociedades();
        String nombreSociedad = nameSociedades.get(transaccion.getKeySociedad());
        PublicardocWs publicardocWs = new PublicardocWs();
        publicardocWs.setFEId(transaccion.getFEId());
        publicardocWs.setRSRuc(transaccion.getDocIdentidadNro());
        publicardocWs.setRSDescripcion(nombreSociedad);
        publicardocWs.setDOCId(transaccion.getDOCId());
        publicardocWs.setFechaPublicacionPortal(LocalDateTime.now());
        Date fechaEmision = transaccion.getDOCFechaEmision();
        publicardocWs.setDOCFechaEmision(LocalDate.from(fechaEmision.toInstant()));
        publicardocWs.setDOCMontoTotal(transaccion.getDOCMontoTotal());
        publicardocWs.setDOCCodigo(transaccion.getDOCCodigo());
        publicardocWs.setSNDocIdentidadNro(transaccion.getSNDocIdentidadNro());
        publicardocWs.setSNRazonSocial(transaccion.getSNRazonSocial());
        publicardocWs.setSNEMail(transaccion.getSNEMail());
        publicardocWs.setRutaPDF(archivos.get("rutaPDF").toString());
        publicardocWs.setRutaXML(archivos.get("rutaXML").toString());
        publicardocWs.setRutaZIP(archivos.get("rutaCDR").toString());
        publicardocWs.setFETipoTrans(transaccion.getFETipoTrans());
        publicardocWs.setSNEMailSecundario(transaccion.getSNEMailSecundario());
        if ("03".equals(transaccion.getDOCCodigo())) {
            publicardocWs.setEstadoSUNAT('D');
        } else {
            publicardocWs.setEstadoSUNAT(transaccion.getFEEstado().charAt(0));
        }
        publicardocWs.setDOCMONCodigo(transaccion.getDOCMONCodigo());
        publicardocWs.setDOCMONNombre(transaccion.getDOCMONNombre());

        if(transaccion.getEMail().equals("-")) publicardocWs.setEMailEmisor(transaccion.getEMail());
        else publicardocWs.setEMailEmisor(transaccion.getEMail());

        publicardocWs.setEstadoPublicacion('A');
        publicardocRepository.saveAndFlush(publicardocWs);
        DocumentoPublicado documentoPublicado = new DocumentoPublicado(properties.getWebService().getWsUsuario(), properties.getWebService().getWsClave(), publicardocWs);
        String location = properties.getWebService().getWsLocation();
        WebClient webClient = WebClient.builder().baseUrl(location).build();
        Object result = webClient.post().uri("/api/publicar").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                bodyValue(documentoPublicado).exchange().flatMap(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                clientResponse.body((clientHttpResponse, context) -> clientHttpResponse.getBody());
                return clientResponse.bodyToMono(String.class);
            } else if (clientResponse.statusCode().is4xxClientError()) {
                clientResponse.body((clientHttpResponse, context) -> clientHttpResponse.getBody());
                return clientResponse.bodyToMono(String.class);
            } else if (clientResponse.statusCode().is2xxSuccessful()) {
                isSuccess = true;
                return clientResponse.bodyToMono(PublicacionResponse.class);
            } else return clientResponse.bodyToMono(PublicacionResponse.class);
        }).block();
        assert result != null;
        Optional<PublicacionResponse> optional = Optional.of(result).filter(PublicacionResponse.class::isInstance).map(PublicacionResponse.class::cast);
        if (optional.isPresent()) {
            PublicacionResponse publicacionResponse = optional.get();
            if (isSuccess) log.info(publicacionResponse.getMensaje());
            else log.error(publicacionResponse.getError());
        } else log.info(result.toString());
        isSuccess = false;
    }

    public void publicardocWsBaja(Transaccion transaccion, HashMap<String, Path> archivos, int codigoResponse) {
        HashMap<String, String> nameSociedades = sapboService.getNameSociedades();
        String nombreSociedad = nameSociedades.get(transaccion.getKeySociedad());
        PublicardocWs publicardocWs = new PublicardocWs();
        publicardocWs.setFEId(transaccion.getFEId());
        publicardocWs.setRSRuc(transaccion.getDocIdentidadNro());
        publicardocWs.setRSDescripcion(nombreSociedad);
        publicardocWs.setDOCId(transaccion.getDOCId());
        publicardocWs.setFechaPublicacionPortal(LocalDateTime.now());
        Date fechaEmision = transaccion.getDOCFechaEmision();
        publicardocWs.setDOCFechaEmision(LocalDate.from(fechaEmision.toInstant()));
        publicardocWs.setDOCMontoTotal(transaccion.getDOCMontoTotal());
        publicardocWs.setDOCCodigo(transaccion.getDOCCodigo());
        publicardocWs.setSNDocIdentidadNro(transaccion.getSNDocIdentidadNro());
        publicardocWs.setSNRazonSocial(transaccion.getSNRazonSocial());
        publicardocWs.setSNEMail(transaccion.getSNEMail());
        publicardocWs.setRutaPDF(archivos.get("rutaPDF").toString());
        publicardocWs.setRutaXML(archivos.get("rutaXML").toString());
        publicardocWs.setRutaZIP(archivos.get("rutaCDR").toString());
        publicardocWs.setFETipoTrans(transaccion.getFETipoTrans());
        publicardocWs.setSNEMailSecundario(transaccion.getSNEMailSecundario());
        if (true) {
            publicardocWs.setEstadoSUNAT('V');
        } else {
            if ("03".equals(transaccion.getDOCCodigo())) {
                publicardocWs.setEstadoSUNAT('D');
            } else if (codigoResponse == 2106) {//CODIGO AÑADIDO PARA MANDAR AL PORTAL COMO APROBADO CUANDO EL DOCUMENTO YA ESTA EMITIDO EN SUNAT
                log.info("Aprobado para publicación de comunicacion de baja al enviar por 2da vez a sunat");
                publicardocWs.setEstadoSUNAT('V');
            } else {
                publicardocWs.setEstadoSUNAT('R');
            }
        }
        publicardocWs.setDOCMONCodigo(transaccion.getDOCMONCodigo());
        publicardocWs.setDOCMONNombre(transaccion.getDOCMONNombre());

        if(transaccion.getEMail().equals("-")) publicardocWs.setEMailEmisor("");
        else publicardocWs.setEMailEmisor(transaccion.getEMail());

        publicardocWs.setEstadoPublicacion('A');
        publicardocRepository.saveAndFlush(publicardocWs);
        DocumentoPublicado documentoPublicado = new DocumentoPublicado(properties.getWebService().getWsUsuario(), properties.getWebService().getWsClave(), publicardocWs);
        String location = properties.getWebService().getWsLocation();
        WebClient webClient = WebClient.builder().baseUrl(location).build();
        String result = webClient.post().uri("/api/publicar").header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                bodyValue(documentoPublicado).exchange().flatMap(clientResponse -> {
            if (clientResponse.statusCode().is5xxServerError()) {
                clientResponse.body((clientHttpResponse, context) -> clientHttpResponse.getBody());
                return clientResponse.bodyToMono(String.class);
            } else if (clientResponse.statusCode().is2xxSuccessful()) {
                return clientResponse.bodyToMono(String.class);
            } else return clientResponse.bodyToMono(String.class);
        }).block();
        log.info(result);
    }
}
