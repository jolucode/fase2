/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao;

import com.sap.smb.sbo.api.*;
import org.ventura.cpe.dao.controller.UsuariocamposJC;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.hb.*;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.SAPNotFoundRegistry;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.loaderbl.Loader;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.query.VSFactory;
import org.ventura.cpe.sb1.DocumentoBL;
import org.ventura.utilidades.entidades.TipoServidor;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.sap.smb.sbo.api.SBOCOMUtil.runRecordsetQuery;
import static java.text.MessageFormat.format;

/**
 * @author VSUser
 */
public class DocumentoDAO {

    public static boolean isSunat;

    private static BigDecimal dsctoTotal;

    private static SBOErrorMessage errMsg = null;

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

    private static Boolean esBaja(String ruta, String serieCorrelativo) {

        Node nodo;
        try {
            String serie;
            String correlativo;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            File f = new File(ruta);
            if (!f.exists()) {
                f.createNewFile();
                doc = db.newDocument();
            } else {
                try {
                    doc = db.parse(f);
                } catch (Exception ex) {
                    LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al leer config.xml. {0}", ex.getMessage());
                }
            }

            /**
             * ****************************ERP***************************
             */
            nodo = getTag(doc, null, "/VoidedDocuments/sac:VoidedDocumentsLine/sac:DocumentSerialID", true);
            serie = nodo.getTextContent();

            nodo = getTag(doc, null, "/VoidedDocuments/sac:VoidedDocumentsLine/sac:DocumentNumberID", true);
            correlativo = nodo.getTextContent();

            String seriecorrelativo = serie + "-" + correlativo;
            if (seriecorrelativo.equalsIgnoreCase(serieCorrelativo)) {
                return true;
            } else {
                return false;
            }

        } catch (ParserConfigurationException | IOException | HeadlessException | DOMException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar par?metros.  {0}", ex.getMessage());
        }
        return false;

    }

    public static List<TransaccionResumen> extraerResumenes(String fechaEmision, int correlativo) throws VenturaExcepcion {

        List<TransaccionResumen> trans = new LinkedList<>();
        VSFactory factory = VSFactory.getInstance();
        String[] parametros = new String[3];
        parametros[0] = fechaEmision;
        parametros[1] = String.valueOf(correlativo);
        IRecordset rs;
        try {
            rs = SBOCOMUtil.runRecordsetQuery(DocumentoBL.Sociedad, factory.GetQuery(12, TipoServidor.TipoServidorQuery, parametros));

            while (!rs.isEoF()) {
                TransaccionResumen doc = new TransaccionResumen();

                doc.setDIRDepartamento(rs.getFields().item("DIR_Departamento").getValue().toString());
                doc.setDIRDireccion(rs.getFields().item("DIR_Direccion").getValue().toString());
                doc.setDIRDistrito(rs.getFields().item("DIR_Distrito").getValue().toString());
                doc.setDIRNomCalle(rs.getFields().item("DIR_NomCalle").getValue().toString());
                doc.setDIRNroCasa(rs.getFields().item("DIR_NroCasa").getValue().toString());
                doc.setDIRPais(rs.getFields().item("DIR_Pais").getValue().toString());
                doc.setDIRProvincia(rs.getFields().item("DIR_Provincia").getValue().toString());
                doc.setDIRUbigeo(rs.getFields().item("DIR_Ubigeo").getValue().toString());
                doc.setDocIdentidadTipo(rs.getFields().item("DocIdentidad_Tipo").getValue().toString());
                doc.setEMail(rs.getFields().item("EMail").getValue().toString());
                doc.setEstado(rs.getFields().item("Estado").getValue().toString());
                doc.setFechaEmision(rs.getFields().item("Fecha_Emision").getValue().toString());
                doc.setFechaGeneracion(rs.getFields().item("Fecha_Generacion").getValue().toString());
                doc.setNombreComercial(rs.getFields().item("NombreComercial").getValue().toString());
                doc.setNumeroRuc(rs.getFields().item("Numero_Ruc").getValue().toString());
                doc.setNumeroTicket(rs.getFields().item("Numero_Ticket").getValue().toString());
                doc.setPersonContacto(rs.getFields().item("PersonContacto").getValue().toString());
                doc.setRazonSocial(rs.getFields().item("RazonSocial").getValue().toString());
                doc.setIdTransaccion(rs.getFields().item("Id_Transaccion").getValue().toString());

                List<TransaccionResumenLinea> lst = ExtransaccionResumenLineas(parametros, TipoServidor.TipoServidorQuery, doc.getIdTransaccion());
                List<TransaccionResumenLineaAnexo> lst2 = ExtransaccionResumenLineas_Anexo(parametros, TipoServidor.TipoServidorQuery, doc.getIdTransaccion());

                if (lst.isEmpty()) {
                    doc.setTransaccionResumenLineaList(null);
                } else {

                    doc.setTransaccionResumenLineaList(lst);
                }

                if (lst2.isEmpty()) {
                    doc.setTransaccionResumenLineaAnexoList(null);
                } else {
                    doc.setTransaccionResumenLineaAnexoList(lst2);
                }

                trans.add(doc);
                rs.moveNext();
            }
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Finalizo la extracci?n del resumen");
            return trans;

        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage()});
            throw ex;
        } catch (SBOCOMException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage()});
            throw new VenturaExcepcion("ExtraerTransacciones: " + ex.getMessage());
        } finally {
            System.gc();
        }

    }

    public static List<TransaccionResumenLinea> ExtransaccionResumenLineas(String[] params, String tipoServidor, String FE_ID) throws VenturaExcepcion {

        List<TransaccionResumenLinea> lineas = new LinkedList<>();
        VSFactory factory = VSFactory.getInstance();
        IRecordset rs;
        try {
            rs = SBOCOMUtil.runRecordsetQuery(DocumentoBL.Sociedad, factory.GetQuery(13, TipoServidor.TipoServidorQuery, params));

            while (!rs.isEoF()) {
                TransaccionResumenLinea trl = new TransaccionResumenLinea();
                TransaccionResumenLineaPK trlpk = new TransaccionResumenLineaPK();
                trlpk.setIdLinea(rs.getFields().item("ID").getValueInteger());
                trlpk.setIdTransaccion(FE_ID);
                trl.setTransaccionResumenLineaPK(trlpk);
                trl.setCodMoneda(rs.getFields().item("codigoMoneda").getValue().toString());
                trl.setImporteOtrosCargos(BigDecimal.valueOf(rs.getFields().item("otrosCargos").getValueDouble()));
                trl.setImporteTotal(BigDecimal.valueOf(rs.getFields().item("totalDoc").getValueDouble()));
                trl.setNumeroCorrelativoFin(rs.getFields().item("fin").getValue().toString());
                trl.setNumeroCorrelativoInicio(rs.getFields().item("inicio").getValue().toString());
                trl.setNumeroSerie(rs.getFields().item("serie").getValue().toString());
                trl.setTipoDocumento(rs.getFields().item("tipo").getValue().toString());
                trl.setTotaIGV(BigDecimal.valueOf(rs.getFields().item("totalIGV").getValueDouble()));
                trl.setTotalISC(BigDecimal.valueOf(rs.getFields().item("totalISC").getValueDouble()));
                trl.setTotalOPExoneradas(BigDecimal.valueOf(rs.getFields().item("totalOpExonerada").getValueDouble()));
                trl.setTotalOPGravadas(BigDecimal.valueOf(rs.getFields().item("totalOpGravada").getValueDouble()));
                trl.setTotalOPInafectas(BigDecimal.valueOf(rs.getFields().item("totalOpInafecta").getValueDouble()));
                trl.setTotalOtrosTributos(BigDecimal.valueOf(rs.getFields().item("otrosImpuestos").getValueDouble()));
                lineas.add(trl);
                rs.moveNext();
            }
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Resumen Diario: Se extrajo correctamente {0}  linea ", new Object[]{lineas.size()});
            return lineas;
        } catch (SBOCOMException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage()});
            throw new VenturaExcepcion("Extransaccion Resumen Lineas. ", ex);
        } finally {
            System.gc();
        }
    }

    public static List<TransaccionResumenLineaAnexo> ExtransaccionResumenLineas_Anexo(String[] params, String tipoServidor, String FE_ID) throws VenturaExcepcion {

        List<TransaccionResumenLineaAnexo> lineas = new LinkedList<>();
        VSFactory factory = VSFactory.getInstance();
        IRecordset rs;
        try {
            rs = SBOCOMUtil.runRecordsetQuery(DocumentoBL.Sociedad, factory.GetQuery(14, TipoServidor.TipoServidorQuery, params));
            int contador = 1;
            while (!rs.isEoF()) {
                TransaccionResumenLineaAnexo trl = new TransaccionResumenLineaAnexo();
                TransaccionResumenLineaAnexoPK trlpk = new TransaccionResumenLineaAnexoPK();
                trlpk.setIdLinea(contador);
                trlpk.setIdTransaccion(FE_ID);
                trl.setTransaccionResumenLineaAnexoPK(trlpk);
                trl.setDocEntry(rs.getFields().item("Docentry").getValue().toString());
                trl.setObjcType(rs.getFields().item("Objtype").getValue().toString());
                trl.setSn(rs.getFields().item("SN").getValue().toString());
                trl.setTipoDocumento(rs.getFields().item("TipoDocumento").getValue().toString());
                trl.setTipoTransaccion(rs.getFields().item("tipoTransaccion").getValue().toString());
                trl.setSerie(rs.getFields().item("Serie").getValue().toString());
                trl.setCorrelativo(rs.getFields().item("Correlativo").getValue().toString());
                lineas.add(trl);
                contador++;
                rs.moveNext();
            }
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Resumen Diario: Se extrajo correctamente {0}  linea ", new Object[]{lineas.size()});
            return lineas;
        } catch (SBOCOMException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage()});
            throw new VenturaExcepcion("Lineas de Transaccion Resumen Anexo. ", ex);
        } finally {
            System.gc();
        }
    }

    public static List<TransaccionLineas> ExtraerTransaccionLineas(String[] params, String tipoServidor, String feID, ICompany Sociedad) throws VenturaExcepcion {
        List<TransaccionLineas> lineas = new LinkedList<>();
        IRecordset rs;
        try {
            VSFactory factory = VSFactory.getInstance();
            rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.GetQuery(3, tipoServidor, params));
            crearCamposUsuario(rs);
            while (!rs.isEoF()) {
                TransaccionLineas linea = new TransaccionLineas(feID, rs.getFields().item("NroOrden").getValueInteger());
                linea.setCantidad(BigDecimal.valueOf(rs.getFields().item("Cantidad").getValueDouble()));
                linea.setDSCTOMonto(BigDecimal.valueOf(rs.getFields().item("DSCTO_Monto").getValueDouble()));
                linea.setDSCTOPorcentaje(BigDecimal.valueOf(rs.getFields().item("DSCTO_Porcentaje").getValueDouble()));
                linea.setDescripcion(rs.getFields().item("Descripcion").getValueString());
                linea.setPrecioDscto(BigDecimal.valueOf(rs.getFields().item("PrecioDscto").getValueDouble()));
                linea.setPrecioIGV(BigDecimal.valueOf(rs.getFields().item("PrecioIGV").getValueDouble()));
                linea.setTotalLineaSinIGV(BigDecimal.valueOf(rs.getFields().item("TotalLineaSinIGV").getValueDouble()));
                linea.setTotalLineaConIGV(BigDecimal.valueOf(rs.getFields().item("TotalLineaConIGV").getValueDouble()));
                linea.setPrecioRefCodigo(rs.getFields().item("PrecioRef_Codigo").getValueString());
                linea.setPrecioRefMonto(BigDecimal.valueOf(rs.getFields().item("PrecioRef_Monto").getValueDouble()));
                linea.setUnidad(rs.getFields().item("Unidad").getValueString());
                linea.setUnidadSunat(rs.getFields().item("UnidadSunat").getValueString());
                linea.setCodArticulo(rs.getFields().item("CodArticulo").getValueString());
                linea.setTotalBruto(BigDecimal.valueOf(rs.getFields().item("TotalBruto").getValueDouble()));
                linea.setLineaImpuesto(BigDecimal.valueOf(rs.getFields().item("LineaImpuesto").getValueDouble()));
                linea.setCodSunat(rs.getFields().item("CodSunat").getValueString());
                linea.setCodProdGS1(rs.getFields().item("CodProdGS1").getValueString());
                linea.setCodUbigeoOrigen(rs.getFields().item("CodUbigeoOrigen").getValueString());
                linea.setDirecOrigen(rs.getFields().item("DirecOrigen").getValueString());
                linea.setCodUbigeoDestino(rs.getFields().item("CodUbigeoDestino").getValueString());
                linea.setDirecDestino(rs.getFields().item("DirecDestino").getValueString());
                linea.setDetalleViaje(rs.getFields().item("DetalleViaje").getValueString());
                linea.setValorTransporte(BigDecimal.valueOf(rs.getFields().item("ValorTransporte").getValueDouble()));
                linea.setValorCargaEfectiva(BigDecimal.valueOf(rs.getFields().item("ValorCargaEfectiva").getValueDouble()));
                linea.setValorCargaUtil(BigDecimal.valueOf(rs.getFields().item("ValorCargaUtil").getValueDouble()));
                linea.setConfVehicular(rs.getFields().item("ConfVehicular").getValueString());
                linea.setcUtilVehiculo(BigDecimal.valueOf(rs.getFields().item("CUtilVehiculo").getValueDouble()));
                linea.setcEfectivaVehiculo(BigDecimal.valueOf(rs.getFields().item("CEfectivaVehiculo").getValueDouble()));
                linea.setValorRefTM(BigDecimal.valueOf(rs.getFields().item("ValorRefTM").getValueDouble()));
                linea.setValorPreRef(BigDecimal.valueOf(rs.getFields().item("ValorPreRef").getValueDouble()));
                linea.setFactorRetorno(rs.getFields().item("FactorRetorno").getValueString());
                int resultado = (rs.getFields().item("NroOrden").getValueInteger() - 1);
                params[2] = String.valueOf(resultado);
                linea.setTransaccionLineaImpuestosList(ExtraerTransaccionLineasImpuestos(params, tipoServidor, linea.getTransaccionLineasPK().getNroOrden(), feID, Sociedad));
                linea.setTransaccionLineasUsucamposList(getTransaccionLineaCamposUsuarios(feID, linea.getTransaccionLineasPK().getNroOrden(), rs.getFields()));
                linea.setTransaccionLineasBillrefList(ExtraerTransaccionLineasBillRefs(params, tipoServidor, linea.getTransaccionLineasPK().getNroOrden(), feID, Sociedad));
                dsctoTotal = dsctoTotal.add(BigDecimal.valueOf(rs.getFields().item("DSCTO_Monto").getValueDouble()));
                lineas.add(linea);
                rs.moveNext();
            }
            return lineas;
        } catch (SBOCOMException | VenturaExcepcion ex) {
            throw new VenturaExcepcion("Lineas de Transaccion. ", ex);
        } finally {
            System.gc();
        }
    }

    public static List<TransaccionLineasBillref> ExtraerTransaccionLineasBillRefs(String[] parametros, String TipoServidor, int linea, String feID, ICompany Sociedad) throws VenturaExcepcion {
        List<TransaccionLineasBillref> billrefs = new LinkedList<>();
        IRecordset rs;
        try {
            VSFactory factory = VSFactory.getInstance();
            rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.GetQuery(5, TipoServidor, parametros));
            while (!rs.isEoF()) {
                TransaccionLineasBillref billref = new TransaccionLineasBillref(feID, linea, rs.getFields().item("LineId").getValueInteger());
                billref.setAdtDocRefId(rs.getFields().item("AdtDocRef_Id").getValueString());
                billref.setAdtDocRefSchemaId(rs.getFields().item("AdtDocRef_SchemaId").getValueString());
                billref.setInvDocRefDocTypeCode(rs.getFields().item("InvDocRef_DocTypeCode").getValueString());
                billref.setInvDocRefId(rs.getFields().item("InvDocRef_Id").getValueString());
                billrefs.add(billref);
                rs.moveNext();
            }
            return billrefs;
        } catch (Exception ex) {
            throw new VenturaExcepcion("Impuesto de linea. ", ex);
        } finally {
            System.gc();
        }
    }

    public static List<TransaccionLineaImpuestos> ExtraerTransaccionLineasImpuestos(String[] param, String tipoServidor, int nroOrden, String feID, ICompany Sociedad) throws VenturaExcepcion {
        List<TransaccionLineaImpuestos> impuestos = new LinkedList<>();
        IRecordset rs;

        try {
            VSFactory factory = VSFactory.getInstance();
            rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.GetQuery(4, tipoServidor, param));
            while (!rs.isEoF()) {
                TransaccionLineaImpuestos impuesto = new TransaccionLineaImpuestos(feID, nroOrden, rs.getFields().item("LineId").getValueInteger());
                impuesto.setMoneda(rs.getFields().item("Moneda").getValueString());
                impuesto.setMonto(BigDecimal.valueOf(rs.getFields().item("Monto").getValueDouble()));
                impuesto.setPorcentaje(BigDecimal.valueOf(rs.getFields().item("Porcentaje").getValueDouble()));
                impuesto.setTipoTributo(rs.getFields().item("TipoTributo").getValueString());
                impuesto.setTipoAfectacion(rs.getFields().item("TipoAfectacion").getValueString());
                impuesto.setAbreviatura(rs.getFields().item("Abreviatura").getValueString());
                impuesto.setCodigo(rs.getFields().item("Codigo").getValueString());
                impuesto.setNombre(rs.getFields().item("Nombre").getValueString());
                impuesto.setValorVenta(BigDecimal.valueOf(rs.getFields().item("ValorVenta").getValueDouble()));
                impuestos.add(impuesto);
                rs.moveNext();
            }
            return impuestos;
        } catch (Exception ex) {
            throw new VenturaExcepcion("Impuesto de linea. ", ex);
        } finally {
            System.gc();
        }
    }

    public static boolean CapturarCodigo(Transaccion tc, String codBarra, String digestValue, ICompany Sociedad, Connection connection) {
        if(connection != null){
            String tablaName = obtenerTablaObjectType(tc.getFEObjectType());
            if (tablaName == null) {
                throw new IllegalArgumentException("ObjectType not supported: " + tc.getFEObjectType());
            }else{
                String updateQuery = "UPDATE "+tablaName+" SET U_VS_DIGESTVALUE = '"+digestValue+"' ,U_VS_CODBARRA= '"+codBarra+"' WHERE \"DocEntry\" = ? AND \"ObjType\" = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, tc.getFEDocEntry());
                    pstmt.setInt(2, Integer.parseInt(tc.getFEObjectType()));
                    pstmt.executeUpdate();
                } catch (SQLException | NumberFormatException e) {
                    throw new RuntimeException("Error updating document state", e);
                }
            }
            return true;
        }else{
            IDocuments doc;
            IPayments pge;
            try {
                if (!tc.getDOCCodigo().equalsIgnoreCase("20")) {
                    doc = SBOCOMUtil.getDocuments(Sociedad, Integer.parseInt(tc.getFEObjectType()), tc.getFEDocEntry());
                    if (doc == null) {
                        throw new VenturaExcepcion("No se ha encontrado el registro");
                    }
                    doc.getUserFields().getFields().item("U_VS_DIGESTVALUE").setValue(digestValue);
                    doc.getUserFields().getFields().item("U_VS_CODBARRA").setValue(codBarra);
                    int ret = doc.update();
                    if (ret != 0) {
                        throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                    }
                } else {
                    pge = SBOCOMUtil.getPayments(Sociedad, Integer.parseInt(tc.getFEObjectType()), tc.getFEDocEntry());
                    if (pge == null) {
                        throw new VenturaExcepcion("No se ha encontrado el registro");
                    }
                    pge.getUserFields().getFields().item("U_VS_DIGESTVALUE").setValue(digestValue);
                    pge.getUserFields().getFields().item("U_VS_CODBARRA").setValue(codBarra);
                    int ret = pge.update();
                    if (ret != 0) {
                        throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                    }
                }

                return true;
            } catch (NumberFormatException | SBOCOMException | VenturaExcepcion ex) {
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Ocurrio un problemas {0}", new Object[]{ex.getMessage()});
                return false;
            } finally {
                System.gc();
            }
        }
    }

    public static boolean ActualizarMensaje(Transaccion tc, String respuesta, String estado, ICompany Sociedad, Connection connection) throws VenturaExcepcion, SBOCOMException {
        if(connection != null){
            String tablaName = obtenerTablaObjectType(tc.getFEObjectType());
            if (tablaName == null) {
                throw new IllegalArgumentException("ObjectType not supported: " + tc.getFEObjectType());
            }else{
                String ticket = "";
                if(tc.getTicketRest()!=null) ticket = " ,U_VS_TICKET= '"+tc.getTicketRest()+"'";
                String updateQuery = "UPDATE "+tablaName+" SET U_VS_FESTAT = '"+estado+"' ,U_VS_CDRRSM= '"+respuesta+"'"+ticket+" WHERE \"DocEntry\" = ? AND \"ObjType\" = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, tc.getFEDocEntry());
                    pstmt.setInt(2, Integer.parseInt(tc.getFEObjectType()));
                    pstmt.executeUpdate();
                } catch (SQLException | NumberFormatException e) {
                    throw new RuntimeException("Error updating document state", e);
                }
            }
            return true;
        }else{
            IPayments pge;
            int objType = Integer.parseInt(tc.getFEObjectType());
            int docEntry = tc.getFEDocEntry();
            String docCodigo = tc.getDOCCodigo();
            try {
                if (tc.getDOCCodigo().equalsIgnoreCase("20")) {
                    pge = SBOCOMUtil.getPayments(Sociedad, Integer.parseInt(tc.getFEObjectType()), tc.getFEDocEntry());
                    if (pge == null) {
                        if (!forceUpdateEstadoDocumento(docEntry, tc.getDOCCodigo(), estado, respuesta, Sociedad)) {
                            throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                        }
                        //throw new VenturaExcepcion("No se ha encontrado el registro");
                    }
                    pge.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                    pge.getUserFields().getFields().item("U_VS_CDRRSM").setValue(respuesta);
                    int ret = pge.update();
                    try {
                        if (!forceUpdateEstadoDocumento(docEntry, tc.getDOCCodigo(), estado, respuesta, Sociedad)) {
                            throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                        }
                    } catch (Exception e) {
                        throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                    }
                } else {

                /*

                if(Sociedad.equals(null)){
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "El valor de sociedad es " + Sociedad.toString() + " " + docEntry + " " + objType);
                }

                IPayments docPayment = SBOCOMUtil.getPayments(Sociedad, objType, docEntry);

                boolean searchDoc = docPayment.getByKey(docEntry);
                if (searchDoc) {
                    docPayment.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                    docPayment.getUserFields().getFields().item("U_VS_CDRRSM").setValue(estado);
                }

                int ret = docPayment.update();
                if (ret == 0) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "Se actualizo correctamente el estado de Factura Electrónica");
                } else {
                    LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Se encontro un incidente al agregar los anexos {0}", Sociedad.getLastErrorDescription());
                }
                */

                    IDocuments doc = Optional.ofNullable(SBOCOMUtil.getDocuments(Sociedad, objType, docEntry)).orElseThrow(() -> new SAPNotFoundRegistry(format("No se ha encontrado el registro por el DocEntry [{0}]", docEntry)));
                    if (doc == null) {
                        if (!forceUpdateEstadoDocumento(docEntry, tc.getDOCCodigo(), estado, respuesta, Sociedad)) {
                            throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                        }
                        //throw new VenturaExcepcion("No se ha encontrado el registro");
                    }
                    doc.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                    doc.getUserFields().getFields().item("U_VS_CDRRSM").setValue(respuesta);
                    if(tc.getTicketRest()!=null)
                        doc.getUserFields().getFields().item("U_VS_TICKET").setValue(tc.getTicketRest());
                    int ret2 = doc.update();
                    if (ret2 != 0) {
                        if (!forceUpdateEstadoDocumento(docEntry, docCodigo, estado, respuesta, Sociedad)) {
                            throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                        }
                    }
                }
                return true;
            } catch (NumberFormatException | SBOCOMException | VenturaExcepcion | SAPNotFoundRegistry ex) {
                if (ex instanceof SAPNotFoundRegistry) {
                    System.out.println(ex.getLocalizedMessage());
                    forceUpdateEstadoDocumento(docEntry, docCodigo, estado, respuesta, Sociedad);
                    return true;
                } else throw new VenturaExcepcion("[ObjType=" + tc.getFEObjectType() + ", DocEntry=" + tc.getFEDocEntry() + "]: Error al actualizar U_VS_CDRRSM='" + respuesta + "'. " + ex.getMessage(), ex);
            } finally {
                System.gc();
            }
        }
    }

    private static boolean forceUpdateEstadoDocumento(int docEntry, String tipoDocumento, String estado, String respuesta, ICompany sociedad) throws SBOCOMException {
        Optional<String> op = Optional.ofNullable(respuesta).map(s -> s.isEmpty() ? null : s);
        String campos = (op.isPresent() ? "U_VS_FESTAT='" + estado + "',U_VS_CDRRSM='" + respuesta + "'" : "U_VS_FESTAT='" + estado + "'");
        /** Harol 16-01-2024 Se agrega case 08 de ND*/
        switch (tipoDocumento) {
            case "01":
            case "03":
            case "08":
            case "40":
                String queryOinv = "UPDATE OINV SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOinv);
                String queryOdpi = "UPDATE ODPI SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOdpi);
                break;
            case "07":
                String query = "UPDATE OVPM SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, query);
                String queryOrin = "UPDATE ORIN SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOrin);
                break;
            case "20":
                String queryOvpm = "UPDATE OVPM SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOvpm);
                break;
            case "09":
                String queryOdln = "UPDATE ODLN SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOdln);
                String queryOige = "UPDATE OIGE SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOige);
                String queryOign = "UPDATE OIGN SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOign);
                String queryOwtr = "UPDATE OWTR SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOwtr);
                String queryOrdn = "UPDATE ORDN SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOrdn);
                break;
        }
        return true;
    }

    public static boolean ActualizarEstado(Transaccion tc, String estado, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        return ActualizarEstado(tc.getFEDocEntry(), tc.getFETipoTrans(), tc.getFEObjectType(), estado, tc.getDOCCodigo(), Sociedad, connection);
    }

    public static boolean ActualizarEstado(int docentry, String tipoTrans, String objType, String estado, String docCodigo, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        if(connection != null){
            String tablaName = obtenerTablaObjectType(objType);
            if (tablaName == null) {
                throw new IllegalArgumentException("ObjectType not supported: " + objType);
            }else{
                String updateQuery = "UPDATE "+tablaName+" SET U_VS_FESTAT = '"+estado+"' WHERE \"DocEntry\" = ? AND \"ObjType\" = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, docentry);
                    pstmt.setInt(2, Integer.parseInt(objType));
                    pstmt.executeUpdate();
                } catch (SQLException | NumberFormatException e) {
                    throw new RuntimeException("Error updating document state", e);
                }
            }
            return true;
        }else{
            IDocuments doc;
            IPayments ip;
            try {

                if (docCodigo.equalsIgnoreCase("20")) {
                    System.out.println("Entro a actualizar Estado");
                    ip = SBOCOMUtil.getPayments(Sociedad, Integer.parseInt(objType), docentry);
                    boolean searchDoc = ip.getByKey(docentry);
                    if (searchDoc) {
                        System.out.println("Encontro el documento de Pago con ");
                    } else {
                        System.out.println("Encontro el documento de Pago");
                    }
                    System.out.println("Gey_Payment");
                    if (tipoTrans == null || tipoTrans.isEmpty()) {
                        throw new VenturaExcepcion("El Tipo de transaccion es vacio o nulo");
                    }
                    System.out.println("Tipo");
                    //ip.setCounterReference("Prueba");
                    ip.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                    System.out.println("Estado" + estado + "DocEntry" + docentry + "Objectype" + objType + "TipoTransaccion" + tipoTrans);
                    int ret = ip.update();
                    if (ret != 0) {
                        throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                    }
                } else {
                    doc = SBOCOMUtil.getDocuments(Sociedad, Integer.parseInt(objType), docentry);
                    if (doc == null) {
                        throw new VenturaExcepcion("No se ha encontrado el registro");
                    }

                    if (tipoTrans == null || tipoTrans.isEmpty()) {
                        throw new VenturaExcepcion("El Tipo de transacci?n es vac?o o nulo");
                    }

                    doc.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                    int ret = doc.update();
                    if (ret != 0) {
                        throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                    }
                }
                return true;
            } catch (NumberFormatException | SBOCOMException | VenturaExcepcion ex) {
                throw new VenturaExcepcion("[ObjType=" + objType + ",DocEntry=" + docentry + "]:Error al actualizar ESTADO='" + estado + "'. " + ex.getMessage() + "Tipo de Documento" + docCodigo, ex);
            } finally {
                System.gc();
            }
        }
    }

    private static String obtenerTablaObjectType(String tabla){
        switch (tabla){
            case "13": return "OINV";
            case "203": return "ODPI";
            case "14": return "ORIN";
            case "46": return "OVPM";
            case "15": return "ODLN";
            case "66": return "ORDN";
            case "67": return "OWTR";
            case "60": return "OIGE";
            case "59": return "OIGN";
            case "21": return "ORPD";
            case "20": return "OPDN";
            default: return null;
        }
    }

    public static boolean setActualizarEstadoEnvioSUNAT(Transaccion tc, String estado, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        return setActualizarEstadoEnvioSUNAT(tc.getFEDocEntry(), tc.getFETipoTrans(), tc.getFEObjectType(), estado, tc.getDOCCodigo(), Sociedad, connection);
    }

    public static boolean setActualizarEstadoEnvioSUNAT(int docentry, String tipoTrans, String objType, String estado, String docCodigo, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        IPayments ip;

        try {
            if (docCodigo.equalsIgnoreCase("20")) {
                System.out.println("Entro a actualizar Estado envio SUNAT - RETENCION");

                ip = SBOCOMUtil.getPayments(Sociedad, Integer.parseInt(objType), docentry);
                System.out.println("DocNum" + ip.getDocNum().toString());
                boolean searchDoc = ip.getByKey(docentry);
                System.out.println("Docnum" + ip.getDocNum());
                System.out.println("CardCode" + ip.getCardCode());
                System.out.println("CardName" + ip.getCardName());
                if (searchDoc) {
                    System.out.println("Encontro el documento de Pago con ");
                } else {
                    System.out.println("Encontro el documento de Pago");
                }
                if (ip == null) {
                    throw new VenturaExcepcion("No se ha encontrado el registro");
                }
                System.out.println("Gey_Payment");

                if (tipoTrans == null || tipoTrans.isEmpty()) {
                    throw new VenturaExcepcion("El Tipo de transaccion es vacio o nulo");
                }

                System.out.println("Tipo");
                //ip.setCounterReference("Prueba");
                ip.getUserFields().getFields().item("U_VS_ENVIO_SUNAT").setValue(estado);
                System.out.println("Estado" + estado + "DocEntry" + docentry + "Objectype" + objType + "TipoTransaccion" + tipoTrans);
                int ret = ip.update();
                if (ret != 0) {
                    throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                }

            } else {
                System.out.println("Entro a actualizar Estado envio SUNAT");
                IDocuments doc = Optional.ofNullable(SBOCOMUtil.getDocuments(Sociedad, Integer.parseInt(objType), docentry)).orElseThrow(() -> new SAPNotFoundRegistry(format("No se ha encontrado el registro por el DocEntry [{0}]", docentry)));
                if (doc == null) {
                    throw new VenturaExcepcion("No se ha encontrado el registro");
                }
                if (tipoTrans == null || tipoTrans.isEmpty()) {
                    throw new VenturaExcepcion("El Tipo de transaccion es vacio o nulo");
                }
                doc.getUserFields().getFields().item("U_VS_ENVIO_SUNAT").setValue(estado);
                int ret = doc.update();
                if (ret != 0) {
                    throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                }
            }
            return true;
        } catch (NumberFormatException | SBOCOMException | VenturaExcepcion | SAPNotFoundRegistry ex) {
            if (ex instanceof SAPNotFoundRegistry) {
                try {
                    if (objType.equalsIgnoreCase("07")) {
                        String query = "UPDATE ORIN SET U_VS_ENVIO_SUNAT='" + estado + "' WHERE \"DocEntry\"=" + docentry;
                        runRecordsetQuery(Sociedad, query);
                    } else {
                        String query = "UPDATE OVPM SET U_VS_ENVIO_SUNAT='" + estado + "' WHERE \"DocEntry\"=" + docentry;
                        runRecordsetQuery(Sociedad, query);
                        String queryOinv = "UPDATE OINV SET U_VS_ENVIO_SUNAT='" + estado + "' WHERE \"DocEntry\"=" + docentry;
                        runRecordsetQuery(Sociedad, queryOinv);
                        String queryODpi = "UPDATE ODPI SET U_VS_ENVIO_SUNAT='" + estado + "' WHERE \"DocEntry\"=" + docentry;
                        runRecordsetQuery(Sociedad, queryODpi);
                    }
                } catch (SBOCOMException e) {
                    return false;
                }
                return true;
            } else
                throw new VenturaExcepcion("[ObjType=" + objType + ",DocEntry=" + docentry + "]:Error al actualizar ESTADO='" + estado + "'. " + ex.getMessage() + "Tipo de Documento" + docCodigo, ex);
        } finally {
            System.gc();
        }
    }

    public static Map<String, String> AgregarAnexos(Transaccion tc, byte[] xml, byte[] pdf, byte[] cdr, Date fecha, Boolean borrador, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        if(connection != null){
            return AgregarAnexosJdbc(tc,xml,pdf,cdr,borrador,connection);
        }else{
            return AgregarAnexos(tc, tc.getFETipoTrans(), tc.getSNDocIdentidadNro(), tc.getDocIdentidadNro(), tc.getFEDocEntry(), tc.getDOCSerie() + "_" + tc.getDOCNumero(), tc.getFEObjectType(), xml, pdf, cdr, tc.getDOCCodigo(), tc.getDOCFechaEmision(), borrador, Sociedad);
        }
    }

    public static Map<String, String> AgregarAnexosJdbc(Transaccion tc, byte[] xml, byte[] pdf, byte[] cdr, Boolean pdfBorrador,Connection connection) throws VenturaExcepcion{
        Map<String, String> pathFiles = new HashMap<>();
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(tc.getDOCFechaEmision());
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        String FE_ID = tc.getDOCSerie() + "_" + tc.getDOCNumero();
        String separator = File.separator;
        String RutaFile = Directorio.ADJUNTOS + separator + anio + separator + mes + separator + dia + separator + tc.getDOCCodigo() + separator + tc.getDocIdentidadNro() + separator + tc.getSNDocIdentidadNro();
        String rutaPDF = RutaFile + separator + getNombreDocumento("pdf",FE_ID,pdfBorrador,tc.getFETipoTrans());
        String rutaXML = RutaFile + separator + getNombreDocumento("xml",FE_ID,pdfBorrador,tc.getFETipoTrans());
        String rutaCDR = RutaFile + separator + getNombreDocumento("cdr",FE_ID,pdfBorrador,tc.getFETipoTrans());
        try{
            File f = new File(RutaFile);
            if (!f.exists()) {
                f.mkdirs();
            }
            ArrayList<String> listaUVS = new ArrayList<>();
            Path parentPath = Paths.get(RutaFile);
            if(!tc.getFETipoTrans().equalsIgnoreCase("B") && pdf != null){
                pathFiles.put("rutaPDF", guardarDocumento(parentPath, getNombreDocumento("pdf",FE_ID,pdfBorrador,tc.getFETipoTrans()), pdf));
                String queryPdf = "PDF";
                if(pdfBorrador) queryPdf = "BORRADOR";
                listaUVS.add("U_VS_ANEXO_"+queryPdf+"= '"+rutaPDF+"'");
            }
            if(xml != null){
                pathFiles.put("rutaXML", guardarDocumento(parentPath, getNombreDocumento("xml",FE_ID,pdfBorrador,tc.getFETipoTrans()), xml));
                listaUVS.add("U_VS_ANEXO_XML= '"+rutaXML+"'");
            }
            if(cdr != null){
                pathFiles.put("rutaCDR", guardarDocumento(parentPath, getNombreDocumento("cdr",FE_ID,pdfBorrador,tc.getFETipoTrans()), cdr));
                listaUVS.add("U_VS_ANEXO_CDR= '"+rutaCDR+"'");
            }
            String anexoQuery = "SET";
            Boolean firstDot = true;
            for (String lista : listaUVS) {
                if(firstDot){
                    anexoQuery = anexoQuery +" "+ lista;
                }else{
                    anexoQuery = anexoQuery +" ,"+ lista;
                }
                firstDot = false;
            }
            if(tc.getFETipoTrans().equalsIgnoreCase("B")) anexoQuery = "SET U_VS_ANEXO_BAJA_CDR= '"+rutaCDR+"'";

            String tablaName = obtenerTablaObjectType(tc.getFEObjectType());
            if (tablaName == null) {
                throw new IllegalArgumentException("ObjectType not supported: " + tc.getFEObjectType());
            }else{
                String updateQuery = "UPDATE "+tablaName+" "+anexoQuery+" WHERE \"DocEntry\" = ? AND \"ObjType\" = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, tc.getFEDocEntry());
                    pstmt.setInt(2, Integer.parseInt(tc.getFEObjectType()));
                    pstmt.executeUpdate();
                } catch (SQLException | NumberFormatException e) {
                    throw new RuntimeException("Error updating document state", e);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
            System.out.println(ex.getLocalizedMessage());
            if (ex.getLocalizedMessage().contains("Enter valid folder path") || ex.getMessage().contains("Enter valid folder path")) {
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "No existen permisos para el anexado de los documentos. Se proceder\u00e1 a actualizar el mensaje" + " pero no se podr\u00e1 anexar ning\u00fan documento.");
                return pathFiles;
            } else {
                System.out.println(DocumentoBL.Sociedad.getLastErrorDescription());
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
                }.getClass().getName(), new Object() {
                }.getClass().getEnclosingMethod().getName(), ex.getMessage()});
                throw new VenturaExcepcion("[ObjType=" + tc.getFEObjectType() + ",DocEntry=" + tc.getFEDocEntry() + "]:Error al agregar anexos. " + ex.getMessage(), ex);
            }
        } finally {
            System.gc();
        }
        return pathFiles;
    }

    private static String getNombreDocumento(String tipo,String FE_ID, Boolean pdfBorrador, String tipoTrans){
        String nombre = "";
        switch (tipo){
            case "pdf": nombre = "_FormatoImpreso";if(pdfBorrador)nombre=nombre+"_BORRADOR";break;
            case "xml": nombre = "_XmlFirmado";break;
            case "cdr": nombre = "_SUNAT_CDR";break;
        }
        if(tipoTrans.equalsIgnoreCase("B")){
            nombre = "_Baja";
        }
        return FE_ID+nombre+"."+tipo;
    }

    public static Map<String, String> AgregarAnexos(Transaccion tc, String tipoTransaccion, String rucCliente, String rucEmpresa, int docentry, String FE_Id, String objType, byte[] xml, byte[] pdf, byte[] cdr, String tipoDocumento, Date fechaEmision, Boolean borrador, ICompany Sociedad) throws VenturaExcepcion {
        Map<String, String> pathFiles = new HashMap<>();
        IPayments docPayment;
        IDocuments doc = null;
        List<String> archivos = new ArrayList<>();
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(fechaEmision);
        int anio = fecha.get(Calendar.YEAR);
        int mes = fecha.get(Calendar.MONTH) + 1;
        int dia = fecha.get(Calendar.DAY_OF_MONTH);
        boolean anexoPdf = true;

        try {
            if (!Sociedad.isInTransaction()) {
                Sociedad.startTransaction();
            }

            String separator = File.separator;
            if (!tipoDocumento.equalsIgnoreCase("20")) {
                /*Si se cambia la ruta tambien se debe de cambiar en el metodo actualizar del responseBL Actualizar*/
                /*Variable donde esta la vaiable de ubicacion de los archivos*/
                String RutaFile = Directorio.ADJUNTOS + separator + anio + separator + mes + separator + dia + separator + tipoDocumento + separator + rucEmpresa + separator + rucCliente;
                /* Variable de los nombers de los archivos */

                String nombPDF = "";
                String nombXML = "";
                String nombCDR = "";
                String nombBorrador = "";

                String rutaPDF = "";
                String rutaXML = "";
                String rutaCDR = "";
                String rutaBorrador = "";

                LoggerTrans.getCDThreadLogger().log(Level.INFO, "Tipo de Documento : {0}", tipoDocumento);

                File f = new File(RutaFile);
                if (!f.exists()) {
                    f.mkdirs();
                }
                try {
                    doc = SBOCOMUtil.getDocuments(Sociedad, Integer.parseInt(objType), docentry);
                } catch (Exception e) {
                    System.out.println("Hubo un problema con el DocEntry");
                }

                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Objeto :{0} --- DocEntry :{1}", new Object[]{objType, docentry});
                if (doc == null) {
                    if (Sociedad.isInTransaction()) {
                        Sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                    }
                    if (borrador) {
                        nombPDF = FE_Id + "_FormatoImpreso_BORRADOR";
                        nombXML = FE_Id + "_XmlFirmado_BORRADOR";
                        nombCDR = FE_Id + "_SUNAT_CDR_BORRADOR";
                    } else {
                        nombPDF = FE_Id + "_FormatoImpreso";
                        nombXML = FE_Id + "_XmlFirmado";
                        nombCDR = FE_Id + "_SUNAT_CDR";
                    }
                    if (tipoTransaccion.compareTo("RDB") == 0) {
                        nombPDF = FE_Id + "_XmlFirmado" + "_RDB";
                        nombXML = FE_Id + "_XmlFirmado" + "_RDB";
                        nombCDR = FE_Id + "_SUNAT_CDR" + "_RDB";
                    }

                    if (pdf != null) {
                        rutaPDF = RutaFile + separator + nombPDF + ".pdf";
                        Path parentPath = Paths.get(RutaFile);
                        pathFiles.put("rutaPDF", guardarDocumento(parentPath, nombPDF + ".pdf", pdf));
                    }
                    if (xml != null) {
                        rutaXML = RutaFile + separator + nombXML + ".xml";
                        Path parentPath = Paths.get(RutaFile);
                        pathFiles.put("rutaXML", guardarDocumento(parentPath, nombXML + ".xml", xml));
                    }
                    if (cdr != null) {
                        rutaCDR = RutaFile + separator + nombCDR + ".zip";
                        Path parentPath = Paths.get(RutaFile);
                        pathFiles.put("rutaCDR", guardarDocumento(parentPath, nombCDR + ".zip", cdr));
                    }
                    updateUserFields(docentry, tipoDocumento, tipoTransaccion, rutaPDF, rutaXML, rutaCDR, rutaPDF, borrador, Sociedad);
                    Map<String, String> stringMap = AgregarAnexos(RutaFile, tipoTransaccion, FE_Id, xml, pdf, cdr, tipoDocumento, borrador);
                    if (stringMap.isEmpty())
                        return pathFiles;
                    else {
                        return stringMap;
                    }
                }
                int idAtt;
                try {
                    idAtt = doc.getAttachmentEntry();
                } catch (Exception e) {
                    idAtt = 0;
                }
                Attachments2 att2;
                if (idAtt != 0) {
                    att2 = (Attachments2) SBOCOMUtil.getAttachments2(Sociedad, idAtt);
                    Optional<Attachments2> optional = Optional.ofNullable(att2);
                    if (!optional.isPresent()) {
                        att2 = (Attachments2) SBOCOMUtil.newAttachments2(Sociedad);
                        idAtt = 0;
                    }
                } else {
                    att2 = (Attachments2) SBOCOMUtil.newAttachments2(Sociedad);
                }
                if (tipoTransaccion.compareTo("RDB") == 0) {
                    nombPDF = FE_Id + "_XmlFirmado" + "_RDB";
                    nombXML = FE_Id + "_XmlFirmado" + "_RDB";
                    nombCDR = FE_Id + "_SUNAT_CDR" + "_RDB";
                    /*
                    if (pdf != null) {
                        rutaPDF = RutaFile + File.separator + nombPDF + ".pdf";
                        converByteToFile(pdf, rutaPDF);
                        AddAttachments(Integer.parseInt(objType), docentry, rutaPDF, Sociedad);
                        pathFiles.put("rutaPDF", rutaPDF);
                    }
                    if (xml != null) {
                        rutaXML = RutaFile + File.separator + nombXML + ".xml";
                        converByteToFile(xml, rutaXML);
                        AddAttachments(Integer.parseInt(objType), docentry, rutaXML, Sociedad);
                        pathFiles.put("rutaXML", rutaXML);
                    }
                    if (cdr != null) {
                        rutaCDR = RutaFile + File.separator + nombCDR + ".zip";
                        converByteToFile(cdr, rutaCDR);
                        AddAttachments(Integer.parseInt(objType), docentry, rutaCDR, Sociedad);
                        pathFiles.put("rutaCDR", rutaCDR);
                    }*/
                    if (pdf != null) {
                        rutaPDF = RutaFile + separator + nombPDF + ".pdf";
                        /*converByteToFile(pdf, rutaPDF);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaPDF);
                         */
                        if(att2 != null){
                            archivos.add(AgregarAnexo(att2, pdf, nombPDF, "pdf", RutaFile));
                        }
                        pathFiles.put("rutaPDF", rutaPDF);
                    }
                    if (xml != null) {
                        rutaXML = RutaFile + separator + nombXML + ".xml";
                        /*
                            converByteToFile(xml, rutaXML);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaXML);
                         */
                        if(att2 != null){
                            archivos.add(AgregarAnexo(att2, xml, nombXML, "xml", RutaFile));
                        }
                        pathFiles.put("rutaXML", rutaXML);
                    }
                    if (cdr != null) {
                        rutaCDR = RutaFile + separator + nombCDR + ".zip";
                        /*converByteToFile(cdr, rutaCDR);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaCDR);*/
                        if(att2 != null) {
                            archivos.add(AgregarAnexo(att2, cdr, nombCDR, "zip", RutaFile));
                        }
                        pathFiles.put("rutaCDR", rutaCDR);
                    }

                } else {
                    if (tipoTransaccion.compareTo("B") == 0) {
                        nombPDF = FE_Id + "_Baja";
                        nombXML = FE_Id + "_Baja";
                        nombCDR = FE_Id + "_Baja";
                        /*
                        if (pdf != null) {
                            rutaPDF = RutaFile + File.separator + nombPDF + ".pdf";
                            converByteToFile(pdf, rutaPDF);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaPDF, Sociedad);
                            pathFiles.put("rutaPDF", rutaPDF);
                        }
                        if (xml != null) {
                            rutaXML = RutaFile + File.separator + nombXML + ".xml";
                            converByteToFile(xml, rutaXML);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaXML, Sociedad);
                            pathFiles.put("rutaXML", rutaXML);
                        }
                        if (cdr != null) {
                            rutaCDR = RutaFile + File.separator + nombCDR + ".zip";
                            converByteToFile(cdr, rutaCDR);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaCDR, Sociedad);
                            pathFiles.put("rutaCDR", rutaCDR);
                        }*/
                        if (pdf != null) {
                            rutaPDF = RutaFile + separator + nombPDF + ".pdf";
                            /*converByteToFile(pdf, rutaPDF);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaPDF);
                             */
                            if(att2 != null) {
                                archivos.add(AgregarAnexo(att2, pdf, nombPDF, "pdf", RutaFile));
                            }
                            pathFiles.put("rutaPDF", rutaPDF);
                        }
                        if (xml != null) {
                            rutaXML = RutaFile + separator + nombXML + ".xml";
                            /*
                            converByteToFile(xml, rutaXML);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaXML);
                             */
                            if(att2 != null) {
                                archivos.add(AgregarAnexo(att2, xml, nombXML, "xml", RutaFile));
                            }
                            pathFiles.put("rutaXML", rutaXML);
                        }
                        if (cdr != null) {
                            rutaCDR = RutaFile + separator + nombCDR + ".zip";
                            /*converByteToFile(cdr, rutaCDR);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaCDR);*/
                            if(att2 != null) {
                                archivos.add(AgregarAnexo(att2, cdr, nombCDR, "zip", RutaFile));
                            }
                            pathFiles.put("rutaCDR", rutaCDR);
                        }

                    } else {

                        if (borrador) {

                            nombPDF = FE_Id + "_FormatoImpreso_BORRADOR";
                            nombXML = FE_Id + "_XmlFirmado_BORRADOR";
                            nombCDR = FE_Id + "_SUNAT_CDR_BORRADOR";

                        } else {

                            nombPDF = FE_Id + "_FormatoImpreso";
                            nombXML = FE_Id + "_XmlFirmado";
                            nombCDR = FE_Id + "_SUNAT_CDR";

                        }

                        if (pdf != null) {

                            rutaPDF = RutaFile + separator + nombPDF + ".pdf";
                            /*converByteToFile(pdf, rutaPDF);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaPDF);
                             */
                            if(att2 != null){
                                archivos.add(AgregarAnexo(att2, pdf, nombPDF, "pdf", RutaFile));
                            }
                            pathFiles.put("rutaPDF", rutaPDF);
                        }
                        if (xml != null) {

                            rutaXML = RutaFile + separator + nombXML + ".xml";
                            /*
                            converByteToFile(xml, rutaXML);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaXML);
                             */
                            if(att2 != null) {
                                archivos.add(AgregarAnexo(att2, xml, nombXML, "xml", RutaFile));
                            }
                            pathFiles.put("rutaXML", rutaXML);
                        }
                        if (cdr != null) {
                            rutaCDR = RutaFile + separator + nombCDR + ".zip";
                            /*converByteToFile(cdr, rutaCDR);
                            AddAttachments(Integer.parseInt(objType), docentry, rutaCDR);*/
                            if(att2 != null) {
                                archivos.add(AgregarAnexo(att2, cdr, nombCDR, "zip", RutaFile));
                            }
                            pathFiles.put("rutaCDR", rutaCDR);
                        }
                    }
                }
                ICompanyService com_service = Sociedad.getCompanyService();
                IPathAdmin oPathAdmin = com_service.getPathAdmin();
                String attachmentsFolderPath = oPathAdmin.getAttachmentsFolderPath();
                oPathAdmin.setAttachmentsFolderPath(RutaFile);
                com_service.updatePathAdmin(oPathAdmin);

                int ret1 = 0;
                //idAtt = 0; // PARA PRUEBAS UNICAMENTE. ELIMINAR ESTA LINEA LUEGO !!!
                String entry = "";
                if (att2.getLines().getCount() > 0) {
                    att2.getLines().setCurrentLine(0);
                    if (att2.getLines().getFileName() != null && !att2.getLines().getFileName().isEmpty()) {
                        if (idAtt == 0) {
                            if ((ret1 = att2.add()) == 0) {
                                entry = Sociedad.getNewObjectKey();
                            }
                        } else {
                            ret1 = att2.update();
                        }
                    }
                }

                if (ret1 != 0) {
                    //throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                }
                int ret = 0;
                //System.out.println("Nueva Clave: " + entry);
                if (entry != null && !entry.isEmpty()) {
                    try {
                        doc.setAttachmentEntry(Integer.parseInt(entry));
                        ret = doc.update();
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                        oPathAdmin.setAttachmentsFolderPath(attachmentsFolderPath);
                        com_service.updatePathAdmin(oPathAdmin);
                        updateUserFields(docentry, tipoDocumento, tipoTransaccion, rutaPDF, rutaXML, rutaCDR, rutaPDF, borrador, Sociedad);
                        Map<String, String> stringMap = AgregarAnexos(RutaFile, tipoTransaccion, FE_Id, xml, pdf, cdr, tipoDocumento, borrador);
                        if (stringMap.isEmpty())
                            return pathFiles;
                        else {
                            return stringMap;
                        }
                    }
                }
                /** Harol 16-01-2024 Anexado campo de usuario*/
                updateUserFields(docentry, tipoDocumento, tipoTransaccion, rutaPDF, rutaXML, rutaCDR, rutaPDF, borrador, Sociedad);
                /** */
                if (ret != 0) {
                    oPathAdmin.setAttachmentsFolderPath(attachmentsFolderPath);
                    com_service.updatePathAdmin(oPathAdmin);
                    //throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                }
                if (Sociedad.isInTransaction()) {
                    Sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                }
                oPathAdmin.setAttachmentsFolderPath(attachmentsFolderPath);
                com_service.updatePathAdmin(oPathAdmin);
            } else {
                String RutaFile = Directorio.ADJUNTOS + separator + anio + separator + mes + separator + dia + separator + tipoDocumento + separator + rucCliente;
                /* Variable de los nombers de los archivos */
                String nombPDF = "";
                String nombXML = "";
                String nombCDR = "";
                if (tipoTransaccion.compareTo("B") == 0) {
                    nombPDF = FE_Id + "_Baja";
                    nombXML = FE_Id + "_Baja";
                    nombCDR = FE_Id + "_Baja";
                } else {
                    if (borrador) {
                        nombPDF = FE_Id + "_FormatoImpreso_BORRADOR";
                        nombXML = FE_Id + "_XmlFirmado_BORRADOR";
                        nombCDR = FE_Id + "_SUNAT_CDR_BORRADOR";
                    } else {
                        nombPDF = FE_Id + "_FormatoImpreso";
                        nombXML = FE_Id + "_XmlFirmado";
                        nombCDR = FE_Id + "_SUNAT_CDR";
                    }
                }
                String FPDF = "";
                String FXML = "";
                String FCDR = "";
                if (pdf != null) {
                    FPDF = AgregarAnexoLink(pdf, nombPDF, "pdf", RutaFile);
                    pathFiles.put("rutaPDF", RutaFile + separator + nombPDF + ".pdf");
                }
                if (xml != null) {
                    FXML = AgregarAnexoLink(xml, nombXML, "xml", RutaFile);
                    pathFiles.put("rutaXML", RutaFile + separator + nombXML + ".xml");
                }
                if (cdr != null) {
                    FCDR = AgregarAnexoLink(cdr, nombCDR, "zip", RutaFile);
                    pathFiles.put("rutaCDR", RutaFile + separator + nombCDR + ".zip");
                }

                docPayment = SBOCOMUtil.getPayments(Sociedad, Integer.parseInt(objType), docentry);
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
                                anexoPdf = false;
                                DocumentoINF.Conector.Desconectar();
                                Loader.CargarParametroErp();
                                DocumentoINF.Conector.Conectar();
                            }
                        }
                    }
                    int ret = docPayment.update();
                    if (ret == 0) {
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "Se anexo correctamente los file");
                    } else {
                        LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Se encontro un incidente al agregar los anexos {0}", Sociedad.getLastErrorDescription());
                    }
                } else {
                    if (Sociedad.isInTransaction()) {
                        Sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                    }
                    return AgregarAnexos(RutaFile, tipoTransaccion, FE_Id, xml, pdf, cdr, tipoDocumento, borrador);
                }
                if (Sociedad.isInTransaction()) {
                    Sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
                }
            }
            if (!anexoPdf) {
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "No se pudo anexar el documento pdf porque está abierto desde otro proceso.");
            }
            return pathFiles;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (Sociedad.isInTransaction()) {
                Sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
            }
            System.out.println(ex.getMessage());
            System.out.println(ex.getLocalizedMessage());
            if (ex.getLocalizedMessage().contains("Enter valid folder path") || ex.getMessage().contains("Enter valid folder path")) {
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "No existen permisos para el anexado de los documentos. Se proceder\u00e1 a actualizar el mensaje" + " pero no se podr\u00e1 anexar ning\u00fan documento.");
                return pathFiles;
            } else {
                System.out.println(DocumentoBL.Sociedad.getLastErrorDescription());
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
                }.getClass().getName(), new Object() {
                }.getClass().getEnclosingMethod().getName(), ex.getMessage()});
                throw new VenturaExcepcion("[ObjType=" + objType + ",DocEntry=" + docentry + "]:Error al agregar anexos. " + ex.getMessage(), ex);
            }
        } finally {
            if (Sociedad.isInTransaction()) {
                Sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
            }
            System.gc();
        }
    }

    private static String guardarDocumento(Path parent, String filename, byte[] bytes) throws IOException {
        if (!Files.exists(parent)) {
            Files.createDirectories(parent);
        }
        Path archivoPath = parent.resolve(filename);
        Files.write(archivoPath, bytes);
        return archivoPath.toString();
    }

    private static void updateUserFields(int docEntry, String tipoDocumento, String tipoTransaccion, String pdfPath, String xmlPath, String cdrPath, String borradorPath, boolean borrador, ICompany sociedad) throws SBOCOMException {
        /** Harol 16-01-2024 Se agrega case 08 de ND*/
        if (borrador) {
            switch (tipoDocumento) {
                case "01":
                case "03":
                case "08":
                case "40":
                    String queryOinv = "UPDATE OINV SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOinv);
                    String queryOdpi = "UPDATE ODPI SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOdpi);
                    break;
                case "07":
                    String query = "UPDATE OVPM SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, query);
                    String queryOrin = "UPDATE ORIN SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOrin);
                    break;
                case "09":
                    String queryOdln = "UPDATE ODLN SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOdln);
                    String queryOige = "UPDATE OIGE SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOige);
                    String queryOign = "UPDATE OIGN SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOign);
                    String queryOwtr = "UPDATE OWTR SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOwtr);
                    String queryOrdn = "UPDATE ORDN SET \"U_VS_ANEXO_BORRADOR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOrdn);
                    break;
            }
        } else if ("B".equalsIgnoreCase(tipoTransaccion)) {
            switch (tipoDocumento) {
                case "01":
                case "03":
                case "08":
                case "40":
                    String queryOinv = "UPDATE OINV SET \"U_VS_ANEXO_BAJA_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOinv);
                    String queryOdpi = "UPDATE ODPI SET \"U_VS_ANEXO_BAJA_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOdpi);
                    break;
                case "07":
                    String query = "UPDATE OVPM SET \"U_VS_ANEXO_BAJA_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, query);
                    String queryOrin = "UPDATE ORIN SET \"U_VS_ANEXO_BAJA_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOrin);
                    break;
                case "09":
                    String queryOdln = "UPDATE ODLN SET \"U_VS_ANEXO_BAJA_CDR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOdln);
                    String queryOige = "UPDATE OIGE SET \"U_VS_ANEXO_BAJA_CDR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOige);
                    String queryOign = "UPDATE OIGN SET \"U_VS_ANEXO_BAJA_CDR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOign);
                    String queryOwtr = "UPDATE OWTR SET \"U_VS_ANEXO_BAJA_CDR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOwtr);
                    String queryOrdn = "UPDATE ORDN SET \"U_VS_ANEXO_BAJA_CDR\"='" + borradorPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOrdn);
                    break;
            }
        } else {
            switch (tipoDocumento) {
                case "01":
                case "03":
                case "08":
                case "40":
                    String queryOinv = "UPDATE OINV SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOinv);
                    String queryOdpi = "UPDATE ODPI SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOdpi);
                    break;
                case "07":
                    String query = "UPDATE OVPM SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, query);
                    String queryOrin = "UPDATE ORIN SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOrin);
                    break;
                case "09":
                    String queryOdln = "UPDATE ODLN SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOdln);
                    String queryOige = "UPDATE OIGE SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOige);
                    String queryOign = "UPDATE OIGN SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOign);
                    String queryOwtr = "UPDATE OWTR SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOwtr);
                    String queryOrdn = "UPDATE ORDN SET \"U_VS_ANEXO_PDF\"='" + pdfPath + "',\"U_VS_ANEXO_XML\"='" + xmlPath + "',\"U_VS_ANEXO_CDR\"='" + cdrPath + "' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOrdn);
                    break;
            }
        }
    }


        public static Map<String, String> AgregarAnexos(String rutaDocumento, String TipoTransaccion, String FE_Id, byte[] xml, byte[] pdf, byte[] cdr, String tipoDocumento, Boolean borrador) {
        Map<String, String> pathFiles = new HashMap<>();
        try {
            String separator = File.separator;
            if (!tipoDocumento.equalsIgnoreCase("20")) {
                /*Si se cambia la ruta tambien se debe de cambiar en el metodo actualizar del responseBL Actualizar*/
                /*Variable donde esta la vaiable de ubicacion de los archivos*/
                /* Variable de los nombers de los archivos */
                String nombPDF = "";
                String nombXML = "";
                String nombCDR = "";

                String rutaPDF = "";
                String rutaXML = "";
                String rutaCDR = "";
                File f = new File(rutaDocumento);
                if (!f.exists()) {
                    f.mkdirs();
                }
                if (TipoTransaccion.compareTo("RDB") == 0) {
                    nombPDF = FE_Id + "_XmlFirmado" + "_RDB";
                    nombXML = FE_Id + "_XmlFirmado" + "_RDB";
                    nombCDR = FE_Id + "_SUNAT_CDR" + "_RDB";
                    if (pdf != null) {
                        rutaPDF = rutaDocumento + separator + nombPDF + ".pdf";
                        Path path = Paths.get(rutaDocumento, nombPDF + ".pdf");
                        Files.write(path, pdf, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                        pathFiles.put("rutaPDF", rutaPDF);
                    }
                    if (xml != null) {
                        rutaXML = rutaDocumento + separator + nombXML + ".xml";
                        Path path = Paths.get(rutaDocumento, nombXML + ".xml");
                        Files.write(path, xml, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                        pathFiles.put("rutaXML", rutaXML);
                    }
                    if (cdr != null) {
                        rutaCDR = rutaDocumento + separator + nombCDR + ".zip";
                        Path path = Paths.get(rutaDocumento, nombCDR + ".zip");
                        Files.write(path, cdr, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                        pathFiles.put("rutaCDR", rutaCDR);
                    }
                } else {
                    if (TipoTransaccion.compareTo("B") == 0) {
                        nombPDF = FE_Id + "_Baja";
                        nombXML = FE_Id + "_Baja";
                        nombCDR = FE_Id + "_Baja";
                        if (pdf != null) {
                            rutaPDF = rutaDocumento + separator + nombPDF + ".pdf";
                            Path path = Paths.get(rutaDocumento, nombPDF + ".pdf");
                            Files.write(path, pdf, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                            pathFiles.put("rutaPDF", rutaPDF);
                        }
                        if (xml != null) {
                            rutaXML = rutaDocumento + separator + nombXML + ".xml";
                            Path path = Paths.get(rutaDocumento, nombXML + ".xml");
                            Files.write(path, xml, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                            pathFiles.put("rutaXML", rutaXML);
                        }
                        if (cdr != null) {
                            rutaCDR = rutaDocumento + separator + nombCDR + ".zip";
                            Path path = Paths.get(rutaDocumento, nombCDR + ".zip");
                            Files.write(path, cdr, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                            pathFiles.put("rutaCDR", rutaCDR);
                        }
                    } else {
                        if (borrador) {
                            nombPDF = FE_Id + "_FormatoImpreso_BORRADOR";
                            nombXML = FE_Id + "_XmlFirmado_BORRADOR";
                            nombCDR = FE_Id + "_SUNAT_CDR_BORRADOR";
                        } else {
                            nombPDF = FE_Id + "_FormatoImpreso";
                            nombXML = FE_Id + "_XmlFirmado";
                            nombCDR = FE_Id + "_SUNAT_CDR";
                        }
                        if (pdf != null) {
                            rutaPDF = rutaDocumento + separator + nombPDF + ".pdf";
                            Path path = Paths.get(rutaDocumento, nombPDF + ".pdf");
                            Files.write(path, pdf, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                            pathFiles.put("rutaPDF", rutaPDF);
                        }
                        if (xml != null) {
                            rutaXML = rutaDocumento + separator + nombXML + ".xml";
                            Path path = Paths.get(rutaDocumento, nombXML + ".xml");
                            Files.write(path, xml, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                            pathFiles.put("rutaXML", rutaXML);
                        }
                        if (cdr != null) {
                            rutaCDR = rutaDocumento + separator + nombCDR + ".zip";
                            Path path = Paths.get(rutaDocumento, nombCDR + ".zip");
                            Files.write(path, cdr, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
                            pathFiles.put("rutaCDR", rutaCDR);
                        }
                    }
                }
            } else {
                /* Variable de los nombers de los archivos */
                String nombPDF = "";
                String nombXML = "";
                String nombCDR = "";
                if (TipoTransaccion.compareTo("B") == 0) {
                    nombPDF = FE_Id + "_Baja";
                    nombXML = FE_Id + "_Baja";
                    nombCDR = FE_Id + "_Baja";
                } else {
                    if (borrador) {
                        nombPDF = FE_Id + "_FormatoImpreso_BORRADOR";
                        nombXML = FE_Id + "_XmlFirmado_BORRADOR";
                        nombCDR = FE_Id + "_SUNAT_CDR_BORRADOR";
                    } else {
                        nombPDF = FE_Id + "_FormatoImpreso";
                        nombXML = FE_Id + "_XmlFirmado";
                        nombCDR = FE_Id + "_SUNAT_CDR";
                    }
                }
                String FPDF = "";
                String FXML = "";
                String FCDR = "";
                if (pdf != null) {
                    FPDF = AgregarAnexoLink(pdf, nombPDF, "pdf", rutaDocumento);
                    System.out.println(FPDF);
                    pathFiles.put("rutaPDF", rutaDocumento + separator + nombPDF + ".pdf");
                }
                if (xml != null) {
                    FXML = AgregarAnexoLink(xml, nombXML, "xml", rutaDocumento);
                    System.out.println(FXML);
                    pathFiles.put("rutaXML", rutaDocumento + separator + nombXML + ".xml");
                }
                if (cdr != null) {
                    FCDR = AgregarAnexoLink(cdr, nombCDR, "zip", rutaDocumento);
                    System.out.println(FCDR);
                    pathFiles.put("rutaCDR", rutaDocumento + separator + nombCDR + ".zip");
                }
            }
            return pathFiles;
        } catch (Exception ex) {
            System.out.println(ex.getLocalizedMessage());
        } finally {
            System.gc();
        }
        return new HashMap<>();
    }

    private static void AddAttachments(int objType, int docEntry, String Xml, ICompany Sociedad) {
        File fileXML = new File(Xml);
        IDocuments doc;
        IAttachments2 oAttachments = null;
        try {
            if (!Sociedad.isInTransaction()) {
                Sociedad.startTransaction();
            }
            doc = SBOCOMUtil.getDocuments(Sociedad, objType, docEntry);
            if (doc != null) {
                String FileExtension = getFileExtension(fileXML);
                System.out.println("FileExtension : " + FileExtension);
                String FileName = getFileName(fileXML).replace("." + getFileExtension(fileXML), "");
                System.out.println("FileName : " + FileName);
                String FilePath = fileXML.getAbsolutePath().replace(getFileName(fileXML), "");
                System.out.println("FilePath : " + FilePath);
                int docAttachEntry = doc.getAttachmentEntry();
                if (docAttachEntry != 0) {
                    oAttachments = SBOCOMUtil.getAttachments2(Sociedad, doc.getAttachmentEntry());
                    boolean verificate = verificateFile(oAttachments, FileName, FileExtension);
                    if (!verificate) {
                        oAttachments.getLines().add();
                        oAttachments.getLines().setFileExtension(FileExtension);
                        oAttachments.getLines().setFileName(FileName);
                        oAttachments.getLines().setSourcePath(FilePath);
                        oAttachments.getLines().setOverride(1);
                        int operation = oAttachments.update();
                        if (operation != 0) {
                            errMsg = Sociedad.getLastError();
                            System.out.println("I cannot connect to database server: " + errMsg.getErrorMessage() + " " + errMsg.getErrorCode());
                        } else {
                            ICompanyService com_service = Sociedad.getCompanyService();
                            IPathAdmin oPathAdmin = com_service.getPathAdmin();
                            String defaultAttach = oPathAdmin.getAttachmentsFolderPath();
                            oPathAdmin.setAttachmentsFolderPath(FilePath);
                            com_service.updatePathAdmin(oPathAdmin);
                            int entry = oAttachments.getAbsoluteEntry();
                            doc.setAttachmentEntry(entry);
                            int update = doc.update();
                            if (update != 0) {
                                errMsg = Sociedad.getLastError();
                                System.out.println("I cannot connect to database server: " + errMsg.getErrorMessage() + " " + errMsg.getErrorCode());
                            } else {
                                System.out.println("Added Attachment.");
                            }
                            oPathAdmin.setAttachmentsFolderPath(defaultAttach);
                            com_service.updatePathAdmin(oPathAdmin);
                        }
                    } else {
                        System.out.println("The attachment is already.");
                    }
                } else {
                    oAttachments = SBOCOMUtil.newAttachments2(Sociedad);
                    oAttachments.getLines().add();
                    oAttachments.getLines().setFileExtension(FileExtension);
                    oAttachments.getLines().setFileName(FileName);
                    oAttachments.getLines().setSourcePath(FilePath);
                    oAttachments.getLines().setOverride(1);
                    int operation = oAttachments.add();
                    if (operation != 0) {
                        errMsg = Sociedad.getLastError();
                        System.out.println("I cannot connect to database server: " + errMsg.getErrorMessage() + " " + errMsg.getErrorCode());
                    } else {

                        ICompanyService com_service = Sociedad.getCompanyService();
                        IPathAdmin oPathAdmin = com_service.getPathAdmin();
                        String defaultAttach = oPathAdmin.getAttachmentsFolderPath();
                        oPathAdmin.setAttachmentsFolderPath(FilePath);

                        com_service.updatePathAdmin(oPathAdmin);
                        String entry = Sociedad.getNewObjectKey();
                        doc.setAttachmentEntry(Integer.parseInt(entry));
                        int update = doc.update();
                        if (update != 0) {
                            errMsg = Sociedad.getLastError();
                            System.out.println("I cannot connect to database server: " + errMsg.getErrorMessage() + " " + errMsg.getErrorCode());
                        } else {
                            System.out.println("Added Attachment.");
                        }

                        oPathAdmin.setAttachmentsFolderPath(defaultAttach);

                        com_service.updatePathAdmin(oPathAdmin);
                    }
                }
            } else {
                System.out.println("The document was not found.");
            }
            if (Sociedad.isInTransaction()) {
                Sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
            }
        } catch (Exception ex) {
            if (Sociedad.isInTransaction()) {
                Sociedad.endTransaction(SBOCOMConstants.BoWfTransOpt_wf_Commit);
            }
            System.out.println("The following ERROR was found : " + ex.getMessage());
        }
    }

    private static String getFileExtension(File file) {
        try {
            String fileName = file.getName();
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                String FileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
                return FileExtension;
            } else {
                return "";
            }
        } catch (Exception ex) {
            System.out.println("The following ERROR was found : " + ex.getMessage());
            return "";
        }
    }

    private static boolean verificateFile(IAttachments2 oAttachments, String fileName, String fileExtension) {
        for (int i = 0; i < oAttachments.getLines().getCount(); i++) {
            oAttachments.getLines().setCurrentLine(i);
            System.out.println("Se encontro el siguiente archivo : " + oAttachments.getLines().getFileName());
            if (oAttachments.getLines().getFileName().equalsIgnoreCase(fileName) && oAttachments.getLines().getFileExtension().equalsIgnoreCase(fileExtension)) {
                return true;
            }
        }
        return false;
    }

    private static String getFileName(File file) {
        try {
            String FileName = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("\\") + 1);
            return FileName;
        } catch (Exception ex) {
            System.out.println("The following ERROR was found : " + ex.getMessage());
            return "";
        }
    }

    private static void converByteToFile(byte[] bytes, String File) {
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        baos1.write(bytes, 0, bytes.length);
        File f = new File(File);

        try (FileOutputStream fos1 = new FileOutputStream(f)) {
            baos1.writeTo(fos1);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DocumentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DocumentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String AgregarAnexoLink(byte[] archivo, String nombre, String ext, String ruta) throws IOException {
        String fullname = ruta + "\\" + nombre + "." + ext;
        File f2 = new File(ruta);
        if (!f2.exists()) {
            f2.mkdirs();
        }
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        baos1.write(archivo, 0, archivo.length);
        File f = new File(fullname);

        try (FileOutputStream fos1 = new FileOutputStream(f)) {
            baos1.writeTo(fos1);
        }

        return fullname;
    }

    private static String AgregarAnexo(Attachments2 att2, byte[] archivo, String nombre, String ext, String ruta) throws IOException {
        IAttachments2_Lines attl = att2.getLines();
        int count = attl.getCount();

        String fullname = "";
        fullname = ruta + File.separator + nombre + "." + ext;

        File f2 = new File(ruta);
        if (!f2.exists()) {
            f2.mkdirs();
        }

        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        baos1.write(archivo, 0, archivo.length);
        File f = new File(fullname);


        if(f.exists()){
            if(fullname.contains(".pdf")){
                try (FileOutputStream fos1 = new FileOutputStream(f)) {
                    baos1.writeTo(fos1);
                }
            }
        }else{
            try (FileOutputStream fos1 = new FileOutputStream(f)) {
                baos1.writeTo(fos1);
            }
        }

        for (int i = 0; i < count; i++) {
            attl.setCurrentLine(i);
            if (attl.getFileName().compareTo(nombre) == 0 && attl.getFileExtension().compareTo(ext) == 0) {
                return "";
            }
        }

        /**
         * ******************************************************************
         */
        if (att2.getLines().getSourcePath().equalsIgnoreCase("")) {
            //System.out.println("No se encontro archivo");
        } else {
            att2.getLines().add();
        }

        att2.getLines().setFileExtension(ext);
        att2.getLines().setFileName(nombre);
        att2.getLines().setOverride(1);
        att2.getLines().setSourcePath(ruta);



        return fullname;
    }

    private static void EliminarTemporales(List<String> archivos) {
        for (String fullPath : archivos) {
            if (fullPath == null || fullPath.isEmpty()) {
                return;
            }
            File F = new File(fullPath);
            if (F.exists()) {
                F.delete();
            }
        }
    }

    private static void crearCamposUsuario(IRecordset rs) throws VenturaExcepcion {
        try {
            //Si existen campos que inician con U_
            IFields campos = rs.getFields();
            int cantidad = campos.getCount();
            for (int i = 0; i < cantidad; i++) {
                IField icampo = campos.item(i);
                String ncampo = icampo.getName();
                if (ncampo.startsWith("U_")) {
                    ncampo = ncampo.substring(2);
                    if (UsuariocamposJC.getIdByNombre(ncampo) == 0) {
                        Usuariocampos uc = new Usuariocampos();
                        uc.setNombre(ncampo);
                        try {
                            UsuariocamposJC.create(uc);
                        } catch (Exception ex) {
                            throw new VenturaExcepcion("No fue posible crear el campo: " + ncampo + ". " + ex.getMessage());
                        }
                    }
                }
            }
            //Propiedades de configuracion
            for (TransaccionUsucampos tuc : Transaccion.getPropiedades()) {
                String ncampo = tuc.getUsuariocampos().getNombre();
                if (UsuariocamposJC.getIdByNombre(ncampo) == 0) {
                    try {
                        UsuariocamposJC.create(tuc.getUsuariocampos());
                    } catch (Exception ex) {
                        throw new VenturaExcepcion("No fue posible crear el campo: " + ncampo + ". " + ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new VenturaExcepcion(ex.getMessage());
        }
    }

    private static TransaccionResumen construir_Reflection_Resumen(IRecordset rs) throws SBOCOMException {
        TransaccionResumen doc = new TransaccionResumen();
        Class clase = doc.getClass();
        for (int i = 0; i < rs.getFields().getCount(); i++) {
            IField icampo = rs.getFields().item(i);
            String ncampo = icampo.getName();
            Integer tcampo = icampo.getType();
            Object objeto = null;
            Class tipocampo = null;
            switch (tcampo) {
                case 0:
                case 1:  //Alpha,Memo
                    tipocampo = String.class;
                    objeto = icampo.getValueString();
                    break;
                case 2:  //Numeric
                    tipocampo = Integer.class;
                    objeto = icampo.getValueInteger();
                    break;
                case 3:  //Date
                    tipocampo = Date.class;
                    objeto = icampo.getValueDate();
                    break;
                case 4: //Float
                    tipocampo = BigDecimal.class;
                    objeto = BigDecimal.valueOf(icampo.getValueDouble());
                    break;
            }
            if (!ncampo.startsWith("U_")) {
                ncampo = ncampo.replace("_", "");
                try {
                    Method m = clase.getMethod("set" + ncampo, tipocampo);
                    m.invoke(doc, objeto);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    System.out.println("Error en campo '" + ncampo + "' : " + ex.getMessage());
                }
            }
        }
        return doc;
    }

    private static List<TransaccionLineasUsucampos> getTransaccionLineaCamposUsuarios(String FE_ID, int nroorden, IFields campos) throws SBOCOMException {
        List<TransaccionLineasUsucampos> camposUsuario = new LinkedList<>();
        for (int i = 0; i < campos.getCount(); i++) {
            IField icampo = campos.item(i);
            String ncampo = icampo.getName();
            Integer tcampo = icampo.getType();
            Object objeto = null;

            if (ncampo.startsWith("U_")) {
                int id = UsuariocamposJC.getIdByNombre(ncampo.substring(2));
                if (id > 0) {
                    TransaccionLineasUsucampos cu = new TransaccionLineasUsucampos(FE_ID, nroorden, id);

                    switch (tcampo) {
                        case 0:
                        case 1:  //Alpha,Memo
                            objeto = icampo.getValueString();
                            break;
                        case 2:  //Numeric
                            objeto = icampo.getValueInteger();
                            break;
                        case 3:  //Date
                            objeto = icampo.getValueDate();
                            break;
                        case 4: //Float
                            objeto = BigDecimal.valueOf(icampo.getValueDouble());
                            break;
                    }

                    cu.setValor(objeto == null ? "" : objeto.toString());
                    camposUsuario.add(cu);
                }
            }
        }
        return camposUsuario;
    }

}
