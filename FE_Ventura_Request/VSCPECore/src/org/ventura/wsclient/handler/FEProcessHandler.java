package org.ventura.wsclient.handler;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sap.smb.sbo.api.ICompany;
import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ResponseCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StatusReasonCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.StatusReasonType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionType;
import oasis.names.specification.ubl.schema.xsd.commonextensioncomponents_2.UBLExtensionsType;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.apache.axis.encoding.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.eclipse.persistence.internal.oxm.ByteArraySource;
import org.ventura.cpe.bl.ReglasIdiomaDocBL;
import org.ventura.cpe.bl.TransaccionBL;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.TransaccionRespuesta.Sunat;
import org.ventura.cpe.dto.hb.*;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.soluciones.commons.config.IUBLConfig;
import org.ventura.soluciones.commons.exception.ConfigurationException;
import org.ventura.soluciones.commons.exception.PDFReportException;
import org.ventura.soluciones.commons.exception.SignerDocumentException;
import org.ventura.soluciones.commons.exception.UBLDocumentException;
import org.ventura.soluciones.commons.exception.error.IVenturaError;
import org.ventura.soluciones.commons.handler.DocumentNameHandler;
import org.ventura.soluciones.commons.handler.UBLDocumentHandler;
import org.ventura.soluciones.commons.handler.UBLDocumentHandler20;
import org.ventura.soluciones.commons.wrapper.UBLDocumentWRP;
import org.ventura.soluciones.pdfcreator.config.IPDFCreatorConfig;
import org.ventura.soluciones.pdfcreator.handler.PDFBasicGenerateHandler;
import org.ventura.soluciones.pdfcreator.handler.PDFGenerateHandler;
import org.ventura.soluciones.signer.config.ISignerConfig;
import org.ventura.soluciones.signer.handler.SignerHandler;
import org.ventura.soluciones.signer.utils.CertificateUtils;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.utilidades.entidades.ListaSociedades;
import org.ventura.utilidades.entidades.TipoVersionUBL;
import org.ventura.utilidades.entidades.VariablesGlobales;
import org.ventura.wsclient.config.ApplicationConfiguration;
import org.ventura.wsclient.config.ISunatConnectorConfig;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.exception.SunatGenericException;
import org.ventura.wsclient.ws.WSConsumerCPR;
import org.ventura.wsclient.ws.WSConsumerConsult;
import org.ventura.wsclient.ws.WSConsumerGR;
import org.ventura.wsclient.ws.WSConsumerNew;
import org.w3c.dom.NodeList;
import pe.gob.sunat.apireset.model.ResponseDTO;
import pe.gob.sunat.apireset.service.ClientJwtSunat;
import sunat.names.specification.ubl.peru.schema.xsd.perception_1.PerceptionType;
import sunat.names.specification.ubl.peru.schema.xsd.retention_1.RetentionType;
import sunat.names.specification.ubl.peru.schema.xsd.voideddocuments_1.VoidedDocumentsType;
import un.unece.uncefact.data.specification.corecomponenttypeschemamodule._2.TextType;

import javax.activation.DataHandler;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Esta clase contiene los metodos que procesan la transaccion de envio a Sunat
 * y creacion de la representacion impresa de un tipo de documento especifico.
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
     * documento de tipo FACTURA.
     *
     * @param transaction objeto Transaccion, contiene la informacion para crear
     *                    un documento de tipo FACTURA.
     * @param doctype
     * @param extractor
     * @param Sociedad
     * @return Retorna la respuesta del proceso de facturacion electronica del
     * documento de tipo FACTURA.
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatGenericException
     * @throws Exception
     */
    public TransaccionRespuesta transactionInvoiceDocument(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection) throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionInvoiceDocument() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = null;
//
//        certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);
//
//        byte[] certificado = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//        String certiPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        System.out.println();
        System.out.println(idSociedad);
        System.out.println(usuarioSec);
        System.out.println();
        String passwordSec = sociedad.getPasswordSec();
        System.out.println(passwordSec);
        System.out.println();
        System.out.println();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug(Arrays.toString(certificado) + "transactionInvoiceDocument() [" + this.docUUID + "] Certificado en bytes: ");
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         *      - Certificado nulo o vacio
         * 	- La contraseña del certificado pueda abrir el certificado.
         */
        //CertificateUtils.checkDigitalCertificateV2(certificado, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto InvoiceType para la FACTURA */
        InvoiceType invoiceType = null;

        if (TipoVersionUBL.factura.equals("20")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Version 2.0 del UBL.");
            }
            UBLDocumentHandler20 ublHandler20 = UBLDocumentHandler20.newInstance(this.docUUID);
            invoiceType = ublHandler20.generateInvoiceType(transaction, signerName);
        } else if (TipoVersionUBL.factura.equals("21")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Version 2.1 del UBL.");
            }
            UBLDocumentHandler ublHandler21 = UBLDocumentHandler.newInstance(this.docUUID);
            invoiceType = ublHandler21.generateInvoiceType(transaction, signerName);
        }

        //UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        //InvoiceType invoiceType = ublHandler.generateInvoiceType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto InvoiceType de la FACTURA.");

//        /*
//         * Validar las casuisticas del documento UBL de tipo Factura.
//         */
//        validationHandler.checkInvoiceDocument(invoiceType);
//        if (logger.isInfoEnabled()) {
//            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo FACTURA.");
//        }
//        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se validaron las casuisticas del documento de tipo FACTURA.");
        /*
         * Se genera el nombre del documento de tipo FACTURA
         */
        String documentName = DocumentNameHandler.getInstance().getInvoiceName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionInvoiceDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.INVOICE_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.INVOICE_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionInvoiceDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(invoiceType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();

        //signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);


        if (logger.isInfoEnabled()) {
            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());

        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
        documentWRP.setInvoiceType((InvoiceType) ublDocument);

        PDFBasicGenerateHandler db = new PDFBasicGenerateHandler(docUUID);

        /* Obtener el digestValue */
        String digestValue = db.generateDigestValue(documentWRP.getInvoiceType().getUBLExtensions());

        /* Generar el codigo de barra */
//        String barcodeValue = db.generateBarcodeInfo(documentWRP.getInvoiceType().getID().getValue(), documentWRP.getInvoiceType()
//                .getInvoiceTypeCode().getValue(),
//                fecha, documentWRP.getInvoiceType()
//                        .getLegalMonetaryTotal().getPayableAmount()
//                        .getValue(), documentWRP.getInvoiceType()
//                        .getTaxTotal(), documentWRP.getInvoiceType()
//                        .getAccountingSupplierParty(), documentWRP.getInvoiceType()
//                        .getAccountingCustomerParty(),
//                documentWRP.getInvoiceType().getUBLExtensions());
        String barcodeValue = db.generateBarCodeInfoString(
                documentWRP.getTransaccion().getDocIdentidadNro(),
                documentWRP.getTransaccion().getDOCCodigo(),
                documentWRP.getTransaccion().getDOCSerie(),
                documentWRP.getTransaccion().getDOCNumero(),
                documentWRP.getInvoiceType().getTaxTotal(),
                documentWRP.getTransaccion().getDOCFechaVencimiento().toString(),
                documentWRP.getTransaccion().getDOCMontoTotal().toString(),
                documentWRP.getTransaccion().getSNDocIdentidadTipo(),
                documentWRP.getTransaccion().getSNDocIdentidadNro(),
                documentWRP.getInvoiceType().getUBLExtensions());

        DocumentoINF.GetEnviarTransaccion.capturarCodigo(transaction, barcodeValue, digestValue, Sociedad, connection);

        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        if (null != zipDocument) {
            /*
             * Verificar si en la configuracion se ha activado el flag
             * del PDF borrador
             */
            if (VariablesGlobales.UsoPdfSinRespuesta) {
                if (logger.isInfoEnabled()) {
                    logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");

                /* Se genera el PDF borrador */
                byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                        transaction.getDOCCodigo(), documentWRP, transaction);

                /* Anexar PDF borrador */
                extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                /*
                 * Retornar RESPUESTA
                 *
                 * Si se ha activado la configuracion del PDF borrador, se acualiza el estado debido a
                 * que no se puede generar n veces el borrador
                 */
                TransaccionBL.MarcarPDFBorrador(transaction);
            }

            /*
             * Verificar si se ha activado el uso de los servicios web de SUNAT.
             */
            if (VariablesGlobales.UsoSunat) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se esta utilizando los WebServices");

                //DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "1");//1:TRX. se ha enviado a servicio SUNAT
                if (transaction.getFEErrCod().equalsIgnoreCase("N")) { //N PRUEBAS NUMA
                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico.
                     */
                    WSConsumerNew wsConsumer = WSConsumerNew.newInstance(this.docUUID);

                    //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
                    wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);

                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendBill.");
                    byte[] cdrConstancy = wsConsumer.sendBill(zipDocument, documentName, documentWRP.getTransaccion(), Sociedad);

                    /*
                     * Verificar si SUNAT devolvio respuesta
                     */
                    if (null != cdrConstancy) {
                        if (logger.isInfoEnabled()) {
                            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                        }
                        transactionResponse = processCDRResponse(cdrConstancy, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                    } else {
                        if (WSConsumerNew.IErrorCode == 1033) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                            transactionResponse = processResponseSinCDR_1033(transaction);

                        } else if (WSConsumerNew.IErrorCode == 9999) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                            transactionResponse = processResponseSinCDRExcepcion(transaction);

                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se pasará al método corregido.", this.docUUID);
                        }
                    }
                } else if (transaction.getFEErrCod().equalsIgnoreCase("C")) {
                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico.
                     */
                    WSConsumerConsult wsConsumer = WSConsumerConsult.newInstance(this.docUUID);

                    wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration);

                    /*
                     * Se colocan los parametros necesarios para la consulta.
                     */
                    String documentRuc = transaction.getDocIdentidadNro();
                    String documentType = transaction.getDOCCodigo();
                    String documentSerie = transaction.getDOCSerie();
                    Integer documentNumber = Integer.valueOf(transaction.getDOCNumero());

                    if (logger.isInfoEnabled()) {
                        logger.info("consultInvoiceDocument() [" + this.docUUID + "] "
                                + "\n################# CONSULT INFORMATION #################"
                                + "\nDocumentRuc: " + documentRuc + "\tDocumentType: " + documentType
                                + "\nDocumentSerie: " + documentSerie + "\tDocumentNumber: " + documentNumber);
                    }
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] INFORMACION DE LA CONSULTA: RUC[" + documentRuc + "], TIPO[" + documentType + "], SERIE[" + documentSerie + "], NUMERO[" + documentNumber + "]");

                    WSConsumerConsult.StatusResponse statusResponse = wsConsumer.getStatusCDR(documentRuc, documentType, documentSerie, documentNumber);

                    if (null != statusResponse) {
                        /*
                         * Verificar si SUNAT devolvio respuesta
                         */
                        if (logger.isInfoEnabled()) {
                            logger.info("consultInvoiceDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                        }
                        transactionResponse = processCDRResponseV2(statusResponse, documentName, configuration, transaction.getFETipoTrans(), transaction);
                        transactionResponse = processCDRResponse(transactionResponse.getZip(), signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);

                        if (transactionResponse == null) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] No se genero la respuesta de la transaccion.", this.docUUID);
                        } else {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genero la respuesta de la transaccion.", this.docUUID);
                        }
                    } else {
                        logger.error("consultInvoiceDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_465.getMessage());
                        throw new NullPointerException(IVenturaError.ERROR_465.getMessage());
                    }
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("transactionInvoiceDocument() [" + this.docUUID + "] El estado no es ni nuevo ni corregido.");
                    }
                }
            } else {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] No se esta utilizando los WebServices", this.docUUID);

                //DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "2", Sociedad); //2:TRX. no se ha enviado a servicio SUNAT

                //TransaccionBL.setEstadoNoEnviadoSUNATTransaccion(transaction);
                transactionResponse = processCDRResponseDummy(null, signedDocument, fileHandler, configuration,
                        documentName, transaction.getDOCCodigo(), documentWRP, transaction);

                if (transactionResponse == null) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] No se genero la respuesta de la transaccion.", this.docUUID);
                } else {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genero la respuesta de la transaccion.", this.docUUID);
                }
            }
        } else {
            logger.error("transactionInvoiceDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionInvoiceDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionInvoiceDocument

    /**
     * Este metodo realiza el proceso de facturacion electronica, creando un
     * documento de tipo BOLETA DE VENTA.
     *
     * @param transaction objeto Transaccion, contiene la informacion para crear
     *                    un documento de tipo BOLETA DE VENTA.
     * @param doctype
     * @param extractor
     * @param Sociedad
     * @return Retorna la respuesta del proceso de facturacion electronica del
     * documento de tipo BOLETA DE VENTA.
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatGenericException
     * @throws Exception
     */
    public TransaccionRespuesta transactionBoletaDocument(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection) throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionBoletaDocument() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionBoletaDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         *      - Certificado nulo o vacio
         *      - La contraseña del certificado pueda abrir el certificado.
         */
        //CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);
        /* Generando el objeto InvoiceType para la BOLETA */
        InvoiceType boletaType = null;
        if (TipoVersionUBL.boleta.equals("20")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionBoletaDocument() [" + this.docUUID + "] Version 2.0 del UBL.");
            }
            UBLDocumentHandler20 ublHandler20 = UBLDocumentHandler20.newInstance(this.docUUID);
            boletaType = ublHandler20.generateBoletaType(transaction, signerName);
        } else if (TipoVersionUBL.boleta.equals("21")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionBoletaDocument() [" + this.docUUID + "] Version 2.1 del UBL.");
            }
            UBLDocumentHandler ublHandler21 = UBLDocumentHandler.newInstance(this.docUUID);
            boletaType = ublHandler21.generateBoletaType(transaction, signerName);
        }
        //UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        //InvoiceType boletaType = ublHandler.generateBoletaType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto InvoiceType de la BOLETA.");

//        /*
//         * Validar las casuisticas del documento UBL de tipo Boleta.
//         */
//        validationHandler.checkBoletaDocument(boletaType);
//        if (logger.isInfoEnabled()) {
//            logger.info("transactionBoletaDocument() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo BOLETA.");
//        }
//        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se validaron las casuisticas del documento de tipo BOLETA.");
        /*
         * Se genera el nombre del documento de tipo BOLETA
         */
        String documentName = DocumentNameHandler.getInstance().getBoletaName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionBoletaDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }
        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);
        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.BOLETA_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.BOLETA_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionBoletaDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }
        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(boletaType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionBoletaDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }
        SignerHandler signerHandler = SignerHandler.newInstance();
        //signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);
        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionBoletaDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());
        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
        documentWRP.setBoletaType((InvoiceType) ublDocument);
        PDFBasicGenerateHandler db = new PDFBasicGenerateHandler(docUUID);
        /* Obtener el digestValue */
        String digestValue = db.generateDigestValue(documentWRP.getBoletaType().getUBLExtensions());
        /* Generar el codigo de barra */
        String barcodeValue = db.generateBarCodeInfoString(documentWRP.getTransaccion().getDocIdentidadNro(), documentWRP.getTransaccion().getDOCCodigo(), documentWRP.getTransaccion().getDOCSerie(), documentWRP.getTransaccion().getDOCNumero(),
                documentWRP.getBoletaType().getTaxTotal(), formatIssueDate(boletaType.getIssueDate().getValue()), documentWRP.getTransaccion().getDOCMontoTotal().toString(), documentWRP.getTransaccion().getSNDocIdentidadTipo(),
                documentWRP.getTransaccion().getSNDocIdentidadNro(), documentWRP.getBoletaType().getUBLExtensions());
        DocumentoINF.GetEnviarTransaccion.capturarCodigo(transaction, barcodeValue, digestValue, Sociedad, connection);
        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionBoletaDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }
//        /*
//         * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
//         * al emisor electronico. Ademas, se guarda el objeto FileHandler para el tratamiento del
//         * documento UBL que se encuentra en DISCO.
//         */
//        WSConsumerNew wsConsumer = WSConsumerNew.newInstance(this.docUUID);
//        //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
//        wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);
//        if (logger.isInfoEnabled()) {
//            logger.info("transactionBoletaDocument() [" + this.docUUID + "] Se realizo las configuraciones del objeto WSConsumer.");
//        }
        if (Optional.ofNullable(zipDocument).isPresent()) {
            if (transaction.getFETipoTrans().equalsIgnoreCase(ISunatConnectorConfig.FE_TIPO_TRANS_BAJA)) {
                return processCDRResponseDummy(signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
            }
            /*
             * Verificar si en la configuracion se ha activado el flag
             * del PDF borrador
             */
            if (VariablesGlobales.UsoPdfSinRespuesta && !transaction.getFEEstado().equalsIgnoreCase("W")) {
                if (logger.isInfoEnabled()) {
                    logger.info("transactionBoletaDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
                /* Se genera el PDF borrador */
                byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                /* Anexar PDF borrador */
                extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);
                if (!VariablesGlobales.UsoSunat) {
                    transactionResponse = processCDRResponseCDRSinPDF(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP);
                }
                /*
                 * Retornar RESPUESTA
                 *
                 * Si se ha activado la configuracion del PDF borrador, se acualiza el estado debido a
                 * que no se puede generar n veces el borrador
                 */
                TransaccionBL.MarcarPDFBorrador(transaction);
            }
            /*
             * Verificar si se ha activado el uso de los servicios web de SUNAT.
             */
            if (VariablesGlobales.UsoSunat) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se esta utilizando los webService");
//                DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "1", Sociedad);//1:TRX. se ha enviado a servicio SUNAT
                /*
                 * Se genera de manera directa el PDF, debido a que se envia como resumen diario
                 */
                //if(doctype.equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)){
                transactionResponse = processCDRResponse(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                //}
            } else {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se esta utilizando los WebServices");
//                DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "2", Sociedad); //2:TRX. No se ha enviado a servicio SUNAT
                /*
                 * Archivo dummy, para simular la respuesta
                 */
                transactionResponse = processCDRResponseDummy(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                if (transactionResponse == null) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
                } else {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
                }
            }
        } else {
            logger.error("transactionBoletaDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionBoletaDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionBoletaDocument

    /**
     * Este metodo realiza el proceso de facturacion electronica, creando un
     * documento de tipo NOTA DE CREDITO.
     *
     * @param transaction Objeto Transaccion, contiene la informacion para crear
     *                    un documento de tipo NOTA DE CREDITO.
     * @param doctype
     * @param extractor
     * @param Sociedad
     * @return Retorna la respuesta del proceso de facturacion electronica del
     * documento de tipo NOTA DE CREDITO.
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatGenericException
     * @throws Exception
     */
    public TransaccionRespuesta transactionCreditNoteDocument(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection)
            throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionCreditNoteDocument() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionCreditNoteDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         *      - Certificado nulo o vacio
         * 	- La contrase�a del certificado pueda abrir el certificado.
         */
        //CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto CreditNoteType para la NOTA DE CREDITO */
        CreditNoteType creditNoteType = null;

        if (TipoVersionUBL.notacredito.equals("20")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] Version 2.0 del UBL.");
            }
            UBLDocumentHandler20 ublHandler20 = UBLDocumentHandler20.newInstance(this.docUUID);
            creditNoteType = ublHandler20.generateCreditNoteType(transaction, signerName);
        } else if (TipoVersionUBL.notacredito.equals("21")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] Version 2.1 del UBL.");
            }
            UBLDocumentHandler ublHandler21 = UBLDocumentHandler.newInstance(this.docUUID);
            creditNoteType = ublHandler21.generateCreditNoteType(transaction, signerName);
        }

        //UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        //CreditNoteType creditNoteType = ublHandler.generateCreditNoteType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto CreditNoteType de la NOTA DE CREDITO.");

//        /*
//         * Validar las casuisticas del documento UBL de tipo Nota de Credito.
//         */
//        validationHandler.checkCreditNoteDocument(creditNoteType);
//        if (logger.isInfoEnabled()) {
//            logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo NOTA DE CREDITO.");
//        }
//        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se validaron las casuisticas del documento de tipo NOTA DE CREDITO.");
        /*
         * Se genera el nombre del documento de tipo BOLETA
         */
        String documentName = DocumentNameHandler.getInstance().getCreditNoteName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionCreditNoteDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.CREDIT_NOTE_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.CREDIT_NOTE_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionCreditNoteDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(creditNoteType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();

        //signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());

        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
        documentWRP.setCreditNoteType((CreditNoteType) ublDocument);

        PDFBasicGenerateHandler db = new PDFBasicGenerateHandler(docUUID);

        /* Obtener el digestValue */
        String digestValue = db.generateDigestValue(documentWRP.getCreditNoteType().getUBLExtensions());

        /* Generar el codigo de barra */
        String barcodeValue = db.generateBarCodeInfoString(
                documentWRP.getTransaccion().getDocIdentidadNro(),
                documentWRP.getTransaccion().getDOCCodigo(),
                documentWRP.getTransaccion().getDOCSerie(),
                documentWRP.getTransaccion().getDOCNumero(),
                documentWRP.getCreditNoteType().getTaxTotal(),
                formatIssueDate(creditNoteType.getIssueDate().getValue()),
                documentWRP.getTransaccion().getDOCMontoTotal().toString(),
                documentWRP.getTransaccion().getSNDocIdentidadTipo(),
                documentWRP.getTransaccion().getSNDocIdentidadNro(),
                documentWRP.getCreditNoteType().getUBLExtensions());

        DocumentoINF.GetEnviarTransaccion.capturarCodigo(transaction, barcodeValue, digestValue, Sociedad,connection);

        /*
         * Verificar si en la configuracion se ha activado el flag
         * del PDF borrador
         */
        if (VariablesGlobales.UsoPdfSinRespuesta && !transaction.getFEEstado().equalsIgnoreCase("W")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
            }
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");

            /* Se genera el PDF borrador */
            byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                    transaction.getDOCCodigo(), documentWRP, transaction);

            /* Anexar PDF borrador */
            extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

            if (!VariablesGlobales.UsoSunat) {
                transactionResponse = processCDRResponseCDRSinPDF(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP);
            }

            /*
             * Retornar RESPUESTA
             *
             * Si se ha activado la configuracion del PDF borrador, se acualiza el estado debido a
             * que no se puede generar n veces el borrador
             */
            TransaccionBL.MarcarPDFBorrador(transaction);
        }

        /*
         * Verificar si se ha activado el uso de los servicios web de SUNAT.
         */
        if (VariablesGlobales.UsoSunat) {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se esta utilizando los WebServices");

            //DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "1", Sociedad);//1:TRX. se ha enviado a servicio SUNAT

            /*
             * En caso que sean NC referenciando Boletas, se genera el PDF
             */
            if ((transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE) || transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE) || transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) && transaction.getDOCSerie().substring(0, 1).equalsIgnoreCase("B")) {

                transactionResponse = processCDRResponse(null, signedDocument, fileHandler, configuration,
                        documentName, transaction.getDOCCodigo(), documentWRP, transaction);
            } else {
                /*
                 * En caso que sean NC referenciando Facturas, se genera el PDF
                 */
                if (transaction.getFEErrCod().equalsIgnoreCase("N")) {

                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendBill.");

                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico.
                     */
                    WSConsumerNew wsConsumer = WSConsumerNew.newInstance(this.docUUID);
                    //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
                    wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);

                    if (logger.isInfoEnabled()) {
                        logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] Se realizo las configuraciones del objeto WSConsumer.");
                    }

                    DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                    if (logger.isInfoEnabled()) {
                        logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
                    }

                    if (null != zipDocument) {

                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\u00fa utlizando los webService de Sunat");

                        if (logger.isInfoEnabled()) {
                            logger.info("transactionCreditNoteDocument() [" + this.docUUID + "] Enviando WS sendBill.");
                        }
                        byte[] cdrConstancy = wsConsumer.sendBill(zipDocument, documentName, documentWRP.getTransaccion(), Sociedad);
                        logger.info("====>>>cdrConstancy: " + cdrConstancy);

                        /*
                         * Verificar si SUNAT devolvio respuesta
                         */
                        if (null != cdrConstancy) {

                            if (logger.isInfoEnabled()) {
                                logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                            }

                            transactionResponse = processCDRResponse(cdrConstancy, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);

                        } else {
                            if (WSConsumerNew.IErrorCode == 1033) {
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                                transactionResponse = processResponseSinCDR_1033(transaction);

                            } else {
                                if (WSConsumerNew.IErrorCode == 9999) {
                                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                                    transactionResponse = processResponseSinCDRExcepcion(transaction);
                                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se pasará al método corregido.", this.docUUID);
                                }
                            }

                        }

                    } else {
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se obtuvo CDR.");
                        logger.error("transactionCreditNoteDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
                        throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("-transactionCreditNoteDocument() [" + this.docUUID + "]");
                    }

                } else {

                    if (transaction.getFEErrCod().equalsIgnoreCase("C")) {
                        /*
                         * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                         * al emisor electronico.
                         */

                        WSConsumerConsult wsConsumer = WSConsumerConsult.newInstance(this.docUUID);
                        wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration);
                        /*
                         *Se coloca los parametros necesarios para la consulta.
                         */
                        String documentRuc = transaction.getDocIdentidadNro();
                        String documentType = transaction.getDOCCodigo();
                        String documentSerie = transaction.getDOCSerie();
                        Integer documentNumber = Integer.valueOf(transaction.getDOCNumero());

                        if (logger.isInfoEnabled()) {
                            logger.info("consultDebitNoteDocument() [" + this.docUUID + "] "
                                    + "\n################# CONSULT INFORMATION #################"
                                    + "\nDocumentRuc: " + documentRuc + "\tDocumentType: " + documentType
                                    + "\nDocumentSerie: " + documentSerie + "\tDocumentNumber: " + documentNumber);
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] INFORMACION DE LA CONSULTA: RUC[" + documentRuc + "], TIPO[" + documentType + "], SERIE[" + documentSerie + "], NUMERO[" + documentNumber + "]");

                        WSConsumerConsult.StatusResponse statusResponse = wsConsumer.getStatusCDR(documentRuc, documentType, documentSerie, documentNumber);
                        if (null != statusResponse) {
                            /*
                             * Verificar si SUNAT devolvio respuesta
                             */
                            if (logger.isInfoEnabled()) {
                                logger.info("consultDebitNoteDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                            }
                            transactionResponse = processCDRResponseV2(statusResponse, documentName, configuration, transaction.getFETipoTrans(), transaction);
                            if (transactionResponse.getCodigo() != TransaccionRespuesta.RQT_EMITIDO_EXCEPTION) {
                                transactionResponse = processCDRResponse(transactionResponse.getZip(), signedDocument, fileHandler, configuration,
                                        documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                            }
                            if (transactionResponse == null) {
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
                            } else {
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
                            }
                        } else {
                            logger.error("consultDebitNoteDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_465.getMessage());
                            throw new NullPointerException(IVenturaError.ERROR_465.getMessage());
                        }
                    }
                }
            }
        } else {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se esta utilizando los webService");
            //DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "2", Sociedad); //2:TRX. No se ha enviado a servicio SUNAT
            transactionResponse = processCDRResponseDummy(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);

            if (transactionResponse == null) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
            } else {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionCreditNoteDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionCreditNoteDocument

    /**
     * Este metodo realiza el proceso de facturacion electronica, creando un
     * documento de tipo NOTA DE DEBITO.
     *
     * @param transaction Objeto Transaccion, contiene la informacion para crear
     *                    un documento de tipo NOTA DE DEBITO.
     * @param doctype
     * @param extractor
     * @param Sociedad
     * @return Retorna la respuesta del proceso de facturacion electronica del
     * documento de tipo NOTA DE DEBITO.
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatGenericException
     * @throws Exception
     */
    public TransaccionRespuesta transactionDebitNoteDocument(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection) throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionDebitNoteDocument() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionDebitNoteDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         * 	- Certificado nulo o vacio
         * 	- La contrase�a del certificado pueda abrir el certificado.
         */
        //CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto DebitNoteType para la NOTA DE DEBITO */
        DebitNoteType debitNoteType = null;

        if (TipoVersionUBL.notadebito.equals("20")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionDebitNoteDocument() [" + this.docUUID + "] Version 2.0 del UBL.");
            }
            UBLDocumentHandler20 ublHandler20 = UBLDocumentHandler20.newInstance(this.docUUID);
            debitNoteType = ublHandler20.generateDebitNoteType(transaction, signerName);
        } else if (TipoVersionUBL.notadebito.equals("21")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionDebitNoteDocument() [" + this.docUUID + "] Version 2.1 del UBL.");
            }
            UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
            debitNoteType = ublHandler.generateDebitNoteType(transaction, signerName);
        }

        //UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        //DebitNoteType debitNoteType = ublHandler.generateDebitNoteType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto DebitNoteType de la NOTA DE DEBITO.");

//        /*
//         * Validar las casuisticas del documento UBL de tipo Nota de Debito.
//         */
//        validationHandler.checkDebitNoteDocument(debitNoteType);
//        if (logger.isInfoEnabled()) {
//            logger.info("transactionDebitNoteDocument() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo NOTA DE DEBITO.");
//        }
//        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se validaron las casuisticas del documento de tipo NOTA DE DEBITO.");
        /*
         * Se genera el nombre del documento de tipo NOTA DE DEBITO
         */
        String documentName = DocumentNameHandler.getInstance().getDebitNoteName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionDebitNoteDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.DEBIT_NOTE_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.DEBIT_NOTE_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionDebitNoteDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(debitNoteType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionDebitNoteDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();

        //signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionDebitNoteDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());

        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
        documentWRP.setDebitNoteType((DebitNoteType) ublDocument);

        PDFBasicGenerateHandler db = new PDFBasicGenerateHandler(docUUID);

        /* Obtener el digestValue */
        String digestValue = db.generateDigestValue(documentWRP.getDebitNoteType().getUBLExtensions());

        /* Generar el codigo de barra */
        String barcodeValue = db.generateBarCodeInfoString(
                documentWRP.getTransaccion().getDocIdentidadNro(),
                documentWRP.getTransaccion().getDOCCodigo(),
                documentWRP.getTransaccion().getDOCSerie(),
                documentWRP.getTransaccion().getDOCNumero(),
                documentWRP.getDebitNoteType().getTaxTotal(),
                documentWRP.getTransaccion().getDOCFechaVencimiento().toString(),
                documentWRP.getTransaccion().getDOCMontoTotal().toString(),
                documentWRP.getTransaccion().getSNDocIdentidadTipo(),
                documentWRP.getTransaccion().getSNDocIdentidadNro(),
                documentWRP.getDebitNoteType().getUBLExtensions());

        DocumentoINF.GetEnviarTransaccion.capturarCodigo(transaction, barcodeValue, digestValue, Sociedad, connection);

        /*
         * Verificar si en la configuracion se ha activado el flag
         * del PDF borrador
         */
        if (VariablesGlobales.UsoPdfSinRespuesta && !transaction.getFEEstado().equalsIgnoreCase("W")) {
            if (logger.isInfoEnabled()) {
                logger.info("transactionDebitNoteDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
            }
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\\u00E1 generando un PDF BORRADOR.");

            /* Se genera el PDF borrador */
            byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                    transaction.getDOCCodigo(), documentWRP, transaction);

            /* Anexar PDF borrador */
            extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

            if (!VariablesGlobales.UsoSunat) {
                transactionResponse = processCDRResponseCDRSinPDF(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP);
            }

            /*
             * Retornar RESPUESTA
             *
             * Si se ha activado la configuracion del PDF borrador, se acualiza el estado debido a
             * que no se puede generar n veces el borrador
             */
            TransaccionBL.MarcarPDFBorrador(transaction);
        }

        /*
         * Verificar si se ha activado el uso de los servicios web de SUNAT.
         */
        if (VariablesGlobales.UsoSunat) {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se esta utilizando los WebServices");

            //DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "1", Sociedad); //1:TRX. se ha enviado a servicio SUNAT

            /*
             * En caso que sean NC referenciando Boletas, se genera el PDF
             */
            if ((transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE) || transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE) || transaction.getDOCCodigo().equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) && transaction.getDOCSerie().substring(0, 1).equalsIgnoreCase("B")) {
                transactionResponse = processCDRResponse(null, signedDocument, fileHandler, configuration,
                        documentName, transaction.getDOCCodigo(), documentWRP, transaction);
            } else {
                /*
                 * En caso que sean NC referenciando Facturas, se genera el PDF
                 */
                if (transaction.getFEErrCod().equalsIgnoreCase("N")) {

                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendBill.");

                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico.
                     */
                    WSConsumerNew wsConsumer = WSConsumerNew.newInstance(this.docUUID);
                    //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
                    wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);

                    if (logger.isInfoEnabled()) {
                        logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Se realizo las configuraciones del objeto WSConsumer.");
                    }

                    DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                    if (logger.isInfoEnabled()) {
                        logger.info("transactionInvoiceDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
                    }

                    /*
                     * Verificar si SUNAT devolvio respuesta
                     */
                    if (null != zipDocument) {

                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est� utlizando los webService de Sunat");
                        if (logger.isInfoEnabled()) {
                            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Se est� utilizando los WebService de SUNAT.");
                        }

                        /*
                         * Enviando la Factura al metodo "sendBill" del servicio web de Sunat.
                         */
                        if (logger.isInfoEnabled()) {
                            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Enviando WS sendBill.");
                        }
                        byte[] cdrConstancy = wsConsumer.sendBill(zipDocument, documentName, documentWRP.getTransaccion(), Sociedad);

                        if (null != cdrConstancy) {

                            if (logger.isInfoEnabled()) {
                                logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                            }

                            transactionResponse = processCDRResponse(cdrConstancy, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);

                        } else {
                            if (WSConsumerNew.IErrorCode == 1033) {
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                                transactionResponse = processResponseSinCDR_1033(transaction);

                            } else {
                                if (WSConsumerNew.IErrorCode == 9999) {
                                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                                    transactionResponse = processResponseSinCDRExcepcion(transaction);
                                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se pasará al método corregido.", this.docUUID);
                                }
                            }

                        }

                    } else {
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se obtuvo CDR.");
                        logger.error("transactionInvoiceDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
                        throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
                    }
                    if (logger.isDebugEnabled()) {
                        logger.debug("-transactionInvoiceDocument() [" + this.docUUID + "]");
                    }

                } else {

                    if (transaction.getFEErrCod().equalsIgnoreCase("C")) {

                        /*
                         * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                         * al emisor electronico.
                         */
                        WSConsumerConsult wsConsumer = WSConsumerConsult.newInstance(this.docUUID);
                        wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration);
                        /*
                         *Se coloca los parametros necesarios para la consulta.
                         */
                        String documentRuc = transaction.getDocIdentidadNro();
                        String documentType = transaction.getDOCCodigo();
                        String documentSerie = transaction.getDOCSerie();
                        Integer documentNumber = Integer.valueOf(transaction.getDOCNumero());

                        if (logger.isInfoEnabled()) {
                            logger.info("consultDebitNoteDocument() [" + this.docUUID + "] "
                                    + "\n################# CONSULT INFORMATION #################"
                                    + "\nDocumentRuc: " + documentRuc + "\tDocumentType: " + documentType
                                    + "\nDocumentSerie: " + documentSerie + "\tDocumentNumber: " + documentNumber);
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] INFORMACION DE LA CONSULTA: RUC[" + documentRuc + "], TIPO[" + documentType + "], SERIE[" + documentSerie + "], NUMERO[" + documentNumber + "]");

                        WSConsumerConsult.StatusResponse statusResponse = wsConsumer.getStatusCDR(documentRuc, documentType, documentSerie, documentNumber);

                        if (null != statusResponse) {/*
                         * Verificar si SUNAT devolvio respuesta
                         */

                            if (logger.isInfoEnabled()) {
                                logger.info("consultDebitNoteDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                            }
                            transactionResponse = processCDRResponseV2(statusResponse, documentName, configuration, transaction.getFETipoTrans(), transaction);

                            if (transactionResponse.getCodigo() != TransaccionRespuesta.RQT_EMITIDO_EXCEPTION) {
                                transactionResponse = processCDRResponse(transactionResponse.getZip(), signedDocument, fileHandler, configuration,
                                        documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                            }

                            if (transactionResponse == null) {
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
                            } else {
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
                            }

                        } else {
                            logger.error("consultDebitNoteDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_465.getMessage());
                            throw new NullPointerException(IVenturaError.ERROR_465.getMessage());
                        }

                    }

                }

            }
        } else {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se esta utilizando los webService");

            // DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "2", Sociedad); //2:TRX. No se ha enviado a servicio SUNAT

            transactionResponse = processCDRResponseDummy(null, signedDocument, fileHandler, configuration,
                    documentName, transaction.getDOCCodigo(), documentWRP, transaction);

            if (transactionResponse == null) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
            } else {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionDebitNoteDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionDebitNoteDocument

    /**
     * Este metodo realiza el proceso de facturacion electronica, creando un
     * documento de tipo PERCEPCION.
     *
     * @param transaction Objeto Transaccion, contiene la informacion para crear
     *                    un documento de tipo PERCEPCION
     * @param doctype
     * @param extractor
     * @param Sociedad
     * @return Retorna la respuesta del proceso de facturacion electronica del
     * documento de tipo PERCEPCION
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatGenericException
     * @throws Exception
     */
    public TransaccionRespuesta transactionPerceptionDocument(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection)
            throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionPerceptionDocument() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionPerceptionDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         *      - Certificado nulo o vacio
         * 	- La contrase�a del certificado pueda abrir el certificado.
         */
        //CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto PerceptionType para la PERCEPCION */
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        PerceptionType perceptionType = ublHandler.generatePerceptionType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto PerceptionType de la PERCEPCION.");

        /*
         * Validar la información necesaria para la percepción
         */
        validationHandler.checkPerceptionDocument(perceptionType);
        if (logger.isInfoEnabled()) {
            logger.info("transactionPerceptionDocument() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo PERCEPCION.");
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se validaron las casuisticas del documento de tipo PERCEPCION.");

        /*
         * Se genera el nombre del documento de tipo PERCEPCION
         */
        String documentName = DocumentNameHandler.getInstance().getPerceptionName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionPerceptionDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.PERCEPTION_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.PERCEPTION_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionPerceptionDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(perceptionType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionPerceptionDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();

        //signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionPerceptionDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());

        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
//        documentWRP.setPerceptionType(perceptionType);
        documentWRP.setPerceptionType((PerceptionType) ublDocument);

        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionPerceptionDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        if (null != zipDocument) {
            /*
             * Verificar si en la configuracion se ha activado el flag
             * del PDF borrador
             */
            if (VariablesGlobales.UsoPdfSinRespuesta && !transaction.getFEEstado().equalsIgnoreCase("W")) {
                if (logger.isInfoEnabled()) {
                    logger.info("transactionPerceptionDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");

                /* Se genera el PDF borrador */
                byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                        transaction.getDOCCodigo(), documentWRP, transaction);

                if (!VariablesGlobales.UsoSunat) {
                    extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                    transactionResponse = processCDRResponseCDRSinPDF(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP);
                }

                /* Anexar PDF borrador */
                extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                if (!VariablesGlobales.UsoSunat) {
                    transactionResponse = processCDRResponseDummy(null, signedDocument, fileHandler, configuration,
                            documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                }

                /*
                 * Retornar RESPUESTA
                 *
                 * Si se ha activado la configuracion del PDF borrador, se acualiza el estado debido a
                 * que no se puede generar n veces el borrador
                 */
                TransaccionBL.MarcarPDFBorrador(transaction);
            }

            /*
             * Verificar si se ha activado el uso de los servicios web de SUNAT.
             */
            if (VariablesGlobales.UsoSunat) {
                if (transaction.getFEErrCod().equalsIgnoreCase("N")) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se esta utilizando los WebServices");
                    //DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "1", Sociedad);//1:TRX. se ha enviado a servicio SUNAT
                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico. Ademas, se guarda el objeto FileHandler para el tratamiento del
                     * documento UBL que se encuentra en DISCO.
                     */
                    WSConsumerCPR wsConsumer = WSConsumerCPR.newInstance(this.docUUID);
                    //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
                    wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);
                    if (logger.isInfoEnabled()) {
                        logger.info("transactionPerceptionDocument() [" + this.docUUID + "] Se realizo las configuraciones del objeto WSConsumer.");
                    }
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendBill.");
                    byte[] cdrConstancy = wsConsumer.sendBill(zipDocument, documentName, transaction, Sociedad);
                    /*
                     * Verificar si SUNAT devolvio respuesta
                     */
                    if (null != cdrConstancy) {
                        if (logger.isInfoEnabled()) {
                            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                        }
                        transactionResponse = processCDRResponse(cdrConstancy, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                    } else {
                        if (WSConsumerCPR.IErrorCode == 1033) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                            transactionResponse = processResponseSinCDR_1033(transaction);
                        } else if (WSConsumerCPR.IErrorCode == 9999) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                            transactionResponse = processResponseSinCDRExcepcion(transaction);
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se pasará al método corregido.", this.docUUID);
                        }
                    }
                } else if (transaction.getFEErrCod().equalsIgnoreCase("C")) {
                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico.
                     */
                    WSConsumerConsult wsConsumer = WSConsumerConsult.newInstance(this.docUUID);
                    wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration);
                    /*
                     * Se colocan los parametros necesarios para la consulta.
                     */
                    String documentRuc = transaction.getDocIdentidadNro();
                    String documentType = transaction.getDOCCodigo();
                    String documentSerie = transaction.getDOCSerie();
                    Integer documentNumber = Integer.valueOf(transaction.getDOCNumero());
                    if (logger.isInfoEnabled()) {
                        logger.info("consultInvoiceDocument() [" + this.docUUID + "] "
                                + "\n################# CONSULT INFORMATION #################"
                                + "\nDocumentRuc: " + documentRuc + "\tDocumentType: " + documentType
                                + "\nDocumentSerie: " + documentSerie + "\tDocumentNumber: " + documentNumber);
                    }
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] INFORMACION DE LA CONSULTA: RUC[" + documentRuc + "], TIPO[" + documentType + "], SERIE[" + documentSerie + "], NUMERO[" + documentNumber + "]");
                    WSConsumerConsult.StatusResponse statusResponse = wsConsumer.getStatusCDR(documentRuc, documentType, documentSerie, documentNumber);
                    if (null != statusResponse) {
                        /*
                         * Verificar si SUNAT devolvio respuesta
                         */
                        if (logger.isInfoEnabled()) {
                            logger.info("consultInvoiceDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                        }
                        transactionResponse = processCDRResponseV2(statusResponse, documentName, configuration, transaction.getFETipoTrans(), transaction);
                        transactionResponse = processCDRResponse(transactionResponse.getZip(), signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                        if (transactionResponse == null) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] No se genero la respuesta de la transaccion.", this.docUUID);
                        } else {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genero la respuesta de la transaccion.", this.docUUID);
                        }
                    } else {
                        logger.error("consultInvoiceDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_465.getMessage());
                        throw new NullPointerException(IVenturaError.ERROR_465.getMessage());
                    }
                }
            } else {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se esta utilizando los WebServices");
//                DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "2", Sociedad); //2:TRX. No se ha enviado a servicio SUNAT
                transactionResponse = processCDRResponseDummy(null, signedDocument, fileHandler, configuration,
                        documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                if (transactionResponse == null) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
                } else {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
                }
            }
        } else {
            logger.error("transactionPerceptionDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        return transactionResponse;
    } //transactionPerceptionDocument

    /**
     * Este metodo realiza el proceso de facturacion electronica, creando un
     * documento de tipo RETENCION.
     *
     * @param transaction Objeto Transaccion, contiene la informacion para crear
     *                    un documento de tipo RETENCION
     * @param doctype
     * @param extractor
     * @param Sociedad
     * @return Retorna la respuesta del proceso de facturacion electronica del
     * documento de tipo RETENCION.
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatGenericException
     * @throws Exception
     */
    public TransaccionRespuesta transactionRetentionDocument(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection)
            throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionRetentionDocument() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRetentionDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         *      - Certificado nulo o vacio
         * 	- La contraseña del certificado pueda abrir el certificado.
         */
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto RetentionType para la RETENCION */
        this.docUUID = transaction.getFEId();
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        RetentionType retentionType = ublHandler.generateRetentionType(transaction, signerName);
        System.out.println(retentionType);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genero el objeto RetentionType de la RETENCION.", this.docUUID);

        /*
         * Validar la informacion necesaria para la retención
         */
        validationHandler.checkRetentionDocument(retentionType);
        if (logger.isInfoEnabled()) {
            logger.info("transactionRetentionDocument() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo RETENCION.");
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se validaron las casuisticas del documento de tipo RETENCI\ufffdN.", this.docUUID);

        /*
         * Se genera el nombre del documento de tipo RETENCION
         */
        String documentName = DocumentNameHandler.getInstance().getRetentionName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRetentionDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.RETENTION_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.RETENTION_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRetentionDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(retentionType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionRetentionDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionRetentionDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El documento [{1}] fue firmado correctamente en: {2}", new Object[]{this.docUUID, documentName, signedDocument.getAbsolutePath()});

        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());

        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
        documentWRP.setRetentionType((RetentionType) ublDocument);

        PDFBasicGenerateHandler db = new PDFBasicGenerateHandler(docUUID);

        /* Obtener el digestValue */
        String digestValue = db.generateDigestValue(documentWRP.getRetentionType().getUblExtensions());

        /* Generar el codigo de barra */
        String barcodeValue = db.generateBarcodeInfoV2(
                documentWRP.getRetentionType().getId().getValue(),
                IUBLConfig.DOC_RETENTION_CODE,
                formatIssueDate(retentionType.getIssueDate().getValue()),
                documentWRP.getRetentionType().getTotalInvoiceAmount().getValue(),
                BigDecimal.ZERO,
                documentWRP.getRetentionType().getAgentParty(),
                documentWRP.getRetentionType().getReceiverParty(),
                documentWRP.getRetentionType().getUblExtensions());

        DocumentoINF.GetEnviarTransaccion.capturarCodigo(transaction, barcodeValue, digestValue, Sociedad, connection);

        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionRetentionDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        if (null != zipDocument) {
            /*
             * Verificar si en la configuracion se ha activado el flag
             * del PDF borrador
             */
            if (VariablesGlobales.UsoPdfSinRespuesta && !transaction.getFEEstado().equalsIgnoreCase("W")) {
                if (logger.isInfoEnabled()) {
                    logger.info("transactionRetentionDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se est\u00e1 generando un PDF BORRADOR.", this.docUUID);

                /* Se genera el PDF borrador */
                byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                        transaction.getDOCCodigo(), documentWRP, transaction);

                /* Anexar PDF borrador */
                extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                if (!VariablesGlobales.UsoSunat) {
                    extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                    transactionResponse = processCDRResponseCDRSinPDF(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP);
                }

                /*
                 * Retornar RESPUESTA
                 *
                 * Si se ha activado la configuracion del PDF borrador, se acualiza el estado debido a
                 * que no se puede generar n veces el borrador
                 */
                TransaccionBL.MarcarPDFBorrador(transaction);
            }

            /*
             * Verificar si se ha activado el uso de los servicios web de SUNAT.
             */
            if (VariablesGlobales.UsoSunat) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se esta utilizando los WebServices");
                try {
                    //DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "1", Sociedad); //1:TRX. se ha enviado a servicio SUNAT
                } catch (Exception e) {
                    logger.error("No se pudo actualizar el campo por: [{" + e.getLocalizedMessage() + "}]");
                }

                if (transaction.getFEErrCod().equalsIgnoreCase("N")) {
                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico. Ademas, se guarda el objeto FileHandler para el tratamiento del
                     * documento UBL que se encuentra en DISCO.
                     */
                    WSConsumerCPR wsConsumer = WSConsumerCPR.newInstance(this.docUUID);
                    wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);
                    if (logger.isInfoEnabled()) {
                        logger.info("transactionRetentionDocument() [" + this.docUUID + "] Se realizo las configuraciones del objeto WSConsumer.");
                    }

                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendBill.");
                    byte[] cdrConstancy = wsConsumer.sendBill(zipDocument, documentName, transaction, Sociedad);

                    /*
                     * Verificar si SUNAT devolvio respuesta
                     */
                    if (null != cdrConstancy) {
                        if (logger.isInfoEnabled()) {
                            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                        }
                        transactionResponse = processCDRResponse(cdrConstancy, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                    } else {
                        if (WSConsumerCPR.IErrorCode == 1033) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                            transactionResponse = processResponseSinCDR_1033(transaction);
                        } else if (WSConsumerCPR.IErrorCode == 9999) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                            transactionResponse = processResponseSinCDRExcepcion(transaction);

                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se pasará al método corregido.", this.docUUID);
                        }
                    }
                } else {
                    if (transaction.getFEErrCod().equalsIgnoreCase("C")) {
                        /*
                         * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                         * al emisor electronico.
                         */
                        WSConsumerConsult wsConsumerConsult = WSConsumerConsult.newInstance(this.docUUID);
                        wsConsumerConsult.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration);

                        /*
                         *Se coloca los parametros necesarios para la consulta.
                         */
                        String documentRuc = transaction.getDocIdentidadNro();
                        String documentType = transaction.getDOCCodigo();
                        String documentSerie = transaction.getDOCSerie();
                        Integer documentNumber = Integer.valueOf(transaction.getDOCNumero());

                        if (logger.isInfoEnabled()) {
                            logger.info("consultDebitNoteDocument() [" + this.docUUID + "] "
                                    + "\n################# CONSULT INFORMATION #################"
                                    + "\nDocumentRuc: " + documentRuc + "\tDocumentType: " + documentType
                                    + "\nDocumentSerie: " + documentSerie + "\tDocumentNumber: " + documentNumber);
                        }

                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] INFORMACION DE LA CONSULTA: RUC[{1}], TIPO[{2}], SERIE[{3}], NUMERO[{4}]", new Object[]{this.docUUID, documentRuc, documentType, documentSerie, documentNumber});

                        WSConsumerConsult.StatusResponse statusResponse = wsConsumerConsult.getStatusCDR(documentRuc, documentType, documentSerie, documentNumber);

                        if (null != statusResponse) {
                            /*
                             * Verificar si SUNAT devolvio respuesta
                             */
                            if (logger.isInfoEnabled()) {
                                logger.info("consultDebitNoteDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                            }
                            transactionResponse = processCDRResponseV2(statusResponse, documentName, configuration, transaction.getFETipoTrans(), transaction);

                            transactionResponse = processCDRResponse(transactionResponse.getZip(), signedDocument, fileHandler, configuration,
                                    documentName, transaction.getDOCCodigo(), documentWRP, transaction);

                            if (transactionResponse == null) {
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] No se genero la respuesta de la transaccion.", this.docUUID);
                            } else {
                                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genero la respuesta de la transaccion.", this.docUUID);
                            }
                        } else {
                            logger.error("consultRetentionDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_465.getMessage());
                            transactionResponse = processResponseSinCDR(transaction);
                            //throw new NullPointerException(IVenturaError.ERROR_465.getMessage());
                        }
                    }
                    //transactionResponse = processResponseConsultaCDR();
                }
            } else {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se esta utilizando los WebServices");
                try {
                    //DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "2", Sociedad); //2:TRX. No se ha enviado a servicio SUNAT
                } catch (Exception e) {
                    logger.error("No se pudo actualizar el campo por: [{" + e.getLocalizedMessage() + "}]");
                }

                transactionResponse = processCDRResponseDummy(null, signedDocument, fileHandler, configuration,
                        documentName, transaction.getDOCCodigo(), documentWRP, transaction);

                if (transactionResponse == null) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
                } else {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
                }
            }
        } else {
            logger.error("transactionRetentionDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionRetentionDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionRetentionDocument

    /**
     * Este metodo realiza el proceso de facturacion electronica, creando un
     * documento de tipo GUIA DE REMISSION.
     *
     * @param transaction objeto Transaccion, contiene la informacion para crear
     *                    un documento de tipo GUIA DE REMISION.
     * @param doctype
     * @param extractor
     * @param Sociedad
     * @return Retorna la respuesta del proceso de facturacion electronica del
     * documento de tipo GUIA DE REMISION.
     * @throws ConfigurationException
     * @throws UBLDocumentException
     * @throws SignerDocumentException
     * @throws SunatGenericException
     * @throws Exception
     */
    public TransaccionRespuesta transactionRemissionGuideDocument(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection)
            throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionRemissionGuideDocument() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRemissionGuideDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         * 	- Certificado nulo o vacio
         * 	- La contrase�a del certificado pueda abrir el certificado.
         */
        ///CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto DespatchAdviceType para la GUIA DE REMISION */
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        DespatchAdviceType despatchAdviceType = ublHandler.generateDespatchAdviceType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto DespatchAdviceType de la GUIA DE REMISION.");

        /*
         * Validar la información necesaria para la RETENCIÓN
         */
        validationHandler.checkRemissionGuideDocument(despatchAdviceType);
        if (logger.isInfoEnabled()) {
            logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo GUIA DE REMISION.");
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se validaron las casuisticas del documento de tipo GUIA DE  REMISIÓN .");

        /*
         * Se genera el nombre del documento de tipo GUIA DE REMISION
         */
        String documentName = DocumentNameHandler.getInstance().getRemissionGuideName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRemissionGuideDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.REMISSION_GUIDE_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.REMISSION_GUIDE_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRemissionGuideDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(despatchAdviceType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());

        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
        documentWRP.setAdviceType((DespatchAdviceType) ublDocument);

        PDFBasicGenerateHandler db = new PDFBasicGenerateHandler(docUUID);
//
//        /* Agregar código de DigestValue */
        String digestValue = db.generateDigestValue(documentWRP.getAdviceType().getUBLExtensions());

        /* Agregar código de Barra */
        Date docFechaVencimiento = transaction.getDOCFechaVencimiento();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = simpleDateFormat.format(docFechaVencimiento);
        DespatchAdviceType guia = documentWRP.getAdviceType();
        String barcodeValue = db.generateGuiaBarcodeInfoV2(guia.getID().getValue(), IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE, fecha, BigDecimal.ZERO, BigDecimal.ZERO,
                guia.getDespatchSupplierParty(), guia.getDeliveryCustomerParty(), guia.getUBLExtensions());
        DocumentoINF.GetEnviarTransaccion.capturarCodigo(transaction, barcodeValue, digestValue, Sociedad, connection);
        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        if (null != zipDocument) {
            /*
             * Verificar si en la configuracion se ha activado el flag
             * del PDF borrador
             */
            if (VariablesGlobales.UsoPdfSinRespuesta && !transaction.getFEEstado().equalsIgnoreCase("W")) {
                if (logger.isInfoEnabled()) {
                    logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");

                /* Se genera el PDF borrador */
                byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                        transaction.getDOCCodigo(), documentWRP, transaction);

                /* Anexar PDF borrador */
                extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                if (!VariablesGlobales.UsoSunat) {
                    extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                    transactionResponse = processCDRResponseCDRSinPDF(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP);
                }

                /*
                 * Retornar RESPUESTA
                 *
                 * Si se ha activado la configuracion del PDF borrador, se acualiza el estado debido a
                 * que no se puede generar n veces el borrador
                 */
                TransaccionBL.MarcarPDFBorrador(transaction);
            }

            /*
             * Verificar si se ha activado el uso de los servicios web de SUNAT.
             */
            if (VariablesGlobales.UsoSunat) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se esta utilizando los WebServices");

//                DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "1", Sociedad); //1:TRX. se ha enviado a servicio SUNAT

                if (transaction.getFEErrCod().equalsIgnoreCase("N")) {
                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico. Ademas, se guarda el objeto FileHandler para el tratamiento del
                     * documento UBL que se encuentra en DISCO.
                     */
                    WSConsumerGR wsConsumer = WSConsumerGR.newInstance(this.docUUID);

                    //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
                    wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);

                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendBill.");
                    byte[] cdrConstancy = wsConsumer.sendBill(zipDocument, documentName, transaction, Sociedad);

                    /*
                     * Verificar si SUNAT devolvio respuesta
                     */
                    if (null != cdrConstancy) {
                        if (logger.isInfoEnabled()) {
                            logger.info("transactionInvoiceDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                        }
                        transactionResponse = processCDRResponse(cdrConstancy, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP, transaction);
                    } else {
                        if (WSConsumerGR.IErrorCode == 1033) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                            transactionResponse = processResponseSinCDR_1033(transaction);
                        } else if (WSConsumerGR.IErrorCode == 9999) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El CDR devuelto est\u00e1 vac\u00edo.", this.docUUID);
                            transactionResponse = processResponseSinCDRExcepcion(transaction);

                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se pasará al método corregido.", this.docUUID);
                        }
                    }
                } else if (transaction.getFEErrCod().equalsIgnoreCase("C")) {
                    /*
                     * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
                     * al emisor electronico.
                     */
                    WSConsumerConsult wsConsumerConsult = WSConsumerConsult.newInstance(this.docUUID);

                    wsConsumerConsult.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration);

                    /*
                     * Se coloca los parametros necesarios para la consulta.
                     */
                    String documentRuc = transaction.getDocIdentidadNro();
                    String documentType = transaction.getDOCCodigo();
                    String documentSerie = transaction.getDOCSerie();
                    Integer documentNumber = Integer.valueOf(transaction.getDOCNumero());

                    if (logger.isInfoEnabled()) {
                        logger.info("consultRemissionGuideDocument() [" + this.docUUID + "] "
                                + "\n################# CONSULT INFORMATION #################"
                                + "\nDocumentRuc: " + documentRuc + "\tDocumentType: " + documentType
                                + "\nDocumentSerie: " + documentSerie + "\tDocumentNumber: " + documentNumber);
                    }
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] INFORMACION DE LA CONSULTA: RUC[" + documentRuc + "], TIPO[" + documentType + "], SERIE[" + documentSerie + "], NUMERO[" + documentNumber + "]");

                    WSConsumerConsult.StatusResponse statusResponse = wsConsumerConsult.getStatusCDR(documentRuc, documentType, documentSerie, documentNumber);

                    if (null != statusResponse) {
                        /*
                         * Verificar si SUNAT devolvio respuesta
                         */
                        if (logger.isInfoEnabled()) {
                            logger.info("consultRemissionGuideDocument() [" + this.docUUID + "] Procesando respuesta CDR obtenida.");
                        }
                        transactionResponse = processCDRResponseV2(statusResponse, documentName, configuration, transaction.getFETipoTrans(), transaction);

                        transactionResponse = processCDRResponse(transactionResponse.getZip(), signedDocument, fileHandler, configuration,
                                documentName, transaction.getDOCCodigo(), documentWRP, transaction);

                        if (transactionResponse == null) {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
                        } else {
                            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
                        }
                    } else {
                        logger.error("consultRemissionGuideDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_465.getMessage());
                        throw new NullPointerException(IVenturaError.ERROR_465.getMessage());
                    }
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] El estado no es ni nuevo ni corregido.");
                    }
                }
            } else {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se esta utilizando los WebServices");

//                DocumentoDAO.setActualizarEstadoEnvioSUNAT(transaction, "2", Sociedad); //2:TRX. No se ha enviado a servicio SUNAT

                transactionResponse = processCDRResponseDummy(null, signedDocument, fileHandler, configuration,
                        documentName, transaction.getDOCCodigo(), documentWRP, transaction);

                if (transactionResponse == null) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] No se genero la respuesta de la transaccion.");
                } else {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero la respuesta de la transaccion.");
                }
            }
        } else {
            logger.error("transactionRemissionGuideDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionRemissionGuideDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionRemissionGuideDocument

    /**
     * GUIAS SUNAT REST
     */
    public TransaccionRespuesta transactionRemissionGuideDocumentRest(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration, Connection connection)
            throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionRemissionGuideDocument() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Serie y correlativo
         * - RUC del emisor
         * - Fecha de emision
         */
        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();

        //GUIAS
        String tipoTransaccion = sociedad.getTipoIntegracionGuias();
        String clientId = sociedad.getClientId();
        String secretId = sociedad.getSecretId();
        String usuarioGuias = sociedad.getUsuarioGuias();
        String passwordGuias = sociedad.getPasswordGuias();
        String scope = sociedad.getScope();


        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRemissionGuideDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         * 	- Certificado nulo o vacio
         * 	- La contrase�a del certificado pueda abrir el certificado.
         */
        ///CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto DespatchAdviceType para la GUIA DE REMISION */
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        DespatchAdviceType despatchAdviceType = ublHandler.generateDespatchAdviceType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto DespatchAdviceType de la GUIA DE REMISION.");

        /*
         * Validar la información necesaria para la RETENCIÓN
         */
        validationHandler.checkRemissionGuideDocument(despatchAdviceType);
        if (logger.isInfoEnabled()) {
            logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo GUIA DE REMISION.");
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se validaron las casuisticas del documento de tipo GUIA DE  REMISIÓN .");

        /*
         * Se genera el nombre del documento de tipo GUIA DE REMISION
         */
        String documentName = DocumentNameHandler.getInstance().getRemissionGuideName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRemissionGuideDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.REMISSION_GUIDE_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.REMISSION_GUIDE_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionRemissionGuideDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(despatchAdviceType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }


        SignerHandler signerHandler = SignerHandler.newInstance();
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());

        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
        documentWRP.setAdviceType((DespatchAdviceType) ublDocument);

        PDFBasicGenerateHandler db = new PDFBasicGenerateHandler(docUUID);
//
//        /* Agregar código de DigestValue */
        String digestValue = db.generateDigestValue(documentWRP.getAdviceType().getUBLExtensions());

        /* Agregar código de Barra */
        Date docFechaVencimiento = transaction.getDOCFechaVencimiento();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = simpleDateFormat.format(docFechaVencimiento);
        DespatchAdviceType guia = documentWRP.getAdviceType();
        String barcodeValue = db.generateGuiaBarcodeInfoV2(guia.getID().getValue(), IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE, fecha, BigDecimal.ZERO, BigDecimal.ZERO,
                guia.getDespatchSupplierParty(), guia.getDeliveryCustomerParty(), guia.getUBLExtensions());
        DocumentoINF.GetEnviarTransaccion.capturarCodigo(transaction, barcodeValue, digestValue, Sociedad, connection);
        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());

        if (logger.isInfoEnabled()) {
            logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        logger.error("ERRROR +++++++++ ==========" + zipDocument.toString());

        if (null != zipDocument) {

            /*
             * Verificar si en la configuracion se ha activado el flag
             * del PDF borrador
             */
            if (VariablesGlobales.UsoPdfSinRespuesta) {
                if (logger.isInfoEnabled()) {
                    logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");

                /* Se genera el PDF borrador */
                byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                        transaction.getDOCCodigo(), documentWRP, transaction);

                /* Anexar PDF borrador */
                extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                if (!VariablesGlobales.UsoSunat) {
                    extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                    transactionResponse = processCDRResponseCDRSinPDF(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP);
                }

                /*
                 * Retornar RESPUESTA
                 *
                 * Si se ha activado la configuracion del PDF borrador, se acualiza el estado debido a
                 * que no se puede generar n veces el borrador
                 */
                TransaccionBL.MarcarPDFBorrador(transaction);
            }

            /*
             * Verificar si se ha activado el uso de los servicios web de SUNAT.
             */

            if (transaction.getFEErrCod().equalsIgnoreCase("N")) {
                //jose luis becerra regalado
                ClientJwtSunat clientJwtSunat = new ClientJwtSunat();
                BdsMaestras bdsMaestras = new BdsMaestras(tipoTransaccion, clientId, secretId, usuarioGuias, passwordGuias, scope);
                ResponseDTO responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);

                if (responseDTOJWT.getStatusCode() == 400 || responseDTOJWT.getStatusCode() == 401) {
                    return generateResponseRest(documentWRP, responseDTOJWT);
                }


                //DECLARE
                ResponseDTO responseDTO = clientJwtSunat.declareSunat(documentName, documentPath.replace("xml", "zip"), responseDTOJWT.getAccess_token());

                //DECLARE
                if (responseDTO.getStatusCode() == 401) {
                    responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);
                    responseDTO = clientJwtSunat.declareSunat(documentName, documentPath.replace("xml", "zip"), responseDTOJWT.getAccess_token());
                }

                sendQR(transaction.getDOCCodigo(), documentWRP);
                Thread.sleep(5000);

                //CONSULT
                responseDTO = clientJwtSunat.consult(responseDTO.getNumTicket(), responseDTOJWT.getAccess_token());
                if (responseDTO.getStatusCode() == 401) {
                    responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);
                    responseDTO = clientJwtSunat.consult(responseDTO.getNumTicket(), responseDTOJWT.getAccess_token());
                }

                int contador = 0;
                while (responseDTO.getCodRespuesta().equals("98")) {
                    Thread.sleep(5000);
                    if (contador == 5) break;
                    responseDTO = clientJwtSunat.consult(responseDTO.getNumTicket(), responseDTOJWT.getAccess_token());
                    contador++;
                }

                //EXTRAER ZIP
                if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("0")) {
                    transactionResponse = generateResponseRest(documentWRP, responseDTO);
                    byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                            transaction.getDOCCodigo(), documentWRP, transaction);
                    transactionResponse.setPdf(pdf);
                    extractor.AgregarAnexos(transaction, null, pdf, null, VariablesGlobales.UsoPdfSinRespuesta, Sociedad, connection);

                    //AGREGAR A SAP CAMPO
                    logger.info("CODIGO TIKCET GUIAS SUNAT - [0]" + responseDTO.getNumTicket());
                    transactionResponse.setTicketRest(responseDTO.getNumTicket());
                } else if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("98")) {
                    logger.info("CODIGO TIKCET GUIAS SUNAT - [98]" + responseDTO.getNumTicket());
                    transactionResponse = generateResponseRest(documentWRP, responseDTO);
                    transactionResponse.setTicketRest(responseDTO.getNumTicket());
                } else if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("99")) {
                    transactionResponse = generateResponseRest(documentWRP, responseDTO);
                }
                byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
                transactionResponse.setXml(documentBytes);
                extractor.AgregarAnexos(transaction, documentBytes, null, null, Boolean.FALSE, Sociedad, connection);

            } else if (transaction.getFEErrCod().equalsIgnoreCase("C")) {

                if (transaction.getTransaccionGuiaRemision().getTicketRest() != null && !transaction.getTransaccionGuiaRemision().getTicketRest().isEmpty()) {
                    //CONSULT
                    ClientJwtSunat clientJwtSunat = new ClientJwtSunat();
                    BdsMaestras bdsMaestras = new BdsMaestras(tipoTransaccion, clientId, secretId, usuarioGuias, passwordGuias, scope);
                    ResponseDTO responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);

                    //ResponseDTO responseDTO = clientJwtSunat.declareSunat(documentName, documentPath.replace("xml", "zip"), responseDTOJWT.getAccess_token());

                    ResponseDTO responseDTO = clientJwtSunat.consult(transaction.getTransaccionGuiaRemision().getTicketRest(), responseDTOJWT.getAccess_token());
                    if (responseDTO.getStatusCode() == 401) {
                        responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);
                        responseDTO = clientJwtSunat.consult(responseDTO.getNumTicket(), responseDTOJWT.getAccess_token());
                    }

                    if (responseDTO.getStatusCode() == 400 || responseDTO.getStatusCode() == 404) {
                        return generateResponseRest(documentWRP, responseDTO);
                    }

                    sendQR(transaction.getDOCCodigo(), documentWRP);

                    //responseDTO.setCodRespuesta("0");
                    if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("0")) {
                        transactionResponse = generateResponseRest(documentWRP, responseDTO);
                        byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                                transaction.getDOCCodigo(), documentWRP, transaction);
                        transactionResponse.setPdf(pdf);
                        extractor.AgregarAnexos(transaction, null, pdf, null, VariablesGlobales.UsoPdfSinRespuesta, Sociedad, connection);

                        //AGREGAR A SAP CAMPO
                        transactionResponse.setTicketRest(responseDTO.getNumTicket());
                    } else if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("98")) {
                        transactionResponse = generateResponseRest(documentWRP, responseDTO);
                    } else if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("99")) {
                        transactionResponse = generateResponseRest(documentWRP, responseDTO);
                    }
                    byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
                    transactionResponse.setXml(documentBytes);
                    extractor.AgregarAnexos(transaction, documentBytes, null, null, Boolean.FALSE, Sociedad, connection);
                }
            }
        } else {
            logger.error("transactionRemissionGuideDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionRemissionGuideDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionRemissionGuideDocument

    public TransaccionRespuesta transactionCarrierGuideDocumentRest(Transaccion transaction, String doctype, DocumentoINF extractor, ICompany Sociedad, Configuracion configuration , Connection connection)
            throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {



        if (logger.isDebugEnabled()) {
            logger.debug("+transactionCarrierGuideDocumentRest() [" + this.docUUID + "] DOC_Id: " + transaction.getDOCId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        boolean isContingencia = false;
        List<TransaccionContractdocref> contractdocrefs = transaction.getTransaccionContractdocrefList();
        for (TransaccionContractdocref contractdocref : contractdocrefs) {
            if ("cu31".equalsIgnoreCase(contractdocref.getUsuariocampos().getNombre())) {
                isContingencia = "Si".equalsIgnoreCase(contractdocref.getValor());
                break;
            }
        }
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation(transaction.getDOCId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision(), transaction.getSNEMail(), transaction.getEMail(), isContingencia);

        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();

        //GUIAS
        String tipoTransaccion = sociedad.getTipoIntegracionGuias();
        String clientId = sociedad.getClientId();
        String secretId = sociedad.getSecretId();
        String usuarioGuias = sociedad.getUsuarioGuias();
        String passwordGuias = sociedad.getPasswordGuias();
        String scope = sociedad.getScope();


        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionCarrierGuideDocumentRest() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         * 	- Certificado nulo o vacio
         * 	- La contrase�a del certificado pueda abrir el certificado.
         */
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto DespatchAdviceType para la GUIA DE TRANSPORTISTA */
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        //DespatchAdviceType despatchAdviceType = ublHandler.generateDespatchAdviceType(transaction, signerName);
        DespatchAdviceType despatchAdviceType = ublHandler.generateCarrierDespatchAdviceType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto DespatchAdviceType de la GUIA DE TRANSPORTISTA.");

        /*
         * Validar la información necesaria para la RETENCIÓN
         */
        validationHandler.checkRemissionGuideDocument(despatchAdviceType);
        if (logger.isInfoEnabled()) {
            logger.info("transactionCarrierGuideDocumentRest() [" + this.docUUID + "] Se validaron las casuisticas del documento de tipo GUIA DE TRANSPORTISTA.");
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se validaron las casuisticas del documento de tipo GUIA DE  TRANSPORTISTA .");

        /*
         * Se genera el nombre del documento de tipo GUIA DE TRANSPORTISTA
         */
        //String documentName = DocumentNameHandler.getInstance().getRemissionGuideName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        String documentName = DocumentNameHandler.getInstance().getRemissionGuideTransportName(transaction.getDocIdentidadNro(), transaction.getDOCId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionCarrierGuideDocumentRest() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.CARRIER_GUIDE_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.CARRIER_GUIDE_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionCarrierGuideDocumentRest() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(despatchAdviceType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionCarrierGuideDocumentRest() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionCarrierGuideDocumentRest() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        Object ublDocument = fileHandler.getSignedDocument(signedDocument, transaction.getDOCCodigo());

        UBLDocumentWRP documentWRP = UBLDocumentWRP.getInstance();
        documentWRP.setTransaccion(transaction);
        documentWRP.setAdviceType((DespatchAdviceType) ublDocument);

        PDFBasicGenerateHandler db = new PDFBasicGenerateHandler(docUUID);

        String digestValue = db.generateDigestValue(documentWRP.getAdviceType().getUBLExtensions());

        /* Agregar código de Barra */
        Date docFechaVencimiento = transaction.getDOCFechaVencimiento();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = simpleDateFormat.format(docFechaVencimiento);
        DespatchAdviceType guia = documentWRP.getAdviceType();
        String barcodeValue = db.generateGuiaBarcodeInfoV2(guia.getID().getValue(), IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE, fecha, BigDecimal.ZERO, BigDecimal.ZERO,
                guia.getDespatchSupplierParty(), guia.getDeliveryCustomerParty(), guia.getUBLExtensions());
        DocumentoINF.GetEnviarTransaccion.capturarCodigo(transaction, barcodeValue, digestValue, Sociedad, connection);
        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());

        if (logger.isInfoEnabled()) {
            logger.info("transactionCarrierGuideDocumentRest() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        logger.error("ERRROR +++++++++ ==========" + zipDocument.toString());

        if (null != zipDocument) {

            /*
             * Verificar si en la configuracion se ha activado el flag
             * del PDF borrador
             */
            if (VariablesGlobales.UsoPdfSinRespuesta) {
                if (logger.isInfoEnabled()) {
                    logger.info("transactionRemissionGuideDocument() [" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se est\u00E1 generando un PDF BORRADOR.");


                byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                        transaction.getDOCCodigo(), documentWRP, transaction);

                extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                if (!VariablesGlobales.UsoSunat) {
                    extractor.AgregarAnexos(transaction, null, pdf, null, Boolean.TRUE, Sociedad, connection);

                    transactionResponse = processCDRResponseCDRSinPDF(null, signedDocument, fileHandler, configuration, documentName, transaction.getDOCCodigo(), documentWRP);
                }

                TransaccionBL.MarcarPDFBorrador(transaction);
            }

            /*
             * Verificar si se ha activado el uso de los servicios web de SUNAT.
             */

            if (transaction.getFEErrCod().equalsIgnoreCase("N")) {
                //jose luis becerra regalado
                ClientJwtSunat clientJwtSunat = new ClientJwtSunat();
                BdsMaestras bdsMaestras = new BdsMaestras(tipoTransaccion, clientId, secretId, usuarioGuias, passwordGuias, scope);
                ResponseDTO responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);

                if (responseDTOJWT.getStatusCode() == 400 || responseDTOJWT.getStatusCode() == 401) {
                    return generateResponseRest(documentWRP, responseDTOJWT);
                }


                //DECLARE
                ResponseDTO responseDTO = clientJwtSunat.declareSunat(documentName, documentPath.replace("xml", "zip"), responseDTOJWT.getAccess_token());

                //DECLARE
                if (responseDTO.getStatusCode() == 401) {
                    responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);
                    responseDTO = clientJwtSunat.declareSunat(documentName, documentPath.replace("xml", "zip"), responseDTOJWT.getAccess_token());
                }

                //sendQR(transaction.getDOCCodigo(), documentWRP);
                Thread.sleep(5000);

                //CONSULT
                responseDTO = clientJwtSunat.consult(responseDTO.getNumTicket(), responseDTOJWT.getAccess_token());
                if (responseDTO.getStatusCode() == 401) {
                    responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);
                    responseDTO = clientJwtSunat.consult(responseDTO.getNumTicket(), responseDTOJWT.getAccess_token());
                }

                int contador = 0;
                while (responseDTO.getCodRespuesta().equals("98")) {
                    Thread.sleep(5000);
                    if (contador == 5) break;
                    responseDTO = clientJwtSunat.consult(responseDTO.getNumTicket(), responseDTOJWT.getAccess_token());
                    contador++;
                }

                //EXTRAER ZIP
                if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("0")) {
                    transactionResponse = generateResponseRest(documentWRP, responseDTO);
                    byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                            transaction.getDOCCodigo(), documentWRP, transaction);
                    transactionResponse.setPdf(pdf);
                    extractor.AgregarAnexos(transaction, null, pdf, null, VariablesGlobales.UsoPdfSinRespuesta, Sociedad, connection);

                    //AGREGAR A SAP CAMPO
                    logger.info("CODIGO TIKCET GUIAS SUNAT - [0]" + responseDTO.getNumTicket());
                    transactionResponse.setTicketRest(responseDTO.getNumTicket());
                } else if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("98")) {
                    logger.info("CODIGO TIKCET GUIAS SUNAT - [98]" + responseDTO.getNumTicket());
                    transactionResponse = generateResponseRest(documentWRP, responseDTO);
                    transactionResponse.setTicketRest(responseDTO.getNumTicket());
                } else if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("99")) {
                    transactionResponse = generateResponseRest(documentWRP, responseDTO);
                }
                byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
                transactionResponse.setXml(documentBytes);
                extractor.AgregarAnexos(transaction, documentBytes, null, null, Boolean.FALSE, Sociedad, connection);

            } else if (transaction.getFEErrCod().equalsIgnoreCase("C")) {

                if (transaction.getTransaccionGuiaRemision().getTicketRest() != null && !transaction.getTransaccionGuiaRemision().getTicketRest().isEmpty()) {
                    //CONSULT
                    ClientJwtSunat clientJwtSunat = new ClientJwtSunat();
                    BdsMaestras bdsMaestras = new BdsMaestras(tipoTransaccion, clientId, secretId, usuarioGuias, passwordGuias, scope);
                    ResponseDTO responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);

                    //ResponseDTO responseDTO = clientJwtSunat.declareSunat(documentName, documentPath.replace("xml", "zip"), responseDTOJWT.getAccess_token());

                    ResponseDTO responseDTO = clientJwtSunat.consult(transaction.getTransaccionGuiaRemision().getTicketRest(), responseDTOJWT.getAccess_token());
                    if (responseDTO.getStatusCode() == 401) {
                        responseDTOJWT = clientJwtSunat.getJwtSunat(bdsMaestras);
                        responseDTO = clientJwtSunat.consult(responseDTO.getNumTicket(), responseDTOJWT.getAccess_token());
                    }

                    if (responseDTO.getStatusCode() == 400 || responseDTO.getStatusCode() == 404) {
                        return generateResponseRest(documentWRP, responseDTO);
                    }

                    sendQR(transaction.getDOCCodigo(), documentWRP);

                    //responseDTO.setCodRespuesta("0");
                    if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("0")) {
                        transactionResponse = generateResponseRest(documentWRP, responseDTO);
                        byte[] pdf = processCDRResponseContigencia(null, signedDocument, fileHandler, configuration, documentName,
                                transaction.getDOCCodigo(), documentWRP, transaction);
                        transactionResponse.setPdf(pdf);
                        extractor.AgregarAnexos(transaction, null, pdf, null, VariablesGlobales.UsoPdfSinRespuesta, Sociedad, connection);

                        //AGREGAR A SAP CAMPO
                        transactionResponse.setTicketRest(responseDTO.getNumTicket());
                    } else if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("98")) {
                        transactionResponse = generateResponseRest(documentWRP, responseDTO);
                    } else if (responseDTO.getCodRespuesta() != null && responseDTO.getCodRespuesta().equals("99")) {
                        transactionResponse = generateResponseRest(documentWRP, responseDTO);
                    }
                    byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
                    transactionResponse.setXml(documentBytes);
                    extractor.AgregarAnexos(transaction, documentBytes, null, null, Boolean.FALSE, Sociedad, connection);
                }
            }
        } else {
            logger.error("transactionRemissionGuideDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionRemissionGuideDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionTransportGuideDocumentRest

    public void sendQR(String documentCode, UBLDocumentWRP wrp) throws PDFReportException {
        if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_INVOICE_CODE)) {
            //pdfBytes = pdfHandler.generateInvoicePDF(wrp);
            String barcodeValue = generateBarCodeInfoString(wrp.getTransaccion().getDocIdentidadNro(), wrp.getTransaccion().getDOCCodigo(), wrp.getTransaccion().getDOCSerie(), wrp.getTransaccion().getDOCNumero(), wrp.getInvoiceType().getTaxTotal(), wrp.getInvoiceType().getIssueDate().toString(), wrp.getTransaccion().getDOCMontoTotal().toString(), wrp.getTransaccion().getSNDocIdentidadTipo(), wrp.getTransaccion().getSNDocIdentidadNro(), wrp.getInvoiceType().getUBLExtensions());

//            String barcodeValue = generateBarCodeInfoString(invoiceType.getInvoiceType().getID().getValue(), invoiceType.getInvoiceType().getInvoiceTypeCode().getValue(),invoiceObj.getIssueDate(), invoiceType.getInvoiceType().getLegalMonetaryTotal().getPayableAmount().getValue(), invoiceType.getInvoiceType().getTaxTotal(), invoiceType.getInvoiceType().getAccountingSupplierParty(), invoiceType.getInvoiceType().getAccountingCustomerParty(),invoiceType.getInvoiceType().getUBLExtensions());
            if (logger.isInfoEnabled()) {
                logger.info("generateInvoicePDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }
            //invoiceObj.setBarcodeValue(barcodeValue);

            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "01" + File.separator + wrp.getInvoiceType().getID().getValue() + ".png";
            File f = new File(Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "01");
            if (!f.exists()) {
                f.mkdirs();
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);
        } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
            String barcodeValue = generateBarCodeInfoString(wrp.getTransaccion().getDocIdentidadNro(), wrp.getTransaccion().getDOCCodigo(), wrp.getTransaccion().getDOCSerie(), wrp.getTransaccion().getDOCNumero(), wrp.getBoletaType().getTaxTotal(), wrp.getBoletaType().getIssueDate().toString(), wrp.getTransaccion().getDOCMontoTotal().toString(), wrp.getTransaccion().getSNDocIdentidadTipo(), wrp.getTransaccion().getSNDocIdentidadNro(), wrp.getBoletaType().getUBLExtensions());

//            String barcodeValue = generateBarCodeInfoString(invoiceType.getInvoiceType().getID().getValue(), invoiceType.getInvoiceType().getInvoiceTypeCode().getValue(),invoiceObj.getIssueDate(), invoiceType.getInvoiceType().getLegalMonetaryTotal().getPayableAmount().getValue(), invoiceType.getInvoiceType().getTaxTotal(), invoiceType.getInvoiceType().getAccountingSupplierParty(), invoiceType.getInvoiceType().getAccountingCustomerParty(),invoiceType.getInvoiceType().getUBLExtensions());
            if (logger.isInfoEnabled()) {
                logger.info("generateBoletaPDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }
            //invoiceObj.setBarcodeValue(barcodeValue);


            String rutaPath = Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "03" + File.separator + wrp.getBoletaType().getID().getValue() + ".png";
            File f = new File(Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "03");
            if (!f.exists()) {
                f.mkdirs();
            }

            InputStream inputStream = generateQRCode(barcodeValue, rutaPath);
            //pdfBytes = pdfHandler.generateBoletaPDF(wrp);
        } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) {


            String barcodeValue = generateBarCodeInfoString(wrp.getTransaccion().getDocIdentidadNro(), wrp.getTransaccion().getDOCCodigo(), wrp.getTransaccion().getDOCSerie(), wrp.getTransaccion().getDOCNumero(), wrp.getCreditNoteType().getTaxTotal(), wrp.getCreditNoteType().getIssueDate().toString(), wrp.getTransaccion().getDOCMontoTotal().toString(), wrp.getTransaccion().getSNDocIdentidadTipo(), wrp.getTransaccion().getSNDocIdentidadNro(), wrp.getCreditNoteType().getUBLExtensions());

//            String barcodeValue = generateBarCodeInfoString(invoiceType.getInvoiceType().getID().getValue(), invoiceType.getInvoiceType().getInvoiceTypeCode().getValue(),invoiceObj.getIssueDate(), invoiceType.getInvoiceType().getLegalMonetaryTotal().getPayableAmount().getValue(), invoiceType.getInvoiceType().getTaxTotal(), invoiceType.getInvoiceType().getAccountingSupplierParty(), invoiceType.getInvoiceType().getAccountingCustomerParty(),invoiceType.getInvoiceType().getUBLExtensions());
            if (logger.isInfoEnabled()) {
                logger.info("generateCreditNotePDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }
            //invoiceObj.setBarcodeValue(barcodeValue);

            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "07" + File.separator + wrp.getCreditNoteType().getID().getValue() + ".png";
            File f = new File(Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "07");
            if (!f.exists()) {
                f.mkdirs();
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);

            //pdfBytes = pdfHandler.generateCreditNotePDF(wrp, transaccionTotales);
        } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE)) {

            String barcodeValue = generateBarCodeInfoString(wrp.getTransaccion().getDocIdentidadNro(), wrp.getTransaccion().getDOCCodigo(), wrp.getTransaccion().getDOCSerie(), wrp.getTransaccion().getDOCNumero(), wrp.getDebitNoteType().getTaxTotal(), wrp.getDebitNoteType().getIssueDate().toString(), wrp.getTransaccion().getDOCMontoTotal().toString(), wrp.getTransaccion().getSNDocIdentidadTipo(), wrp.getTransaccion().getSNDocIdentidadNro(), wrp.getDebitNoteType().getUBLExtensions());
            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "08" + File.separator + wrp.getDebitNoteType().getID().getValue() + ".png";
            File f = new File(Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "08");
            if (!f.exists()) {
                f.mkdirs();
            }

            if (logger.isInfoEnabled()) {
                logger.debug("generateDebitNotePDF() [" + this.docUUID + "] rutaPath: \n" + rutaPath);
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);
            ///pdfBytes = pdfHandler.generateDebitNotePDF(wrp, transaccionTotales);
        } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_PERCEPTION_CODE)) {

            String barcodeValue = generateBarCodeInfoString(wrp.getTransaccion().getDocIdentidadNro(), wrp.getTransaccion().getDOCCodigo(),
                    wrp.getTransaccion().getDOCSerie(), wrp.getTransaccion().getDOCNumero(),
                    null, wrp.getPerceptionType().getIssueDate().toString(), wrp.getTransaccion().getDOCMontoTotal().toString(),
                    wrp.getTransaccion().getSNDocIdentidadTipo(), wrp.getTransaccion().getSNDocIdentidadNro(), wrp.getPerceptionType().getUblExtensions());
            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "40" + File.separator + wrp.getPerceptionType().getId().getValue() + ".png";
            File f = new File(Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "40");
            if (!f.exists()) {
                f.mkdirs();
            }

            if (logger.isInfoEnabled()) {
                logger.debug("generateDebitNotePDF() [" + this.docUUID + "] rutaPath: \n" + rutaPath);
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);


            //pdfBytes = pdfHandler.generatePerceptionPDF(wrp);
        } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_RETENTION_CODE)) {

            String barcodeValue = generateBarcodeInfoV2(wrp.getRetentionType().getId().getValue(), IUBLConfig.DOC_RETENTION_CODE,
                    wrp.getRetentionType().getIssueDate().toString(), wrp.getRetentionType().getTotalInvoiceAmount().getValue(),
                    BigDecimal.ZERO, wrp.getRetentionType().getAgentParty(), wrp.getRetentionType().getReceiverParty(), wrp.getRetentionType().getUblExtensions());

            if (logger.isInfoEnabled()) {
                logger.info("generateInvoicePDF() [" + this.docUUID + "] BARCODE: \n" + barcodeValue);
            }

            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + wrp.getRetentionType().getId().getValue() + ".png";
            File f = new File(Directorio.ADJUNTOS + File.separator + "CodigoQR");
            if (!f.exists()) {
                f.mkdirs();
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);
            //pdfBytes = pdfHandler.generateRetentionPDF(wrp);
        } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE)) {
            String barcodeValue = generateBarCodeInfoString(wrp.getTransaccion().getDocIdentidadNro(), wrp.getTransaccion().getDOCCodigo(),
                    wrp.getTransaccion().getDOCSerie(), wrp.getTransaccion().getDOCNumero(),
                    null, wrp.getAdviceType().getIssueDate().toString(), wrp.getTransaccion().getDOCMontoTotal().toString(),
                    wrp.getTransaccion().getSNDocIdentidadTipo(), wrp.getTransaccion().getSNDocIdentidadNro(), wrp.getAdviceType().getUBLExtensions());
            InputStream inputStream;
            InputStream inputStreamPDF;
            String rutaPath = Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "09" + File.separator + wrp.getAdviceType().getID().getValue() + ".png";
            File f = new File(Directorio.ADJUNTOS + File.separator + "CodigoQR" + File.separator + "09");
            if (!f.exists()) {
                f.mkdirs();
            }

            if (logger.isInfoEnabled()) {
                logger.debug("generateDebitNotePDF() [" + this.docUUID + "] rutaPath: \n" + rutaPath);
            }

            inputStream = generateQRCode(barcodeValue, rutaPath);


            //pdfBytes = pdfHandler.generateDespatchAdvicePDF(wrp);
        }

    }

    public String generateBarcodeInfoV2(String identifier,
                                        String documentType, String issueDate, BigDecimal payableAmountVal,
                                        BigDecimal taxTotalList,
                                        PartyType accSupplierParty,
                                        PartyType accCustomerParty, UBLExtensionsType ublExtensions)
            throws PDFReportException {
        if (logger.isDebugEnabled()) {
            logger.debug("+generateBarcodeInfo()");
        }
        String barcodeValue = null;

        try {
            /* a.) Numero de RUC del emisor electronico */
            String senderRuc = accSupplierParty.getPartyIdentification().get(0).getID().getValue();

            /* b.) Tipo de comprobante de pago electronico */
            /* Parametro de entrada del metodo */

            /* c.) Numeracion conformada por serie y numero correlativo */
            String serie = identifier.substring(0, 4);
            String correlative = Integer.valueOf(identifier.substring(5))
                    .toString();

            /* d.) Sumatoria IGV, de ser el caso */
            String igvTax = null;
            BigDecimal igvTaxBigDecimal = BigDecimal.ZERO;

            igvTax = igvTaxBigDecimal.setScale(2, RoundingMode.HALF_UP)
                    .toString();

            /* e.) Importe total de la venta, cesion en uso o servicio prestado */
            String payableAmount = payableAmountVal.toString();

            /* f.) Fecha de emision */
            /* Parametro de entrada del metodo */

            /* g,) Tipo de documento del adquiriente o usuario */
            String receiverDocType = accCustomerParty.getPartyIdentification()
                    .get(0).getID().getSchemeID();

            /* h.) Numero de documento del adquiriente */
            String receiverDocNumber = accCustomerParty.getPartyIdentification()
                    .get(0).getID().getValue();

            /* i.) Valor resumen <ds:DigestValue> */
            String digestValue = getDigestValue(ublExtensions);
            // String digestValue=null;
            /* j.) Valor de la Firma digital <ds:SignatureValue> */
            String signatureValue = getSignatureValue(ublExtensions);

            /*
             * Armando el codigo de barras
             */
            barcodeValue = MessageFormat.format(
                    IPDFCreatorConfig.BARCODE_PATTERN, senderRuc, documentType,
                    serie, correlative, igvTax, payableAmount, issueDate,
                    receiverDocType, receiverDocNumber, digestValue,
                    signatureValue);
        } catch (PDFReportException e) {
            logger.error("generateBarcodeInfo() [" + this.docUUID + "] ERROR: "
                    + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateBarcodeInfo() [" + this.docUUID + "] ERROR: "
                    + IVenturaError.ERROR_418.getMessage());
            throw new PDFReportException(IVenturaError.ERROR_418);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-generateBarcodeInfo()");
        }
        return barcodeValue;
    }// generateBarcodeInfo}

    private String getSignatureValue(UBLExtensionsType ublExtensions)
            throws PDFReportException, Exception {
        String signatureValue = null;
        try {
            int lastIndex = ublExtensions.getUBLExtension().size() - 1;
            UBLExtensionType ublExtension = ublExtensions.getUBLExtension()
                    .get(lastIndex);

            NodeList nodeList = ublExtension.getExtensionContent().getAny()
                    .getElementsByTagName(IUBLConfig.UBL_SIGNATUREVALUE_TAG);
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeName()
                        .equalsIgnoreCase(IUBLConfig.UBL_SIGNATUREVALUE_TAG)) {
                    signatureValue = nodeList.item(i).getTextContent();
                    break;
                }
            }

            if (StringUtils.isBlank(signatureValue)) {
                throw new PDFReportException(IVenturaError.ERROR_424);
            }
        } catch (PDFReportException e) {
            throw e;
        } catch (Exception e) {
            logger.error("getSignatureValue() Exception -->" + e.getMessage());
            throw e;
        }
        return signatureValue;
    } // getSignatureValue

    private String getDigestValue(UBLExtensionsType ublExtensions)
            throws PDFReportException, Exception {
        String digestValue = null;
        try {
            int lastIndex = ublExtensions.getUBLExtension().size() - 1;
            UBLExtensionType ublExtension = ublExtensions.getUBLExtension()
                    .get(lastIndex);

            NodeList nodeList = ublExtension.getExtensionContent().getAny()
                    .getElementsByTagName(IUBLConfig.UBL_DIGESTVALUE_TAG);
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeName()
                        .equalsIgnoreCase(IUBLConfig.UBL_DIGESTVALUE_TAG)) {
                    digestValue = nodeList.item(i).getTextContent();
                    break;
                }
            }

            if (StringUtils.isBlank(digestValue)) {
                throw new PDFReportException(IVenturaError.ERROR_423);
            }
        } catch (PDFReportException e) {
            throw e;
        } catch (Exception e) {
            logger.error("getDigestValue() Exception -->" + e.getMessage());
            throw e;
        }
        return digestValue;
    } // getDigestValue

    public static InputStream generateQRCode(String qrCodeData, String filePath) {

        try {

            String charset = "utf-8"; // or "ISO-8859-1"
            Map hintMap = new HashMap();

            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
            createQRCode(qrCodeData, filePath, charset, hintMap, 200, 200);

            FileInputStream fis = new FileInputStream(filePath);
            InputStream is = fis;
            return is;

        } catch (WriterException | IOException ex) {
            java.util.logging.Logger.getLogger(PDFGenerateHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void createQRCode(String qrCodeData, String filePath, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
        MatrixToImageWriter.writeToFile(matrix, filePath.substring(filePath.lastIndexOf('.') + 1), new File(filePath));
    }

    public String generateBarCodeInfoString(String RUC_emisor_electronico, String documentType, String serie, String correlativo, List<TaxTotalType> taxTotalList, String issueDate, String Importe_total_venta, String Tipo_documento_adquiriente, String Numero_documento_adquiriente, UBLExtensionsType ublExtensions) throws PDFReportException {
        String barcodeValue = "";
        try {

            /***/
            String digestValue = getDigestValue(ublExtensions);
//            logger.debug("Digest Value" + digestValue);

            /**El elemento opcional KeyInfo contiene información sobre la llave que se necesita para validar la firma, como lo muestra*/
            String signatureValue = getSignatureValue(ublExtensions);
//            logger.debug("signatureValue" + signatureValue);
            String Sumatoria_IGV = getTaxTotalValueV21(taxTotalList).toString();
//            logger.debug("Sumatoria_IGV" + Sumatoria_IGV);
            barcodeValue = MessageFormat.format(IPDFCreatorConfig.BARCODE_PATTERN, RUC_emisor_electronico, documentType, serie, correlativo, Sumatoria_IGV, Importe_total_venta, issueDate, Tipo_documento_adquiriente, Numero_documento_adquiriente, digestValue);

        } catch (PDFReportException e) {
            logger.error("generateBarcodeInfo() [" + this.docUUID + "] ERROR: "
                    + e.getError().getId() + "-" + e.getError().getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("generateBarcodeInfo() [" + this.docUUID + "] ERROR: "
                    + IVenturaError.ERROR_418.getMessage());
            throw new PDFReportException(IVenturaError.ERROR_418);
        }

        return barcodeValue;
    }

    public TransaccionRespuesta generateResponseRest(UBLDocumentWRP documentWRP, ResponseDTO responseDTO) {

        Sunat sunatResponse = new Sunat();
        sunatResponse.setCodigo(0);
        sunatResponse.setId(documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero());

        TransaccionRespuesta transactionResponse = new TransaccionRespuesta();
        if (responseDTO.getStatusCode() == 400 || responseDTO.getStatusCode() == 401) {
            transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_RECHAZADO_REST);
            //transactionResponse.setCodigoWS(Integer.parseInt(responseDTO.getCodRespuesta()));
            transactionResponse.setMensaje(responseDTO.getResponseDTO400().getError() + " - " + responseDTO.getResponseDTO400().getError_description());
            transactionResponse.setUuid(this.docUUID);
            sunatResponse.setAceptado(false);
            transactionResponse.setSunat(sunatResponse);
        } else if (responseDTO.getStatusCode() == 404) {
            transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_RECHAZADO_REST);
            //transactionResponse.setCodigoWS(Integer.parseInt(responseDTO.getCodRespuesta()));
            transactionResponse.setMensaje("El número de ticket para consultar no existe");
            transactionResponse.setUuid(this.docUUID);
            sunatResponse.setAceptado(false);
            transactionResponse.setSunat(sunatResponse);
        } else {
            if (responseDTO.getCodRespuesta().equals("0")) {

                String rptBase64 = responseDTO.getArcCdr();
                byte[] bytesZip = Base64.decode(rptBase64);
                transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_APROBADO_REST);
                transactionResponse.setCodigoWS(Integer.parseInt(responseDTO.getCodRespuesta()));
                transactionResponse.setMensaje(responseDTO.getCodRespuesta() + " - " + "Documento aprobado");
                transactionResponse.setZip(bytesZip);
                transactionResponse.setUuid(this.docUUID);
                sunatResponse.setAceptado(true);
                transactionResponse.setSunat(sunatResponse);

            } else if (responseDTO.getCodRespuesta().equals("98")) {

                transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_EN_PROCESO_REST);
                transactionResponse.setCodigoWS(Integer.parseInt(responseDTO.getCodRespuesta()));
                transactionResponse.setMensaje(responseDTO.getCodRespuesta() + " - " + "Documento en proceso, volver a consultar.");
                transactionResponse.setUuid(this.docUUID);
                sunatResponse.setAceptado(false);
                transactionResponse.setSunat(sunatResponse);

            } else if (responseDTO.getCodRespuesta().equals("99")) {

                if (responseDTO.getIndCdrGenerado().equals("1")) { //con CDR
                    String rptBase64 = responseDTO.getArcCdr();
                    byte[] bytesZip = Base64.decode(rptBase64);
                    transactionResponse.setZip(bytesZip);
                }
                transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_RECHAZADO_REST);
                transactionResponse.setCodigoWS(Integer.parseInt(responseDTO.getCodRespuesta()));
                transactionResponse.setMensaje(responseDTO.getError().getNumError() + " - " + responseDTO.getError().getDesError());
                transactionResponse.setUuid(this.docUUID);
                sunatResponse.setAceptado(false);
                transactionResponse.setSunat(sunatResponse);

            }
        }
        //transactionResponse.setCodigoWS(Integer.parseInt(responseDTO.getCodRespuesta()));
        transactionResponse.setCodigoWS(responseDTO.getStatusCode());
        return transactionResponse;
    }





    protected BigDecimal getTaxTotalValueV21(List<TaxTotalType> taxTotalList) {

        if (taxTotalList != null) {
            for (int i = 0; i < taxTotalList.size(); i++) {
                for (int j = 0; j < taxTotalList.get(i).getTaxSubtotal().size(); j++) {
                    if (taxTotalList.get(i).getTaxSubtotal().get(j).getTaxCategory().getTaxScheme().getID().getValue().equalsIgnoreCase("1000")) {
                        return taxTotalList.get(i).getTaxAmount().getValue();
                    }
                }

            }
        }

        return BigDecimal.ZERO;
    }
    public TransaccionRespuesta transactionVoidedDocument(Transaccion transaction, String doctype, Configuracion configuration, Connection connection) throws ConfigurationException, UBLDocumentException, SignerDocumentException, SunatGenericException, Exception {
        if (transaction.getFEComentario().isEmpty()) {
            TransaccionRespuesta transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RSP_RAZON_BAJA);
            transactionResponse.setMensaje("Ingresar razón de anulación, y colocar APROBADO y volver a consultar");
            transactionResponse.setUuid(this.docUUID);
            return transactionResponse;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("+transactionVoidedDocument() [" + this.docUUID + "] Identifier: " + transaction.getANTICIPOId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Identificador
         * - RUC del emisor
         * - Fecha de emision
         */
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation2(transaction.getANTICIPOId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision());

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocument() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         * 		- Certificado nulo o vacio
         * 		- La contrase�a del certificado pueda abrir el certificado.
         */
        //CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto VoidedDocumentsType para la COMUNICACION DE BAJA */
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        VoidedDocumentsType voidedDocumentType = ublHandler.generateVoidedDocumentType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto VoidedDocumentsType de la COMUNICACION DE BAJA.");

        /*
         * Se genera el nombre del documento de tipo COMUNICACION DE BAJA
         */
        String documentName = DocumentNameHandler.getInstance().getVoidedDocumentName(transaction.getDocIdentidadNro(), transaction.getANTICIPOId());
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocument() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.VOIDED_DOCUMENT_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.VOIDED_DOCUMENT_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocument() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(voidedDocumentType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();

        //signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocument() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        if (null != zipDocument) {
            /*
             * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
             * al emisor electronico. Ademas, se guarda el objeto FileHandler para el tratamiento del
             * documento UBL que se encuentra en DISCO. clientName: production
             */
            WSConsumerNew wsConsumer = WSConsumerNew.newInstance(this.docUUID);

            //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
            wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);

            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendSummary.");
            //String ticket = wsConsumer.sendSummary(zipDocument, documentName);

            /**SOLUCION BAJA
             * Si viene vacío es porque es la primera vez que se va a generar ticket
             * caso contrario ya tiene un ticket asigando y solo usamos el ticket para la consulta
             * **/
            String ticket = "";
            //if(transaction.getTicket().isEmpty()) {
            ticket = wsConsumer.sendSummary(zipDocument, documentName);
            transaction.setTicket(ticket);
            //actualizar base datos intermedia
            //}else{
            //   ticket = transaction.getTicket();
            //   transaction.setTicket(ticket);
            //TransaccionJC.edit(transaction);

            //}

            if (logger.isInfoEnabled()) {
                logger.info("transactionVoidedDocument() [" + this.docUUID + "] El WS retorno el ticket: " + ticket);
            }

            /**Solución al caso en que se genera ticket correcto, pero response cae en NULL**/
            //if(transaction.getDOCId().isEmpty()) {
            TransaccionBL.ActualizarTique(transaction, ticket);
            //tambien actualiza SAP con el campo TICKET
            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RQT_BAJA_APROBADO);

            transactionResponse.setNroTique(ticket);
            transactionResponse.setIdentificador(transaction.getANTICIPOId());
            transactionResponse.setUuid(this.docUUID);
            /*}else {
                transactionResponse = new TransaccionRespuesta();
                transactionResponse.setCodigo(TransaccionRespuesta.RQT_BAJA_APROBADO);
                transactionResponse.setNroTique(transaction.getDOCId());
                transactionResponse.setIdentificador(transaction.getANTICIPOId());
                transactionResponse.setUuid(this.docUUID);
            }*/

        } else {
            logger.error("transactionVoidedDocument() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionVoidedDocument() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionVoidedDocument


    public int ocurrenciasPorCliente(String ruta, String idEmisor, String idCliente, String patron) {
        // C:\VSCPE\FE_Ventura\Transacciones\baja\XML\20510910517\20601897882 ejemplo directorio a buscar
        // 20510910517-RA-20211018-00001 ejemplo archivo a buscar
        String directorio = ruta + "\\" + "XML" + "\\" + idEmisor + "\\" + idCliente + "\\";
        String archivo = idEmisor + "-" + patron + "0000*.xml";
        File dir = new File(directorio);
        Collection archivos = FileUtils.listFiles(dir, new WildcardFileFilter(archivo), null);

        if (!archivos.isEmpty()) {
            return archivos.size() + 1;
        }

        return 1;

    }

    public TransaccionRespuesta transactionVoidedDocumentPerception(Transaccion transaction, String doctype, Configuracion configuration, Connection connection) throws ConfigurationException, UBLDocumentException,
            SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionVoidedDocumentPerception() [" + this.docUUID + "] Identifier: " + transaction.getANTICIPOId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Identificador
         * - RUC del emisor
         * - Fecha de emision
         */
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation2(transaction.getANTICIPOId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision());

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocumentPerception() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         * 		- Certificado nulo o vacio
         * 		- La contrase�a del certificado pueda abrir el certificado.
         */
        //CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto VoidedDocumentsType para la REVERSION DE PERCEPCION */
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        VoidedDocumentsType voidedDocumentType = ublHandler.generateReversionDocumentType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto VoidedDocumentsType de la REVERSION DE PERCEPCION.");

        /*
         * Se genera el nombre del documento de tipo REVERSION DE PERCEPCION
         */
        String documentName = DocumentNameHandler.getInstance().getVoidedCPEDocumentName(transaction.getDocIdentidadNro(), transaction.getANTICIPOId().replace("RA", "RR"));
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocumentPerception() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.REVERSION_DOCUMENT_PATH + File.separator + ISunatConnectorConfig.PERCEPTION_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.REVERSION_DOCUMENT_PATH + File.separator + ISunatConnectorConfig.PERCEPTION_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocumentPerception() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(voidedDocumentType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocumentPerception() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();

        //signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocumentPerception() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocumentPerception() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        if (null != zipDocument) {
            /*
             * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
             * al emisor electronico. Ademas, se guarda el objeto FileHandler para el tratamiento del
             * documento UBL que se encuentra en DISCO. clientName: production
             */
            WSConsumerCPR wsConsumer = WSConsumerCPR.newInstance(this.docUUID);

            //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
            wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);

            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendSummary.");
            String ticket = wsConsumer.sendSummary(zipDocument, documentName);

            if (logger.isInfoEnabled()) {
                logger.info("transactionVoidedDocumentPerception() [" + this.docUUID + "] El WS retorno el ticket: " + ticket);
            }
            TransaccionBL.ActualizarTique(transaction, ticket);
            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RQT_BAJA_APROBADO);
            transactionResponse.setNroTique(ticket);
            transactionResponse.setIdentificador(transaction.getANTICIPOId());
            transactionResponse.setUuid(this.docUUID);
        } else {
            logger.error("transactionVoidedDocumentPerception() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionVoidedDocumentPerception() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionVoidedDocumentPerception

    public TransaccionRespuesta transactionVoidedDocumentRetention(Transaccion transaction, String doctype, Configuracion configuration, Connection connection) throws ConfigurationException, UBLDocumentException,
            SignerDocumentException, SunatGenericException, Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+transactionVoidedDocumentRetention() [" + this.docUUID + "] Identifier: " + transaction.getANTICIPOId() + " DocIdentidad_Nro: " + transaction.getDocIdentidadNro());
        }
        TransaccionRespuesta transactionResponse = null;

        /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
        //Configuracion configuration = ApplicationConfiguration.getInstance().getConfiguration();

        /* Generando el nombre del firmante */
        String signerName = ISignerConfig.SIGNER_PREFIX + transaction.getDocIdentidadNro();

        /*
         * Validando informacion basica
         * - Identificador
         * - RUC del emisor
         * - Fecha de emision
         */
        ValidationHandler validationHandler = ValidationHandler.newInstance(this.docUUID);
        validationHandler.checkBasicInformation2(transaction.getANTICIPOId(), transaction.getDocIdentidadNro(), transaction.getDOCFechaEmision());

//        /*
//         * Extrayendo el certificado de la ruta en DISCO
//         */
//        byte[] certificate = CertificateUtils.getCertificateInBytes(configuration.getCertificadoDigital().getRutaCertificado());
//
//        boolean encrypted = Boolean.parseBoolean(configuration.getCertificadoDigital().getPasswordCertificado().getEncriptado());
//        String certPassword = null;
//        if (encrypted) {
//            /* Desencriptar la clave del certificado */
//            certPassword = Criptor.Desencriptar(configuration.getCertificadoDigital().getPasswordCertificado().getValue());
//        } else {
//            certPassword = configuration.getCertificadoDigital().getPasswordCertificado().getValue();
//        }
        //UTILIZANDO HASH MAP DE ENTIDADES
        String idSociedad = transaction.getKeySociedad();
        ListaSociedades sociedad = VariablesGlobales.MapSociedades.get(idSociedad);

        byte[] certificado = sociedad.getRutaCD();
        String certiPassword = sociedad.getPasswordCD();
        String usuarioSec = sociedad.getUsuarioSec();
        String passwordSec = sociedad.getPasswordSec();

        String ksProvider = configuration.getCertificadoDigital().getProveedorKeystore();
        String ksType = configuration.getCertificadoDigital().getTipoKeystore();
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocumentRetention() [" + this.docUUID + "] Certificado en bytes: " + certificado);
        }

        /*
         * Validando el Certificado Digital
         * Se valida:
         * 		- Certificado nulo o vacio
         * 		- La contrase�a del certificado pueda abrir el certificado.
         */
        //CertificateUtils.checkDigitalCertificateV2(certificate, certPassword, ksProvider, ksType);
        CertificateUtils.checkDigitalCertificateV2(certificado, certiPassword, ksProvider, ksType);

        /* Generando el objeto VoidedDocumentsType para la COMUNICACION DE BAJA */
        UBLDocumentHandler ublHandler = UBLDocumentHandler.newInstance(this.docUUID);
        VoidedDocumentsType voidedDocumentType = ublHandler.generateReversionDocumentType(transaction, signerName);
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el objeto VoidedDocumentsType de la COMUNICACION DE BAJA.");

        /*
         * Se genera el nombre del documento de tipo COMUNICACION DE BAJA
         */
        String documentName = DocumentNameHandler.getInstance().getVoidedCPEDocumentName(transaction.getDocIdentidadNro(), transaction.getANTICIPOId().replace("RA", "RR"));
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocumentRetention() [" + this.docUUID + "] El nombre del documento: " + documentName);
        }

        FileHandler fileHandler = FileHandler.newInstance(this.docUUID);

        /*
         * Setear la ruta del directorio
         */
        String attachmentPath = null;
        if (Boolean.parseBoolean(configuration.getDirectorio().getAdjuntos().getEncriptado())) {
            /* Desencriptar la ruta del directorio */
            attachmentPath = Criptor.Desencriptar(configuration.getDirectorio().getAdjuntos().getValue()) + File.separator + ISunatConnectorConfig.REVERSION_DOCUMENT_PATH + File.separator + ISunatConnectorConfig.RETENTION_PATH;
        } else {
            attachmentPath = configuration.getDirectorio().getAdjuntos().getValue() + File.separator + ISunatConnectorConfig.REVERSION_DOCUMENT_PATH + File.separator + ISunatConnectorConfig.RETENTION_PATH;
        }
        fileHandler.setBaseDirectory(attachmentPath);
        if (logger.isDebugEnabled()) {
            logger.debug("transactionVoidedDocumentRetention() [" + this.docUUID + "] Ruta para los archivos adjuntos: " + attachmentPath);
        }

        /*
         * Guardando el documento UBL en DISCO
         */
        String documentPath = fileHandler.storeDocumentInDisk(voidedDocumentType, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocumentRetention() [" + this.docUUID + "] El documento [" + documentName + "] fue guardado en DISCO en: " + documentPath);
        }

        SignerHandler signerHandler = SignerHandler.newInstance();

        //signerHandler.setConfiguration(certificate, certPassword, ksType, ksProvider, signerName);
        signerHandler.setConfiguration(certificado, certiPassword, ksType, ksProvider, signerName);

        File signedDocument = signerHandler.signDocument(documentPath, docUUID);
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocumentRetention() [" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());
        }
        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue firmado correctamente en: " + signedDocument.getAbsolutePath());

        DataHandler zipDocument = fileHandler.compressUBLDocument(signedDocument, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
        if (logger.isInfoEnabled()) {
            logger.info("transactionVoidedDocumentRetention() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP.");
        }

        if (null != zipDocument) {
            /*
             * Se crea un nuevo objeto WS Consumidor, agregandole las configuraciones correspondientes
             * al emisor electronico. Ademas, se guarda el objeto FileHandler para el tratamiento del
             * documento UBL que se encuentra en DISCO. clientName: production
             */
            WSConsumerCPR wsConsumer = WSConsumerCPR.newInstance(this.docUUID);

            //wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), configuration, fileHandler, doctype);
            wsConsumer.setConfiguration(transaction.getDocIdentidadNro(), usuarioSec, passwordSec, configuration, fileHandler, doctype);
            if (logger.isInfoEnabled()) {
                logger.info("transactionVoidedDocumentRetention() [" + this.docUUID + "] Se realizo las configuraciones del objeto WSConsumer.");
            }

            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Enviando WS sendSummary.");
            String ticket = wsConsumer.sendSummary(zipDocument, documentName);

            if (logger.isInfoEnabled()) {
                logger.info("transactionVoidedDocumentRetention() [" + this.docUUID + "] El WS retorno el ticket: " + ticket);
            }
            TransaccionBL.ActualizarTique(transaction, ticket);
            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RQT_BAJA_APROBADO);
            transactionResponse.setNroTique(ticket);
            transactionResponse.setIdentificador(transaction.getANTICIPOId());
            transactionResponse.setUuid(this.docUUID);
        } else {
            logger.error("transactionVoidedDocumentRetention() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_457.getMessage());
            throw new NullPointerException(IVenturaError.ERROR_457.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-transactionVoidedDocumentRetention() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionVoidedDocumentRetention

    //*********************************************************************************************
    //*************************************** OTROS METODOS ***************************************
    //*********************************************************************************************

    /**
     * Este metodo maneja la constancia de recepcion de SUNAT / OSE y otros
     * valores, para retornar un objeto TransaccionRespuesta.
     * <p>
     * NOTA: Este metodo no puede retornar ninguna excepcion porque de todas
     * maneras tenemos que retornar un objeto TransaccionRespuesta.
     *
     * @param cdrConstancy   La constancia de recepcion en bytes.
     * @param signedDocument La ruta del documento UBL firmado, en formato XML.
     * @param fileHandler    El manejador de archivos para poder guardar el PDF en
     *                       DISCO.
     * @param configuration  Configuracion del aplicativo
     * @param documentName   El nombre del documento de la transaccion.
     * @param documentCode   El tipo de documento de la transaccion.
     * @return Retorna un objeto TransaccionRespuesta que contiene la
     * informacion obtenida durante la transaccion.
     */
    private TransaccionRespuesta processCDRResponse(byte[] cdrConstancy, File signedDocument, FileHandler fileHandler, Configuracion configuration,
                                                    String documentName, String documentCode, UBLDocumentWRP documentWRP, Transaccion transaction) {
        if (logger.isDebugEnabled()) {
            logger.debug("+processCDRResponse() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = null;

        try {
            if ((documentCode.equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE) || documentCode.equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE) || documentCode.equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) && documentWRP.getTransaccion().getDOCSerie().substring(0, 1).equalsIgnoreCase("B")) {
                Sunat sunatResponse = new Sunat();
                sunatResponse.setCodigo(0);
                sunatResponse.setMensaje("El Documento esta a la espera de respuesta del Resumen Diario");
                sunatResponse.setId(documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero());

                byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
                Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);

                byte[] pdfBytes = createPDFDocument(ublDocument, documentCode, configuration, documentWRP, transaction);

                if (null != pdfBytes && 0 < pdfBytes.length) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("processCDRResponse() [" + this.docUUID + "] Si existe PDF en bytes.");
                    }
                    /*
                     * Guardar el PDF en DISCO
                     */
                    boolean isPDFOk = fileHandler.storePDFDocumentInDisk(pdfBytes, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                    if (logger.isInfoEnabled()) {
                        logger.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                    }
                } else {
                    logger.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
                }

                transactionResponse = new TransaccionRespuesta();
                transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITDO_ESPERA);
                transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                transactionResponse.setIdentificador(sunatResponse.getId());
                transactionResponse.setMensaje(sunatResponse.getMensaje());
                transactionResponse.setSunat(sunatResponse);
                transactionResponse.setUuid(this.docUUID);
                transactionResponse.setXml(documentBytes);
                transactionResponse.setZip(cdrConstancy);
                transactionResponse.setPdf(pdfBytes);
            } else {
                Sunat sunatResponse = proccessResponse(cdrConstancy, transaction, configuration.getSunat().getIntegracionWS());
                byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
                Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);
                if (sunatResponse == null) {
                    transactionResponse = new TransaccionRespuesta();
                    transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION);
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("processCDRResponse() [" + this.docUUID + "]"
                                + "\n############################ RESPUESTA CDR ############################"
                                + "\n SUNAT_code: " + sunatResponse.getCodigo()
                                + "\n SUNAT_message: " + sunatResponse.getMensaje()
                                + "\n#######################################################################");
                    }
                    /*
                     * APROBADO:
                     * 		- Si la constancia CDR retorna un codigo igual a 0.
                     * 		- Si la constancia CDR retorna un codigo mayor o igual a 4000,
                     * 		  esta aprobado pero con observaciones.
                     *
                     * RECHAZADO:
                     * 		- Si la constancia CDR retorna un codigo entre el 2000 al 3999,
                     * 		  esta rechazado por Sunat.
                     */

                    if ((IVenturaError.ERROR_0.getId() == sunatResponse.getCodigo()) || (4000 <= sunatResponse.getCodigo())) {
                        if (logger.isInfoEnabled()) {
                            logger.info("processCDRResponse() [" + this.docUUID + "] El documento fue APROBADO por SUNAT.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El documento [{1}] fue APROBADO por SUNAT.", new Object[]{this.docUUID, documentName});

                        /*
                         * Procediendo a generar el PDF del documento
                         */
                        byte[] pdfBytes = createPDFDocument(ublDocument, documentCode, configuration, documentWRP, transaction);

                        if (null != pdfBytes && 0 < pdfBytes.length) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("processCDRResponse() [" + this.docUUID + "] Si existe PDF en bytes.");
                            }
                            /*
                             * Guardar el PDF en DISCO
                             */
                            boolean isPDFOk = fileHandler.storePDFDocumentInDisk(pdfBytes, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                            if (logger.isInfoEnabled()) {
                                logger.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                            }
                        } else {
                            logger.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
                        }
                        transactionResponse = new TransaccionRespuesta();
                        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_APROBADO);
                        transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                        transactionResponse.setIdentificador(sunatResponse.getId());
                        transactionResponse.setMensaje(sunatResponse.getMensaje());
                        transactionResponse.setSunat(sunatResponse);
                        transactionResponse.setUuid(this.docUUID);
                        transactionResponse.setXml(documentBytes);
                        transactionResponse.setZip(cdrConstancy);
                        transactionResponse.setPdf(pdfBytes);
                    } else {
                        if (logger.isInfoEnabled()) {
                            logger.info("processCDRResponse() [" + this.docUUID + "] El documento fue RECHAZADO por SUNAT.");
                        }
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] El documento [{1}] fue RECHAZADO por SUNAT.", new Object[]{this.docUUID, documentName});

                        transactionResponse = new TransaccionRespuesta();

                        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_RECHAZADO);
                        transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                        transactionResponse.setIdentificador(sunatResponse.getId());
                        transactionResponse.setMensaje(sunatResponse.getMensaje());
                        transactionResponse.setSunat(sunatResponse);
                        transactionResponse.setUuid(this.docUUID);
                        transactionResponse.setXml(documentBytes);
                        transactionResponse.setZip(cdrConstancy);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-processCDRResponse() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //processCDRResponse

    private TransaccionRespuesta processResponseSinCDR_1033(Transaccion transaction) {
        if (logger.isDebugEnabled()) {
            logger.debug("+processResponseSinCDR_1033() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = new TransaccionRespuesta();

        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION);
        transactionResponse.setCodigoWS(998);
        transactionResponse.setMensaje(IVenturaError.ERROR_1033.getMessage());
        transactionResponse.setEmitioError("Sunat");

        if (logger.isDebugEnabled()) {
            logger.debug("-processResponseSinCDR_1033() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //processResponseSinCDR_1033

    private TransaccionRespuesta processResponseSinCDRExcepcion(Transaccion transaction) {
        if (logger.isDebugEnabled()) {
            logger.debug("+processResponseSinCDRExcepcion() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = new TransaccionRespuesta();

        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_CDR_NULO);
        transactionResponse.setCodigoWS(998);
        transactionResponse.setMensaje(IVenturaError.ERROR_458.getMessage());
        transactionResponse.setEmitioError("Sunat");

        if (logger.isDebugEnabled()) {
            logger.debug("-processResponseSinCDRExcepcion() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //processResponseSinCDRExcepcion

    /**
     * Este metodo maneja la constancia de recepcion de Sunat para retornar un
     * objeto TransaccionRespuesta.
     * <p>
     * NOTA: Este metodo no puede retornar ninguna excepcion porque de todas
     * maneras tenemos que retornar un objeto TransaccionRespuesta.
     *
     * @param statusResponse El objeto StatusResponseConsult que es respuesta
     *                       del WS de Sunat.
     * @param documentName   El nombre del documento de la transaccion.
     * @param configuration
     * @return Retorna un objeto TransaccionRespuesta que contiene la
     * informacion obtenida durante la transaccion.
     */
    private TransaccionRespuesta processCDRResponseV2(WSConsumerConsult.StatusResponse statusResponse, String documentName, Configuracion configuration, String tipoTransaccion, Transaccion transaccion) {
        if (logger.isDebugEnabled()) {
            logger.debug("+processCDRResponseV2() [" + this.docUUID + "]");
        }
        logger.info("######################################### VERSION 3.0_START");
        logger.info("######################################### VERSION 3.0_END");
        TransaccionRespuesta transactionResponse = null;

        try {
            Sunat sunatResponse = proccessResponse(statusResponse.getContent(), transaccion, configuration.getSunat().getIntegracionWS());
            if (logger.isInfoEnabled()) {
                logger.info("processCDRResponseV2() [" + this.docUUID + "]"
                        + "\n############################ RESPUESTA CDR ############################"
                        + "\n STATUS_RESP_code: " + statusResponse.getStatusCode()
                        + "\n STATUS_RESP_message: " + statusResponse.getStatusMessage()
                        + "\n SUNAT_code: " + (null != sunatResponse ? sunatResponse.getCodigo() : "")
                        + "\n SUNAT_message: " + (null != sunatResponse ? sunatResponse.getCodigo() : "")
                        + "\n#######################################################################");
            }

            if (null != sunatResponse) {
                /*
                 * APROBADO:
                 * 		- Si la constancia CDR retorna un codigo igual a 0.
                 * 		- Si la constancia CDR retorna un codigo mayor o igual a 4000,
                 * 		  esta aprobado pero con observaciones.
                 *
                 * RECHAZADO:
                 * 		- Si la constancia CDR retorna un codigo entre el 2000 al 3999,
                 * 		  esta rechazado por Sunat.
                 */
                if ((IVenturaError.ERROR_0.getId() == sunatResponse.getCodigo()) || (4000 <= sunatResponse.getCodigo())) {
                    if (logger.isInfoEnabled()) {
                        logger.info("processCDRResponseV2() [" + this.docUUID + "] El documento fue APROBADO por SUNAT.");
                    }
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue APROBADO por SUNAT.");

                    transactionResponse = new TransaccionRespuesta();

                    transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_APROBADO);
                    transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                    transactionResponse.setIdentificador(sunatResponse.getId());
                    transactionResponse.setMensaje(sunatResponse.getMensaje());
                    transactionResponse.setSunat(sunatResponse);
                    transactionResponse.setUuid(this.docUUID);
                    transactionResponse.setZip(statusResponse.getContent());

                    //transactionResponse.setPdf(filePDF);
                    //transactionResponse.setXml(fileXML);
                } else {
                    if (logger.isInfoEnabled()) {
                        logger.info("processCDRResponseV2() [" + this.docUUID + "] El documento fue RECHAZADO por SUNAT.");
                    }
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] El documento [" + documentName + "] fue RECHAZADO por SUNAT.");

                    /*
                     * Documento RECHAZADO por Sunat.
                     */
                    transactionResponse = new TransaccionRespuesta();

                    transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_RECHAZADO);
                    transactionResponse.setCodigoWS(sunatResponse.getCodigo());
                    transactionResponse.setIdentificador(sunatResponse.getId());
                    transactionResponse.setMensaje(sunatResponse.getMensaje());
                    transactionResponse.setSunat(sunatResponse);
                    transactionResponse.setUuid(this.docUUID);
                    transactionResponse.setZip(statusResponse.getContent());
                    //transactionResponse.setPdf(filePDF);
                    //transactionResponse.setXml(fileXML);
                }
            } else {
                logger.error("processCDRResponseV2() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_458.getMessage());
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] ERROR: {1}", new Object[]{this.docUUID, IVenturaError.ERROR_458.getMessage()});

                transactionResponse = new TransaccionRespuesta();

                transactionResponse = processResponseSinCDR(transaccion);
            }
        } catch (Exception e) {
            logger.error("processCDRResponseV2() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("processCDRResponseV2() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-processCDRResponseV2() [" + this.docUUID + "]");
        }
        return transactionResponse;
    }

    public Sunat proccessResponse(byte[] cdrConstancy, Transaccion transaction, String sunatType) {
        try {
            String descripcionRespuesta = "";
            Optional<byte[]> unzipedResponse = unzipResponse(cdrConstancy);
            int codigoObservacion = 0;
            int codigoRespuesta = 0;
            String identificador = "OSE";
            if (unzipedResponse.isPresent()) {
                StringBuilder descripcion = new StringBuilder();
                JAXBContext jaxbContext = JAXBContext.newInstance(ApplicationResponseType.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                JAXBElement<ApplicationResponseType> jaxbElement = unmarshaller.unmarshal(new ByteArraySource(unzipedResponse.get()), ApplicationResponseType.class);
                ApplicationResponseType applicationResponse = jaxbElement.getValue();
                List<DocumentResponseType> documentResponse = applicationResponse.getDocumentResponse();
                List<TransaccionRespuesta.Observacion> observaciones = new ArrayList<>();
                for (DocumentResponseType documentResponseType : documentResponse) {
                    ResponseType response = documentResponseType.getResponse();
                    ResponseCodeType responseCode = response.getResponseCode();
                    codigoRespuesta = Optional.ofNullable(responseCode.getValue()).map(s -> s.isEmpty() ? null : s).map(Integer::parseInt).orElse(0);
                    List<DescriptionType> descriptions = response.getDescription();
                    for (DescriptionType description : descriptions) {
                        descripcion.append(description.getValue());
                    }
                    if (sunatType.equalsIgnoreCase("OSE") || sunatType.equalsIgnoreCase("CONOSE")) { //cambio aqui NUMA
                        identificador = documentResponseType.getDocumentReference().getID().getValue();
                    } else {
                        identificador = documentResponseType.getResponse().getReferenceID().getValue();
                    }
                    List<StatusType> statusTypes = response.getStatus();
                    for (StatusType statusType : statusTypes) {
                        List<StatusReasonType> statusReason = statusType.getStatusReason();
                        String mensajes = statusReason.parallelStream().map(TextType::getValue).collect(Collectors.joining("\n"));
                        StatusReasonCodeType statusReasonCode = statusType.getStatusReasonCode();
                        codigoObservacion = Optional.ofNullable(statusReasonCode.getValue()).map(s -> s.isEmpty() ? null : s).map(Integer::parseInt).orElse(0);
                        TransaccionRespuesta.Observacion observacion = new TransaccionRespuesta.Observacion();
                        observacion.setCodObservacion(codigoObservacion);
                        observacion.setMsjObservacion(mensajes);
                        observaciones.add(observacion);
                    }
                }
                descripcionRespuesta = descripcion.toString();
                System.out.println(descripcionRespuesta);
                TransaccionRespuesta.Sunat sunatResponse = new TransaccionRespuesta.Sunat();
                sunatResponse.setListaObs(observaciones);
                sunatResponse.setId(identificador);
                sunatResponse.setCodigo(codigoRespuesta);
                sunatResponse.setMensaje(descripcionRespuesta);
                sunatResponse.setEmisor(transaction.getDocIdentidadNro());
                return sunatResponse;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Sunat();
    }

    public Optional<byte[]> unzipResponse(byte[] cdr) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(cdr);
        ZipInputStream zis = new ZipInputStream(bais);
        ZipEntry entry = zis.getNextEntry();
        byte[] xml = null;
        if (entry != null) { // valida dos veces lo mismo
            while (entry != null) {
                if (!entry.isDirectory()) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] bytesIn = new byte['?'];
                    int read;
                    while ((read = zis.read(bytesIn)) != -1) {
                        baos.write(bytesIn, 0, read);
                    }
                    baos.close();
                    xml = baos.toByteArray();
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
            zis.close();
            return Optional.ofNullable(xml);
        } else {
            zis.close();
            return Optional.empty();
        }
    }

    private byte[] processCDRResponseContigencia(byte[] cdrConstancy, File signedDocument, FileHandler fileHandler,
                                                 Configuracion configuration, String documentName, String documentCode, UBLDocumentWRP documentWRP, Transaccion transaccion) {
        byte[] pdfBytes = null;

        if (logger.isDebugEnabled()) {
            logger.debug("+processCDRResponse() [" + this.docUUID + "]");
        }

        try {

            Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);

            pdfBytes = createPDFDocument(ublDocument, documentCode, configuration, documentWRP, transaccion);

            if (null != pdfBytes && 0 < pdfBytes.length) {
                if (logger.isDebugEnabled()) {
                    logger.debug("processCDRResponse() [" + this.docUUID + "] Si existe PDF en bytes.");
                }
                /*
                 * Guardar el PDF en DISCO
                 */
                boolean isPDFOk = fileHandler.storePDFContigenciaDocumentInDisk(pdfBytes, documentName, transaccion.getSNDocIdentidadNro(), transaccion.getDocIdentidadNro());
                if (logger.isInfoEnabled()) {
                    logger.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                }
            } else {
                logger.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
            }

        } catch (Exception e) {
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-processCDRResponse() [" + this.docUUID + "]");
        }

        return pdfBytes;

    }

    private TransaccionRespuesta processCDRResponseDummy(byte[] cdrConstancy, File signedDocument, FileHandler fileHandler,
                                                         Configuracion configuration, String documentName, String documentCode, UBLDocumentWRP documentWRP, Transaccion transaction) {
        if (logger.isDebugEnabled()) {
            logger.debug("+processCDRResponse() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = null;
        String nameDoc = "";
        try {
            switch (documentCode) {
                case "01":
                    nameDoc = "La Factura";
                    break;
                case "03":
                    nameDoc = "La Boleta";
                    break;
                case "07":
                    nameDoc = "La Nota de Credito";
                    break;
                case "08":
                    nameDoc = "La Nota de Debito";
                    break;
                case "09":
                    nameDoc = "La Guia de Remisiin";
                    break;
                case "20":
                    nameDoc = "El Comprobante de Retencion";
                    break;
                case "40":
                    nameDoc = "El Comprobante de Perpcecion";
                    break;
            }

            String SerieCorrelativo = documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero();
            Sunat sunatResponse = new Sunat();
            sunatResponse.setCodigo(0);
            sunatResponse.setMensaje("[" + SerieCorrelativo + "](0)" + nameDoc + "numero" + SerieCorrelativo + " ,ha sido aceptada");
            sunatResponse.setId(documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero());
            byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
            Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);

            byte[] pdfBytes = createPDFDocument(ublDocument, documentCode, configuration, documentWRP, transaction);

            if (null != pdfBytes && 0 < pdfBytes.length) {
                if (logger.isDebugEnabled()) {
                    logger.debug("processCDRResponse() [" + this.docUUID + "] Si existe PDF en bytes.");
                }
                /*
                 * Guardar el PDF en DISCO
                 */
                boolean isPDFOk = fileHandler.storePDFDocumentInDisk(pdfBytes, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                if (logger.isInfoEnabled()) {
                    logger.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                }
            } else {
                logger.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
            }

            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_APROBADO);
            transactionResponse.setCodigoWS(sunatResponse.getCodigo());
            transactionResponse.setIdentificador(sunatResponse.getId());
            transactionResponse.setMensaje(sunatResponse.getMensaje());
            transactionResponse.setSunat(sunatResponse);
            transactionResponse.setUuid(this.docUUID);
            transactionResponse.setXml(documentBytes);
            transactionResponse.setZip(cdrConstancy);
            transactionResponse.setPdf(pdfBytes);

            if (logger.isInfoEnabled()) {
                logger.info("processCDRResponse() [" + this.docUUID + "]"
                        + "\n############################ RESPUESTA CDR ############################"
                        + "\n SUNAT_code: " + sunatResponse.getCodigo()
                        + "\n SUNAT_message: " + sunatResponse.getMensaje()
                        + "\n#######################################################################");
            }

        } catch (Exception e) {
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-processCDRResponse() [" + this.docUUID + "]");
        }
        return transactionResponse;
    } //transactionInvoiceDocument

    private TransaccionRespuesta processCDRResponseDummy(File signedDocument, FileHandler fileHandler, Configuracion configuration, String documentName, String documentCode, UBLDocumentWRP documentWRP, Transaccion transaction) {
        if (logger.isDebugEnabled()) {
            logger.debug("+processCDRResponse() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = null;
        String nameDoc = "";
        try {
            switch (documentCode) {
                case "01":
                    nameDoc = "La Factura";
                    break;
                case "03":
                    nameDoc = "La Boleta";
                    break;
                case "07":
                    nameDoc = "La Nota de Credito";
                    break;
                case "08":
                    nameDoc = "La Nota de Debito";
                    break;
                case "09":
                    nameDoc = "La Guia de Remisiin";
                    break;
                case "20":
                    nameDoc = "El Comprobante de Retencion";
                    break;
                case "40":
                    nameDoc = "El Comprobante de Perpcecion";
                    break;
            }

            String SerieCorrelativo = documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero();
            Sunat sunatResponse = new Sunat();
            sunatResponse.setCodigo(0);
            sunatResponse.setMensaje("[" + SerieCorrelativo + "](0)" + nameDoc + "numero" + SerieCorrelativo + " ,ha sido aceptada");
            sunatResponse.setId(documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero());
            byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
            Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);

            byte[] pdfBytes = createPDFDocument(ublDocument, documentCode, configuration, documentWRP, transaction);

            if (null != pdfBytes && 0 < pdfBytes.length) {
                if (logger.isDebugEnabled()) {
                    logger.debug("processCDRResponse() [" + this.docUUID + "] Si existe PDF en bytes.");
                }
                /*
                 * Guardar el PDF en DISCO
                 */
                boolean isPDFOk = fileHandler.storePDFDocumentInDisk(pdfBytes, documentName, transaction.getSNDocIdentidadNro(), transaction.getDocIdentidadNro());
                if (logger.isInfoEnabled()) {
                    logger.info("processCDRResponse() [" + this.docUUID + "] El documento PDF fue almacenado en DICO: " + isPDFOk);
                }
            } else {
                logger.error("processCDRResponse() [" + this.docUUID + "] " + IVenturaError.ERROR_461.getMessage());
            }

            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITDO_ESPERA);
            transactionResponse.setCodigoWS(sunatResponse.getCodigo());
            transactionResponse.setIdentificador(sunatResponse.getId());
            transactionResponse.setMensaje("El documento se encuentra en espera de resumen para realizar la baja.");
            transactionResponse.setSunat(sunatResponse);
            transactionResponse.setUuid(this.docUUID);
            transactionResponse.setXml(documentBytes);
            transactionResponse.setPdf(pdfBytes);

            if (logger.isInfoEnabled()) {
                logger.info("processCDRResponse() [" + this.docUUID + "]"
                        + "\n############################ RESPUESTA CDR ############################"
                        + "\n SUNAT_code: " + sunatResponse.getCodigo()
                        + "\n SUNAT_message: " + sunatResponse.getMensaje()
                        + "\n#######################################################################");
            }

        } catch (Exception e) {
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-processCDRResponse() [" + this.docUUID + "]");
        }
        return transactionResponse;
    }

    private TransaccionRespuesta processResponseSinCDR(Transaccion transaction) {
        if (logger.isDebugEnabled()) {
            logger.debug("+processResponseSinResponse() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = null;
        transactionResponse = new TransaccionRespuesta();
        transactionResponse.setCodigo(TransaccionRespuesta.RQT_EMITIDO_EXCEPTION);
        transactionResponse.setCodigoWS(998);
        transactionResponse.setMensaje(IVenturaError.ERROR_458.getMessage());
        transactionResponse.setEmitioError("Sunat");

        if (logger.isDebugEnabled()) {
            logger.debug("-processCDRResponse() [" + this.docUUID + "]");
        }
        return transactionResponse;
    }

    private TransaccionRespuesta processCDRResponseCDRSinPDF(byte[] cdrConstancy, File signedDocument, FileHandler fileHandler,
                                                             Configuracion configuration, String documentName, String documentCode, UBLDocumentWRP documentWRP) {
        if (logger.isDebugEnabled()) {
            logger.debug("+processCDRResponse() [" + this.docUUID + "]");
        }
        TransaccionRespuesta transactionResponse = null;
        String nameDoc = "";
        try {
            switch (documentCode) {
                case "01":
                    nameDoc = "La Factura";
                    break;
                case "03":
                    nameDoc = "La Boleta";
                    break;
                case "07":
                    nameDoc = "La Nota de Cr\u00e1dito";
                    break;
                case "08":
                    nameDoc = "La Nota de D\u00e1bito";
                    break;
                case "09":
                    nameDoc = "La Gu\u00eda de Remisi\u00f3n";
                    break;
                case "20":
                    nameDoc = "El Comprobante de Retenci\u00f3n";
                    break;
                case "40":
                    nameDoc = "El Comprobante de Perpceci\u00f3n";
                    break;
            }

            String SerieCorrelativo = documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero();
            Sunat sunatResponse = new Sunat();
            sunatResponse.setCodigo(0);
            sunatResponse.setMensaje("[" + SerieCorrelativo + "](0)" + nameDoc + "n�mero" + SerieCorrelativo + " ,ha sido aceptada");
            sunatResponse.setId(documentWRP.getTransaccion().getDOCSerie() + "-" + documentWRP.getTransaccion().getDOCNumero());
            byte[] documentBytes = fileHandler.convertFileToBytes(signedDocument);
            Object ublDocument = fileHandler.getSignedDocument(signedDocument, documentCode);

            transactionResponse = new TransaccionRespuesta();
            transactionResponse.setCodigo(TransaccionRespuesta.RSP_EMITIDO_APROBADO);
            transactionResponse.setCodigoWS(sunatResponse.getCodigo());
            transactionResponse.setIdentificador(sunatResponse.getId());
            transactionResponse.setMensaje(sunatResponse.getMensaje());
            transactionResponse.setSunat(sunatResponse);
            transactionResponse.setUuid(this.docUUID);
            transactionResponse.setXml(documentBytes);
            transactionResponse.setZip(cdrConstancy);

            if (logger.isInfoEnabled()) {
                logger.info("processCDRResponse() [" + this.docUUID + "]"
                        + "\n############################ RESPUESTA CDR ############################"
                        + "\n SUNAT_code: " + sunatResponse.getCodigo()
                        + "\n SUNAT_message: " + sunatResponse.getMensaje()
                        + "\n#######################################################################");
            }

        } catch (Exception e) {
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            logger.error("processCDRResponse() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-processCDRResponse() [" + this.docUUID + "]");
        }
        return transactionResponse;
    }

    private String formatIssueDate(XMLGregorianCalendar xmlGregorianCal)
            throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+formatIssueDate() [" + this.docUUID + "]");
        }
        Date inputDate = xmlGregorianCal.toGregorianCalendar().getTime();

        Locale locale = new Locale(IPDFCreatorConfig.LOCALE_ES,
                IPDFCreatorConfig.LOCALE_PE);

        SimpleDateFormat sdf = new SimpleDateFormat(
                IPDFCreatorConfig.PATTERN_DATE, locale);
        String issueDate = sdf.format(inputDate);

        if (logger.isDebugEnabled()) {
            logger.debug("-formatIssueDate() [" + this.docUUID + "]");
        }
        return issueDate;
    }

    private String cargarAnalizarReglasFormatoDOC(Transaccion transaction, String documentCode) {
        List<ReglasIdiomasDoc> listReglasIdiomasDoc = ReglasIdiomaDocBL.listarReglas();
        for (ReglasIdiomasDoc reglaIdiomasDoc : listReglasIdiomasDoc) {
            String codeDOC = reglaIdiomasDoc.getFETipoDOC();
            if (codeDOC.equalsIgnoreCase(documentCode)) {
                Object campoFE = reglaIdiomasDoc.getFECampoDOC();
                Object operadorFE = reglaIdiomasDoc.getFEOperador();
                Object valorComparativo = reglaIdiomasDoc.getFEValorComparativo();
                Object documentSelect = reglaIdiomasDoc.getFEDOCFinal();
                boolean flagValidadorRegla = processObjectTransaction(transaction, campoFE, operadorFE, valorComparativo);
                if (flagValidadorRegla) {
                    return documentSelect.toString();
                }
            }
        }
        return "";
    }

    private boolean processObjectTransaction(Transaccion transaction, Object campoFE, Object operadorFE, Object valorComparativo) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            Method m;
            Object resultCampoFE = null;
            if (campoFE.toString().startsWith("U_")) {
                for (int i = 0; i < transaction.getTransaccionContractdocrefList().size(); i++) {
                    if (campoFE.equals(transaction.getTransaccionContractdocrefList().get(i).getUsuariocampos().getNombre())) {
                        resultCampoFE = transaction.getTransaccionContractdocrefList().get(i).getValor();
                    }
                }
            } else {
                m = transaction.getClass().getMethod("get" + campoFE);
                resultCampoFE = m.invoke(transaction);
            }
            Object result = null;
            if (resultCampoFE != null) {
                if (resultCampoFE instanceof String) {
                    if (operadorFE.toString().equals("!=")) {
                        result = !resultCampoFE.equals(valorComparativo);
                    } else if (operadorFE.toString().equals("==")) {
                        result = resultCampoFE.equals(valorComparativo);
                    }
                } else if ((resultCampoFE instanceof Integer) || (resultCampoFE instanceof Double) || (resultCampoFE instanceof BigDecimal)) {
                    String expression = resultCampoFE.toString() + operadorFE + valorComparativo;
                    result = engine.eval(expression);
                } else if (resultCampoFE instanceof Date) {
                    Date valorComparativoFormat = new Date(valorComparativo.toString());
                    if (operadorFE.toString().equals("<=")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) <= 0;
                    } else if (operadorFE.toString().equals("<")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) < 0;
                    } else if (operadorFE.toString().equals("==")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) == 0;
                    } else if (operadorFE.toString().equals(">=")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) >= 0;
                    } else if (operadorFE.toString().equals(">")) {
                        result = ((Date) resultCampoFE).compareTo(valorComparativoFormat) > 0;
                    } else if (operadorFE.toString().equals("!=")) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        String resultCampoFEFormat = dateFormat.format((Date) resultCampoFE);
                        String expression = resultCampoFEFormat + operadorFE + valorComparativo;
                        result = engine.eval(expression);
                    }
                } else {
                    System.out.println("Tipos de datos comparados no son correctos");
                }
            }
            if (result != null && result.equals(true)) {
                return true;
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ScriptException ex) {
            System.out.println("Error en campo '" + campoFE + "' : " + ex.getMessage());
            return false;
        }
        return false;
    }

    /**
     * Este metodo gestiona el tipo de documento impreso a generar, devolviendo
     * un arreglo de bytes.
     * <p>
     * NOTA: La trama de excepciones se ha determinado trabajarlas internamente,
     * porque si se retorna una excepcion implicaria perder la informacion del
     * CDR, el cual en este punto del flujo ya existe.
     *
     * @param ublDocument   Documento UBL firmado.
     * @param documentCode  El codigo del tipo de documento.
     * @param configuration Configuracion del aplicativo.
     * @return Retorna un arreglo de bytes que representan el documento PDF.
     */
    private byte[] createPDFDocument(Object ublDocument, String documentCode, Configuracion configuration, UBLDocumentWRP wrp, Transaccion transaction) {
        if (logger.isDebugEnabled()) {
            logger.debug("+createPDFDocument() [" + this.docUUID + "]");
        }
        byte[] pdfBytes = null;
        List<TransaccionTotales> transaccionTotales = new ArrayList<>(transaction.getTransaccionTotalesList());
        try {
            PDFGenerateHandler pdfHandler = PDFGenerateHandler.newInstance(this.docUUID);
            //UTILIZANDO HASH MAP DE ENTIDADES
            String idsociedad = transaction.getKeySociedad();
            ListaSociedades sociedades = VariablesGlobales.MapSociedades.get(idsociedad);

            String logoSociedad = null;

            logoSociedad = sociedades.getLogoSociedad();

            //configuration.getEmisorElectronico().getSenderLogo()
            String rutaDOCSelected = "";
            String rutaPaymentSelected = configuration.getPdf().getPaymentSubReportPath(); // Numa

            if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_INVOICE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_INVOICE_CODE);

                if (!rutaDOCSelected.isEmpty()) {
                    pdfHandler.setConfiguration(rutaDOCSelected, configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                } else {
                    pdfHandler.setConfiguration(configuration.getPdf().getInvoiceReportPath(), configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la FACTURA.");
                }
                pdfBytes = pdfHandler.generateInvoicePDF(wrp);
                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Se genero el PDF de la FACTURA.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genero el PDF de la FACTURA.", this.docUUID);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_BOLETA_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                } else {
                    pdfHandler.setConfiguration(configuration.getPdf().getBoletaReportPath(), configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la BOLETA.");
                }
                pdfBytes = pdfHandler.generateBoletaPDF(wrp);
                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Se genero el PDF de la BOLETA.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genero el PDF de la BOLETA.", this.docUUID);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_CREDIT_NOTE_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                } else {
                    pdfHandler.setConfiguration(configuration.getPdf().getCreditNoteReportPath(), configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la NOTA DE CREDITO.");
                }
                pdfBytes = pdfHandler.generateCreditNotePDF(wrp, transaccionTotales);
                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Se genero el PDF de la NOTA DE CREDITO.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se genero el PDF de la NOTA DE CREDITO.", this.docUUID);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_DEBIT_NOTE_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                } else {
                    pdfHandler.setConfiguration(configuration.getPdf().getDebitNoteReportPath(), configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la NOTA DE DEBITO.");
                }
                pdfBytes = pdfHandler.generateDebitNotePDF(wrp, transaccionTotales);
                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Se genero el PDF de la NOTA DE DEBITO.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el PDF de la NOTA DE DEBITO.");
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_PERCEPTION_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_PERCEPTION_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                } else {
                    pdfHandler.setConfiguration(configuration.getPdf().getPerceptionReportPath(), configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF del COMPROBANTE DE PERCEPCIón.");
                }
                pdfBytes = pdfHandler.generatePerceptionPDF(wrp);
                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Se genero el PDF del COMPROBANTE DE PERCEPCIón");
                }

                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el PDF del COMPROBANTE DE PERCEPCIón.");

            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_RETENTION_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_RETENTION_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                } else {
                    pdfHandler.setConfiguration(configuration.getPdf().getRetentionReportPath(), configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la NOTA DE DEBITO.");
                }
                pdfBytes = pdfHandler.generateRetentionPDF(wrp);
                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Se genero el PDF de COMPROBANTE DE RETENCIÓN.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el PDF de COMPROBANTE DE RETENCIÓN.");
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected,
                            logoSociedad, configuration.getEmisorElectronico().getRS());
                } else {
                    pdfHandler.setConfiguration(configuration.getPdf().getRemissionGuideReportPath(), configuration.getPdf().getLegendSubReportPath(),
                            rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la GUIA DE REMISION.");
                }
                pdfBytes = pdfHandler.generateDespatchAdvicePDF(wrp);
                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Se genero el PDF de GUIA DE REMISION.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el PDF de GUIA DE REMISION.");
            }else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_SENDER_CARRIER_GUIDE_CODE)) {
                /*
                 * Configurando el HANDLER creador de PDF's
                 */
                rutaDOCSelected = cargarAnalizarReglasFormatoDOC(transaction, IUBLConfig.DOC_SENDER_CARRIER_GUIDE_CODE);
                if (!rutaDOCSelected.equals("")) {
                    pdfHandler.setConfiguration(rutaDOCSelected, configuration.getPdf().getLegendSubReportPath(), rutaPaymentSelected,
                            logoSociedad, configuration.getEmisorElectronico().getRS());
                } else {
                    pdfHandler.setConfiguration(configuration.getPdf().getCarrierGuideReportPath(), configuration.getPdf().getLegendSubReportPath(),
                            rutaPaymentSelected, logoSociedad, configuration.getEmisorElectronico().getRS());
                }

                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Generando el PDF de la GUIA DE REMISION.");
                }
                pdfBytes = pdfHandler.generateDespatchAdvicePDF(wrp);
                if (logger.isInfoEnabled()) {
                    logger.info("createPDFDocument() [" + this.docUUID + "] Se genero el PDF de COMPROBANTE DE RETENCIÓN.");
                }
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + this.docUUID + "] Se genero el PDF de COMPROBANTE DE RETENCIÓN.");
            } else {
                logger.error("createPDFDocument() [" + this.docUUID + "] " + IVenturaError.ERROR_460.getMessage());
                throw new ConfigurationException(IVenturaError.ERROR_460.getMessage());
            }
        } catch (PDFReportException e) {
            logger.error("createPDFDocument() [" + this.docUUID + "] PDFReportException - ERROR: " + e.getError().getId() + "-" + e.getError().getMessage());
            logger.error("createPDFDocument() [" + this.docUUID + "] PDFReportException -->" + ExceptionUtils.getStackTrace(e));
        } catch (ConfigurationException e) {
            logger.error("createPDFDocument() [" + this.docUUID + "] ConfigurationException - ERROR: " + e.getMessage());
            logger.error("createPDFDocument() [" + this.docUUID + "] ConfigurationException -->" + ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            logger.error("createPDFDocument() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") ERROR: " + e.getMessage());
            logger.error("createPDFDocument() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-createPDFDocument() [" + this.docUUID + "]");
        }
        return pdfBytes;
    } //createPDFDocument}

} // FEProcessHandler
