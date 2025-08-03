package org.ventura.cpe.core.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.StatusType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StatusReasonCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StatusReasonType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.xml.transform.StringSource;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.config.ISunatConfig;
import org.ventura.cpe.core.config.IUBLConfig;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.exception.VenturaExcepcion;
import org.ventura.cpe.core.pdfcreator.PDFCreator;
import org.ventura.cpe.core.services.AppFileService;
import org.ventura.cpe.core.util.RespuestaSunat;
import org.ventura.cpe.core.wrapper.UBLDocumentWRP;
import org.ventura.cpe.core.ws.response.TransaccionRespuesta;
import org.ventura.cpe.core.wsclient.handler.FileHandler;
import pe.gob.sunat.service.ose_tci.CdrStatusResponse;
import un.unece.uncefact.data.specification.corecomponenttypeschemamodule._2.TextType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResponseProcessor {

    private final PDFCreator pdfCreator;

    private final AppProperties properties;

    private final AppFileService fileService;

    private String docUUID;

    public TransaccionRespuesta processCDRResponse(byte[] cdrConstancy, File signedDocument, FileHandler fileHandler, String documentName, String documentCode, UBLDocumentWRP documentWRP, Transaccion transaction) {
        if (log.isDebugEnabled()) {
            log.debug("+processCDRResponse() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = null;
        try {
            if ((documentCode.equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE) || documentCode.equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE) || documentCode.equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) && documentWRP.getTransaccion().getDOCSerie().substring(0, 1).equalsIgnoreCase("B")) {
                TransaccionRespuesta.Sunat sunatResponse = new TransaccionRespuesta.Sunat();
                sunatResponse.setCodigo(0);
                sunatResponse.setMensaje("El Documento esta a la espera de respuesta del Resumen Diario");
                sunatResponse.setId(documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero());
                byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
                Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);
                Optional<byte[]> optionalBytes = pdfCreator.createPDFDocument(ublDocument, documentCode, documentWRP, transaction);
                if (optionalBytes.isPresent()) {
                    if (log.isDebugEnabled()) {
                        log.debug("processCDRResponse() [" + this.docUUID + "] Si existe PDF en bytes.");
                    }
                    /*
                     * Guardar el PDF en DISCO
                     */
                    boolean isPDFOk = fileHandler.storePDFDocumentInDisk(optionalBytes.get(), documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                    if (log.isInfoEnabled()) {
                        log.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                    }
                } else {
                    log.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
                }
                transactionResponse = new TransaccionRespuesta();
                transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITDO_ESPERA);
                transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                transactionResponse.setIdentificador(sunatResponse.getId());
                transactionResponse.setMensaje(sunatResponse.getMensaje());
                transactionResponse.setSunat(sunatResponse);
                transactionResponse.setUuid(this.docUUID);
                transactionResponse.setXml(documentBytes);
                transactionResponse.setZip(cdrConstancy);
                optionalBytes.ifPresent(transactionResponse::setPdf);
            } else {
                TransaccionRespuesta.Sunat sunatResponse = RespuestaSunat.SetRespuestaSUNAT(cdrConstancy, Integer.parseInt(properties.getSunat().getAmbiente()), documentCode, properties.getSunat().getIntegracionWS());
                byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
                Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);
                if (sunatResponse == null) {
                    transactionResponse = new TransaccionRespuesta();
                    transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION);
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("processCDRResponse() [" + this.docUUID + "]" + "\n############################ RESPUESTA CDR ############################" + "\n SUNAT_code: " + sunatResponse.getCodigo() + "\n SUNAT_message: " + sunatResponse.getMensaje() + "\n#######################################################################");
                    }
                    /*
                     * APROBADO:
                     * 		- Si la constancia CDR retorna un codigo igual a 0.
                     * 		- Si la constancia CDR retorna un codigo mayor o igual a 4000,
                     * 		  esta aprobado pero con observaciones.
                     *
                     * RECHAZADO:
                     * 		- Si la constancia CDR retorna un codigo entre el 2000 al 3999,
                     * 		  esta rechazado por Sunat.
                     */
                    if ((IVenturaError.ERROR_0.getId() == sunatResponse.getCodigo()) || (4000 <= sunatResponse.getCodigo())) {
                        if (log.isInfoEnabled()) {
                            log.info("processCDRResponse() [" + this.docUUID + "] El documento fue APROBADO por SUNAT.");
                        }
                        log.info("[{}] El documento [{}] fue APROBADO por SUNAT.", this.docUUID, documentName);
                        Optional<byte[]> optionalBytes = pdfCreator.createPDFDocument(ublDocument, documentCode, documentWRP, transaction);
                        if (optionalBytes.isPresent()) {
                            boolean isPDFOk = fileHandler.storePDFDocumentInDisk(optionalBytes.get(), documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                            if (log.isInfoEnabled()) {
                                log.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                            }
                        } else {
                            log.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
                        }
                        transactionResponse = new TransaccionRespuesta();
                        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_APROBADO);
                        transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                        transactionResponse.setIdentificador(sunatResponse.getId());
                        transactionResponse.setMensaje(sunatResponse.getMensaje());
                        transactionResponse.setSunat(sunatResponse);
                        transactionResponse.setUuid(this.docUUID);
                        transactionResponse.setXml(documentBytes);
                        transactionResponse.setZip(cdrConstancy);
                        optionalBytes.ifPresent(transactionResponse::setPdf);
                    } else {
                        log.info("[{}] El documento [{}] fue RECHAZADO por SUNAT.", this.docUUID, documentName);
                        transactionResponse = new TransaccionRespuesta();
                        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_RECHAZADO);
                        transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                        transactionResponse.setIdentificador(sunatResponse.getId());
                        transactionResponse.setMensaje(sunatResponse.getMensaje());
                        transactionResponse.setSunat(sunatResponse);
                        transactionResponse.setUuid(this.docUUID);
                        transactionResponse.setXml(documentBytes);
                        transactionResponse.setZip(cdrConstancy);
                    }
                }
            }
        } catch (VenturaExcepcion e) {
            log.error("processCDRResponse() [" + this.docUUID + "] VenturaExcepcion -->" + ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            log.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        return transactionResponse;
    }

    public TransaccionRespuesta processResponseSinCDR_1033(Transaccion transaction) {
        if (log.isDebugEnabled()) {
            log.debug("+processResponseSinCDR_1033() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = new TransaccionRespuesta();
        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION);
        transactionResponse.setCodigoWS(998);
        transactionResponse.setMensaje(IVenturaError.ERROR_458.getMessage());
        transactionResponse.setEmitioError("Sunat");
        if (log.isDebugEnabled()) {
            log.debug("-processResponseSinCDR_1033() [" + this.docUUID + "]");
        }
        return transactionResponse;
    }

    public TransaccionRespuesta processResponseSinCDRExcepcion(Transaccion transaction) {
        if (log.isDebugEnabled()) {
            log.debug("+processResponseSinCDRExcepcion() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = new TransaccionRespuesta();
        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_CDR_NULO);
        transactionResponse.setCodigoWS(998);
        transactionResponse.setMensaje(IVenturaError.ERROR_458.getMessage());
        transactionResponse.setEmitioError("Sunat");
        if (log.isDebugEnabled()) {
            log.debug("-processResponseSinCDRExcepcion() [" + this.docUUID + "]");
        }
        return transactionResponse;
    }

    public byte[] processCDRResponseContigencia(byte[] cdrConstancy, File signedDocument, FileHandler fileHandler, String documentName, String documentCode, UBLDocumentWRP documentWRP, Transaccion transaccion) {
        try {
            Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);
            Optional<byte[]> optionalBytes = pdfCreator.createPDFDocument(ublDocument, documentCode, documentWRP, transaccion);
            if (optionalBytes.isPresent()) {
                if (log.isDebugEnabled()) {
                    log.debug("processCDRResponse() [" + this.docUUID + "] Si existe PDF en bytes.");
                }
                boolean isPDFOk = fileHandler.storePDFContigenciaDocumentInDisk(optionalBytes.get(), documentName, transaccion.getSNDocIdentidadNro(), transaccion.getDocIdentidadNro());
                if (log.isInfoEnabled()) {
                    log.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                }
                return optionalBytes.get();
            } else {
                log.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
            }
        } catch (Exception e) {
            log.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    public TransaccionRespuesta processCDRResponseDummy(byte[] cdrConstancy, File signedDocument, FileHandler fileHandler, String documentName, String documentCode, UBLDocumentWRP documentWRP, Transaccion transaction) {
        if (log.isDebugEnabled()) {
            log.debug("+processCDRResponse() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = null;
        String nameDoc = "";
        try {
            switch (documentCode) {
                case "01":
                    nameDoc = "La Factura";
                    break;
                case "03":
                    nameDoc = "La Boleta";
                    break;
                case "07":
                    nameDoc = "La Nota de Credito";
                    break;
                case "08":
                    nameDoc = "La Nota de Debito";
                    break;
                case "09":
                    nameDoc = "La Guia de Remisiin";
                    break;
                case "20":
                    nameDoc = "El Comprobante de Retencion";
                    break;
                case "40":
                    nameDoc = "El Comprobante de Perpcecion";
                    break;
            }

            String SerieCorrelativo = documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero();
            TransaccionRespuesta.Sunat sunatResponse = new TransaccionRespuesta.Sunat();
            sunatResponse.setCodigo(0);
            sunatResponse.setMensaje("[" + SerieCorrelativo + "](0)" + nameDoc + "numero" + SerieCorrelativo + " ,ha sido aceptada");
            sunatResponse.setId(documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero());
            byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
            Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);

            Optional<byte[]> optionalBytes = pdfCreator.createPDFDocument(ublDocument, documentCode, documentWRP, transaction);

            if (optionalBytes.isPresent()) {
                if (log.isDebugEnabled()) {
                    log.debug("processCDRResponse() [" + this.docUUID + "] Si existe PDF en bytes.");
                }
                /*
                 * Guardar el PDF en DISCO
                 */
                boolean isPDFOk = fileHandler.storePDFDocumentInDisk(optionalBytes.get(), documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                if (log.isInfoEnabled()) {
                    log.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                }
            } else {
                log.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
            }

            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_APROBADO);
            transactionResponse.setCodigoWS(sunatResponse.getCodigo());
            transactionResponse.setIdentificador(sunatResponse.getId());
            transactionResponse.setMensaje(sunatResponse.getMensaje());
            transactionResponse.setSunat(sunatResponse);
            transactionResponse.setUuid(this.docUUID);
            transactionResponse.setXml(documentBytes);
            transactionResponse.setZip(cdrConstancy);
            optionalBytes.ifPresent(transactionResponse::setPdf);

            if (log.isInfoEnabled()) {
                log.info("processCDRResponse() [" + this.docUUID + "]" + "\n############################ RESPUESTA CDR ############################" + "\n SUNAT_code: " + sunatResponse.getCodigo() + "\n SUNAT_message: " + sunatResponse.getMensaje() + "\n#######################################################################");
            }

        } catch (Exception e) {
            log.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        return transactionResponse;
    }

    public TransaccionRespuesta processCDRResponseV2(CdrStatusResponse statusResponse, String documentName, Transaccion transaccion) {
        TransaccionRespuesta transactionResponse = null;
        try {
            TransaccionRespuesta.Sunat sunatResponse = RespuestaSunat.SetRespuestaSUNAT(statusResponse.getContent(), Integer.parseInt(properties.getSunat().getAmbiente()), "BJ", properties.getSunat().getIntegracionWS());
            if (log.isInfoEnabled()) {
                log.info("processCDRResponseV2() [" + this.docUUID + "]" + "\n############################ RESPUESTA CDR ############################" + "\n STATUS_RESP_code: " + statusResponse.getStatusCode() + "\n STATUS_RESP_message: " + statusResponse.getStatusMessage() + "\n SUNAT_code: " + (null != sunatResponse ? sunatResponse.getCodigo() : "") + "\n SUNAT_message: " + (null != sunatResponse ? sunatResponse.getCodigo() : "") + "\n#######################################################################");
            }
            if (null != sunatResponse) {
                /*
                 * APROBADO:
                 * 		- Si la constancia CDR retorna un codigo igual a 0.
                 * 		- Si la constancia CDR retorna un codigo mayor o igual a 4000,
                 * 		  esta aprobado pero con observaciones.
                 *
                 * RECHAZADO:
                 * 		- Si la constancia CDR retorna un codigo entre el 2000 al 3999,
                 * 		  esta rechazado por Sunat.
                 */
                if ((IVenturaError.ERROR_0.getId() == sunatResponse.getCodigo()) || (4000 <= sunatResponse.getCodigo())) {
                    if (log.isInfoEnabled()) {
                        log.info("processCDRResponseV2() [" + this.docUUID + "] El documento fue APROBADO por SUNAT.");
                    }
                    log.info("[" + this.docUUID + "] El documento [" + documentName + "] fue APROBADO por SUNAT.");
                    transactionResponse = new TransaccionRespuesta();
                    transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_APROBADO);
                    transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                    transactionResponse.setIdentificador(sunatResponse.getId());
                    transactionResponse.setMensaje(sunatResponse.getMensaje());
                    transactionResponse.setSunat(sunatResponse);
                    transactionResponse.setUuid(this.docUUID);
                    transactionResponse.setZip(statusResponse.getContent());
                    //transactionResponse.setPdf(filePDF);
                    //transactionResponse.setXml(fileXML);
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("processCDRResponseV2() [" + this.docUUID + "] El documento fue RECHAZADO por SUNAT.");
                    }
                    log.info("[" + this.docUUID + "] El documento [" + documentName + "] fue RECHAZADO por SUNAT.");
                    /*
                     * Documento RECHAZADO por Sunat.
                     */
                    transactionResponse = new TransaccionRespuesta();
                    transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_RECHAZADO);
                    transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                    transactionResponse.setIdentificador(sunatResponse.getId());
                    transactionResponse.setMensaje(sunatResponse.getMensaje());
                    transactionResponse.setSunat(sunatResponse);
                    transactionResponse.setUuid(this.docUUID);
                    transactionResponse.setZip(statusResponse.getContent());
                    //transactionResponse.setPdf(filePDF);
                    //transactionResponse.setXml(fileXML);
                }
            } else {
                log.info("[{}] ERROR: {}", this.docUUID, IVenturaError.ERROR_458.getMessage());
                transactionResponse = new TransaccionRespuesta();

                transactionResponse = processResponseSinCDR(transaccion);
            }
        } catch (VenturaExcepcion e) {
            log.error("processCDRResponseV2() [" + this.docUUID + "] VenturaExcepcion -->" + ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            log.error("processCDRResponseV2() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        return transactionResponse;
    }

    private TransaccionRespuesta processResponseSinCDR(Transaccion transaction) {
        TransaccionRespuesta transactionResponse = null;
        transactionResponse = new TransaccionRespuesta();
        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION);
        transactionResponse.setCodigoWS(998);
        transactionResponse.setMensaje(IVenturaError.ERROR_458.getMessage());
        transactionResponse.setEmitioError("Sunat");

        if (log.isDebugEnabled()) {
            log.debug("-processCDRResponse() [" + this.docUUID + "]");
        }
        return transactionResponse;
    }

    public TransaccionRespuesta proccessResponse(byte[] cdrConstancy, File signedDocument, String documentName, String documentCode, UBLDocumentWRP documentWRP, Transaccion transaction) throws JAXBException, IOException {
        Optional<byte[]> unzipedResponse = unzipResponse(cdrConstancy);
        String descripcionRespuesta = "";
        TransaccionRespuesta transactionResponse = new TransaccionRespuesta();
        int codigoObservacion = 0;
        int codigoRespuesta = 0;
        if (unzipedResponse.isPresent()) {
            StringBuilder descripcion = new StringBuilder();
            String result = new String(unzipedResponse.get());
            StringSource stringResult = new StringSource(result);
            JAXBContext jaxbContext = JAXBContext.newInstance(ApplicationResponseType.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<ApplicationResponseType> jaxbElement = unmarshaller.unmarshal(stringResult, ApplicationResponseType.class);
            ApplicationResponseType applicationResponse = jaxbElement.getValue();
            List<DocumentResponseType> documentResponse = applicationResponse.getDocumentResponse();
            List<TransaccionRespuesta.Observacion> observaciones = new ArrayList<>();
            for (DocumentResponseType documentResponseType : documentResponse) {
                ResponseType response = documentResponseType.getResponse();
                ResponseCodeType responseCode = response.getResponseCode();
                codigoRespuesta = Optional.ofNullable(responseCode.getValue()).map(s -> s.isEmpty() ? null : s).map(Integer::parseInt).orElse(0);
                List<DescriptionType> descriptions = response.getDescription();
                for (DescriptionType description : descriptions) {
                    descripcion.append(description.getValue());
                }
                List<StatusType> statusTypes = response.getStatus();
                for (StatusType statusType : statusTypes) {
                    List<StatusReasonType> statusReason = statusType.getStatusReason();
                    String mensajes = statusReason.parallelStream().map(TextType::getValue).collect(Collectors.joining("\n"));
                    StatusReasonCodeType statusReasonCode = statusType.getStatusReasonCode();
                    codigoObservacion = Optional.ofNullable(statusReasonCode.getValue()).map(s -> s.isEmpty() ? null : s).map(Integer::parseInt).orElse(0);
                    TransaccionRespuesta.Observacion observacion = new TransaccionRespuesta.Observacion(codigoObservacion, mensajes);
                    observaciones.add(observacion);
                }
            }
            descripcionRespuesta = descripcion.toString();
            System.out.println(descripcionRespuesta);
            TransaccionRespuesta.Sunat sunatResponse = new TransaccionRespuesta.Sunat();
            sunatResponse.setListaObs(observaciones);
            sunatResponse.setCodigo(codigoRespuesta);
            sunatResponse.setMensaje(descripcionRespuesta);
            sunatResponse.setEmisor(transaction.getDocIdentidadNro());
            boolean isDocumentType = (documentCode.equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE) || documentCode.equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE)
                    || documentCode.equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) && documentWRP.getTransaccion().getDOCSerie().substring(0, 1).equalsIgnoreCase("B");
            if (isDocumentType) {
                sunatResponse.setCodigo(0);
                sunatResponse.setMensaje(descripcionRespuesta);
                sunatResponse.setId(documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero());
                byte[] documentBytes = fileService.convertFileToBytes(signedDocument);
                Object ublDocument = fileService.getSignedDocument(signedDocument, documentCode);
                Optional<byte[]> optionalBytes = pdfCreator.createPDFDocument(ublDocument, documentCode, documentWRP, transaction);
                if (optionalBytes.isPresent()) {
                    boolean isPDFOk = fileService.storePDFDocumentInDisk(optionalBytes.get(), documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                    log.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DISCO: [{}]", isPDFOk ? "CORRECTO" : "ERROR");
                } else {
                    log.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
                }
                transactionResponse = new TransaccionRespuesta();
                transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITDO_ESPERA);
                transactionResponse.setCodigoWS(codigoObservacion);
                transactionResponse.setIdentificador(sunatResponse.getId());
                transactionResponse.setMensaje(sunatResponse.getMensaje());
                transactionResponse.setSunat(sunatResponse);
                transactionResponse.setUuid(this.docUUID);
                transactionResponse.setXml(documentBytes);
                transactionResponse.setZip(cdrConstancy);
                transactionResponse.setPdf(optionalBytes.orElse(null));
            } else {
                return chequearRespuestaValida(cdrConstancy, signedDocument, transaction, documentName, documentCode, documentWRP, sunatResponse);
            }
        }
        return transactionResponse;
    }

    private TransaccionRespuesta chequearRespuestaValida(byte[] cdrConstancy, File signedDocument, Transaccion transaction, String documentName, String documentCode, UBLDocumentWRP documentWRP, TransaccionRespuesta.Sunat sunatResponse) {
        TransaccionRespuesta transactionResponse;
        byte[] documentBytes = fileService.convertFileToBytes(signedDocument);
        Object ublDocument = fileService.getSignedDocument(signedDocument, documentCode);
        if (log.isInfoEnabled()) {
            log.info("processCDRResponse() [" + this.docUUID + "]"
                    + "\n############################ RESPUESTA CDR ############################"
                    + "\n SUNAT_code: " + sunatResponse.getCodigo()
                    + "\n SUNAT_message: " + sunatResponse.getMensaje()
                    + "\n#######################################################################");
        }
        /*
         * APROBADO:
         * 		- Si la constancia CDR retorna un codigo igual a 0.
         * 		- Si la constancia CDR retorna un codigo mayor o igual a 4000,
         * 		  esta aprobado pero con observaciones.
         *
         * RECHAZADO:
         * 		- Si la constancia CDR retorna un codigo entre el 2000 al 3999,
         * 		  esta rechazado por Sunat.
         */

        if (ISunatConfig.SUCCESS_RESPONSE == sunatResponse.getCodigo() || 4000 <= sunatResponse.getCodigo()) {
            pdfCreator.setDocUUID(this.docUUID);
            Optional<byte[]> optional = pdfCreator.createPDFDocument(ublDocument, documentCode, documentWRP, transaction);
            if (optional.isPresent()) {
                boolean isPDFOk = fileService.storePDFDocumentInDisk(optional.get(), documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                if (log.isInfoEnabled()) {
                    log.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DISCO: [{}]", isPDFOk ? "CORRECTO" : "ERROR");
                }
            } else {
                log.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
            }
            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_APROBADO);
            transactionResponse.setCodigoWS(sunatResponse.getCodigo());
            transactionResponse.setIdentificador(sunatResponse.getId());
            transactionResponse.setMensaje(sunatResponse.getMensaje());
            transactionResponse.setSunat(sunatResponse);
            transactionResponse.setUuid(this.docUUID);
            transactionResponse.setXml(documentBytes);
            transactionResponse.setZip(cdrConstancy);
            optional.ifPresent(transactionResponse::setPdf);
        } else {
            if (log.isInfoEnabled()) {
                log.info("processCDRResponse() [" + this.docUUID + "] El documento fue RECHAZADO por SUNAT.");
            }
            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_RECHAZADO);
            transactionResponse.setCodigoWS(sunatResponse.getCodigo());
            transactionResponse.setIdentificador(sunatResponse.getId());
            transactionResponse.setMensaje(sunatResponse.getMensaje());
            transactionResponse.setSunat(sunatResponse);
            transactionResponse.setUuid(this.docUUID);
            transactionResponse.setXml(documentBytes);
            transactionResponse.setZip(cdrConstancy);
        }
        return transactionResponse;
    }

    public Optional<byte[]> unzipResponse(byte[] cdr) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(cdr);
        ZipInputStream zis = new ZipInputStream(bais);
        ZipEntry entry = zis.getNextEntry();
        byte[] xml = null;
        if (entry != null) {
            while (entry != null) {
                if (!entry.isDirectory()) {
                    String name = entry.getName();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] bytesIn = new byte['?'];
                    int read;
                    while ((read = zis.read(bytesIn)) != -1) {
                        baos.write(bytesIn, 0, read);
                    }
                    baos.close();
                    xml = baos.toByteArray();
                    fileService.storeCdrResponseInDisk(xml, name);
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
            zis.close();
            return Optional.ofNullable(xml);
        } else {
            zis.close();
            log.error("Extraer respuesta SUNAT: El Cdr devuelvo está dañado");
            return Optional.empty();
        }
    }

    public void setDocUUID(String docUUID) {
        this.docUUID = docUUID;
    }
}
