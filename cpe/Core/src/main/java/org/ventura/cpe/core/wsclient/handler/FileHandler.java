package org.ventura.cpe.core.wsclient.handler;

import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.ventura.cpe.core.config.IUBLConfig;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.exception.VenturaExcepcion;
import org.ventura.cpe.core.wsclient.config.ISunatConnectorConfig;
import sunat.names.specification.ubl.peru.schema.xsd.perception_1.PerceptionType;
import sunat.names.specification.ubl.peru.schema.xsd.retention_1.RetentionType;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Esta clase HANDLER contiene metodos para transformar archivos y carpetas.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class FileHandler {

    private final Logger logger = Logger.getLogger(FileHandler.class);

    private String baseDirectory;

    private String xmlDirectory = "XML";

    private String cdrDirectory = "CDR";

    private String pdfDirectory = "PDF";

    private String docUUID;

    /**
     * Constructor privado para evitar instancias.
     *
     * @param docUUID UUID que identifica al documento de la transaccion.
     */
    private FileHandler(String docUUID) {
        this.docUUID = docUUID;
    } //FileHandler

    /**
     * Este metodo crea una nueva instancia de la clase FileHandler.
     *
     * @param docUUID UUID que identifica al documento de la transaccion.
     * @return
     */
    public static synchronized FileHandler newInstance(String docUUID) {
        return new FileHandler(docUUID);
    } //newInstance

    /**
     * Este metodo guarda el directorio base en el cual se almacenan los
     * documentos.
     *
     * @param directoryPath El directorio base en donde se almacenan los
     *                      documentos.
     */
    public void setBaseDirectory(String directoryPath) {
        this.baseDirectory = directoryPath;
        if (logger.isDebugEnabled()) {
            logger.debug("+-setBaseDirectory() [" + this.docUUID + "] baseDirectory: " + this.baseDirectory);
        }
    } //setBaseDirectory

    /**
     * Este metodo guarda el documento UBL en DISCO segun la ruta configurada.
     *
     * @param ublDocument  El documento UBL que se almacena.
     * @param documentName El nombre del documento UBL.
     * @param rucCliente
     * @return Retorna la ubicacion en donde fue guardado el documento.
     * @throws Exception
     */
    public String storeDocumentInDisk(Object ublDocument, String documentName, String rucCliente, String rucEmpresa) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+storeDocumentInDisk() [" + this.docUUID + "]");
        }
        String documentPath = null;
        try {
            String separator = File.separator;
            documentPath = this.baseDirectory + separator + this.xmlDirectory + separator + rucEmpresa + separator + rucCliente + separator + documentName + ISunatConnectorConfig.EE_XML;
            System.out.println();
            System.out.println();
            System.out.println(documentPath);
            System.out.println();
            System.out.println();
            if (logger.isDebugEnabled()) {
                logger.debug("storeDocumentInDisk() [" + this.docUUID + "] Se guardo el documento UBL en: " + documentPath);
            }
            File file = new File(this.baseDirectory + separator + this.xmlDirectory + separator + rucEmpresa + separator + rucCliente);
            if (!file.exists()) {
                file.mkdirs();
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(ublDocument.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, ISunatConnectorConfig.ENCODING_UTF);
            marshaller.marshal(ublDocument, new File(documentPath));
            if (logger.isDebugEnabled()) {
                logger.debug("storeDocumentInDisk() [" + this.docUUID + "] Se guardo el documento UBL en: " + documentPath);
            }
        } catch (JAXBException e) {
            e.printStackTrace();
            logger.error("storeDocumentInDisk() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_454.getMessage() + e.getMessage());
            throw new Exception(IVenturaError.ERROR_454.getMessage());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("-storeDocumentInDisk() [" + this.docUUID + "]");
        }
        return documentPath;
    } //storeDocumentInDisk

    /**
     * Este metodo comprime el documento UBL en formato ZIP.
     *
     * @param document     El objeto File que contiene la ruta del documento UBL.
     * @param documentName El nombre del documento UBL.
     * @return Retorna un objeto MAP que contiene el DataHandler y la ruta del
     * documento UBL en formato ZIP.
     * @throws IOException
     */
    public DataHandler compressUBLDocument(File document, String documentName, String rucCliente, String rucEmpresa) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("+compressUBLDocument() [" + this.docUUID + "]");
        }
        DataHandler zipDocument = null;
        try {
            String separator = File.separator;
            File zip = new File(this.baseDirectory + separator + this.xmlDirectory + separator + rucEmpresa + separator + rucCliente + separator + documentName + ISunatConnectorConfig.EE_ZIP);
            File file = new File(this.baseDirectory + separator + this.xmlDirectory + separator + rucEmpresa + separator + rucCliente);
            if (!file.exists()) {
                file.mkdir();
            }
            try (FileInputStream fis = new FileInputStream(document)) {
                FileOutputStream fos = new FileOutputStream(zip);
                try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                    byte[] array = new byte[10000];
                    int read = 0;
                    zos.putNextEntry(new ZipEntry(document.getName()));
                    while ((read = fis.read(array, 0, array.length)) != -1) {
                        zos.write(array, 0, read);
                    }
                    zos.closeEntry();
                }
            }
            /* Retornando el objeto DATAHANDLER */
            zipDocument = new DataHandler(new javax.activation.FileDataSource(zip));
            if (logger.isDebugEnabled()) {
                logger.debug("compressUBLDocument() [" + this.docUUID + "] El documento UBL fue convertido a formato ZIP correctamente.");
            }
        } catch (Exception e) {
            logger.error("compressUBLDocument() [" + this.docUUID + "] " + e.getMessage());
            throw new IOException(IVenturaError.ERROR_455.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-compressUBLDocument() [" + this.docUUID + "]");
        }
        return zipDocument;
    } //compressUBLDocument

    /**
     * Este metodo guarda la constancia de recepcion de Sunat (CDR) en DISCO.
     *
     * @param cdrResponse      Constancia de recepcion de Sunat (CDR).
     * @param cdrConstancyName El nombre de la constancia de recepcion.
     * @return Retorna un valor booleano que indica si la constancia fue
     * guardada correctamente o no.
     * @throws Exception
     */
    public boolean saveCDRConstancy(byte[] cdrResponse, String cdrConstancyName, String rucCliente, String rucEmpresa) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+saveCDRConstancy() [" + this.docUUID + "]");
        }
        boolean flag = false;
        try {
            String separator = File.separator;
            String cdrDirectory = this.baseDirectory + separator + this.cdrDirectory + separator + rucEmpresa + separator + rucCliente;
            File zipDocument = new File(cdrDirectory + separator + cdrConstancyName + ISunatConnectorConfig.EE_ZIP);
            File file = new File(cdrDirectory);
            if (!file.exists()) {
                file.mkdirs();
            }
            /*
             * Guardando el CDR de respuesta en el File
             * de salida.
             */
            saveBytesToFile(cdrResponse, zipDocument);
            if (logger.isDebugEnabled()) {
                logger.debug("saveCDRConstancy() [" + this.docUUID + "] Se guardo la constancia CDR en DISCO, RUTA: " + zipDocument.getAbsolutePath());
            }
            flag = true;
        } catch (Exception e) {
            logger.error("saveCDRConstancy() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw e;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-saveCDRConstancy() [" + this.docUUID + "]");
        }
        return flag;
    } //saveCDRConstancy

    /**
     * Este metodo guarda un archivo que se encuentra en bytes ha una ruta
     * especifica.
     *
     * @param bytes El archivo en bytes.
     * @param file  La ruta en donde se guardara el archivo.
     * @throws FileNotFoundException
     * @throws Exception
     */
    public void saveBytesToFile(byte[] bytes, File file) throws FileNotFoundException, Exception {
        try {
            Path path = Paths.get(file.toURI());
            Files.write(path, bytes);
        } catch (FileNotFoundException e) {
            logger.error("saveBytesToFile() [" + this.docUUID + "] FileNotFoundException - ERROR: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("El CDR obtenido por parte de SUNAT es NULO");
            if (logger.isDebugEnabled() || logger.isInfoEnabled()) {
                logger.error("saveBytesToFile() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            }
            throw e;
        }
    } //saveBytesToFile

    /**
     * Este metodo convierte un archivo, que se encuentra en una ruta
     * especifica, en bytes.
     *
     * @param signedDocument La ruta del archivo a convertir en bytes.
     * @return Retorna el archivo convertido en bytes.
     */
    public byte[] convertFileToBytes(File signedDocument) {
        if (logger.isDebugEnabled()) {
            logger.debug("+convertFileToBytes() signedDocument: " + signedDocument.getAbsolutePath());
        }
        try {
            Path path = Paths.get(signedDocument.toURI());
            byte[] documentBytes = Files.readAllBytes(path);
            return documentBytes;
        } catch (Exception e) {
            logger.error("convertFileToBytes() Exception(" + e.getClass().getName() + ") ERROR: " + e.getMessage());
            return null;
        }
    } //convertFileToBytes

    /**
     * Este metodo almacena un documento PDF en el DISCO DURO.
     *
     * @param pdfBytes     El document PDF en formato bytes.
     * @param documentName El nombre del documento PDF.
     * @return Retorna un valor booleano que indica si el documento fue
     * almacenado o no.
     */
    public boolean storePDFDocumentInDisk(byte[] pdfBytes, String documentName, String rucCliente, String rucEmpresa) {
        if (logger.isDebugEnabled()) {
            logger.debug("+storePDFDocumentInDisk() [" + this.docUUID + "]");
        }
        boolean flag = false;
        try {
            String separator = File.separator;
            File file = new File(this.baseDirectory + separator + this.pdfDirectory + separator + rucEmpresa + separator + rucCliente);
            if (!file.exists()) {
                file.mkdirs();
            }
            String filePath = this.baseDirectory + separator + this.pdfDirectory + separator + rucEmpresa + separator + rucCliente + separator + documentName + ISunatConnectorConfig.EE_PDF;
            File newFile = new File(filePath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            } else {
                boolean canWrite = newFile.canWrite();
                if (!canWrite) {
                    throw new VenturaExcepcion("No se puede guardar el documento PDF porque est\u00fa siendo usado por otro proceso. Cierre el documento y realice nuevamente el env\u00d3o");
                }
            }
            Path path = Paths.get(filePath);
            Files.write(path, pdfBytes);
            flag = true;
        } catch (IOException | VenturaExcepcion e) {
            logger.error("storePDFDocumentInDisk() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") ERROR: " + e.getMessage());
            logger.error("storePDFDocumentInDisk() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("+storePDFDocumentInDisk() [" + this.docUUID + "]");
        }
        return flag;
    } //storePDFDocumentInDisk

    /**
     * Este metodo almacena un documento PDF en el DISCO DURO.
     *
     * @param pdfBytes     El document PDF en formato bytes.
     * @param documentName El nombre del documento PDF.
     * @return Retorna un valor booleano que indica si el documento fue
     * almacenado o no.
     */
    public boolean storePDFContigenciaDocumentInDisk(byte[] pdfBytes, String documentName, String rucCliente, String rucEmpresa) {
        if (logger.isDebugEnabled()) {
            logger.debug("+storePDFDocumentInDisk() [" + this.docUUID + "]");
        }
        boolean flag = false;
        try {
            String separator = File.separator;
            File file = new File(this.baseDirectory + separator + this.pdfDirectory + separator + rucEmpresa + separator + rucCliente);
            if (!file.exists()) {
                file.mkdirs();
            }
            String filePath = file.getAbsolutePath() + separator + documentName + "_Borrador" + ISunatConnectorConfig.EE_PDF;
            File newFile = new File(filePath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            } else {
                boolean canWrite = newFile.canWrite();
                if (!canWrite) {
                    throw new VenturaExcepcion("No se puede guardar el documento PDF porque est\u00fa siendo usado por otro proceso. Cierre el documento y realice nuevamente el env\u00d3o");
                }
            }
            Path path = Paths.get(filePath);
            Files.write(path, pdfBytes);
            flag = true;
        } catch (Exception e) {
            logger.error("storePDFDocumentInDisk() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") ERROR: " + e.getMessage());
            logger.error("storePDFDocumentInDisk() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("+storePDFDocumentInDisk() [" + this.docUUID + "]");
        }
        return flag;
    } //storePDFDocumentInDisk

    public Object getSignedDocument(File signedDocument, String documentCode) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getSignedDocument()");
        }
        JAXBContext jaxbContext = null;
        Object object = null;

        try {
            if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_INVOICE_CODE) || documentCode.equalsIgnoreCase(IUBLConfig.DOC_BOLETA_CODE)) {
                jaxbContext = JAXBContext.newInstance(InvoiceType.class);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_CREDIT_NOTE_CODE)) {
                jaxbContext = JAXBContext.newInstance(CreditNoteType.class);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_DEBIT_NOTE_CODE)) {
                jaxbContext = JAXBContext.newInstance(DebitNoteType.class);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_RETENTION_CODE)) {
                jaxbContext = JAXBContext.newInstance(RetentionType.class);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_PERCEPTION_CODE)) {
                jaxbContext = JAXBContext.newInstance(PerceptionType.class);
            } else if (documentCode.equalsIgnoreCase(IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE)) {
                jaxbContext = JAXBContext.newInstance(DespatchAdviceType.class);
            }
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            object = (Object) unmarshaller.unmarshal(signedDocument);
        } catch (Exception e) {
            logger.error("getSignedDocument() ERROR: " + e.getMessage());
        }
        return object;
    } //getSignedDocument

} //FileHandler
