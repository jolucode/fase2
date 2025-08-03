package org.ventura.wsclient.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.tempuri.CONOSEClient;
import org.tempuri.IbillService;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.ConectionSunatException;
import org.ventura.wsclient.exception.SoapFaultException;
import org.ventura.wsclient.handler.FileHandler;

import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.commons.exception.error.ErrorObj;
import ventura.soluciones.commons.exception.error.IVenturaError;
import ventura.soluciones.commons.handler.DocumentNameHandler;
import ventura.soluciones.sunatws_ose.tci.client.OSEClient;
import ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import ventura.soluciones.sunatwsfe.consumer.Consumer;
import ventura.soluciones.sunatwsfe.factory.ISunatClient;
import ventura.soluciones.sunatwsfe.factory.SunatClientFactory;

/**
 * Esta clase contiene los metodos para enviar los documentos UBL firmados al
 * servicio web de Sunat.
 * implements
 * ISunatClient
 */
public class WSConsumerNew
{
    private final Logger logger = Logger.getLogger(WSConsumerNew.class);

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
     * Este metodo define la configuracion del objeto WS Consumidor segun las configuraciones del 
     * archivo config.xml
     * 
     * @param senderRuc
     *      Numero RUC del emisos electronico.
     * @param usuarioSec
     *      Usuario SOL secundario del emisor electronico.
     * @param passwordSec
     *      Clave SOL secundaria del emisor electronico.
     * @param configuracionObj
     *      Archivo de configuracion convertido en objeto.
     * @param fileHandler
     *      Objeto FileHandler que se encarga de manipular el documento UBL.
     * @param documentType
     *      Tipo de documento a enviar.
     * @throws ConfigurationException 
     */
    public void setConfiguration(String senderRuc, String usuarioSec, String passwordSec, Configuracion configuracionObj, FileHandler fileHandler, 
            String documentType) throws ConfigurationException
    {
        if (logger.isDebugEnabled()) {logger.debug("+setConfiguration() [" + this.docUUID + "]");}
        
        if (null == configuracionObj)
        {
            logger.error("setConfiguration() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_456.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_456.getMessage());
        }
        
        
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT"))
        {
            ventura.soluciones.sunatwsfe.consumer.Consumer consumer = new Consumer(usuarioSec, passwordSec, senderRuc);
            
            sunatClient = SunatClientFactory.getInstance().getSunatClient(configuracionObj.getSunat().getClienteSunat(), documentType);
            sunatClient.setConsumer(consumer);
            sunatClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));
            
            if (logger.isInfoEnabled())
            {
                logger.info("setConfiguration() [" + this.docUUID + "]\n" +
                        "######################### CONFIGURACION SUNAT #########################\n" +
                        "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
                        "# Cliente WS: " + configuracionObj.getSunat().getClienteSunat() + "\n" +
                        "# Mostrar SOAP: " + configuracionObj.getSunat().getMostrarSoap() + "\n" +
                        "#######################################################################");
            }
        }
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("OSE"))
            {
                /*
                String claveSol;
                if (Boolean.parseBoolean(configuracionObj.getSunat().getUsuario().getClaveSol().getEncriptado()))
                {
                    claveSol = Criptor.Desencriptar(configuracionObj.getSunat().getUsuario().getClaveSol().getValue());
                }
                else
                {
                    claveSol = configuracionObj.getSunat().getUsuario().getClaveSol().getValue();
                }
                ventura.soluciones.sunatws_ose.tci.consumer.Consumer consumer = new ventura.soluciones.sunatws_ose.tci.consumer.Consumer(configuracionObj.getSunat().getUsuario().getUsuarioSol(), claveSol);
                 */



                String claveSol = passwordSec;
                ventura.soluciones.sunatws_ose.tci.consumer.Consumer consumer = new ventura.soluciones.sunatws_ose.tci.consumer.Consumer(usuarioSec, claveSol);

                oseClient = new OSEClient(configuracionObj.getSunat().getClienteSunat());
                oseClient.setConsumer(consumer);
                oseClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));

                if (logger.isInfoEnabled())
                {
                    logger.info("setConfiguration() [" + this.docUUID + "]\n" +
                            "######################### CONFIGURACION OSE #########################\n" +
                            "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
                            "# Cliente WS: " + configuracionObj.getSunat().getClienteSunat() + "\n" +
                            "# Mostrar SOAP: " + configuracionObj.getSunat().getMostrarSoap() + "\n" +
                            "#######################################################################");
                }
            }
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE"))
        {

            /*
            String claveSol;
            if (Boolean.parseBoolean(configuracionObj.getSunat().getUsuario().getClaveSol().getEncriptado()))
                { claveSol = Criptor.Desencriptar(configuracionObj.getSunat().getUsuario().getClaveSol().getValue()); }
            else
                {  claveSol = configuracionObj.getSunat().getUsuario().getClaveSol().getValue(); }
*/
            String claveSol = passwordSec;
            ventura.soluciones.sunatws_ose.tci.consumer.Consumer consumer = new ventura.soluciones.sunatws_ose.tci.consumer.Consumer(usuarioSec, claveSol);


            conoseClient = new CONOSEClient(configuracionObj.getSunat().getClienteSunat());
            conoseClient.setConsumer(consumer);
            //conoseClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));

            if (logger.isInfoEnabled())
            {
                logger.info("setConfiguration() [" + this.docUUID + "]\n" +
                        "######################### CONFIGURACION OSE #########################\n" +
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
        
        if (logger.isDebugEnabled()) {logger.debug("-setConfiguration() [" + this.docUUID + "]");}
    } //setConfiguration
    
    public String sendSummary(DataHandler zipDocument, String documentName) throws Exception
    {
        if (logger.isDebugEnabled()) {logger.debug("+sendSummary() [" + this.docUUID + "]");}
        
        validarConnectionInternet();
        
        String ticket = null;
        try
        {
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT"))
            {
                ticket = sunatClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("OSE"))
            {
                ticket = oseClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), zipDocument);
            }
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE"))
            {
                ByteArrayOutputStream zipDocumentBAOS = new ByteArrayOutputStream();
                zipDocument.writeTo(zipDocumentBAOS);
                byte[] archivoBytes = zipDocumentBAOS.toByteArray();
                String party = configuracion.getEmisorElectronico().getRS();
                ticket = conoseClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName),archivoBytes,party);
            }
            if (logger.isDebugEnabled()) {logger.debug("sendSummary() [" + this.docUUID + "] Se obtuvo respuesta del WS. Ticket: " + ticket);}
        }
        catch (SOAPFaultException e)
        {
            String sErrorCodeSUNAT = null;
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT"))
            {
                sErrorCodeSUNAT = obtenerErrorCodeSUNAT(e.getFault().getFaultCode());
            }
            else if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("OSE") || configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE"))
            {
                sErrorCodeSUNAT = e.getFault().getFaultString();
            }
            if (logger.isDebugEnabled()) {logger.debug("sendSummary() [" + this.docUUID + "] Codigo de la excepcion: " + sErrorCodeSUNAT);}
            
            
            logger.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultCode: " + e.getFault().getFaultCode());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de SUNAT FaultCode: \"{1}\"", new Object[] {this.docUUID, e.getFault().getFaultCode()});            
            
            logger.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultString: " + e.getFault().getFaultString());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de SUNAT FaultString: \"{1}\"", new Object[] {this.docUUID, e.getFault().getFaultString()});
            
            Detail detail = e.getFault().getDetail();
            String sErrorMessageSUNAT = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());
            
            if (null != detail)
            {
                logger.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultDetail: " + detail.getTextContent());
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de SUNAT FaultDetail: \"{1}\"", new Object[] {this.docUUID, detail.getTextContent()});
            }
            
            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " : " + sErrorMessageSUNAT);
        }
        catch (WebServiceException e)
        {
            logger.error("sendSummary() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de tipo WebServiceException: {1}", new Object[] {this.docUUID, e.getMessage()});
            
            throw new ConectionSunatException(IVenturaError.ERROR_152);
        }
        catch (Exception e)
        {
            logger.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de tipo Gen\u00E9rica: {1}", new Object[] {this.docUUID, e.getMessage()});

            //throw new ConectionSunatException(IVenturaError.ERROR_153);
            ErrorObj ERROR_ws = new ErrorObj(999, (String)((org.tempuri.IbillServiceSendBillMessageFaultMessage)e).getFaultInfo().toString());
            throw new ConectionSunatException(ERROR_ws);
        }
        if (logger.isDebugEnabled()) {logger.debug("-sendSummary() [" + this.docUUID + "]");}
        return ticket;
    } //sendSummary
    
    public void validarConnectionInternet() throws ConectionSunatException
    {
        try
        {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Verificando conexi\u00F3on a internet...");
            
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Conexi\u00F3on exitosa a internet");
        }
        catch (MalformedURLException ex)
        {
            throw new ConectionSunatException(IVenturaError.ERROR_5000);
        }
        catch (IOException ex)
        {
            throw new ConectionSunatException(IVenturaError.ERROR_5000);
        }
    } //validarConnectionInternet
    
    private String obtenerErrorCodeSUNAT(String sFaultCode)
    {
        String[] sErrorCodeSplit = sFaultCode.split("[\\D|\\W]");
        
        String sErrorCodeSUNAT = "";
        for (String aux : sErrorCodeSplit)
        {
            if (!aux.equals(""))
            {
                sErrorCodeSUNAT = aux;
            }
        }
        return sErrorCodeSUNAT;
    } //obtenerErrorCodeSUNAT
    
} //WSConsumer
