/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao;

import com.sap.smb.sbo.api.ICompany;
import com.sap.smb.sbo.api.IRecordset;
import com.sap.smb.sbo.api.SBOCOMUtil;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.query.VSFactory;
import org.ventura.cpe.reporte.Reporte;
import org.ventura.cpe.sb1.DocumentoBL;
import org.ventura.utilidades.entidades.TipoServidor;
import org.ventura.utilidades.sunat.RespuestaSunat;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author VSUser
 */
public class DocumentoDAO {

    private static Node getTag(Document doc, Node padre, String ruta, boolean encriptable) {
        Node nodo = null;
        String[] partes = ruta.substring(1).split("/");
        if (partes.length == 0) {
            return null;
        } else {
            String nombre = partes[0];
            NodeList nodos = (padre == null ? doc.getChildNodes() : padre.getChildNodes());
            //NodeList nodos = padre.getChildNodes();
            for (int i = 0; i < nodos.getLength(); i++) {
                Node n = nodos.item(i);
                if (n.getNodeType() == 1 && n.getNodeName().compareTo(nombre) == 0) {
                    nodo = n;
                    break;
                }
            }
            if (nodo == null) {
                //nodo = padre.appendChild(doc.createElement(nombre));
                if (padre == null) {
                    nodo = doc.appendChild(doc.createElement(nombre));
                } else {
                    nodo = padre.appendChild(doc.createElement(nombre));
                }
            }
            if (partes.length > 1) {
                ruta = ruta.substring(nombre.length() + 1);
                return getTag(doc, nodo, ruta, encriptable);
            } else {
                if (encriptable) {
                    Attr prop = (Attr) nodo.getAttributes().getNamedItem("encriptado");
                    if (prop == null) {
                        prop = doc.createAttribute("encriptado");
                        nodo.getAttributes().setNamedItem(prop);
                    }
                    if (prop.getNodeValue() == null || prop.getNodeValue().isEmpty()) {
                        prop.setNodeValue("false");
                    }
                }
                return nodo;
            }
        }
    }

    static Document doc;

    public static String VerificarDocumentosSinRuc(Reporte r) {
        Node nodo;

//        this.baseDirectory + separator + this.xmlDirectory + separator + rucEmpresa + separator + rucCliente + separator + documentName + ISunatConnectorConfig.EE_XML;
        String rutaBaja = Directorio.ADJUNTOS + File.separator + "baja" + File.separator + "XML";
        //System.out.println("Ruta Baja :" + rutaBaja);
        File fbaja = new File(rutaBaja);
        if (fbaja.exists()) {

            File[] ficherosbaja = fbaja.listFiles();
            for (int x = 0; x < ficherosbaja.length; x++) {
                if (ficherosbaja[x].getName().endsWith(".xml")) {
                    try {

                        /**
                         * String serie, correlativo, idBaja;*
                         */
                        String serie, correlativo, idBaja, tipoDoc = null;

                        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                        DocumentBuilder db = dbf.newDocumentBuilder();
                        File f = new File(ficherosbaja[x].getAbsolutePath());
                        if (f.exists()) {

                            doc = db.parse(f);
                            nodo = getTag(doc, null, "/VoidedDocuments/cbc:ID", true);
                            idBaja = nodo.getTextContent();
                            nodo = getTag(doc, null, "/VoidedDocuments/sac:VoidedDocumentsLine/cbc:DocumentTypeCode", true);
                            tipoDoc = nodo.getTextContent();
                            nodo = getTag(doc, null, "/VoidedDocuments/sac:VoidedDocumentsLine/sac:DocumentSerialID", true);
                            serie = nodo.getTextContent();
                            nodo = getTag(doc, null, "/VoidedDocuments/sac:VoidedDocumentsLine/sac:DocumentNumberID", true);
                            correlativo = nodo.getTextContent();
                            System.out.println("Id Baja :" + idBaja);
                            System.out.println("TipoDoc BJ :" + tipoDoc + "TipoDoc RV:" + r.getTipoDocumento().substring(0, 2));
                            if (r.getNumeroSerie().equalsIgnoreCase(serie + "-" + correlativo) && r.getTipoDocumento().substring(0, 2).equalsIgnoreCase(tipoDoc)) {
                                String rutaBajaCDR = null;
                                rutaBajaCDR = Directorio.ADJUNTOS + File.separator + "baja" + File.separator + "CDR" + File.separator + "R-" + r.getIdEmisor() + "-" + idBaja + ".zip";

                                File dir = new File(rutaBajaCDR);
                                if (dir.exists()) {
                                    return rutaBajaCDR;
                                } else {
                                    if (ficherosbaja[x].delete()) {
                                        System.out.println("Se procedi? a eliminar");
                                    } else {
                                        System.out.println("No se pudo eliminar correctamente");
                                    }

                                    VerificarDocumentosSinRuc(r);
                                }

                            }

                        } else {
                            if (ficherosbaja[x].delete()) {
                                System.out.println("Se elimino correctamente");
                            } else {
                                System.out.println("No se pudo eliminar");
                            }
                            VerificarDocumentosSinRuc(r);
                        }

                    } catch (ParserConfigurationException | SAXException | IOException ex) {
                        Logger.getLogger(DocumentoDAO.class.getName()).log(Level.SEVERE, null, ex);
                        System.out.println(ex.getMessage());
                    }
                }
            }

        }

        return null;
    }

    public static String VerificarDocumentosConRuc(Reporte r) {

        String rutaActual = null;

        Calendar fecha = Calendar.getInstance();
        fecha.setTime(r.getFecha());
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);

        boolean encontroEmision = false;
        boolean encontroBaja = false;
        boolean encontroResumen = false;
        String s = File.separator;
        String rutaEmision = Directorio.ADJUNTOS + s + anio + s + mes + s + dia + s + r.getTipoDocumento().subSequence(0, 2) + s + r.getRucCliente();

        System.out.println("Ruta" + rutaEmision);
        File femision = new File(rutaEmision);
        if (femision.exists()) {

            File[] ficherosemision = femision.listFiles();
            for (int x = 0; x < ficherosemision.length; x++) {
                if (ficherosemision[x].isDirectory()) {
                    VerificarDocumentosConRuc(r);
                } else {
                    System.out.println("Ruta_File :" + ficherosemision[x].getAbsolutePath());
                    if (ficherosemision[x].getName().equalsIgnoreCase(r.getNumeroSerie().replace("-", "_") + "_SUNAT_CDR.zip") || ficherosemision[x].getName().contains(r.getNumeroSerie().replace("-", "_") + "SUNAT_CDR_RDB.zip")) {
                        if (r.getTipoDocumento().equalsIgnoreCase("03")) {
                            encontroResumen = true;
                        }
                        encontroEmision = true;
                        rutaActual = null;
                        rutaActual = ficherosemision[x].getAbsolutePath();
                    } else {
                        if (ficherosemision[x].getName().equalsIgnoreCase(r.getNumeroSerie().replace("-", "_") + "_SUNAT_CDR_baja.zip") || ficherosemision[x].getName().equalsIgnoreCase(r.getNumeroSerie().replace("-", "_") + "baja.zip")) {
                            encontroBaja = true;
                            rutaActual = null;
                            rutaActual = ficherosemision[x].getAbsolutePath();
                            return rutaActual;
                        }
                    }
                }
            }
            if (!encontroResumen && !encontroEmision) {
                String rutaResumen = Directorio.ADJUNTOS + s + "resumen" + s + "CDR";
                File fresumen = new File(rutaResumen);
                File[] ficherosresumen = fresumen.listFiles();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
                for (int x = 0; x < ficherosresumen.length; x++) {
                    String fechaResumen = formatter.format(r.getFecha());
                    if (ficherosresumen[x].getName().contains(fechaResumen)) {
                        rutaActual = null;
                        rutaActual = ficherosresumen[x].getAbsolutePath();
                    }
                }
            }
            if (encontroBaja) {
                String rutaBajaCDR = null;
                String rutaBaja = Directorio.ADJUNTOS + s + "baja" + s + "XML";
                System.out.println("Ruta Baja :" + rutaBaja);
                File fbaja = new File(rutaBaja);
                File[] ficherosbaja = fbaja.listFiles();
                for (int x = 0; x < ficherosbaja.length; x++) {
                    if (ficherosbaja[x].getName().endsWith(".xml")) {
                        try {
                            String serie, correlativo, idBaja, tipoDoc = null;
                            Node nodo;
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                            DocumentBuilder db = dbf.newDocumentBuilder();
                            File f = new File(ficherosbaja[x].getAbsolutePath());
                            doc = db.parse(f);
                            nodo = getTag(doc, null, "/VoidedDocuments/sac:VoidedDocumentsLine/sac:DocumentSerialID", true);
                            serie = nodo.getTextContent();
                            nodo = getTag(doc, null, "/VoidedDocuments/sac:VoidedDocumentsLine/sac:DocumentNumberID", true);
                            correlativo = nodo.getTextContent();
                            nodo = getTag(doc, null, "/VoidedDocuments/cbc:ID", true);
                            idBaja = nodo.getTextContent();
                            nodo = getTag(doc, null, "/VoidedDocuments/sac:VoidedDocumentsLine/cbc:DocumentTypeCode", true);
                            tipoDoc = nodo.getTextContent();

                            if (r.getNumeroSerie().equalsIgnoreCase(serie + "-" + correlativo) && r.getTipoDocumento().substring(0, 2).equalsIgnoreCase(tipoDoc)) {
                                rutaBajaCDR = Directorio.ADJUNTOS + s + "baja" + s + "CDR";
                                File directorio1 = new File(rutaBajaCDR);
                                File[] ficheros1 = directorio1.listFiles();
                                for (int p = 0; p < ficheros1.length; p++) {
                                    if (ficheros1[p].getName().contains(idBaja)) {
                                        rutaBajaCDR = ficheros1[p].getAbsolutePath();
                                        return rutaBajaCDR;
                                    }
                                }
                            }
                        } catch (ParserConfigurationException | SAXException | IOException ex) {
                            Logger.getLogger(DocumentoDAO.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println(ex.getMessage());
                        }
                    }
                }
                if (rutaBajaCDR == null && encontroEmision) {
                    return rutaActual;
                }
            }
            return rutaActual;
        } else {
            return null;
        }
    }

    public static List<Reporte> extraerReporte(String fechaInicio, String FechaFin) {
        List<Reporte> trans = new LinkedList<>();
        VSFactory factory = VSFactory.getInstance();
        IRecordset rs;
        String[] parametros = new String[3];
        if (TipoServidor.TipoServidorQuery.equalsIgnoreCase("9")) {
            parametros[0] = fechaInicio.replace("-", "");
            parametros[1] = FechaFin.replace("-", "");
        } else {
            parametros[0] = fechaInicio;
            parametros[1] = FechaFin;
        }
        parametros[2] = "N";

        try {
            Set<ICompany> listSociedades = DocumentoBL.listSociedades;
            List<ICompany> companies = new ArrayList<>(listSociedades);
            ICompany company = listSociedades.isEmpty() ? DocumentoBL.Sociedad : companies.get(0);
            rs = SBOCOMUtil.runRecordsetQuery(company, factory.GetQuery(15, TipoServidor.TipoServidorQuery, parametros));
            while (!rs.isEoF()) {

                Reporte r = new Reporte();
                r.setNumeroSerie(rs.getFields().item("Serie").getValue().toString() + "-" + rs.getFields().item("Numero").getValue().toString());
                //System.out.println(rs.getFields().item("Serie").getValue().toString() + "-" + rs.getFields().item("Numero").getValue().toString());
                r.setTipoDocumento(rs.getFields().item("Tipo").getValue().toString().substring(0, 2));
                //System.out.println(rs.getFields().item("Tipo").getValue().toString());
                r.setRucCliente(rs.getFields().item("NumeroRUC").getValue().toString());
                r.setIdEmisor(rs.getFields().item("RUC").getValue().toString());
                //System.out.println(rs.getFields().item("NumeroRUC").getValue().toString());
                //System.out.println(rs.getFields().item("Fecha Emision").getValue());
                Integer tcampo = rs.getFields().item("Fecha Emision").getType();
                //System.out.println("Tipo de Campo " + tcampo);
                switch (tcampo) {
                    case 0:
                    case 1:  //Alpha,Memo
                        //System.out.println("Alpha,Memo");
                        DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String dateAsString = rs.getFields().item("Fecha Emision").getValue().toString();
                        Date date = sourceFormat.parse(dateAsString);
                        r.setFecha(date);
                        break;
                    case 2:  //Numeric

                        break;
                    case 3:  //Date
                        //System.out.println("Date");
                        r.setFecha(rs.getFields().item("Fecha Emision").getValueDate());
                        break;
                    case 4: //Float

                        break;
                }

                BigDecimal bg = new BigDecimal(rs.getFields().item("Importe Total").getValueDouble().toString());
                r.setTotalDocumento(bg);
                System.out.println(r.getNumeroSerie());
                if (r.getRucCliente().equalsIgnoreCase("")) {
                    String RespuestaCDR = null;
                    String rutaFile = VerificarDocumentosSinRuc(r);
                    System.out.println("Baja :" + rutaFile);
                    if (rutaFile != null) {
                        byte[] cdrByte = convertirFileByte(rutaFile);
                        TransaccionRespuesta.Sunat sunatResponse = RespuestaSunat.SetRespuestaSUNAT(cdrByte, 2, r.getTipoDocumento().substring(0, 2));
                        if (sunatResponse == null) {
                            sunatResponse = RespuestaSunat.SetRespuestaSUNAT(cdrByte, 1, r.getTipoDocumento().substring(0, 2));
                        }

                        if (sunatResponse == null) {
                            return null;
                        }
                        RespuestaCDR = sunatResponse.toString();
                        System.out.println("Ruta :" + rutaFile);
                        r.setRespuestaSunat(RespuestaCDR);
                    } else {
                        r.setRespuestaSunat(null);
                    }
                } else {
                    String RespuestaCDR = null;
                    String rutaFile = VerificarDocumentosConRuc(r);
                    if (rutaFile != null) {
                        byte[] cdrByte = convertirFileByte(rutaFile);
                        TransaccionRespuesta.Sunat sunatResponse = RespuestaSunat.SetRespuestaSUNAT(cdrByte, 2, r.getTipoDocumento().substring(0, 2));
                        if (sunatResponse == null) {
                            sunatResponse = RespuestaSunat.SetRespuestaSUNAT(cdrByte, 1, r.getTipoDocumento().substring(0, 2));
                        }

                        if (sunatResponse == null) {
                            return null;
                        }
                        RespuestaCDR = sunatResponse.toString();
                        System.out.println("Ruta :" + rutaFile);
                        r.setRespuestaSunat(RespuestaCDR.substring(3));
                    } else {
                        r.setRespuestaSunat(null);
                    }

                }
                trans.add(r);
                rs.moveNext();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return trans;

    }

    public static byte[] convertirFileByte(String rutaUbicacionFile) throws FileNotFoundException {
        System.out.println("Ruta File: " + rutaUbicacionFile);
        if (!"".equals(rutaUbicacionFile)) {
            try {
                File file = new File(rutaUbicacionFile);
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                        bos.write(buf, 0, readNum); //no doubt here is 0
                    }
                } catch (Exception e) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, " ERROR {0} ", e.getMessage());
                }
                byte[] byteconvertido = bos.toByteArray();
                return byteconvertido;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                //LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object(){}.getClass().getName(),new Object(){}.getClass().getEnclosingMethod().getName(),e.getMessage()});
                return null;
            }
        } else {
            return null;
        }
    }

}
