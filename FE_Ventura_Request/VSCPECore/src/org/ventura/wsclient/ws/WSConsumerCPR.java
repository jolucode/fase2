package org.ventura.wsclient.ws;

import com.sap.smb.sbo.api.ICompany;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.tempuri.CONOSEClient;
import org.tempuri.IbillService;
import org.ventura.cpe.bl.TransaccionBL;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.soluciones.commons.exception.ConfigurationException;
import org.ventura.soluciones.commons.exception.error.IVenturaError;
import org.ventura.soluciones.commons.handler.DocumentNameHandler;
import org.ventura.soluciones.sunatws.cpr.factory.ISunatClient;
import org.ventura.soluciones.sunatws.cpr.factory.SunatClientFactory;
import org.ventura.soluciones.sunatws_ose.tci.client.OSEClient;
import org.ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import org.ventura.soluciones.sunatws_ose.tci.consumer.Consumer;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.ConectionSunatException;
import org.ventura.wsclient.exception.SoapFaultException;
import org.ventura.wsclient.handler.FileHandler;

import javax.activation.DataHandler;
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

public class WSConsumerCPR {

    private final Logger logger = Logger.getLogger(WSConsumerCPR.class);

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
    private WSConsumerCPR(String docUUID) {
        this.docUUID = docUUID;
    } //WSConsumer

    /**
     * Este metodo crea una nueva instancia de la clase WSConsumer.
     *
     * @param docUUID UUID identificador del documento.
     * @return Retorna una nueva instancia de la clase WSConsumer.
     */
    public static synchronized WSConsumerCPR newInstance(String docUUID) {
        return new WSConsumerCPR(docUUID);
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
            org.ventura.soluciones.sunatws.cpr.consumer.Consumer consumer = new org.ventura.soluciones.sunatws.cpr.consumer.Consumer(usuarioSec, passwordSec, senderRuc);

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
        } if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {
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
            //coneseClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));

            if (logger.isInfoEnabled()) {
                logger.info("setConfiguration() [" + this.docUUID + "]\n" +
                        "######################### CONFIGURACION OSE #########################\n" +
                        "# Usuario CONOSE: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
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

        validarConnectionInternet();

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
                String party = configuracion.getEmisorElectronico().getRS(); // opcional
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
            logger.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("sendBill() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\\u00F3n de tipo Gen\\u00E9rica: {1}", new Object[]{this.docUUID, e.getMessage()});

            logger.error("sendBill() [" + this.docUUID + "] Esta transaccion pasa al metodo reenviar.");
            //TransaccionBL.ReenviarTransaccion(transaccion);

            throw new ConectionSunatException(IVenturaError.ERROR_153);
        }


        /* Guardar la respuesta en el DISCO DURO */
        try {
            if (null != cdrResponse && 0 < cdrResponse.length) {
                boolean isCDROk = fileHandler.saveCDRConstancy(cdrResponse, DocumentNameHandler.getInstance().getCDRConstancyName(documentName), transaccion.getSNDocIdentidadNro(), transaccion.getDocIdentidadNro());
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

        validarConnectionInternet();

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
                ticket = conoseClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), archivoBytes, party); //partyType
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

    private void validarConnectionInternet() throws ConectionSunatException {
        try {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Verificando conexion a internet...");

            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();

            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Conexion exitosa a internet");
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

} //WSConsumerCPR
