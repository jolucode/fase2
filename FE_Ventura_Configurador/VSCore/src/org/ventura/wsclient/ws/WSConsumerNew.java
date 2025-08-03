package org.ventura.wsclient.ws;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.ConectionSunatException;
import org.ventura.wsclient.exception.SoapFaultException;
import org.ventura.wsclient.handler.FileHandler;
import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.commons.exception.error.IVenturaError;
import ventura.soluciones.commons.handler.DocumentNameHandler;
import ventura.soluciones.sunatwsfe.consumer.Consumer;
import ventura.soluciones.sunatwsfe.factory.ISunatClient;
import ventura.soluciones.sunatwsfe.factory.SunatClientFactory;

import javax.activation.DataHandler;
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

/**
 * Esta clase contiene los metodos para enviar los documentos UBL firmados al
 * servicio web de Sunat.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com) implements
 * ISunatClient
 */
public class WSConsumerNew {

    private final Logger logger = Logger.getLogger(WSConsumerNew.class);

    /*
     * Cliente del servicio web
     */
    private ISunatClient wsClient;

    /*
     * Objeto HANDLER para manipular los 
     * documentos UBL.
     */
    private FileHandler fileHandler;

    private String docUUID;

    /**
     * Constructor privado para evitar instancias.
     *
     * @param docUUID UUID identificador del documento.
     */
    private WSConsumerNew(String docUUID) {
        this.docUUID = docUUID;
    } //WSConsumer

    /**
     * Este metodo crea una nueva instancia de la clase WSConsumer.
     *
     * @param docUUID UUID identificador del documento.
     * @return Retorna una nueva instancia de la clase WSConsumer.
     */
    public static synchronized WSConsumerNew newInstance(String docUUID) {
        return new WSConsumerNew(docUUID);
    } //newInstance

    /**
     * Este metodo define la configuracion del objeto WS Consumidor segun las
     * configuraciones del config.xml
     *
     * @param senderRuc El numero RUC del emisor electronico.
     * @param configuration Objeto Configuracion que representa el archivo de
     * configuracion guardado en DISCO.
     * @param fileHandler El objeto FileHandler para manipular el documento UBL.
     * @throws ConfigurationException
     */
    public void setConfiguration(String senderRuc, Configuracion configuration, FileHandler fileHandler, String documentType)
            throws ConfigurationException {
        if (logger.isDebugEnabled()) {
            logger.debug("+setConfiguration() [" + this.docUUID + "]");
        }
        if (null != configuration) {
            boolean encrypted = Boolean.parseBoolean(configuration.getSunat().getUsuario().getClaveSol().getEncriptado());
            String sunatPwd = null;
            if (encrypted) {
                sunatPwd = Criptor.Desencriptar(configuration.getSunat().getUsuario().getClaveSol().getValue());
            } else {
                sunatPwd = configuration.getSunat().getUsuario().getClaveSol().getValue();
            }
            Consumer consumer = new Consumer(configuration.getSunat().getUsuario().getUsuarioSol(), sunatPwd, senderRuc);

            wsClient = SunatClientFactory.getInstance().getSunatClient(configuration.getSunat().getClienteSunat(), documentType);
            wsClient.setConsumer(consumer);
            wsClient.printSOAP(Boolean.parseBoolean(configuration.getSunat().getMostrarSoap()));

            if (logger.isInfoEnabled()) {
                logger.info("setConfiguration() [" + this.docUUID + "] \n####################### WS CONSUMER #######################"
                        + "\nCONSUMER_ruc: " + consumer.getRuc() + "\tCONSUMER_username: " + consumer.getUsername() + "\tCONSUMER_password? " + (null != consumer.getPassword())
                        + "\nWS_Client: " + configuration.getSunat().getClienteSunat()
                        + "\nPRINT_SOAP: " + configuration.getSunat().getMostrarSoap());
            }

            /* Agregando el objeto FileHandler */
            this.fileHandler = fileHandler;
        } else {
            logger.error("setConfiguration() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_456.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_456.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-setConfiguration() [" + this.docUUID + "]");
        }
    } //setConfiguration

    private String obtenerCodeErrorSUNAT(String sFaultCode) {
        String sErrorCodeSUNAT = "";
        String[] sErrorCode = sFaultCode.split("[\\D|\\W]");
        for (String sErrorCode1 : sErrorCode) {
            if (!sErrorCode1.equals("")) {
                sErrorCodeSUNAT = sErrorCode1;
            }
        }
        return sErrorCodeSUNAT;
    }

    public void validarConnectionInternet() throws ConectionSunatException {
        try {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Verificando conexion a internet...");
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Conexion exitosa a internet");
        } catch (MalformedURLException ex) {
            //LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "ERR: (5000) No se pudo establecer conexion a internet");
            throw new ConectionSunatException(IVenturaError.ERROR_5000);
        } catch (IOException ex) {
            //LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "ERR: (5000) No se pudo establecer conexion a internet");
            throw new ConectionSunatException(IVenturaError.ERROR_5000);
        }
    }

    public String sendSummary(DataHandler zipDocument, String documentName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+sendSummary() [" + this.docUUID + "]");
        }
        validarConnectionInternet();
        String ticket = null;
        try {
            ticket = wsClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            if (logger.isDebugEnabled()) {
                logger.debug("sendSummary() [" + this.docUUID + "] ticket: " + ticket);
            }
        } catch (SOAPFaultException e) {
            String sErrorCodeSUNAT = obtenerCodeErrorSUNAT(e.getFault().getFaultCode());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: {1} Excepcion de SUNAT FaultCode \"{1}\"", new Object[]{this.docUUID, sErrorCodeSUNAT});
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: {1} Excepcion de SUNAT FaulString \"{1}\"", new Object[]{this.docUUID, e.getFault().getFaultString()});
            if (logger.isDebugEnabled()) {
                logger.error("EXCEPCION de Sunat [" + this.docUUID + "] Fault Code : " + sErrorCodeSUNAT);
                logger.error("EXCEPCION de Sunat [" + this.docUUID + "] Fault String : " + e.getFault().getFaultString());
            }
            
            Detail detail = e.getFault().getDetail();
            String message = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: {1} Excepcion de SUNAT Details \"{1}\"", new Object[]{this.docUUID, message});
            logger.error("EXCEPCION de Sunat [" + this.docUUID + "] Details : " + message);

            throw new SoapFaultException("Sunat Message :: " + e.getFault().getFaultString() + " " + message);
        } catch (WebServiceException e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepcion de WebServiceException : {1}", new Object[]{this.docUUID, e.getMessage()});
            logger.error("sendSummary() [" + this.docUUID + "] WebServiceException -->" + ExceptionUtils.getStackTrace(e));
            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepcion Generica : {1}", new Object[]{this.docUUID, e.getMessage()});
            logger.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-sendSummary() [" + this.docUUID + "]");
        }
        return ticket;
    } //sendSummary

} //WSConsumer
