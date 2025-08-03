/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.core.wsclient.ws;

import com.sap.smb.sbo.api.ICompany;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.exception.ConfigurationException;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.services.TransaccionService;
import org.ventura.cpe.core.wsclient.exception.ConectionSunatException;
import org.ventura.cpe.core.wsclient.exception.SoapFaultException;
import org.ventura.cpe.core.wsclient.handler.FileHandler;
import org.ventura.soluciones.commons.handler.DocumentNameHandler;
import org.ventura.soluciones.sunatws.gr.factory.ISunatClient;
import org.ventura.soluciones.sunatws.gr.factory.SunatClientFactory;
import org.ventura.soluciones.sunatws_ose.tci.client.OSEClient;
import org.ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import org.ventura.soluciones.sunatwsconsult.consumer.Consumer;

import javax.activation.DataHandler;
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class WSConsumerGR {

    public static int IErrorCode = 0;

    private final AppProperties properties;

    private final TransaccionService transaccionService;

    private ISunatClient sunatClient;

    private IOSEClient oseClient;

    /*
     * Objeto HANDLER para manipular los
     * documentos UBL.
     */
    private FileHandler fileHandler;

    private String docUUID;

    /**
     * Este metodo define la properties del objeto WS Consumidor segun las propertieses del
     * archivo config.xml
     *
     * @param senderRuc    Numero RUC del emisos electronico.
     * @param usuarioSec   Usuario SOL secundario del emisor electronico.
     * @param passwordSec  Clave SOL secundaria del emisor electronico.
     * @param fileHandler  Objeto FileHandler que se encarga de manipular el documento UBL.
     * @param documentType Tipo de documento a enviar.
     * @throws ConfigurationException
     */
    public void setConfiguration(String senderRuc, String usuarioSec, String passwordSec, FileHandler fileHandler, String documentType) throws ConfigurationException {
        if (log.isDebugEnabled()) {
            log.debug("+setConfiguration() [" + this.docUUID + "]");
        }

        if (null == properties) {
            log.error("setConfiguration() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_456.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_456.getMessage());
        }

        if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
            org.ventura.soluciones.sunatws.gr.consumer.Consumer consumer = new org.ventura.soluciones.sunatws.gr.consumer.Consumer(usuarioSec, passwordSec, senderRuc);

            sunatClient = SunatClientFactory.getInstance().getSunatClient(properties.getSunat().getClienteSunat(), documentType);
            sunatClient.setConsumer(consumer);
            sunatClient.printSOAP(Boolean.parseBoolean(properties.getSunat().getMostrarSoap()));

            if (log.isInfoEnabled()) {
                log.info("setConfiguration() [" + this.docUUID + "]\n" + "######################### properties SUNAT #########################\n" + "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" + "# Cliente WS: " + properties.getSunat().getClienteSunat() + "\n" + "# Mostrar SOAP: " + properties.getSunat().getMostrarSoap() + "\n" + "#######################################################################");
            }
        } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {

            /*String claveSol = properties.getSunat().getUsuario().getClaveSol();
            Consumer consumer = new Consumer(properties.getSunat().getUsuario().getUsuarioSol(), claveSol, "");*/
            String claveSol = passwordSec;
            Consumer consumer = new Consumer(usuarioSec, claveSol, "");
            oseClient = new OSEClient(properties.getSunat().getClienteSunat(), properties);
            oseClient.setConsumer(consumer);
            oseClient.printSOAP(Boolean.parseBoolean(properties.getSunat().getMostrarSoap()));

            if (log.isInfoEnabled()) {
                log.info("setConfiguration() [" + this.docUUID + "]\n" + "######################### properties OSE #########################\n" + "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" + "# Cliente WS: " + properties.getSunat().getClienteSunat() + "\n" + "# Mostrar SOAP: " + properties.getSunat().getMostrarSoap() + "\n" + "#######################################################################");
            }
        }
        /* Agregando el objeto FileHandler */
        this.fileHandler = fileHandler;
        if (log.isDebugEnabled()) {
            log.debug("-setConfiguration() [" + this.docUUID + "]");
        }
    } //setConfiguration

    public byte[] sendBill(DataHandler zipDocument, String documentName, Transaccion transaccion, ICompany Sociedad) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("+sendBill() [" + this.docUUID + "]");
        }

        validarConnectionInternet();

        byte[] cdrResponse = null;
        try {
            if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                cdrResponse = sunatClient.sendBill(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                cdrResponse = oseClient.sendBill(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
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

            if (sErrorCodeSUNAT.contentEquals("1033")) {
                IErrorCode = 1033;
                return null;
            }
            log.error("sendBill() [" + this.docUUID + "] SOAPFaultException - FaultCode: " + e.getFault().getFaultCode());
            log.error("[{}]: Excepci\u00F3n de SUNAT FaultCode: \"{}\"", new Object[]{this.docUUID, e.getFault().getFaultCode()});
            log.error("sendBill() [" + this.docUUID + "] SOAPFaultException - FaultString: " + e.getFault().getFaultString());
            log.error("[{}]: Excepci\u00F3n de SUNAT FaultString: \"{}\"", new Object[]{this.docUUID, e.getFault().getFaultString()});
            Detail detail = e.getFault().getDetail();
            String sErrorMessageSUNAT = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());
            if (null != detail) {
                log.error("sendBill() [" + this.docUUID + "] SOAPFaultException - FaultDetail: " + detail.getTextContent());
                log.error("[{}]: Excepci\u00F3n de SUNAT FaultDetail: \"{}\"", new Object[]{this.docUUID, detail.getTextContent()});
            }
            log.error("sendBill() [" + this.docUUID + "] Esta transaccion pasa al metodo reenviar.");
            transaccionService.reenviarTransaccion(transaccion);
            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " " + sErrorMessageSUNAT);
        } catch (WebServiceException e) {
            log.error("sendBill() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            log.error("[{}]: Excepci\u00F3n de tipo WebServiceException: {}", new Object[]{this.docUUID, e.getMessage()});
            log.error("sendBill() [" + this.docUUID + "] Esta transaccion pasa al metodo reenviar.");
            transaccionService.reenviarTransaccion(transaccion);
            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            log.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            log.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            log.error("[{}]: Excepcion de Generica : {}", new Object[]{this.docUUID, e.getMessage()});
            log.error("sendBill() [" + this.docUUID + "] Esta transaccion pasa al metodo reenviar.");
            transaccionService.reenviarTransaccion(transaccion);
            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }

        /* Guardar la respuesta en el DISCO DURO */
        try {
            if (cdrResponse != null && 0 < cdrResponse.length) {
                boolean isCDROk = fileHandler.saveCDRConstancy(cdrResponse, DocumentNameHandler.getInstance().
                        getCDRConstancyName(documentName), transaccion.getSNDocIdentidadNro(), transaccion.getDocIdentidadNro());
                if (log.isDebugEnabled()) {
                    log.debug("sendBill() [" + this.docUUID + "] Se guardo la constancia CDR en DISCO? " + isCDROk);
                }
            } else {
                IErrorCode = 9999;
                return null;
            }
        } catch (Exception e) {
            log.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            log.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            return null;
        }
        if (log.isDebugEnabled()) {
            log.debug("-sendBill() [" + this.docUUID + "]");
        }
        return cdrResponse;
    } //sendBill

    public String sendSummary(DataHandler zipDocument, String documentName) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("+sendSummary() [" + this.docUUID + "]");
        }

        validarConnectionInternet();

        String ticket = null;
        try {
            if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                ticket = sunatClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                ticket = oseClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
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

            log.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultString: " + e.getFault().getFaultString());
            log.error("[{}]: Excepci\u00F3n de SUNAT FaultString: \"{}\"", new Object[]{this.docUUID, e.getFault().getFaultString()});

            Detail detail = e.getFault().getDetail();
            String sErrorMessageSUNAT = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());

            if (null != detail) {
                log.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultDetail: " + detail.getTextContent());
                log.error("[{}]: Excepci\u00F3n de SUNAT FaultDetail: \"{}\"", new Object[]{this.docUUID, detail.getTextContent()});
            }

            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " " + sErrorMessageSUNAT);
        } catch (WebServiceException e) {
            log.error("sendSummary() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            log.error("[{}]: Excepcion WebServiceException : {}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            log.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            log.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            log.error("[{}]: Excepcion Generica : {}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }
        if (log.isDebugEnabled()) {
            log.debug("-sendSummary() [" + this.docUUID + "]");
        }
        return ticket;
    } //sendSummary

    public StatusResponse getStatus(String ticket, String documentName, Transaccion tc) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("+getStatus() [" + this.docUUID + "]");
        }

        validarConnectionInternet();

        StatusResponse statusResponse = null;

        try {
            if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                pe.gob.sunat.service.gr.StatusResponse statusResponseSUNAT = sunatClient.getStatus(ticket);
                statusResponse = getStatusResponse(statusResponseSUNAT);
            } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                pe.gob.sunat.service.ose_tci.StatusResponse statusResponseOSE = oseClient.getStatus(ticket);
                statusResponse = getStatusResponse(statusResponseOSE);
            }
            if (log.isDebugEnabled()) {
                log.debug("getStatus() [" + this.docUUID + "] Se obtuvo respuesta: " + statusResponse);
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


            log.error("getStatusCDR() [" + this.docUUID + "] SOAPFaultException - FaultCode: " + e.getFault().getFaultCode());
            log.error("[{}]: Excepci\u00F3n de SUNAT FaultCode: \"{}\"", new Object[]{this.docUUID, e.getFault().getFaultCode()});

            log.error("getStatusCDR() [" + this.docUUID + "] SOAPFaultException - FaultString: " + e.getFault().getFaultString());
            log.error("[{}]: Excepci\u00F3n de SUNAT FaultString: \"{}\"", new Object[]{this.docUUID, e.getFault().getFaultString()});

            Detail detail = e.getFault().getDetail();
            String sErrorMessageSUNAT = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());

            if (null != detail) {
                log.error("getStatusCDR() [" + this.docUUID + "] SOAPFaultException - FaultDetail: " + detail.getTextContent());
                log.error("[{}]: Excepci\u00F3n de SUNAT FaultDetail: \"{}\"", new Object[]{this.docUUID, detail.getTextContent()});
            }

            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " " + sErrorMessageSUNAT);
        } catch (WebServiceException e) {
            log.error("getStatus() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            log.error("[{}]: Excepci\u00F3n de tipo WebServiceException: {}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            log.error("getStatus() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            log.error("getStatus() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            log.error("[{}]: Excepci\u00F3n de tipo Gen\u00E9rica: {}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }


        /* Guardar la respuesta en el DISCO DURO */
        try {
            boolean isCDROk = fileHandler.saveCDRConstancy(statusResponse.getContent(), DocumentNameHandler.getInstance().
                    getCDRConstancyName(documentName), tc.getSNDocIdentidadNro(), tc.getDocIdentidadNro());
            if (log.isDebugEnabled()) {
                log.debug("getStatus() [" + this.docUUID + "] Se guardo la constancia CDR en DISCO? " + isCDROk);
            }
        } catch (Exception e) {
            log.error("getStatus() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            log.error("getStatus() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (log.isDebugEnabled()) {
            log.debug("-getStatus() [" + this.docUUID + "]");
        }
        return statusResponse;
    } //getStatus

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

    private StatusResponse getStatusResponse(Object object) {
        if (object instanceof pe.gob.sunat.service.gr.StatusResponse) {
            return new StatusResponse(((pe.gob.sunat.service.gr.StatusResponse) object).getStatusCode(), null, ((pe.gob.sunat.service.gr.StatusResponse) object).getContent());
        } else if (object instanceof pe.gob.sunat.service.ose_tci.StatusResponse) {
            return new StatusResponse(((pe.gob.sunat.service.ose_tci.StatusResponse) object).getStatusCode(), null, ((pe.gob.sunat.service.ose_tci.StatusResponse) object).getContent());
        }
        return null;
    } //getStatusResponse

    public class StatusResponse {

        private String statusCode;

        private String statusMessage;

        private byte[] content;

        public StatusResponse(String statusCode, String statusMessage, byte[] content) {
            this.statusCode = statusCode;
            this.statusMessage = statusMessage;
            this.content = content;
        } //StatusResponse

        public String getStatusCode() {
            return statusCode;
        } //getStatusCode

        public void setStatusCode(String value) {
            this.statusCode = value;
        } //setStatusCode

        public String getStatusMessage() {
            return statusMessage;
        } //getStatusMessage

        public void setStatusMessage(String value) {
            this.statusMessage = value;
        } //setStatusMessage

        public byte[] getContent() {
            return content;
        } //getContent

        public void setContent(byte[] value) {
            this.content = value;
        } //setContent

    } //StatusResponse

} //WSConsumerGR
