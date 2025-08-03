package org.ventura.cpe.sb1;

import com.sap.smb.sbo.api.*;
import org.apache.commons.io.FilenameUtils;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.loaderbl.Configuracion;
import org.ventura.cpe.log.LoggerTrans;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;

import static com.sap.smb.sbo.api.SBOCOMUtil.runRecordsetQuery;

public class AnexoContingenciaService implements Runnable {

    private final Transaccion transaccion;

    private final byte[] pdf;

    private final byte[] xml;

    private final byte[] cdr;

    public AnexoContingenciaService(Transaccion transaccion, byte[] pdf, byte[] xml, byte[] cdr) {
        this.transaccion = transaccion;
        this.pdf = pdf;
        this.xml = xml;
        this.cdr = cdr;
    }

    @Override
    public void run() {
        ICompany sociedad = null;
        try {
            String sRutaConfigReal = System.getProperty("user.dir");
            String[] sRutaConfigGeneral = sRutaConfigReal.split("[\\\\/]", -1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";
            File file = new File(sRutaConfigReal);
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuracion.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Configuracion configuracion = (org.ventura.cpe.loaderbl.Configuracion) jaxbUnmarshaller.unmarshal(file);

            String servidorLicencias = configuracion.getErp().getServidorLicencias().normalValue();
            String servidor = configuracion.getErp().getServidorBD().normalValue();
            String tipoServidor = configuracion.getErp().getTipoServidor().normalValue();
            String companyDb = configuracion.getErp().getBaseDeDatos().normalValue();
            String dbUsername = configuracion.getErp().getUser().normalValue();
            String dbPassword = configuracion.getErp().getPassword().normalValue();
            String username = configuracion.getErp().getErpUser().normalValue();
            String password = configuracion.getErp().getErpPass().normalValue();

            sociedad = SBOCOMUtil.newCompany();
            sociedad.setLicenseServer(servidorLicencias);
            sociedad.setServer(servidor);
            sociedad.setDbServerType(Integer.parseInt(tipoServidor));
            sociedad.setCompanyDB(companyDb);
            sociedad.setDbUserName(dbUsername);
            sociedad.setDbPassword(dbPassword);
            sociedad.setUserName(username);
            sociedad.setPassword(password);
            sociedad.setLanguage(SBOCOMConstants.BoSuppLangs_ln_English);
            sociedad.setUseTrusted(Boolean.FALSE);

            int ret = sociedad.connect();
            if (ret == 0) {
                System.out.println("Se conecto a la sociedad");
                String FE_Id = transaccion.getFEId();
                String nombPDF = FE_Id + "_FormatoImpreso";
                String nombXML = FE_Id + "_XmlFirmado";
                String nombCDR = FE_Id + "_SUNAT_CDR";
                Date fechaEmision = transaccion.getDOCFechaEmision();
                String tipoDocumento = transaccion.getDOCCodigo();
                Integer docEntry = transaccion.getFEDocEntry();
                int objType = Integer.parseInt(transaccion.getFEObjectType());
                Calendar fecha = Calendar.getInstance();
                fecha.setTime(fechaEmision);
                int anio = fecha.get(Calendar.YEAR);
                int mes = fecha.get(Calendar.MONTH) + 1;
                int dia = fecha.get(Calendar.DAY_OF_MONTH);
                String rucCliente = transaccion.getSNDocIdentidadNro();
                String rucEmpresa = transaccion.getDocIdentidadNro();

                if (!sociedad.isInTransaction()) {
                    sociedad.startTransaction();
                }
                int idAttachments = 0;
                boolean isDocumentNotFound = false, isAnexoEmpty = false;
                IDocuments documents = SBOCOMUtil.getDocuments(sociedad, objType, docEntry);
                if (documents == null) {
                    System.out.println("Aunque ya no habia nada que hacer se me ocurrio una solucion");
                    isDocumentNotFound = true;
                } else {
                    isAnexoEmpty = true;
                    idAttachments = documents.getAttachmentEntry();
                }
                IAttachments2 iAttachments2;
                boolean isOverride = false;
                if (idAttachments == 0) {
                    iAttachments2 = SBOCOMUtil.newAttachments2(sociedad);
                    isAnexoEmpty = true;
                } else {
                    iAttachments2 = SBOCOMUtil.getAttachments2(sociedad, idAttachments);
                }
                List<String> rutas = new ArrayList<>();
                Path rutaFile = Paths.get(Directorio.ADJUNTOS, "" + anio, "" + mes, "" + dia, tipoDocumento, rucEmpresa, rucCliente);
                if (pdf != null) {
                    String rutaPDF = FilenameUtils.getName(rutaFile.resolve(nombPDF + ".pdf").toString());
                    rutas.add(guardarDocumento(rutaFile, rutaPDF, pdf));
//                    AgregarAnexo(att2, pdf, nombPDF, "pdf", rutaFile, isAnexoEmpty);
//                    if (isDocumentNotFound) {
//                        int newKey = Integer.parseInt(sociedad.getNewObjectKey());
//                        addAttachmentEntry(docEntry, newKey, tipoDocumento, sociedad);
//                    }
                }
                if (xml != null) {
                    String rutaXML = FilenameUtils.getName(rutaFile.resolve(nombXML + ".xml").toString());
                    rutas.add(guardarDocumento(rutaFile, rutaXML, xml));
//                    AgregarAnexo(att2, xml, nombXML, "xml", rutaFile, isAnexoEmpty);
//                    if (isDocumentNotFound) {
//                        int newKey = Integer.parseInt(sociedad.getNewObjectKey());
//                        addAttachmentEntry(docEntry, newKey, tipoDocumento, sociedad);
//                    }
                }
                if (cdr != null) {
                    String rutaCDR = FilenameUtils.getName(rutaFile.resolve(nombCDR + ".zip").toString());
                    rutas.add(guardarDocumento(rutaFile, rutaCDR, cdr));
//                    AgregarAnexo(att2, cdr, nombCDR, "zip", rutaFile, isAnexoEmpty);
//                    if (isDocumentNotFound) {
//                        int newKey = Integer.parseInt(sociedad.getNewObjectKey());
//                        addAttachmentEntry(docEntry, newKey, tipoDocumento, sociedad);
//                    }
                }

                for (String rutaDocumento : rutas) {
                    String baseName = FilenameUtils.getBaseName(rutaDocumento);
                    String extension = FilenameUtils.getExtension(rutaDocumento);
                    String parentPath = rutaFile.toString();
                    int count = iAttachments2.getLines().getCount();
                    boolean hasToAdd = false;
                    if (count == 1 && isAnexoEmpty) {
                        iAttachments2.getLines().setCurrentLine(0);
                    } else {
                        int documentPosition = this.findDocumentPosition(iAttachments2, baseName);
                        isOverride = documentPosition != -1;
                        iAttachments2.getLines().setCurrentLine(isOverride ? documentPosition : count - 1);
                    }
                    if (!isAnexoEmpty && !isOverride) {
                        iAttachments2.getLines().add();
                        hasToAdd = true;
                    }
                    iAttachments2.getLines().setFileExtension(extension);
                    iAttachments2.getLines().setFileName(baseName);
                    iAttachments2.getLines().setOverride(1);
                    iAttachments2.getLines().setSourcePath(parentPath);
                    if (isAnexoEmpty || hasToAdd) {
                        int add = iAttachments2.add();
                        if (add != 0) {
                            System.err.println(sociedad.getLastErrorDescription());
                            if (sociedad.isInTransaction()) {
                                sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                            }
                        }
                        int newKey = Integer.parseInt(sociedad.getNewObjectKey());
                        if (!isDocumentNotFound) {
                            documents.setAttachmentEntry(newKey);
                            int update = documents.update();
                            if (update != 0) {
                                System.err.println(sociedad.getLastErrorDescription());
                                if (sociedad.isInTransaction()) {
                                    sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                                }
                            }
                        } else {
                            addAttachmentEntry(docEntry, newKey, tipoDocumento, sociedad);
                        }
                        isAnexoEmpty = false;
                    } else {
                        int update = iAttachments2.update();
                        if (update != 0) {
                            System.err.println(sociedad.getLastErrorDescription());
                            if (sociedad.isInTransaction()) {
                                sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                            }
                        }
                    }
                }
                if (sociedad.isInTransaction()) {
                    sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                }
                sociedad.disconnect();
            } else {
                String msj = "ERR: (" + ret + ")" + sociedad.getLastErrorDescription();
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se pudo conectar {0}", msj);
            }
        } catch (JAXBException | VenturaExcepcion | SBOCOMException | IOException e) {
            e.printStackTrace();
        } finally {
            Optional.ofNullable(sociedad).ifPresent(iCompany -> {
                if (iCompany.isInTransaction()) {
                    iCompany.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                }
            });
        }
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

    private void addAttachmentEntry(int docentry, int newKey, String tipoDocumento, ICompany sociedad) throws SBOCOMException {
        switch (tipoDocumento) {
            case "01":
                String queryOinv = "UPDATE OINV SET \"AtcEntry\"=" + newKey + " WHERE \"DocEntry\"=" + docentry;
                runRecordsetQuery(sociedad, queryOinv);
                break;
            case "07":
                String query = "UPDATE OVPM SET \"AtcEntry\"=" + newKey + " WHERE \"DocEntry\"=" + docentry;
                runRecordsetQuery(sociedad, query);
                break;
        }
    }

    private String guardarDocumento(Path parent, String filename, byte[] bytes) throws IOException {
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        Path archivoPath = parent.resolve(filename);
        Files.write(archivoPath, bytes);
        return archivoPath.toString();
    }

    private String AgregarAnexo(Attachments2 att2, byte[] archivo, String nombre, String ext, Path ruta, boolean isAnexoEmpty) throws IOException {
        IAttachments2_Lines attl = att2.getLines();
        int count = attl.getCount();
        if (!Files.exists(ruta)) {
            Files.createDirectories(ruta);
        }
        Path fullname = ruta.resolve(nombre + "." + ext);
        Files.write(fullname, archivo);
        for (int i = 0; i < count; i++) {
            attl.setCurrentLine(i);
            if (attl.getFileName().compareTo(nombre) == 0 && attl.getFileExtension().compareTo(ext) == 0) {
                return "";
            }
        }

        /**
         * ******************************************************************
         */
        if (isAnexoEmpty) {
            att2.getLines().add();
        }

        att2.getLines().setFileExtension(ext);
        att2.getLines().setFileName(nombre);
        att2.getLines().setOverride(1);
        att2.getLines().setSourcePath(ruta.toString());

        for (int i = 0; i < att2.getLines().getCount(); i++) {
            System.out.println(att2.getLines().getFileName());
            System.out.println(att2.getLines().getFileExtension());
            System.out.println(att2.getLines().getSourcePath());
        }
        return fullname.toString();
    }
}
