package org.ventura.cpe.core.wsclient.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.exception.Document1033Exception;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.services.TransaccionService;
import org.ventura.cpe.core.util.AppFaultMessageResolver;
import org.ventura.cpe.core.wsclient.exception.ConectionSunatException;
import org.ventura.cpe.core.wsclient.exception.SoapFaultException;
import org.ventura.soluciones.commons.handler.DocumentNameHandler;
import pe.gob.sunat.service.ose_tci.*;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBElement;
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

/**
 * Esta clase contiene los metodos para enviar los documentos UBL firmados al
 * servicio web de Sunat.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com) implements
 * ISunatClient
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WSConsumerNew {

    public static int IErrorCode = 0;

    private final AppProperties properties;

    private final TransaccionService transaccionService;

    private final WebServiceTemplate webServiceTemplate;

    private final AppFaultMessageResolver messageResolver;

    private String docUUID;

    public byte[] sendBill(DataHandler zipDocument, String documentName, Transaccion transaccion) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("+sendBill() [" + this.docUUID + "]");
        }
        validarConnectionInternet();
        byte[] cdrResponse = null;
        try {
            String zipName = DocumentNameHandler.getInstance().getZipName(documentName);
            SendBill sendBill = new SendBill();
            sendBill.setContentFile(zipDocument);
            sendBill.setFileName(zipName);
            Optional<Object> optional = Optional.ofNullable(webServiceTemplate.marshalSendAndReceive(sendBill));
            if (optional.isPresent()) {
                Object response = optional.get();
                if (response instanceof JAXBElement) {
                    Object value = ((JAXBElement) response).getValue();
                    Optional<SendBillResponse> declararOptional = Optional.ofNullable(value).filter(SendBillResponse.class::isInstance).map(SendBillResponse.class::cast);
                    if (declararOptional.isPresent()) {
                        SendBillResponse billResponse = declararOptional.get();
                        return billResponse.getApplicationResponse();
                    }
                }
            } else {
                String lastError = messageResolver.getLastError();
                if (lastError.contains("1033")) {
                    throw new Document1033Exception(IVenturaError.ERROR_1033.getMessage());
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("sendBill() [" + this.docUUID + "] Se obtuvo respuesta del WS.");
            }
        } catch (SOAPFaultException e) {
            String sErrorCodeSUNAT = null;
            if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                sErrorCodeSUNAT = obtenerErrorCodeSUNAT(e.getFault().getFaultCode());
            } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                sErrorCodeSUNAT = e.getFault().getFaultString();
            }
            if (log.isDebugEnabled()) {
                log.debug("sendBill() [" + this.docUUID + "] Codigo de la excepcion: " + sErrorCodeSUNAT);
            }

            assert sErrorCodeSUNAT != null;
            if (sErrorCodeSUNAT.contentEquals("1033")) {
                IErrorCode = 1033;
            }
            log.error("sendBill() [" + this.docUUID + "] SOAPFaultException - FaultCode: " + e.getFault().getFaultCode());
            log.error("sendBill() [" + this.docUUID + "] SOAPFaultException - FaultString: " + e.getFault().getFaultString());
            Detail detail = e.getFault().getDetail();
            String sErrorMessageSUNAT = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());
            if (null != detail) {
                log.error("sendBill() [" + this.docUUID + "] SOAPFaultException - FaultDetail: " + detail.getTextContent());
            }
            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " : " + sErrorMessageSUNAT);
        } catch (WebServiceException e) {
            log.error("sendBill() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            transaccionService.reenviarTransaccion(transaccion);
            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            if (e instanceof Document1033Exception) {
                throw new Document1033Exception(IVenturaError.ERROR_1033.getMessage());
            }
            log.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            transaccionService.reenviarTransaccion(transaccion);
            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }
        return cdrResponse;
    }

    public CdrStatusResponse getStatusCDR(String documentRUC, String documentType, String documentSerie, Integer documentNumber) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("+getStatusCDR() [" + this.docUUID + "]");
        }
        validarConnectionInternet();
        CdrStatusResponse statusResponse = null;
        try {
            GetStatusCdr statusCdr = new GetStatusCdr();
            statusCdr.setRucComprobante(documentRUC);
            statusCdr.setTipoComprobante(documentType);
            statusCdr.setSerieComprobante(documentSerie);
            statusCdr.setNumeroComprobante(documentNumber);
            Optional<Object> optional = Optional.ofNullable(webServiceTemplate.marshalSendAndReceive(statusCdr));
            if (optional.isPresent()) {
                Object response = optional.get();
                if (response instanceof JAXBElement) {
                    Object value = ((JAXBElement) response).getValue();
                    Optional<GetStatusCdrResponse> declararOptional = Optional.ofNullable(value).filter(GetStatusCdrResponse.class::isInstance).map(GetStatusCdrResponse.class::cast);
                    if (declararOptional.isPresent()) {
                        GetStatusCdrResponse cdrResponse = declararOptional.get();
                        return cdrResponse.getStatus();
                    }
                }
            }
        } catch (SOAPFaultException e) {
            String sErrorCodeSUNAT = null;
            if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                sErrorCodeSUNAT = obtenerErrorCodeSUNAT(e.getFault().getFaultCode());
            } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                sErrorCodeSUNAT = e.getFault().getFaultString();
            }
            if (log.isDebugEnabled()) {
                log.debug("sendBill() [" + this.docUUID + "] Codigo de la excepcion: " + sErrorCodeSUNAT);
            }
            log.error("[{}]: Excepci\u00F3n de SUNAT FaultCode: \"{}\"", new Object[]{this.docUUID, e.getFault().getFaultCode()});
            log.error("[{}]: Excepci\u00F3n de SUNAT FaultString: \"{}\"", new Object[]{this.docUUID, e.getFault().getFaultString()});
            Detail detail = e.getFault().getDetail();
            String sErrorMessageSUNAT = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());
            if (null != detail) {
                log.error("[{}]: Excepci\u00F3n de SUNAT FaultDetail: \"{}\"", new Object[]{this.docUUID, detail.getTextContent()});
            }
            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " : " + sErrorMessageSUNAT);
        } catch (WebServiceException e) {
            log.error("[{}]: Excepci\\u00F3n de tipo WebServiceException : {}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_154);
        } catch (Exception e) {
            log.error("getStatusCDR() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new ConectionSunatException(IVenturaError.ERROR_155);
        }
        if (log.isDebugEnabled()) {
            log.debug("-getStatus() [" + this.docUUID + "]");
        }
        return statusResponse;
    } //getStatusCDR

    public String sendSummary(DataHandler zipDocument, String documentName) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("+sendSummary() [" + this.docUUID + "]");
        }
        validarConnectionInternet();
        String ticket = null;
        try {
            if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                log.info("Es Sunat");
//                ticket = sunatClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                log.info("Es OSE");
//                ticket = oseClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            }
            if (log.isDebugEnabled()) {
                log.debug("sendSummary() [" + this.docUUID + "] Se obtuvo respuesta del WS. Ticket: " + ticket);
            }
        } catch (SOAPFaultException e) {
            String sErrorCodeSUNAT = null;
            if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                sErrorCodeSUNAT = obtenerErrorCodeSUNAT(e.getFault().getFaultCode());
            } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                sErrorCodeSUNAT = e.getFault().getFaultString();
            }
            if (log.isDebugEnabled()) {
                log.debug("sendSummary() [" + this.docUUID + "] Codigo de la excepcion: " + sErrorCodeSUNAT);
            }
            log.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultCode: " + e.getFault().getFaultCode());
            log.error("[{}]: Excepci\u00F3n de SUNAT FaultCode: \"{}\"", new Object[]{this.docUUID, e.getFault().getFaultCode()});
            Detail detail = e.getFault().getDetail();
            String sErrorMessageSUNAT = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());
            if (null != detail) {
                log.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultDetail: " + detail.getTextContent());
                log.error("[{}]: Excepci\u00F3n de SUNAT FaultDetail: \"{}\"", new Object[]{this.docUUID, detail.getTextContent()});
            }
            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " : " + sErrorMessageSUNAT);
        } catch (WebServiceException e) {
            log.error("sendSummary() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            log.error("[{}]: Excepci\u00F3n de tipo WebServiceException: {}", new Object[]{this.docUUID, e.getMessage()});
            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            log.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            log.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            log.error("[{}]: Excepci\u00F3n de tipo Gen\u00E9rica: {}", new Object[]{this.docUUID, e.getMessage()});
            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }
        if (log.isDebugEnabled()) {
            log.debug("-sendSummary() [" + this.docUUID + "]");
        }
        return ticket;
    } //sendSummary

    private void validarConnectionInternet() throws ConectionSunatException {
        try {
            log.info("Verificando conexion a internet...");
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            log.info("Conexion exitosa a internet");
        } catch (IOException ex) {
            throw new ConectionSunatException(IVenturaError.ERROR_5000);
        }
    } //validarConnectionInternet

    private String obtenerErrorCodeSUNAT(String sFaultCode) {
        String[] sErrorCodeSplit = sFaultCode.split("[\\D|\\W]");
        String sErrorCodeSUNAT = "";
        for (String aux : sErrorCodeSplit) {
            if (!aux.equals("")) {
                sErrorCodeSUNAT = aux;
            }
        }
        return sErrorCodeSUNAT;
    } //obtenerErrorCodeSUNAT

    public void setDocUUID(String docUUID) {
        this.docUUID = docUUID;
    }
} //WSConsumer
