package org.ventura.cpe.core.wsclient.ws;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.exception.ConfigurationException;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.wsclient.exception.ConectionSunatException;
import org.ventura.cpe.core.wsclient.exception.SoapFaultException;
import org.ventura.soluciones.sunatws_ose.tci.client.OSEClient;
import org.ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import org.ventura.soluciones.sunatwsconsult.consumer.Consumer;
import org.ventura.soluciones.sunatwsconsult.factory.ISunatClient;
import org.ventura.soluciones.sunatwsconsult.factory.SunatClientFactory;

import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Esta clase contiene los metodos para consultar si se genero la constancia de
 * recepcion de un tipo de documento especifico conectandose al servicio web de
 * CONSULTAS de Sunat.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WSConsumerConsult {

    private final AppProperties properties;

    /*
     * Cliente del servicio web de consultas
     */
    private ISunatClient sunatClient;

    private IOSEClient oseClient;

    private String docUUID;

    /**
     * Este metodo define la properties del objeto WS Consumidor de consultas segun las
     * propertieses del archivo config.xml
     *
     * @param senderRuc   Numero RUC del emisor electronico.
     * @param usuarioSec  Usuario SOL secundario del emisor electronico.
     * @param passwordSec Clave SOL secundaria del emisor electronico.
     * @throws ConfigurationException
     */
    public void setConfiguration(String senderRuc, String usuarioSec, String passwordSec) throws ConfigurationException {
        if (log.isDebugEnabled()) {
            log.debug("+setConfiguration() [" + this.docUUID + "]");
        }

        if (null == properties) {
            log.error("setConfiguration() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_464.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_464.getMessage());
        }
        if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
            org.ventura.soluciones.sunatwsconsult.consumer.Consumer consumer = new org.ventura.soluciones.sunatwsconsult.consumer.Consumer(usuarioSec, passwordSec, senderRuc);
            sunatClient = SunatClientFactory.getInstance().getSunatClient(properties.getSunat().getClienteSunat());
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
        /* Archivo de properties */
        if (log.isDebugEnabled()) {
            log.debug("-setConfiguration() [" + this.docUUID + "]");
        }
    } //setConfiguration

    public StatusResponse getStatusCDR(String documentRUC, String documentType, String documentSerie, Integer documentNumber) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("+getStatusCDR() [" + this.docUUID + "]");
        }
        validarConnectionInternet();
        StatusResponse statusResponse = null;
        try {
            if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                pe.gob.sunat.service.consult.StatusResponse statusResponseSUNAT = sunatClient.getStatusCDR(documentRUC, documentType, documentSerie, documentNumber);
                statusResponse = getStatusResponse(statusResponseSUNAT);
            } else if (properties.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                pe.gob.sunat.service.ose_tci.CdrStatusResponse statusResponseOSE = oseClient.getStatusCDR(documentRUC, documentType, documentSerie, documentNumber);
                statusResponse = getStatusResponse(statusResponseOSE);
            }
            if (log.isInfoEnabled()) {
                log.info("getStatusCDR() [" + this.docUUID + "] Se obtuvo respuesta: " + statusResponse);
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

            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " : " + sErrorMessageSUNAT);
        } catch (WebServiceException e) {
            log.error("getStatusCDR() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            log.error("[{0}]: Excepci\\u00F3n de tipo WebServiceException : {1}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_154);
        } catch (Exception e) {
            log.error("getStatusCDR() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            log.error("getStatusCDR() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            log.error("[{0}]: Excepci\\u00F3n de tipo Gen\\u00E9rica: {1}", new Object[]{this.docUUID, e.getMessage()});
            throw new ConectionSunatException(IVenturaError.ERROR_155);
        }
        if (log.isDebugEnabled()) {
            log.debug("-getStatus() [" + this.docUUID + "]");
        }
        return statusResponse;
    } //getStatusCDR

    private void validarConnectionInternet() throws ConectionSunatException {
        try {
            log.info("CONSULTAR DOCUMENTO >>> Verificando conexi\u00F3n a internet...");
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            log.info("CONSULTAR DOCUMENTO >>> Conexi\u00F3n exitosa a internet");
        } catch (MalformedURLException ex) {
            throw new ConectionSunatException(IVenturaError.ERROR_5000);
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
        if (object instanceof pe.gob.sunat.service.consult.StatusResponse) {
            return new StatusResponse(((pe.gob.sunat.service.consult.StatusResponse) object).getStatusCode(), ((pe.gob.sunat.service.consult.StatusResponse) object).getStatusMessage(), ((pe.gob.sunat.service.consult.StatusResponse) object).getContent());
        } else if (object instanceof pe.gob.sunat.service.ose_tci.CdrStatusResponse) {
            return new StatusResponse(((pe.gob.sunat.service.ose_tci.CdrStatusResponse) object).getStatusCode(), ((pe.gob.sunat.service.ose_tci.CdrStatusResponse) object).getStatusMessage(), ((pe.gob.sunat.service.ose_tci.CdrStatusResponse) object).getContent());
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

} //WSConsumerConsult
