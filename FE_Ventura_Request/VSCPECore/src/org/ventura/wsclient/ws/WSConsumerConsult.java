package org.ventura.wsclient.ws;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.tempuri.*;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.soluciones.commons.exception.ConfigurationException;
import org.ventura.soluciones.commons.exception.error.IVenturaError;
import org.ventura.soluciones.sunatws_ose.tci.client.OSEClient;
import org.ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import org.ventura.soluciones.sunatws_ose.tci.consumer.Consumer;
import org.ventura.soluciones.sunatwsconsult.factory.ISunatClient;
import org.ventura.soluciones.sunatwsconsult.factory.SunatClientFactory;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.ConectionSunatException;
import org.ventura.wsclient.exception.SoapFaultException;
import pe.gob.sunat.service.StatusCdrResponse;

import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Esta clase contiene los metodos para consultar si se genero la constancia de
 * recepcion de un tipo de documento especifico conectandose al servicio web de
 * CONSULTAS de Sunat.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class WSConsumerConsult {

    private final Logger logger = Logger.getLogger(WSConsumerConsult.class);

    /*
     * Cliente del servicio web de consultas
     */
    private ISunatClient sunatClient;

    private IOSEClient oseClient;

    private IbillService conoseClient;

    /* Archivo de configuracion */
    private Configuracion configuracion;

    private final String docUUID;

    /**
     * Constructor privado para evitar instancias.
     *
     * @param docUUID UUID identificador del documento.
     */
    private WSConsumerConsult(String docUUID) {
        this.docUUID = docUUID;
    } //WSConsumerConsult

    /**
     * Este metodo crea una nueva instancia de la clase WSConsumerConsult.
     *
     * @param docUUID UUID identificador del documento.
     * @return Retorna una nueva instancia de la clase WSConsumerConsult.
     */
    public static synchronized WSConsumerConsult newInstance(String docUUID) {
        return new WSConsumerConsult(docUUID);
    } //newInstance

    /**
     * Este metodo define la configuracion del objeto WS Consumidor de consultas segun las
     * configuraciones del archivo config.xml
     *
     * @param senderRuc        Numero RUC del emisor electronico.
     * @param usuarioSec       Usuario SOL secundario del emisor electronico.
     * @param passwordSec      Clave SOL secundaria del emisor electronico.
     * @param configuracionObj Archivo de configuracion convertido en objeto.
     * @throws ConfigurationException
     */
    public void setConfiguration(String senderRuc, String usuarioSec, String passwordSec, Configuracion configuracionObj)
            throws ConfigurationException {
        this.configuracion = configuracionObj;
        if (logger.isDebugEnabled()) {
            logger.debug("+setConfiguration() [" + this.docUUID + "]");
        }
        if (null == configuracionObj) {
            logger.error("setConfiguration() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_464.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_464.getMessage());
        }
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
            org.ventura.soluciones.sunatwsconsult.consumer.Consumer consumer = new org.ventura.soluciones.sunatwsconsult.consumer.Consumer(usuarioSec, passwordSec, senderRuc);
            sunatClient = SunatClientFactory.getInstance().getSunatClient(configuracionObj.getSunat().getClienteSunat());
            sunatClient.setConsumer(consumer);
            sunatClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));
            if (logger.isInfoEnabled()) {
                logger.info("setConfiguration() [" + this.docUUID + "]\n" +
                        "######################### CONFIGURACION SUNAT #########################\n" +
                        "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
                        "# Cliente WS: " + configuracionObj.getSunat().getClienteSunat() + "\n" +
                        "# Mostrar SOAP: " + configuracionObj.getSunat().getMostrarSoap() + "\n" +
                        "#######################################################################");
            }
        }  if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
            String claveSol = passwordSec;//configuracionObj.getSunat().getUsuario().getClaveSol().getValue();
            Consumer consumer = new Consumer(usuarioSec, claveSol);

            oseClient = new OSEClient(configuracionObj.getSunat().getClienteSunat());
            oseClient.setConsumer(consumer);
            oseClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));

            if (logger.isInfoEnabled()) {
                logger.info("setConfiguration() [" + this.docUUID + "]\n" +
                        "######################### CONFIGURACION OSE #########################\n" +
                        "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
                        "# Cliente WS: " + configuracionObj.getSunat().getClienteSunat() + "\n" +
                        "# Mostrar SOAP: " + configuracionObj.getSunat().getMostrarSoap() + "\n" +
                        "#######################################################################");
            }
        }
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {
            /*String claveSol;
            if (Boolean.parseBoolean(configuracionObj.getSunat().getUsuario().getClaveSol().getEncriptado())) {
                claveSol = Criptor.Desencriptar(configuracionObj.getSunat().getUsuario().getClaveSol().getValue());
            } else {
                claveSol = configuracionObj.getSunat().getUsuario().getClaveSol().getValue();
            }*/
            /*String claveSol = passwordSec;
            if (Boolean.parseBoolean(configuracionObj.getSunat().getUsuario().getClaveSol().getEncriptado())) {
                claveSol = Criptor.Desencriptar(configuracionObj.getSunat().getUsuario().getClaveSol().getValue());
            }*/
            String claveSol = passwordSec;
            Consumer consumer = new Consumer(usuarioSec, claveSol);
            conoseClient = new CONOSEClient(configuracionObj.getSunat().getClienteSunat());
            conoseClient.setConsumer(consumer);
            //conoseClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));

            if (logger.isInfoEnabled()) {
                logger.info("setConfiguration() [" + this.docUUID + "]\n" +
                        "######################### CONFIGURACION CONOSE #########################\n" +
                        "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
                        "# Cliente WS: " + configuracionObj.getSunat().getClienteSunat() + "\n" +
                        "# Mostrar SOAP: " + configuracionObj.getSunat().getMostrarSoap() + "\n" +
                        "#######################################################################");
            }
        }
        /* Archivo de configuracion */
        this.configuracion = configuracionObj;
        if (logger.isDebugEnabled()) {
            logger.debug("-setConfiguration() [" + this.docUUID + "]");
        }
    } //setConfiguration

    public StatusResponse getStatusCDR(String documentRUC, String documentType, String documentSerie, Integer documentNumber) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+getStatusCDR() [" + this.docUUID + "]");
        }
        StatusResponse statusResponse = null;
        try {
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                pe.gob.sunat.service.consult.StatusResponse statusResponseSUNAT = sunatClient.getStatusCDR(documentRUC, documentType, documentSerie, documentNumber);
                statusResponse = getStatusResponse(statusResponseSUNAT);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                pe.gob.sunat.service.ose_tci.CdrStatusResponse statusResponseOSE = oseClient.getStatusCDR(documentRUC, documentType, documentSerie, documentNumber);
                statusResponse = getStatusResponse(statusResponseOSE);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {
                pe.gob.sunat.service.conose.StatusCdrResponse statusResponseCONOSE = conoseClient.getStatusCdr(documentRUC, documentType, documentSerie, documentNumber);
                statusResponse = getStatusResponse(statusResponseCONOSE);
            }
            if (logger.isInfoEnabled()) {
                logger.info("getStatusCDR() [" + this.docUUID + "] Se obtuvo respuesta: " + statusResponse);
            }
        } catch (SOAPFaultException e) {
            String sErrorCodeSUNAT = null;
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                sErrorCodeSUNAT = e.getFault().getFaultCode() + " - " +e.getFault().getFaultString();
            }else if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                sErrorCodeSUNAT = e.getFault().getFaultString();
            }else if(configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {
                sErrorCodeSUNAT = e.getFault().getFaultCode();
            }

            Detail detail = e.getFault().getDetail();
            if (null != detail) {
                logger.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultDetail: " + detail.getTextContent());
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de SUNAT FaultDetail: \"{1}\"", new Object[]{this.docUUID, detail.getTextContent()});
            }

            throw new SoapFaultException(sErrorCodeSUNAT);
        } catch (WebServiceException e) {
            logger.error("getStatusCDR() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\\u00F3n de tipo WebServiceException : {1}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_154);
        } catch (Exception e) {
            logger.error("getStatusCDR() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("getStatusCDR() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\\u00F3n de tipo Gen\\u00E9rica: {1}", new Object[]{this.docUUID, e.getMessage()});
            throw new ConectionSunatException(IVenturaError.ERROR_155);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getStatus() [" + this.docUUID + "]");
        }
        return statusResponse;
    } //getStatusCDR

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
        if (object instanceof pe.gob.sunat.service.consult.StatusResponse && this.configuracion.getSunat().getIntegracionWS().contains("SUNAT")) {
            return new StatusResponse(((pe.gob.sunat.service.consult.StatusResponse) object).getStatusCode(),
                    ((pe.gob.sunat.service.consult.StatusResponse) object).getStatusMessage(), ((pe.gob.sunat.service.consult.StatusResponse) object).getContent());
        }
        if (object instanceof pe.gob.sunat.service.ose_tci.CdrStatusResponse) {
            return new StatusResponse(((pe.gob.sunat.service.ose_tci.CdrStatusResponse) object).getStatusCode(),
                    ((pe.gob.sunat.service.ose_tci.CdrStatusResponse) object).getStatusMessage(), ((pe.gob.sunat.service.ose_tci.CdrStatusResponse) object).getContent());
        }
        if (object instanceof pe.gob.sunat.service.conose.StatusCdrResponse && this.configuracion.getSunat().getIntegracionWS().contains("CONOSE")) {
            return new StatusResponse(((pe.gob.sunat.service.conose.StatusCdrResponse) object).getStatusCode(),
                    ((pe.gob.sunat.service.conose.StatusCdrResponse) object).getStatusMessage(), ((pe.gob.sunat.service.conose.StatusCdrResponse) object).getContent());
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

        public boolean hasContent() {
            return Optional.ofNullable(content).isPresent();
        }

    } //StatusResponse

} //WSConsumerConsult
