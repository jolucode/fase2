package org.ventura.cpe.core.services;

import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.creditnote_2.CreditNoteType;
import oasis.names.specification.ubl.schema.xsd.debitnote_2.DebitNoteType;
import oasis.names.specification.ubl.schema.xsd.despatchadvice_2.DespatchAdviceType;
import oasis.names.specification.ubl.schema.xsd.invoice_2.InvoiceType;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.config.AppConstant;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.config.IUBLConfig;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.cpe.core.exception.VenturaExcepcion;
import org.ventura.cpe.core.handler.DocumentNameHandler;
import org.ventura.cpe.core.util.AnexoDocumento;
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
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class AppFileService {

    private String baseDirectory;

    private String xmlDirectory = "XML";

    private String cdrDirectory = "CDR";

    private String pdfDirectory = "PDF";

    private String docUUID;

    private final AppProperties properties;

    @Autowired
    public AppFileService(AppProperties properties) {
        this.properties = properties;
        this.baseDirectory = properties.getDirectorio().getAdjuntos();
    }

    public AnexoDocumento storeAllDocumentsInDisk(byte[] pdf, byte[] xml, byte[] zip, Transaccion transaccion) {
        AnexoDocumento anexoDocumento = new AnexoDocumento(transaccion.getFEId(), transaccion.getKeySociedad(), transaccion.getFEDocEntry());
        String pdfFilename = DocumentNameHandler.getAnexoName(transaccion.getDOCSerie(), transaccion.getDOCNumero(), AppConstant.PDF_SUFFIX);
        Optional<Path> pdfPathOptional = storeDocumentPDFInDisk(pdf, transaccion, pdfFilename + AppConstant.EE_PDF);
        pdfPathOptional.ifPresent(path -> anexoDocumento.setPdfPath(path.toString()));
        String xmlFilename = DocumentNameHandler.getAnexoName(transaccion.getDOCSerie(), transaccion.getDOCNumero(), AppConstant.XML_SUFFIX);
        Optional<Path> xmlPathOptional = storeAppDocumentXmlInDisk(xml, transaccion, xmlFilename + AppConstant.EE_XML);
        xmlPathOptional.ifPresent(path -> anexoDocumento.setXmlPath(path.toString()));
        String cdrFilename = DocumentNameHandler.getAnexoName(transaccion.getDOCSerie(), transaccion.getDOCNumero(), AppConstant.CDR_SUFFIX);
        Optional<Path> cdrPathOptional = storeDocumentPDFInDisk(zip, transaccion, cdrFilename + AppConstant.EE_ZIP);
        cdrPathOptional.ifPresent(path -> anexoDocumento.setCdrPath(path.toString()));
        return anexoDocumento;
    }

    public Optional<Path> storeDocumentPDFInDisk(byte[] archivo, Transaccion transaccion, String filename) {
        Path path = getPathFromTransaccion(transaccion, filename);
        Path parentPath = path.getParent();
        try {
            Files.createDirectories(parentPath);
            boolean isDeleted = Files.deleteIfExists(path);
            return Optional.of(Files.write(path, archivo, isDeleted ? StandardOpenOption.CREATE_NEW : StandardOpenOption.CREATE));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Path> storeAppDocumentXmlInDisk(byte[] archivo, Transaccion transaccion, String filename) {
        Path path = getPathFromTransaccion(transaccion, filename);
        Path parentPath = path.getParent();
        try {
            Files.createDirectories(parentPath);
            boolean isDeleted = Files.deleteIfExists(path);
            return Optional.of(Files.write(path, archivo, isDeleted ? StandardOpenOption.CREATE_NEW : StandardOpenOption.CREATE));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Path> storeAppDocumentCdrInDisk(byte[] archivo, Transaccion transaccion, String filename) {
        Path path = getPathFromTransaccion(transaccion, filename);
        Path parentPath = path.getParent();
        try {
            Files.createDirectories(parentPath);
            boolean isDeleted = Files.deleteIfExists(path);
            return Optional.of(Files.write(path, archivo, isDeleted ? StandardOpenOption.CREATE_NEW : StandardOpenOption.CREATE));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Path> storeCdrResponseInDisk(byte[] response, String filename) {
        Path path = Paths.get(properties.getDirectorio().getAdjuntos(), filename);
        Path parentPath = path.getParent();
        try {
            Files.createDirectories(parentPath);
            boolean isDeleted = Files.deleteIfExists(path);
            return Optional.of(Files.write(path, response, isDeleted ? StandardOpenOption.CREATE_NEW : StandardOpenOption.CREATE));
        } catch (IOException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return Optional.empty();
    }

    public Path getPathFromTransaccion(Transaccion transaccion, String filename) {
        String appDocumentPath = properties.getDirectorio().getAdjuntos();
        Date docFechaEmision = transaccion.getDOCFechaEmision();
        LocalDate fecha = docFechaEmision.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String rucCliente = transaccion.getSNDocIdentidadNro(), rucEmpresa = transaccion.getDocIdentidadNro(), objectType = transaccion.getFEObjectType();
        return Paths.get(appDocumentPath, rucCliente, rucEmpresa, "" + fecha.getYear(), "" + fecha.getMonthValue(), "" + fecha.getDayOfMonth(), objectType, filename);
    }

    public String storeDocumentInDisk(Object ublDocument, String documentName, String rucCliente, String rucEmpresa) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("+storeDocumentInDisk() [" + this.docUUID + "]");
        }
        Path documentPath = Paths.get(this.baseDirectory, this.xmlDirectory, rucEmpresa, rucCliente, documentName + ISunatConnectorConfig.EE_XML);
        try {
            System.out.println();
            System.out.println(documentPath.toString());
            System.out.println();
            if (log.isDebugEnabled()) {
                log.debug("storeDocumentInDisk() [" + this.docUUID + "] Se guardo el documento UBL en: {}", documentPath);
            }
            Path path = Paths.get(this.baseDirectory, this.xmlDirectory, rucEmpresa, rucCliente);
            File file = path.toFile();
            if (!file.exists()) {
                file.mkdirs();
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(ublDocument.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, ISunatConnectorConfig.ENCODING_UTF);
            marshaller.marshal(ublDocument, documentPath.toFile());
            if (log.isDebugEnabled()) {
                log.debug("storeDocumentInDisk() [" + this.docUUID + "] Se guardo el documento UBL en: " + documentPath);
            }
        } catch (JAXBException e) {
            log.error("storeDocumentInDisk() [" + this.docUUID + "] ERROR: " + IVenturaError.ERROR_454.getMessage() + e.getMessage());
            throw new Exception(IVenturaError.ERROR_454.getMessage());
        }
        return documentPath.toString();
    }

    public DataHandler compressUBLDocument(File document, String documentName, String rucCliente, String rucEmpresa) throws IOException {
        if (log.isDebugEnabled()) {
            log.debug("+compressUBLDocument() [" + this.docUUID + "]");
        }
        DataHandler zipDocument;
        try {
            Path zipPath = Paths.get(this.baseDirectory, this.xmlDirectory, rucEmpresa, rucCliente, documentName + ISunatConnectorConfig.EE_ZIP);
            File zip = zipPath.toFile();
            Path filePath = Paths.get(this.baseDirectory, this.xmlDirectory, rucEmpresa, rucCliente);
            File file = filePath.toFile();
            if (!file.exists()) {
                file.mkdir();
            }
            try (FileInputStream fis = new FileInputStream(document)) {
                FileOutputStream fos = new FileOutputStream(zip);
                try (ZipOutputStream zos = new ZipOutputStream(fos)) {
                    byte[] array = new byte[10000];
                    int read;
                    zos.putNextEntry(new ZipEntry(document.getName()));
                    while ((read = fis.read(array, 0, array.length)) != -1) {
                        zos.write(array, 0, read);
                    }
                    zos.closeEntry();
                }
            }
            /* Retornando el objeto DATAHANDLER */
            zipDocument = new DataHandler(new javax.activation.FileDataSource(zip));
        } catch (Exception e) {
            log.error("compressUBLDocument() [" + this.docUUID + "] " + e.getMessage());
            throw new IOException(IVenturaError.ERROR_455.getMessage());
        }
        return zipDocument;
    }

    public boolean saveCDRConstancy(byte[] cdrResponse, String cdrConstancyName, String rucCliente, String rucEmpresa) throws Exception {
        boolean flag = false;
        try {
            String separator = File.separator;
            String cdrDirectory = this.baseDirectory + separator + this.cdrDirectory + separator + rucEmpresa + separator + rucCliente;
            File zipDocument = new File(cdrDirectory + separator + cdrConstancyName + ISunatConnectorConfig.EE_ZIP);
            File file = new File(cdrDirectory);
            if (!file.exists()) {
                file.mkdirs();
            }
            saveBytesToFile(cdrResponse, zipDocument);
            if (log.isDebugEnabled()) {
                log.debug("saveCDRConstancy() [" + this.docUUID + "] Se guardo la constancia CDR en DISCO, RUTA: " + zipDocument.getAbsolutePath());
            }
            flag = true;
        } catch (Exception e) {
            log.error("saveCDRConstancy() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") - ERROR: " + e.getMessage());
            throw e;
        }
        return flag;
    }

    public void saveBytesToFile(byte[] bytes, File file) throws Exception {
        try {
            Path path = Paths.get(file.toURI());
            Files.write(path, bytes);
        } catch (FileNotFoundException e) {
            log.error("saveBytesToFile() [" + this.docUUID + "] FileNotFoundException - ERROR: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("El CDR obtenido por parte de SUNAT es NULO");
            throw e;
        }
    }

    public byte[] convertFileToBytes(File signedDocument) {
        if (log.isDebugEnabled()) {
            log.debug("+convertFileToBytes() signedDocument: " + signedDocument.getAbsolutePath());
        }
        try {
            Path path = Paths.get(signedDocument.toURI());
            return Files.readAllBytes(path);
        } catch (Exception e) {
            log.error("convertFileToBytes() Exception(" + e.getClass().getName() + ") ERROR: " + e.getMessage());
            return null;
        }
    }

    public boolean storePDFDocumentInDisk(byte[] pdfBytes, String documentName, String rucCliente, String rucEmpresa) {
        if (log.isDebugEnabled()) {
            log.debug("+storePDFDocumentInDisk() [" + this.docUUID + "]");
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
            log.error("storePDFDocumentInDisk() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") ERROR: " + e.getMessage());
            log.error("storePDFDocumentInDisk() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        if (log.isDebugEnabled()) {
            log.debug("+storePDFDocumentInDisk() [" + this.docUUID + "]");
        }
        return flag;
    }

    public boolean storePDFContigenciaDocumentInDisk(byte[] pdfBytes, String documentName, String rucCliente, String rucEmpresa) {
        if (log.isDebugEnabled()) {
            log.debug("+storePDFDocumentInDisk() [" + this.docUUID + "]");
        }
        boolean flag = false;
        try {
            Path parentPath = Paths.get(this.baseDirectory, this.pdfDirectory, rucEmpresa, rucCliente);
            Files.createDirectories(parentPath);
            File file = parentPath.toFile();
            if (!file.exists()) {
                file.mkdirs();
            }
            Path filePath = parentPath.resolve(documentName + "_Borrador" + ISunatConnectorConfig.EE_PDF);
            File newFile = filePath.toFile();
            if (!newFile.exists()) {
                newFile.createNewFile();
            } else {
                boolean canWrite = newFile.canWrite();
                if (!canWrite) {
                    throw new VenturaExcepcion("No se puede guardar el documento PDF porque est\u00fa siendo usado por otro proceso. Cierre el documento y realice nuevamente el env\u00d3o");
                }
            }
            Files.write(filePath, pdfBytes);
            flag = true;
        } catch (Exception e) {
            log.error("storePDFDocumentInDisk() [" + this.docUUID + "] Exception(" + e.getClass().getName() + ") -->" + ExceptionUtils.getStackTrace(e));
        }
        return flag;
    }

    public Object getSignedDocument(File signedDocument, String documentCode) {
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
            assert jaxbContext != null;
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            object = unmarshaller.unmarshal(signedDocument);
        } catch (Exception e) {
            log.error("getSignedDocument() ERROR: " + e.getMessage());
        }
        return object;
    }

    public void setDocUUID(String docUUID) {
        this.docUUID = docUUID;
    }
}
