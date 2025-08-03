/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.wsclient.ws;

import com.sap.smb.sbo.api.ICompany;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.tempuri.CONOSEClient;
import org.tempuri.IbillService;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.soluciones.commons.exception.ConfigurationException;
import org.ventura.soluciones.commons.exception.error.ErrorObj;
import org.ventura.soluciones.commons.exception.error.IVenturaError;
import org.ventura.soluciones.commons.handler.DocumentNameHandler;
import org.ventura.soluciones.sunatws.gr.factory.ISunatClient;
import org.ventura.soluciones.sunatws.gr.factory.SunatClientFactory;
import org.ventura.soluciones.sunatws_ose.tci.client.OSEClient;
import org.ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import org.ventura.soluciones.sunatws_ose.tci.consumer.Consumer;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.ConectionSunatException;
import org.ventura.wsclient.exception.SoapFaultException;
import org.ventura.wsclient.handler.FileHandler;
import pe.gob.sunat.service.StatusCdrResponse;

import javax.activation.DataHandler;
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;

public class WSConsumerGR {

    private final Logger logger = Logger.getLogger(WSConsumerNew.class);

    public static int IErrorCode = 0;

    /*
     * Cliente del servicio web
     */
    private ISunatClient sunatClient;

    private IOSEClient oseClient;

    private IbillService conoseClient;

    /*
     * Objeto HANDLER para manipular los
     * documentos UBL.
     */
    private FileHandler fileHandler;

    /* Archivo de configuracion */
    private Configuracion configuracion;

    private String docUUID;

    /**
     * Constructor privado para evitar instancias.
     *
     * @param docUUID UUID identificador del documento.
     */
    private WSConsumerGR(String docUUID) {
        this.docUUID = docUUID;
    } //WSConsumer

    /**
     * Este metodo crea una nueva instancia de la clase WSConsumer.
     *
     * @param docUUID UUID identificador del documento.
     * @return Retorna una nueva instancia de la clase WSConsumer.
     */
    public static synchronized WSConsumerGR newInstance(String docUUID) {
        return new WSConsumerGR(docUUID);
    } //newInstance

    /**
     * Este metodo define la configuracion del objeto WS Consumidor segun las configuraciones del
     * archivo config.xml
     *
     * @param senderRuc        Numero RUC del emisos electronico.
     * @param usuarioSec       Usuario SOL secundario del emisor electronico.
     * @param passwordSec      Clave SOL secundaria del emisor electronico.
     * @param configuracionObj Archivo de configuracion convertido en objeto.
     * @param fileHandler      Objeto FileHandler que se encarga de manipular el documento UBL.
     * @param documentType     Tipo de documento a enviar.
     * @throws ConfigurationException
     */
    public void setConfiguration(String senderRuc, String usuarioSec, String passwordSec, Configuracion configuracionObj, FileHandler fileHandler,
                                 String documentType) throws ConfigurationException {
        if (logger.isDebugEnabled()) {
            logger.debug("+setConfiguration() [" + this.docUUID + "]");
        }

        if (null == configuracionObj) {
            logger.error("setConfiguration() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_456.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_456.getMessage());
        }


        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
            org.ventura.soluciones.sunatws.gr.consumer.Consumer consumer = new org.ventura.soluciones.sunatws.gr.consumer.Consumer(usuarioSec, passwordSec, senderRuc);

            sunatClient = SunatClientFactory.getInstance().getSunatClient(configuracionObj.getSunat().getClienteSunat(), documentType);
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
        }
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
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
            Consumer consumer = new Consumer(configuracionObj.getSunat().getUsuario().getUsuarioSol(), claveSol);
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

        /* Agregando el objeto FileHandler */
        this.fileHandler = fileHandler;
        if (logger.isDebugEnabled()) {
            logger.debug("-setConfiguration() [" + this.docUUID + "]");
        }
    } //setConfiguration

    public byte[] sendBill(DataHandler zipDocument, String documentName, Transaccion transaccion, ICompany Sociedad) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendBill() [" + this.docUUID + "]");
        }

        byte[] cdrResponse = null;
        try {
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                cdrResponse = sunatClient.sendBill(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                cdrResponse = oseClient.sendBill(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {
                ByteArrayOutputStream zipDocumentBAOS = new ByteArrayOutputStream();
                zipDocument.writeTo(zipDocumentBAOS);
                byte[] archivoBytes = zipDocumentBAOS.toByteArray();
                cdrResponse = conoseClient.sendBill(DocumentNameHandler.getInstance().getZipName(documentName), archivoBytes);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("sendBill() [" + this.docUUID + "] Se obtuvo respuesta del WS.");
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
            logger.error("sendBill() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de tipo WebServiceException: {1}", new Object[]{this.docUUID, e.getMessage()});

            logger.error("sendBill() [" + this.docUUID + "] Esta transaccion pasa al metodo reenviar.");
            //TransaccionBL.ReenviarTransaccion(transaccion);

            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            if(configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {
                throw new SoapFaultException(e.getMessage());
            }
            logger.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepcion de Generica : {1}", new Object[]{this.docUUID, e.getMessage()});

            logger.error("sendBill() [" + this.docUUID + "] Esta transaccion pasa al metodo reenviar.");
            //TransaccionBL.ReenviarTransaccion(transaccion);

            //throw new ConectionSunatException(IVenturaError.ERROR_153);
            ErrorObj ERROR_ws = new ErrorObj(999, (String)((org.tempuri.IbillServiceSendBillMessageFaultMessage)e).getFaultInfo().toString());
            throw new ConectionSunatException(ERROR_ws);
        }

        /* Guardar la respuesta en el DISCO DURO */
        try {
            if (cdrResponse != null && 0 < cdrResponse.length) {
                boolean isCDROk = fileHandler.saveCDRConstancy(cdrResponse, DocumentNameHandler.getInstance().
                        getCDRConstancyName(documentName), transaccion.getSNDocIdentidadNro(), transaccion.getDocIdentidadNro());
                if (logger.isDebugEnabled()) {
                    logger.debug("sendBill() [" + this.docUUID + "] Se guardo la constancia CDR en DISCO? " + isCDROk);
                }
            } else {
                IErrorCode = 9999;
                return null;
            }
        } catch (Exception e) {
            logger.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            return null;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-sendBill() [" + this.docUUID + "]");
        }
        return cdrResponse;
    } //sendBill

    public String sendSummary(DataHandler zipDocument, String documentName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendSummary() [" + this.docUUID + "]");
        }
        String ticket = null;
        try {
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                ticket = sunatClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                ticket = oseClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {
                ByteArrayOutputStream zipDocumentBAOS = new ByteArrayOutputStream();
                zipDocument.writeTo(zipDocumentBAOS);
                byte[] archivoBytes = zipDocumentBAOS.toByteArray();
                String party = configuracion.getEmisorElectronico().getRS();
                ticket = conoseClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), archivoBytes, party);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("sendSummary() [" + this.docUUID + "] Se obtuvo respuesta del WS. Ticket: " + ticket);
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
            logger.error("sendSummary() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepcion WebServiceException : {1}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            logger.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepcion Generica : {1}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-sendSummary() [" + this.docUUID + "]");
        }
        return ticket;
    } //sendSummary

    public StatusResponse getStatus(String ticket, String documentName, Transaccion tc) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+getStatus() [" + this.docUUID + "]");
        }
        StatusResponse statusResponse = null;
        try {
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                pe.gob.sunat.service.gr.StatusResponse statusResponseSUNAT = sunatClient.getStatus(ticket);
                statusResponse = getStatusResponse(statusResponseSUNAT);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
                pe.gob.sunat.service.ose_tci.StatusResponse statusResponseOSE = oseClient.getStatus(ticket);
                statusResponse = getStatusResponse(statusResponseOSE);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {
                pe.gob.sunat.service.conose.StatusResponse statusResponseCONOSE = conoseClient.getStatus(ticket);
                statusResponse = getStatusResponse(statusResponseCONOSE);
            }
            if (logger.isDebugEnabled()) {
                logger.debug("getStatus() [" + this.docUUID + "] Se obtuvo respuesta: " + statusResponse);
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
            logger.error("getStatus() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de tipo WebServiceException: {1}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            logger.error("getStatus() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("getStatus() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de tipo Gen\u00E9rica: {1}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }


        /* Guardar la respuesta en el DISCO DURO */
        try {
            boolean isCDROk = fileHandler.saveCDRConstancy(statusResponse.getContent(), DocumentNameHandler.getInstance().
                    getCDRConstancyName(documentName), tc.getSNDocIdentidadNro(), tc.getDocIdentidadNro());
            if (logger.isDebugEnabled()) {
                logger.debug("getStatus() [" + this.docUUID + "] Se guardo la constancia CDR en DISCO? " + isCDROk);
            }
        } catch (Exception e) {
            logger.error("getStatus() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("getStatus() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getStatus() [" + this.docUUID + "]");
        }
        return statusResponse;
    } //getStatus

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
            return new StatusResponse(((pe.gob.sunat.service.gr.StatusResponse) object).getStatusCode(),
                    null, ((pe.gob.sunat.service.gr.StatusResponse) object).getContent());
        }
        if (object instanceof pe.gob.sunat.service.ose_tci.StatusResponse) {
            return new StatusResponse(((pe.gob.sunat.service.ose_tci.StatusResponse) object).getStatusCode(),
                    null, ((pe.gob.sunat.service.ose_tci.StatusResponse) object).getContent());
        }
        if (object instanceof pe.gob.sunat.service.conose.StatusResponse) {
            return new StatusResponse(((pe.gob.sunat.service.conose.StatusResponse) object).getStatusCode(),
                    null, ((pe.gob.sunat.service.conose.StatusResponse) object).getContent());
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
