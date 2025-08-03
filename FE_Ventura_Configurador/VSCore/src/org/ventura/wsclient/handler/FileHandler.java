package org.ventura.wsclient.handler;

import org.apache.log4j.Logger;
import org.ventura.wsclient.config.ISunatConnectorConfig;
import ventura.soluciones.commons.exception.error.IVenturaError;

import javax.activation.DataHandler;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
     * documentos.
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
     * @param ublDocument El documento UBL que se almacena.
     * @param documentName El nombre del documento UBL.
     * @return Retorna la ubicacion en donde fue guardado el documento.
     * @throws Exception
     */
    public String storeDocumentInDisk(Object ublDocument, String documentName) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("+storeDocumentInDisk() [" + this.docUUID + "]");
        }
        String documentPath = null;

        try {
            documentPath = this.baseDirectory + File.separator + this.xmlDirectory
                    + File.separator + documentName + ISunatConnectorConfig.EE_XML;

            if (logger.isDebugEnabled()) {
                logger.debug("storeDocumentInDisk() [" + this.docUUID + "] Se guardo el documento UBL en: " + documentPath);
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
     * @param document El objeto File que contiene la ruta del documento UBL.
     * @param documentName El nombre del documento UBL.
     * @return Retorna un objeto MAP que contiene el DataHandler y la ruta del
     * documento UBL en formato ZIP.
     * @throws IOException
     */
    public DataHandler compressUBLDocument(File document, String documentName) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("+compressUBLDocument() [" + this.docUUID + "]");
        }
        DataHandler zipDocument = null;

        try {
            File zip = new File(this.baseDirectory + File.separator + this.xmlDirectory
                    + File.separator + documentName + ISunatConnectorConfig.EE_ZIP);

            FileInputStream fis = new FileInputStream(document);
            FileOutputStream fos = new FileOutputStream(zip);
            ZipOutputStream zos = new ZipOutputStream(fos);

            byte[] array = new byte[10000];
            int read = 0;

            zos.putNextEntry(new ZipEntry(document.getName()));

            while ((read = fis.read(array, 0, array.length)) != -1) {
                zos.write(array, 0, read);
            }

            zos.closeEntry();
            fis.close();
            zos.close();

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

} //FileHandler
