package org.ventura.cpe.core.erp.sapbo;

import com.sap.smb.sbo.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.services.AppFileService;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SAPBOAnexoService {

    private final AppFileService fileService;

    public boolean agregarAnexo(Transaccion trans, Path path, boolean borrador, ICompany sociedad) throws SBOCOMException {
        String tipoDocumento = trans.getDOCCodigo();
        if (tipoDocumento.equalsIgnoreCase("20")) {
            return agregarAnexoPago(trans, path, sociedad);
        } else {
            return agregarAnexoDocumento(trans, path, sociedad);
        }
    }

    public boolean agregarAnexo(Transaccion trans, Path path, boolean borrador) throws SBOCOMException {
        String tipoDocumento = trans.getDOCCodigo();
        ICompany sociedad = SAPBOService.getSociedades().get(trans.getKeySociedad());
        if (tipoDocumento.equalsIgnoreCase("20")) {
            return agregarAnexoPago(trans, path, sociedad);
        } else {
            return agregarAnexoDocumento(trans, path, sociedad);
        }
    }


    private boolean agregarAnexoPago(Transaccion trans, Path path, ICompany sociedad) {
        return false;
    }

    private int findDocumentPosition(IAttachments2 iAttachments, String filename) {
        int cantidad = iAttachments.getLines().getCount();
        for (int i = 0; i < cantidad; i++) {
            iAttachments.getLines().setCurrentLine(i);
            if (filename.equalsIgnoreCase(iAttachments.getLines().getFileName())) {
                return i;
            }
        }
        return -1;
    }

    private boolean agregarAnexoDocumento(Transaccion transaccion, Path documentPath, ICompany sociedad) throws SBOCOMException {
        String objType = transaccion.getFEObjectType();
        int docEntry = transaccion.getFEDocEntry();
        if (!sociedad.isInTransaction()) {
            sociedad.startTransaction();
        }
        IDocuments documents = SBOCOMUtil.getDocuments(sociedad, Integer.parseInt(objType), docEntry);
        int idAttachments = documents.getAttachmentEntry();
        IAttachments2 iAttachments2;
        boolean isAnexoEmpty = false, isOverride = false;
        if (idAttachments == 0) {
            iAttachments2 = SBOCOMUtil.newAttachments2(sociedad);
            isAnexoEmpty = true;
        } else {
            iAttachments2 = SBOCOMUtil.getAttachments2(sociedad, idAttachments);
        }
        String rutaDocumento = documentPath.toString();
        String baseName = FilenameUtils.getBaseName(rutaDocumento);
        String extension = FilenameUtils.getExtension(rutaDocumento);
        String parentPath = documentPath.getParent().toString();
        int count = iAttachments2.getLines().getCount();
        if (count == 1 && isAnexoEmpty) {
            iAttachments2.getLines().setCurrentLine(0);
        } else {
            int documentPosition = this.findDocumentPosition(iAttachments2, baseName);
            isOverride = documentPosition != -1;
            iAttachments2.getLines().setCurrentLine(isOverride ? documentPosition : count - 1);
        }
        if (!isAnexoEmpty && !isOverride) {
            iAttachments2.getLines().add();
        }
        iAttachments2.getLines().setFileExtension(extension);
        iAttachments2.getLines().setFileName(baseName);
        iAttachments2.getLines().setOverride(1);
        iAttachments2.getLines().setSourcePath(parentPath);
        if (isAnexoEmpty) {
            int add = iAttachments2.add();
            if (add != 0) {
                log.error(sociedad.getLastErrorDescription());
                if (sociedad.isInTransaction()) {
                    sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                }
                return false;
            }
            int newKey = Integer.parseInt(sociedad.getNewObjectKey());
            documents.setAttachmentEntry(newKey);
            int update = documents.update();
            if (update != 0) {
                log.error(sociedad.getLastErrorDescription());
                if (sociedad.isInTransaction()) {
                    sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                }
                return false;
            }
            if (sociedad.isInTransaction()) {
                sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
            }
            return true;
        } else {
            int update = iAttachments2.update();
            if (update != 0) {
                log.error(sociedad.getLastErrorDescription());
                if (sociedad.isInTransaction()) {
                    sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                }
                return false;
            } else {
                if (sociedad.isInTransaction()) {
                    sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                }
                return true;
            }
        }
    }

    private Map<String, String> agregarAnexoPago(Transaccion trans, byte[] xml, byte[] pdf, byte[] cdr, boolean borrador, ICompany sociedad) {
        Map<String, String> pathFiles = new HashMap<>();
        String feId = trans.getDOCSerie() + "_" + trans.getDOCNumero();
        String objType = trans.getFEObjectType(), tipoTransaccion = trans.getFETipoTrans();
        int docentry = trans.getFEDocEntry();
        if (!sociedad.isInTransaction()) {
            sociedad.startTransaction();
        }
        String nombPDF = "";
        String nombXML = "";
        String nombCDR = "";
        if (tipoTransaccion.compareTo("B") == 0) {
            nombPDF = feId + "_Baja";
            nombXML = feId + "_Baja";
            nombCDR = feId + "_Baja";
        } else {
            if (borrador) {
                nombPDF = feId + "_FormatoImpreso_BORRADOR";
                nombXML = feId + "_XmlFirmado_BORRADOR";
                nombCDR = feId + "_SUNAT_CDR_BORRADOR";
            }
        }
        String FPDF = "";
        String FXML = "";
        String FCDR = "";
        if (pdf != null) {
            Optional<Path> optional = fileService.storeDocumentPDFInDisk(pdf, trans, nombPDF + ".pdf");
            optional.ifPresent(path -> {
                pathFiles.put("rutaPDF", path.toString());
                log.info("PDF Guardado en: {}", path.toString());
            });
        }
        if (xml != null) {
            Optional<Path> optional = fileService.storeAppDocumentXmlInDisk(xml, trans, nombXML + ".xml");
            optional.ifPresent(path -> {
                pathFiles.put("rutaXML", path.toString());
                log.info("XML Guardado en: {}", path.toString());
            });
        }
        if (cdr != null) {
            Optional<Path> optional = fileService.storeAppDocumentCdrInDisk(cdr, trans, nombCDR + ".zip");
            optional.ifPresent(path -> {
                pathFiles.put("rutaCDR", path.toString());
                log.info("CDR Guardado en: {}", path.toString());
            });
        }
        try {
            IPayments docPayment = SBOCOMUtil.getPayments(sociedad, Integer.parseInt(objType), docentry);
            boolean searchDoc = docPayment.getByKey(docentry);
            if (searchDoc) {
                if (tipoTransaccion.compareTo("B") == 0) {
                    docPayment.getUserFields().getFields().item("U_VS_ANEXO_BAJA_CDR").setValue(FCDR);
                } else {
                    if (borrador) {
                        docPayment.getUserFields().getFields().item("U_VS_ANEXO_BORRADOR").setValue(FPDF);
                    } else {
                        docPayment.getUserFields().getFields().item("U_VS_ANEXO_XML").setValue(FXML);
                        docPayment.getUserFields().getFields().item("U_VS_ANEXO_CDR").setValue(FCDR);
                        File rutaPdf = new File(FPDF);
                        if (rutaPdf.canExecute() && rutaPdf.canRead() && rutaPdf.canWrite()) {
                            docPayment.getUserFields().getFields().item("U_VS_ANEXO_PDF").setValue(FPDF);
                        } else {
//                            anexoPdf = false;
                        }
                    }
                }
                int ret = docPayment.update();
                if (ret == 0) {
                    log.info("Se anexo correctamente los archivos");
                } else {
                    log.error("Se encontro un incidente al agregar los anexos {}", sociedad.getLastErrorDescription());
                }
            }
            if (sociedad.isInTransaction()) {
                sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
            }
        } catch (SBOCOMException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return pathFiles;
    }
}
