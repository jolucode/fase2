package org.ventura.wsclient.handler;

import org.apache.log4j.Logger;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.wsclient.config.ApplicationConfiguration;
import org.ventura.wsclient.config.ISunatConnectorConfig;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.SunatGenericException;
import org.ventura.wsclient.ws.WSConsumerNew;
import sunat.names.specification.ubl.peru.schema.xsd.summarydocuments_1.SummaryDocumentsType;
import ventura.soluciones.commons.config.IUBLConfig;
import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.commons.exception.SignerDocumentException;
import ventura.soluciones.commons.exception.UBLDocumentException;
import ventura.soluciones.commons.exception.error.IVenturaError;
import ventura.soluciones.commons.handler.DocumentNameHandler;
import ventura.soluciones.commons.handler.UBLDocumentHandler;
import ventura.soluciones.signer.config.ISignerConfig;
import ventura.soluciones.signer.handler.SignerHandler;
import ventura.soluciones.signer.utils.CertificateUtils;

import javax.activation.DataHandler;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;

/**
 * Esta clase contiene los metodos que procesan la transaccion de envio a Sunat
 * y creacion de la representacion impresa de un tipo de documento especifico.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)ClienteSunat
 */
public class FEProcessHandler {

    private final Logger logger = Logger.getLogger(FEProcessHandler.class);

    private String docUUID;

    /**
     * Constructor basico para la clase FEProcessHandler.
     */
    private FEProcessHandler(String docUUID) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-FEProcessHandler() [" + docUUID + "] constructor");
        }
        this.docUUID = docUUID;
    } //FEProcessHandler

    /**
     * Este metodo crea una nueva instancia de la clase FEProcessHandler.
     *
     * @param docUUID UUID para el seguimiento del proceso.
     * @return Retorna una instancia de la clase FEProcessHandler.
     */
    public static synchronized FEProcessHandler newInstance(String docUUID) {
        return new FEProcessHandler(docUUID);
    } //newInstance

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
    public TransaccionRespuesta transactionSummaryDocument(TransaccionResumen transaction, String doctype) throws ConfigurationException, UBLDocumentException,
            SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionSummaryDocument() [" + this.docUUID + "] Identifier: " + transaction.getIdTransaccion() + " NumeroRuc: " + transaction.getNumeroRuc());
        }
        TransaccionRespuesta transactionResponse = null;

        /*
         * Extrayendo la informacion del archivo de configuracion 'config.xml'
         */
        Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getNumeroRuc();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation3(transaction.getIdTransaccion(), transaction.getNumeroRuc(), transaction.getFechaEmision());

        /*
         * Extrayendo el certificado de la ruta en DISCO
         */
        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());

        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
        String certPassword = null;
        if (encrypted) {
            /* Desencriptar la clave del certificado */
            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
        } else {
            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
        }
        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionSummaryDocument() [" + this.docUUID + "] Certificado en bytes: " + certificate);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         * 		- Certificado nulo o vacio
         * 		- La contrase�a del certificado pueda abrir el certificado.
         */
        CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);

        /*
         * Generando el objeto SummaryDocumentsType para el Resumen Diario.
         */
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        SummaryDocumentsType summaryDocumentsType = ublHandler.generateSummaryDocumentsType(transaction, signerName);
        if (logger.isInfoEnabled()) {
            logger.info("transactionSummaryDocument() [" + this.docUUID + "] Se genero el objeto SummaryDocumentsType del RESUMEN DIARIO.");
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto SummaryDocumentsType del RESUMEN DIARIO.");

        /*
         * Se genera el nombre del documento de tipo RESUMEN DIARIO
         */
        String documentName = DocumentNameHandler.getInstance().getSummaryDocumentName(transaction.getNumeroRuc(), transaction.getIdTransaccion());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionSummaryDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        encrypted = Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado());
        String attachmentPath = null;
        if (encrypted) {
            /* Desencriptar la ruta del archivo */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.SUMMARY_DOCUMENT_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos() + File.separator + ISunatConnectorConfig.SUMMARY_DOCUMENT_PATH;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("transactionSummaryDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);
        fileHandler.setBaseDirectory(attachmentPath);

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(summaryDocumentsType, documentName);
        if (logger.isInfoEnabled()) {
            logger.info("transactionSummaryDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();
        signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionSummaryDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        /*
         * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
         * al emisor electronico. Ademas, se guarda el objeto FileHandler para el tratamiento del
         * documento UBL que se encuentra en DISCO.
         */
        WSConsumerNew wsConsumer = WSConsumerNew.newInstance(this.docUUID);
        wsConsumer.setConfiguration(transaction.getNumeroRuc(), configuration, fileHandler, IUBLConfig.DOC_SUMMARY_DOCUMENT_CODE);
        if (logger.isInfoEnabled()) {
            logger.info("transactionSummaryDocument() [" + this.docUUID + "] Se realizo las configuraciones del objeto WSConsumer.");
        }

        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName);
        if (logger.isInfoEnabled()) {
            logger.info("transactionSummaryDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        if (null != zipDocument) {
            /*
             * Enviando la Comunicacion de Baja al metodo "sendSummary" del servicio web de Sunat.
             */
            if (logger.isInfoEnabled()) {
                logger.info("transactionSummaryDocument() [" + this.docUUID + "] Enviando WS sendSummary.");
            }
            String ticket = wsConsumer.sendSummary(zipDocument, documentName);

            if (logger.isInfoEnabled()) {
                logger.info("transactionSummaryDocument() [" + this.docUUID + "] El WS retorno el ticket: " + ticket);
            }
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El WS retorno el ticket: " + ticket);

            /*
             * El tipo de CODIGO RQT ha utilizar sera EMITIDO,
             * segun coordinaciones con VENTURA SOFTWARE S.A.C.
             */
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
    } //transactionSummaryDocument

    //***********************************************************************************
    //******************************* CONSULTAR DOCUMENTOS ******************************
    //***********************************************************************************
    public byte[] ConvertirFileToByte(String ruta) {

        try {
            File file = new File(ruta);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            try {
                for (int readNum; (readNum = fis.read(buf)) != -1;) {
                    bos.write(buf, 0, readNum); //no doubt here is 0
                }
            } catch (Exception e) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, " ERROR {0} ", e.getMessage());
            }
            byte[] byteconvertido = bos.toByteArray();
            return byteconvertido;
        } catch (Exception e) {
            //LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object(){}.getClass().getName(),new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage()});
            return null;
        }

    }

} // FEProcessHandler
