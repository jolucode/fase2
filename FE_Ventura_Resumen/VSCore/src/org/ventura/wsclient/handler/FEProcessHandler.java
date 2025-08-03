package org.ventura.wsclient.handler;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.tempuri.CONOSEClient;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.utilidades.entidades.ListaSociedades;
import org.ventura.utilidades.entidades.VariablesGlobales;
import org.ventura.wsclient.config.ApplicationConfiguration;
import org.ventura.wsclient.config.ISunatConnectorConfig;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.ConectionSunatException;
import org.ventura.wsclient.exception.SoapFaultException;
import org.ventura.wsclient.exception.SunatGenericException;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;
import ventura.soluciones.commons.config.IUBLConfig;
import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.commons.exception.SignerDocumentException;
import ventura.soluciones.commons.exception.UBLDocumentException;
import ventura.soluciones.commons.exception.error.ErrorObj;
import ventura.soluciones.commons.exception.error.IVenturaError;
import ventura.soluciones.commons.handler.DocumentNameHandler;
import ventura.soluciones.commons.handler.UBLDocumentHandler;
import ventura.soluciones.signer.config.ISignerConfig;
import ventura.soluciones.signer.handler.SignerHandler;
import ventura.soluciones.signer.utils.CertificateUtils;
import ventura.soluciones.sunatws_ose.tci.client.OSEClient;
import ventura.soluciones.sunatws_ose.tci.config.IOSEClient;
import ventura.soluciones.sunatwsfe.consumer.Consumer;
import ventura.soluciones.sunatwsfe.factory.ISunatClient;
import ventura.soluciones.sunatwsfe.factory.SunatClientFactory;

import javax.activation.DataHandler;
import javax.xml.soap.Detail;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Esta clase contiene los metodos que procesan la transaccion de envio a Sunat
 * y creacion de la representacion impresa de un tipo de documento especifico.
 * ClienteSunat
 */
public class FEProcessHandler {

    private final Logger logger = Logger.getLogger(FEProcessHandler.class);

    private String docUUID;

    public ISunatClient sunatClient;

    public IOSEClient oseClient;

    private org.tempuri.CONOSEClient coneseClient;

    public Configuracion configuracion;

    /**
     * Constructor basico para la clase FEProcessHandler.
     */
    public FEProcessHandler() {

    } //FEProcessHandler

    /**
     * Este metodo realiza el proceso de facturacion electronica, creando un
     * documento de tipo resumen diario de boletas y notas electronicas.
     *
     * @param transaction El objeto Transaccion que contiene la informacion para
     * la creacion del Resumen Diario.
     * @return Retorna una respuesta del proceso, conteniendo el ticket de la
     * operacion.
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatServerException
     * @throws SunatClientException
     * @throws SunatGenericException
     * @throws Exception
     */


    /**
     * @param transaction
     * @return
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatGenericException
     * @throws Exception
     */
    public TransaccionRespuesta transactionSummaryDocument(TransaccionResumen transaction) throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("transactionSummaryDocument() [" + this.docUUID + "]");
        }
        try {
            //System.out.println("Llego hasta aqui.");
            TransaccionRespuesta transactionResponse = null;
            String idTransaccion = transaction.getIdTransaccion();
            if (logger.isDebugEnabled()) {
                logger.debug("transactionSummaryDocument() [" + this.docUUID + "] Identifier: " + idTransaccion + " NumeroRuc: " + transaction.getNumeroRuc());
            }
            Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();
            String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getNumeroRuc();
            ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
            validationHandler.checkBasicInformation3(idTransaccion, transaction.getNumeroRuc(), transaction.getFechaEmision());

            String idSociedad = transaction.getKeySociedad();
            ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

            byte[] certificado = sociedad.getRutaCD();
            String certiPassword = sociedad.getPasswordCD();
            String usuarioSec = sociedad.getUsuarioSec();
            String passwordSec = sociedad.getPasswordSec();

            String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
            String ksType = configuration.getCertificadoDigital().getTipoKeystore();
            if (logger.isDebugEnabled()) {
                logger.debug("transactionSummaryDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
            }
            CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);
            UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
            SummaryDocumentsType summaryDocumentsType = ublHandler.generateSummaryDocumentsTypeV2(transaction, signerName);
            String documentName = DocumentNameHandler.getInstance().getSummaryDocumentName(transaction.getNumeroRuc(), idTransaccion);
            if (logger.isDebugEnabled()) {
                logger.debug("transactionSummaryDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
            }
            FileHandler fileHandler = FileHandler.newInstance(this.docUUID);
            String attachmentPath = null;
            if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
                attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.SUMMARY_DOCUMENT_PATH;
            } else {
                attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.SUMMARY_DOCUMENT_PATH;
            }
            File fileAdjunto = new File(attachmentPath);
            if (!fileAdjunto.exists()) {
                fileAdjunto.mkdirs();
            }
            fileHandler.setBaseDirectory(attachmentPath);
            if (logger.isDebugEnabled()) {
                logger.debug("transactionSummaryDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
            }
            String documentPath = fileHandler.storeDocumentInDisk(summaryDocumentsType, documentName);
            if (logger.isInfoEnabled()) {
                logger.info("transactionSummaryDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
            }
            SignerHandler signerHandler = SignerHandler.newInstance();
            signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);
            File signedDocument = signerHandler.signDocument(documentPath, docUUID);
            if (logger.isInfoEnabled()) {
                logger.info("transactionSummaryDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
            }
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El documento [{1}] fue firmado correctamente en: {2}", new Object[]{this.docUUID, documentName, signedDocument.getAbsolutePath()});
            DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName);
            if (logger.isInfoEnabled()) {
                logger.info("transactionSummaryDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
            }
            Optional<DataHandler> optional = Optional.ofNullable(zipDocument);
            if (optional.isPresent()) {
                setConfiguration(transaction.getNumeroRuc(), usuarioSec, passwordSec, configuration, IUBLConfig.DOC_SUMMARY_DOCUMENT_CODE);
                if (logger.isInfoEnabled()) {
                    logger.info("transactionSummaryDocument() [" + this.docUUID + "] Se realizo las configuraciones del objeto WSConsumer.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Enviando al WS_sendSummary.", this.docUUID);
                String ticket = sendSummary(zipDocument, documentName);
                if (logger.isInfoEnabled()) {
                    logger.info("transactionSummaryDocument() [" + this.docUUID + "] El WS retorno el ticket: " + ticket);
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El WS retorno el ticket: {1}", new Object[]{this.docUUID, ticket});
                transactionResponse = new TransaccionRespuesta();
                transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_APROBADO);
                transactionResponse.setNroTique(ticket);
                transactionResponse.setIdentificador(transaction.getIdTransaccion());
                transactionResponse.setUuid(this.docUUID);
            } else {
                logger.error("transactionSummaryDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
                throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("-transactionSummaryDocument() [" + this.docUUID + "]");
            }
            return transactionResponse;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void setConfiguration(String senderRuc, String usuarioSec, String passwordSec, Configuracion configuracionObj, String documentType) throws ConfigurationException {
        if (logger.isDebugEnabled()) {
            logger.debug("+setConfiguration() [" + docUUID + "]");
        }
        if (null == configuracionObj) {
            logger.error("setConfiguration() [" + docUUID + "] ERROR: " + IVenturaError.ERROR_456.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_456.getMessage());
        }
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
            ventura.soluciones.sunatwsfe.consumer.Consumer consumer = new Consumer(usuarioSec, passwordSec, senderRuc);
            sunatClient = SunatClientFactory.getInstance().getSunatClient(configuracionObj.getSunat().getClienteSunat(), documentType);
            sunatClient.setConsumer(consumer);
            sunatClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));
            if (logger.isInfoEnabled()) {
                logger.info("setConfiguration() [" + docUUID + "]\n" +
                        "######################### CONFIGURACION SUNAT #########################\n" +
                        "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
                        "# Cliente WS: " + configuracionObj.getSunat().getClienteSunat() + "\n" +
                        "# Mostrar SOAP: " + configuracionObj.getSunat().getMostrarSoap() + "\n" +
                        "#######################################################################");
            }
        }
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("OSE")) {

            /*
            String claveSol;
            if (Boolean.parseBoolean(configuracionObj.getSunat().getUsuario().getClaveSol().getEncriptado())) {
                claveSol = Criptor.Desencriptar(configuracionObj.getSunat().getUsuario().getClaveSol().getValue());
            } else {
                claveSol = configuracionObj.getSunat().getUsuario().getClaveSol().getValue();
            }
            ventura.soluciones.sunatws_ose.tci.consumer.Consumer consumer = new ventura.soluciones.sunatws_ose.tci.consumer.Consumer(configuracionObj.getSunat().getUsuario().getUsuarioSol(), claveSol);
            */

            String claveSol = passwordSec;
            ventura.soluciones.sunatws_ose.tci.consumer.Consumer consumer = new ventura.soluciones.sunatws_ose.tci.consumer.Consumer(usuarioSec, claveSol);


            oseClient = new OSEClient(configuracionObj.getSunat().getClienteSunat());
            oseClient.setConsumer(consumer);
            oseClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));
            if (logger.isInfoEnabled()) {
                logger.info("setConfiguration() [" + docUUID + "]\n" +
                        "######################### CONFIGURACION OSE #########################\n" +
                        "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
                        "# Cliente WS: " + configuracionObj.getSunat().getClienteSunat() + "\n" +
                        "# Mostrar SOAP: " + configuracionObj.getSunat().getMostrarSoap() + "\n" +
                        "#######################################################################");
            }
        }
        if (configuracionObj.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {

            /*
            String claveSol;
            if (Boolean.parseBoolean(configuracionObj.getSunat().getUsuario().getClaveSol().getEncriptado())) {
                claveSol = Criptor.Desencriptar(configuracionObj.getSunat().getUsuario().getClaveSol().getValue());
            } else {
                claveSol = configuracionObj.getSunat().getUsuario().getClaveSol().getValue();
            }
             */
            String claveSol = passwordSec;
            ventura.soluciones.sunatws_ose.tci.consumer.Consumer consumer = new ventura.soluciones.sunatws_ose.tci.consumer.Consumer(usuarioSec, claveSol);
            coneseClient = new CONOSEClient(configuracionObj.getSunat().getClienteSunat());
            coneseClient.setConsumer(consumer);
            //coneseClient.printSOAP(Boolean.parseBoolean(configuracionObj.getSunat().getMostrarSoap()));
            if (logger.isInfoEnabled()) {
                logger.info("setConfiguration() [" + docUUID + "]\n" +
                        "######################### CONFIGURACION OSE #########################\n" +
                        "# Usuario SOL: " + consumer.getUsername() + "\tClave SOL: " + (null != consumer.getPassword()) + "\n" +
                        "# Cliente WS: " + configuracionObj.getSunat().getClienteSunat() + "\n" +
                        "# Mostrar SOAP: " + configuracionObj.getSunat().getMostrarSoap() + "\n" +
                        "#######################################################################");
            }
        }
        this.configuracion = configuracionObj;
    }

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
                ticket = coneseClient.sendSummary(DocumentNameHandler.getInstance().getZipName(documentName), archivoBytes, party);

            }

            System.out.println("**************************************************");
            System.out.println("**  Este es el numero de ticket  ***");
            System.out.println("**" + ticket + "**");
            System.out.println("**************************************************");

            if (logger.isDebugEnabled()) {
                logger.debug("sendSummary() [" + this.docUUID + "] Se obtuvo respuesta del WS. Ticket: " + ticket);
            }
        } catch (SOAPFaultException e) {
            String sErrorCodeSUNAT = null;
            if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("SUNAT")) {
                sErrorCodeSUNAT = obtenerErrorCodeSUNAT(e.getFault().getFaultCode());
            } else if (configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("OSE") || configuracion.getSunat().getIntegracionWS().equalsIgnoreCase("CONOSE")) {
                sErrorCodeSUNAT = e.getFault().getFaultCode() + " - " +e.getFault().getFaultString();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("sendSummary() [" + this.docUUID + "] Codigo de la excepcion: " + sErrorCodeSUNAT);
            }
            logger.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultCode: " + e.getFault().getFaultCode());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de SUNAT FaultCode: \"{1}\"", new Object[]{this.docUUID, e.getFault().getFaultCode()});
            logger.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultString: " + e.getFault().getFaultString());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de SUNAT FaultString: \"{1}\"", new Object[]{this.docUUID, e.getFault().getFaultString()});
            if (sErrorCodeSUNAT.equalsIgnoreCase("0402")) {
                return "88888888";
            }
            Detail detail = e.getFault().getDetail();
            String sErrorMessageSUNAT = (detail != null ? detail.getTextContent() : e.getFault().getFaultString());
            if (null != detail) {
                logger.error("sendSummary() [" + this.docUUID + "] SOAPFaultException - FaultDetail: " + detail.getTextContent());
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de SUNAT FaultDetail: \"{1}\"", new Object[]{this.docUUID, detail.getTextContent()});
            }
            throw new SoapFaultException("Sunat Message :: " + sErrorCodeSUNAT + " : " + sErrorMessageSUNAT);
        } catch (WebServiceException e) {
            logger.error("sendSummary() [" + this.docUUID + "] WebServiceException - ERROR: " + e.getMessage());
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de tipo WebServiceException: {1}", new Object[]{this.docUUID, e.getMessage()});

            throw new ConectionSunatException(IVenturaError.ERROR_152);
        } catch (Exception e) {
            logger.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("sendSummary() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: Excepci\u00F3n de tipo Gen\u00E9rica: {1}", new Object[]{this.docUUID, e.getMessage()});

            //throw new ConectionSunatException(IVenturaError.ERROR_153);
            ErrorObj ERROR_ws = new ErrorObj(999, (String)((org.tempuri.IbillServiceSendBillMessageFaultMessage)e).getFaultInfo().toString());
            throw new ConectionSunatException(ERROR_ws);
        }
        return ticket;
    }

    private String obtenerErrorCodeSUNAT(String sFaultCode) {
        String[] sErrorCodeSplit = sFaultCode.split("[\\D|\\W]");
        String sErrorCodeSUNAT = "";
        for (String aux : sErrorCodeSplit) {
            if (!aux.equals("")) {
                sErrorCodeSUNAT = aux;
            }
        }
        return sErrorCodeSUNAT;
    }

    public void setDocUUID(String docUUID) {
        this.docUUID = docUUID;
    }
}
