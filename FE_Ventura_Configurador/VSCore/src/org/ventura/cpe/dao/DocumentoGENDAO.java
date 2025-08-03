/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao;

/**
 *
 * @author VSUser
 */
public class DocumentoGENDAO {
    
//    public static boolean escribirArchivo(byte[] fileBytes, String archivoDestino) {
//        boolean correcto = false;
//        try {
//            OutputStream out = new FileOutputStream(archivoDestino);
//            out.write(fileBytes);
//            out.close();
//            correcto = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return correcto;
//
//    }
//
//    public static boolean ActualizarMensaje(Transaccion tc, String respuesta, String estado) {
//        org.ventura.cpe.generico.dto.hb.Transaccion tran = TransaccionJC.findTransaccion(tc.getFEId());
//        tran.setEstadoRespuesta(estado);
//        tran.setMensajeRespuesta(respuesta);
//        try {
//            TransaccionJC.edit(tran);
//        } catch (NonexistentEntityException ex) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, ex.getMessage());
//        } catch (Exception ex) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, ex.getMessage());
//        }
//
//        return true;
//
//    }
//
//    public static Map<String, String> AgregarAnexos(Transaccion t, byte[] xml, byte[] pdf, byte[] cdr, String tipoDocumento) {
//
//        Map<String, String> PathFile = new HashMap<>();
//
//        // your date
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(t.getDOCFechaEmision());
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH) + 1;
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//
//        String RutaFile = Directorio.ADJUNTOS + File.separator + year + File.separator + month + File.separator + day + File.separator + tipoDocumento + File.separator + t.getSNDocIdentidadNro();
//        /* Variable de los nombers de los archivos */
//        String nombPDF = t.getDOCId() + "_FormatoImpreso" + (t.getFETipoTrans().compareTo("B") == 0 ? "_baja" : "");
//        String nombXML = t.getDOCId() + "_XmlFirmado" + (t.getFETipoTrans().compareTo("B") == 0 ? "_baja" : "");
//        String nombCDR = t.getDOCId() + "_SUNAT_CDR" + (t.getFETipoTrans().compareTo("B") == 0 ? "_baja" : "");
//
//        String RutaPDF = RutaFile + File.separator + nombPDF + ".pdf";
//        String RutaXML = RutaFile + File.separator + nombXML + ".xml";
//        String RutaCDR = RutaFile + File.separator + nombCDR + ".zip";
//
//        File folder = new File(RutaFile);
//        folder.mkdirs();
//        boolean subirXML = escribirArchivo(xml, RutaXML);
//        if (subirXML) {
//            PathFile.put("rutaXML", RutaXML);
//            System.out.println(RutaXML);
//        }
//        boolean subirPDF = escribirArchivo(pdf, RutaPDF);
//        if (subirPDF) {
//            PathFile.put("rutaPDF", RutaPDF);
//            System.out.println(RutaPDF);
//        }
//        boolean subirCDR = escribirArchivo(cdr, RutaCDR);
//        if (subirCDR) {
//            PathFile.put("rutaCDR", RutaCDR);
//            System.out.println(RutaCDR);
//        }
//
//        org.ventura.cpe.generico.dto.hb.Transaccion te = TransaccionJC.findTransaccion(t.getFEId());
//        te.setRutaCDR(RutaCDR);
//        te.setRutaPDF(RutaPDF);
//        te.setRutaXML(RutaXML);
//        try {
//            TransaccionJC.edit(te);
//        } catch (NonexistentEntityException ex) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, ex.getMessage());
//        } catch (Exception ex) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, ex.getMessage());
//        }
//
//        return PathFile;
//
//    }
//
//    public static boolean ActualizarEstado(Transaccion transaccion) {
//
//        try {
//            String estado;
//            estado = (transaccion.getFETipoTrans().compareTo("B") == 0 ? "D" : "S");
//            org.ventura.cpe.generico.dto.hb.Transaccion objTran = TransaccionJC.findTransaccion(transaccion.getFEId());
//            objTran.setFEEstado(estado);
//            TransaccionJC.edit(objTran);
//
//            return true;
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        } finally {
//            System.gc();
//        }
//        return false;
//    }
//
//    public static List<TransaccionResumen> extraerResumenDiario(String fecha, int correlativo) throws VenturaExcepcion {
//
//        List<TransaccionResumen> lstResumen = new ArrayList<>();
//        List<org.ventura.cpe.generico.dto.hb.TransaccionResumen> lstGenResumen = TransaccionResumenJC.findPendientes();
//
//        for (int i = 0; i < lstGenResumen.size(); i++) {
//
//            TransaccionResumen tr = new TransaccionResumen();
//            tr.setDIRDepartamento(lstGenResumen.get(i).getDIRDepartamento());
//            tr.setDIRDireccion(lstGenResumen.get(i).getDIRDireccion());
//            tr.setDIRDistrito(lstGenResumen.get(i).getDIRDistrito());
//            tr.setDIRNomCalle(lstGenResumen.get(i).getDIRNomCalle());
//            tr.setDIRNroCasa(lstGenResumen.get(i).getDIRNroCasa());
//            tr.setDIRPais(lstGenResumen.get(i).getDIRPais());
//            tr.setDIRProvincia(lstGenResumen.get(i).getDIRProvincia());
//            tr.setDIRUbigeo(lstGenResumen.get(i).getDIRUbigeo());
//            tr.setDocIdentidadTipo(lstGenResumen.get(i).getDocIdentidadTipo());
//            tr.setEMail(lstGenResumen.get(i).getEMail());
//            tr.setEstado(lstGenResumen.get(i).getEstado());
//            tr.setFechaEmision(lstGenResumen.get(i).getFechaEmision());
//            tr.setFechaGeneracion(lstGenResumen.get(i).getFechaGeneracion());
//            tr.setIdTransaccion(lstGenResumen.get(i).getIdTransaccion());
//            tr.setNombreComercial(lstGenResumen.get(i).getNombreComercial());
//            tr.setPersonContacto(lstGenResumen.get(i).getPersonContacto());
//            tr.setNumeroRuc(lstGenResumen.get(i).getNumeroRuc());
//            tr.setRazonSocial(lstGenResumen.get(i).getRazonSocial());
//
//            tr.setTransaccionResumenLineaList(extraerResumenLinea(lstGenResumen.get(i).getTransaccionResumenLineaList()));
//            tr.setTransaccionResumenLineaAnexoList(extraerResumenLineaAnexo(lstGenResumen.get(i).getTransaccionResumenLineaAnexoList()));
//            
//
//            lstResumen.add(tr);
//        }
//
//        return lstResumen;
//
//    }
//
//    public static List<TransaccionResumenLinea> extraerResumenLinea(List<org.ventura.cpe.generico.dto.hb.TransaccionResumenLinea> lstResumen) throws VenturaExcepcion {
//
//        List<TransaccionResumenLinea> lstExResumen = new ArrayList<>();
//
//        for (int i = 0; i < lstResumen.size(); i++) {
//
//            TransaccionResumenLinea trl = new TransaccionResumenLinea(lstResumen.get(i).getTransaccionResumenLineaPK().getIdTransaccion(), lstResumen.get(i).getTransaccionResumenLineaPK().getIdLinea());
//            trl.setCodMoneda(lstResumen.get(i).getCodMoneda());
//            trl.setImporteOtrosCargos(lstResumen.get(i).getImporteOtrosCargos());
//            trl.setImporteTotal(lstResumen.get(i).getImporteTotal());
//            trl.setNumeroCorrelativoFin(lstResumen.get(i).getNumeroCorrelativoFin());
//            trl.setNumeroCorrelativoInicio(lstResumen.get(i).getNumeroCorrelativoInicio());
//            trl.setNumeroSerie(lstResumen.get(i).getNumeroSerie());
//            trl.setTipoDocumento(lstResumen.get(i).getTipoDocumento());
//            trl.setTotaIGV(lstResumen.get(i).getTotaIGV());
//            trl.setTotalISC(lstResumen.get(i).getTotalISC());
//            trl.setTotalOPExoneradas(lstResumen.get(i).getTotalOPExoneradas());
//            trl.setTotalOPGravadas(lstResumen.get(i).getTotalOPGravadas());
//            trl.setTotalOPInafectas(lstResumen.get(i).getTotalOPInafectas());
//            trl.setTotalOtrosTributos(lstResumen.get(i).getTotalOtrosTributos());
//
//            lstExResumen.add(trl);
//        }
//
//        return lstExResumen;
//    }
//    
//    public static List<TransaccionResumenLineaAnexo> extraerResumenLineaAnexo(List<org.ventura.cpe.generico.dto.hb.TransaccionResumenLineaAnexo> lstAnexo) throws VenturaExcepcion{
//    
//        List<TransaccionResumenLineaAnexo> lstExAnexo = new ArrayList<>();
//        
//        for (int i = 0; i < lstExAnexo.size(); i++) {
//            
//            TransaccionResumenLineaAnexo trla = new TransaccionResumenLineaAnexo(lstAnexo.get(i).getTransaccionResumenLineaAnexoPK().getIdTransaccion(),lstAnexo.get(i).getTransaccionResumenLineaAnexoPK().getIdLinea());
//            trla.setCorrelativo(lstAnexo.get(i).getCorrelativo());
//            trla.setDocEntry(lstAnexo.get(i).getDocEntry());
//            trla.setObjcType(lstAnexo.get(i).getObjcType());
//            trla.setSerie(lstAnexo.get(i).getSerie());
//            trla.setSn(lstAnexo.get(i).getSn());
//            trla.setTipoDocumento(lstAnexo.get(i).getTipoDocumento());
//            trla.setTipoTransaccion(lstAnexo.get(i).getTipoTransaccion());
//            
//            lstExAnexo.add(trla);
//            
//        }
//        
//        return lstExAnexo;
//    }
//
//    public static List<Transaccion> ExtraerTransacciones() throws VenturaExcepcion, org.ventura.cpe.excepciones.VenturaExcepcion {
//
//        List<Transaccion> lstTransaccion = new LinkedList<>();
//
//        List<org.ventura.cpe.generico.dto.hb.Transaccion> lstGenerica = TransaccionJC.findPendientes();
//
//        for (int i = 0; i < lstGenerica.size(); i++) {
//
//            Transaccion transaccion = new Transaccion();
//
//            transaccion.setANTCIPOTipoDocID(lstGenerica.get(i).getANTCIPOTipoDocID());
//            transaccion.setANTICIPOId(lstGenerica.get(i).getANTCIPOTipoDocID());
//            transaccion.setANTICIPOMonto(lstGenerica.get(i).getANTICIPOMonto());
//            transaccion.setANTICIPOTipo(lstGenerica.get(i).getANTICIPOTipo());
//            transaccion.setDIRDepartamento(lstGenerica.get(i).getDIRDepartamento());
//            transaccion.setDIRDireccion(lstGenerica.get(i).getDIRDireccion());
//            transaccion.setDIRDistrito(lstGenerica.get(i).getDIRDistrito());
//            transaccion.setDIRNomCalle(lstGenerica.get(i).getDIRNomCalle());
//            transaccion.setDIRNroCasa(lstGenerica.get(i).getDIRNroCasa());
//            transaccion.setDIRPais(lstGenerica.get(i).getDIRPais());
//            transaccion.setDIRProvincia(lstGenerica.get(i).getDIRProvincia());
//            transaccion.setDIRUbigeo(lstGenerica.get(i).getDIRUbigeo());
//            transaccion.setDIRUrbanizacion(lstGenerica.get(i).getDIRUrbanizacion());
//            transaccion.setDOCCodigo(lstGenerica.get(i).getDOCCodigo());
//            transaccion.setDOCCondPago(lstGenerica.get(i).getDOCCondPago());
//            transaccion.setDOCDescuento(lstGenerica.get(i).getDOCDescuento());
//            transaccion.setDOCDescuentoTotal(lstGenerica.get(i).getDOCDescuentoTotal());
//            transaccion.setDOCDscrpcion(lstGenerica.get(i).getDOCDscrpcion());
//            transaccion.setDOCFechaEmision(lstGenerica.get(i).getDOCFechaEmision());
//            transaccion.setDOCFechaVencimiento(lstGenerica.get(i).getDOCFechaVencimiento());
//            transaccion.setDOCId(lstGenerica.get(i).getDOCId());
//            transaccion.setDOCImporte(lstGenerica.get(i).getDOCImporte());
//            transaccion.setDOCMONCodigo(lstGenerica.get(i).getDOCMONCodigo());
//            transaccion.setDOCMONNombre(lstGenerica.get(i).getDOCMONNombre());
//            transaccion.setDOCMontoTotal(lstGenerica.get(i).getDOCMontoTotal());
//            transaccion.setDOCNumero(lstGenerica.get(i).getDOCNumero());
//            transaccion.setDOCPorcImpuesto(lstGenerica.get(i).getDOCPorcImpuesto());
//            transaccion.setDOCSerie(lstGenerica.get(i).getDOCSerie());
//            transaccion.setDocIdentidadNro(lstGenerica.get(i).getDocIdentidadNro());
//            transaccion.setDocIdentidadTipo(lstGenerica.get(i).getDocIdentidadTipo());
//            transaccion.setEMail(lstGenerica.get(i).getEMail());
//            transaccion.setFEComentario(lstGenerica.get(i).getFEComentario());
//            transaccion.setFEDocEntry(lstGenerica.get(i).getFEDocEntry());
//            transaccion.setFEDocNum(lstGenerica.get(i).getFEDocNum());
//            transaccion.setFEErrCod(lstGenerica.get(i).getFEErrCod());
//            transaccion.setFEErrMsj(lstGenerica.get(i).getFEErrMsj());
//            transaccion.setFEErrores(lstGenerica.get(i).getFEErrores());
//            transaccion.setFEEstado(lstGenerica.get(i).getFEEstado());
//            transaccion.setFEFormSAP(lstGenerica.get(i).getFEFormSAP());
//            transaccion.setFEId(lstGenerica.get(i).getFEId());
//            transaccion.setFEMaxSalto(lstGenerica.get(i).getFEMaxSalto());
//            transaccion.setFEObjectType(lstGenerica.get(i).getFEObjectType());
//            transaccion.setFESaltos(lstGenerica.get(i).getFESaltos());
//            transaccion.setFETipoTrans(lstGenerica.get(i).getFETipoTrans());
//            transaccion.setImportePagado(lstGenerica.get(i).getImportePagado());
//            transaccion.setMonedaPagado(lstGenerica.get(i).getMonedaPagado());
//            transaccion.setNombreComercial(lstGenerica.get(i).getNombreComercial());
//            transaccion.setObservaciones(lstGenerica.get(i).getObservaciones());
//            transaccion.setPersonContacto(lstGenerica.get(i).getPersonContacto());
//            transaccion.setREFDOCId(lstGenerica.get(i).getREFDOCId());
//            transaccion.setREFDOCMotivCode(lstGenerica.get(i).getREFDOCMotivCode());
//            transaccion.setREFDOCMotivDesc(lstGenerica.get(i).getREFDOCMotivDesc());
//            transaccion.setREFDOCTipo(lstGenerica.get(i).getREFDOCTipo());
//            transaccion.setRETRegimen(lstGenerica.get(i).getRETRegimen());
//            transaccion.setRETTasa(lstGenerica.get(i).getRETTasa());
//            transaccion.setRazonSocial(lstGenerica.get(i).getRazonSocial());
//            transaccion.setSNDIRDepartamento(lstGenerica.get(i).getSNDIRDepartamento());
//            transaccion.setSNDIRDireccion(lstGenerica.get(i).getSNDIRDireccion());
//            transaccion.setSNDIRDistrito(lstGenerica.get(i).getSNDIRDistrito());
//            transaccion.setSNDIRNomCalle(lstGenerica.get(i).getSNDIRNomCalle());
//            transaccion.setSNDIRNroCasa(lstGenerica.get(i).getDIRNroCasa());
//            transaccion.setSNDIRPais(lstGenerica.get(i).getSNDIRPais());
//            transaccion.setSNDIRProvincia(lstGenerica.get(i).getSNDIRProvincia());
//            transaccion.setSNDIRUbigeo(lstGenerica.get(i).getDIRUbigeo());
//            transaccion.setSNDIRUrbanizacion(lstGenerica.get(i).getSNDIRUrbanizacion());
//            transaccion.setSNDocIdentidadNro(lstGenerica.get(i).getSNDocIdentidadNro());
//            transaccion.setSNDocIdentidadTipo(lstGenerica.get(i).getSNDocIdentidadTipo());
//            transaccion.setSNEMail(lstGenerica.get(i).getSNEMail());
//            transaccion.setSNNombreComercial(lstGenerica.get(i).getSNNombreComercial());
//            transaccion.setSNRazonSocial(lstGenerica.get(i).getSNRazonSocial());
//            transaccion.setSNSegundoNombre(lstGenerica.get(i).getSNSegundoNombre());
//            transaccion.setSUNATTransact(lstGenerica.get(i).getSUNATTransact());
//            transaccion.setTelefono(lstGenerica.get(i).getTelefono());
//            transaccion.setTelefono1(lstGenerica.get(i).getTelefono1());
//            transaccion.setWeb(lstGenerica.get(i).getWeb());
//
//            transaccion.setTransaccionImpuestosList(ExtraerImpuesto(lstGenerica.get(i).getTransaccionImpuestosList()));
//            transaccion.setTransaccionAnticipoList(ExtraerAnticipo(lstGenerica.get(i).getTransaccionAnticipoList()));
//            transaccion.setTransaccionLineasList(ExtraerLineas(lstGenerica.get(i).getTransaccionLineasList()));
//            transaccion.setTransaccionContractdocrefList(ExtraerContractDocRef(lstGenerica.get(i).getTransaccionContractdocrefList()));
//            transaccion.setTransaccionTotalesList(ExtraerTotales(lstGenerica.get(i).getTransaccionTotalesList()));
//            transaccion.setTransaccionPropiedadesList(ExtraerPropiedades(lstGenerica.get(i).getTransaccionPropiedadesList()));
//            transaccion.setTransaccionDocrefersList(ExtraerDocRefers(lstGenerica.get(i).getTransaccionDocrefersList()));
//            transaccion.setTransaccionComprobantePagoList(ExtraerComprobantePago(lstGenerica.get(i).getTransaccionComprobantePagoList()));
//
//            lstTransaccion.add(transaccion);
//
//        }
//
//        return lstTransaccion;
//
//    }
//
//    public static List<TransaccionAnticipo> ExtraerAnticipo(List<org.ventura.cpe.generico.dto.hb.TransaccionAnticipo> lstAnticipo) throws VenturaExcepcion {
//
//        List<TransaccionAnticipo> lstExAnticipo = new ArrayList<>();
//        try {
//
//            for (int i = 0; i < lstAnticipo.size(); i++) {
//
//                TransaccionAnticipo anticipo = new TransaccionAnticipo(lstAnticipo.get(i).getTransaccionAnticipoPK().getFEId(), lstAnticipo.get(i).getTransaccionAnticipoPK().getNroAnticipo());
//                anticipo.setAntiDOCSerieCorrelativo(lstAnticipo.get(i).getAntiDOCSerieCorrelativo());
//                anticipo.setAntiDOCTipo(lstAnticipo.get(i).getAntiDOCTipo());
//                anticipo.setAnticipoMonto(lstAnticipo.get(i).getAnticipoMonto());
//                anticipo.setDOCMoneda(lstAnticipo.get(i).getDOCMoneda());
//                anticipo.setDOCNumero(lstAnticipo.get(i).getDOCNumero());
//                anticipo.setDOCTipo(lstAnticipo.get(i).getDOCTipo());
//                lstExAnticipo.add(anticipo);
//            }
//
//        } catch (Exception e) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de Anticipo");
//        }
//
//        return lstExAnticipo;
//
//    }
//
//    public static List<TransaccionLineas> ExtraerLineas(List<org.ventura.cpe.generico.dto.hb.TransaccionLineas> lstLineas) throws VenturaExcepcion, org.ventura.cpe.excepciones.VenturaExcepcion {
//
//        List<TransaccionLineas> lstExLineas = new ArrayList<>();
//        try {
//            for (int i = 0; i < lstLineas.size(); i++) {
//
//                TransaccionLineas transaccionLineas = new TransaccionLineas(lstLineas.get(i).getTransaccionLineasPK().getFEId(), lstLineas.get(i).getTransaccionLineasPK().getNroOrden());
//                transaccionLineas.setCantidad(lstLineas.get(i).getCantidad());
//                transaccionLineas.setCodArticulo(lstLineas.get(i).getCodArticulo());
//                transaccionLineas.setDSCTOMonto(lstLineas.get(i).getDSCTOMonto());
//                transaccionLineas.setDSCTOPorcentaje(lstLineas.get(i).getDSCTOPorcentaje());
//                transaccionLineas.setDescripcion(lstLineas.get(i).getDescripcion());
//                transaccionLineas.setPrecioDscto(lstLineas.get(i).getPrecioDscto());
//                transaccionLineas.setPrecioIGV(lstLineas.get(i).getPrecioIGV());
//                transaccionLineas.setPrecioRefCodigo(lstLineas.get(i).getPrecioRefCodigo());
//                transaccionLineas.setPrecioRefMonto(lstLineas.get(i).getPrecioRefMonto());
//                transaccionLineas.setTotalLineaConIGV(lstLineas.get(i).getTotalLineaConIGV());
//                transaccionLineas.setTotalLineaSinIGV(lstLineas.get(i).getTotalLineaSinIGV());
//                transaccionLineas.setUnidad(lstLineas.get(i).getUnidad());
//                transaccionLineas.setUnidadSunat(lstLineas.get(i).getUnidadSunat());
//                transaccionLineas.setTransaccionLineasUsucamposList(ExtraerLineasUsuCampos(lstLineas.get(i).getTransaccionLineasCampousuarioList()));
//                transaccionLineas.setTransaccionLineasBillrefList(extraerBillref(lstLineas.get(i).getTransaccionLineasBillrefList()));
//                transaccionLineas.setTransaccionLineaImpuestosList(ExtraerLineasImpuestos(lstLineas.get(i).getTransaccionLineaImpuestosList()));
//                lstExLineas.add(transaccionLineas);
//
//            }
//        } catch (Exception e) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de Lineas");
//        }
//
//        return lstExLineas;
//
//    }
//
//    public static List<TransaccionLineaImpuestos> ExtraerLineasImpuestos(List<org.ventura.cpe.generico.dto.hb.TransaccionLineaImpuestos> lstLineasImpuestos) throws VenturaExcepcion {
//
//        List<TransaccionLineaImpuestos> lstExLineaImpuesto = new ArrayList<>();
//
//        try {
//
//            for (int i = 0; i < lstLineasImpuestos.size(); i++) {
//
//                TransaccionLineaImpuestos lineaImpuestos = new TransaccionLineaImpuestos(lstLineasImpuestos.get(i).getTransaccionLineaImpuestosPK().getFEId(), lstLineasImpuestos.get(i).getTransaccionLineaImpuestosPK().getNroOrden(), lstLineasImpuestos.get(i).getTransaccionLineaImpuestosPK().getLineId());
//                lineaImpuestos.setMoneda(lstLineasImpuestos.get(i).getMoneda());
//                lineaImpuestos.setMonto(lstLineasImpuestos.get(i).getMonto());
//                lineaImpuestos.setPorcentaje(lstLineasImpuestos.get(i).getPorcentaje());
//                lineaImpuestos.setTierRange(lstLineasImpuestos.get(i).getTierRange());
//                lineaImpuestos.setTipoAfectacion(lstLineasImpuestos.get(i).getTipoAfectacion());
//                lineaImpuestos.setTipoTributo(lstLineasImpuestos.get(i).getTipoTributo());
//
//                lstExLineaImpuesto.add(lineaImpuestos);
//            }
//
//        } catch (Exception e) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de Lineas Impuestos");
//        }
//
//        return lstExLineaImpuesto;
//
//    }
//
//    public static List<TransaccionLineasUsucampos> ExtraerLineasUsuCampos(List<org.ventura.cpe.generico.dto.hb.TransaccionLineasCampousuario> lstCampoUsuario) throws VenturaExcepcion, org.ventura.cpe.excepciones.VenturaExcepcion {
//
//        List<TransaccionLineasUsucampos> lstExLineaUsuCampos = new ArrayList<>();
//
//        try {
//
//            for (int i = 0; i < lstCampoUsuario.size(); i++) {
//
//                int idUser = UsuariocamposJC.getIdByNombre(lstCampoUsuario.get(i).getNombreCampo());
//                Usuariocampos usuariocampos = UsuariocamposJC.findUsuariocampos(idUser);
//                if (usuariocampos == null) {
//
//                    usuariocampos = new Usuariocampos();
//                    //usuariocampos.setId(lstCampoUsuario.get(i).getTransaccionLineasCampousuarioPK().getIdCampoUsuario());
//                    usuariocampos.setNombre(lstCampoUsuario.get(i).getNombreCampo());
//                    try {
//                        UsuariocamposJC.create(usuariocampos);
//                        int nuevoId = UsuariocamposJC.getIdByNombre(lstCampoUsuario.get(i).getNombreCampo());
//                        usuariocampos = UsuariocamposJC.findUsuariocampos(nuevoId);
//                    } catch (Exception ex) {
//                        System.out.println(ex.getMessage());
//                    }
//                }
//                TransaccionLineasUsucampos lineasUsucampos = new TransaccionLineasUsucampos(lstCampoUsuario.get(i).getTransaccionLineasCampousuarioPK().getFEId(), lstCampoUsuario.get(i).getTransaccionLineasCampousuarioPK().getNroOrden(), usuariocampos.getId());
//
//                lineasUsucampos.setUsuariocampos(usuariocampos);
//                lineasUsucampos.setValor(lstCampoUsuario.get(i).getValor());
//
//                lstExLineaUsuCampos.add(lineasUsucampos);
//
//            }
//
//        } catch (Exception e) {
//
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de Lineas Usucampos");
//        }
//
//        return lstExLineaUsuCampos;
//
//    }
//
//    public static List<TransaccionImpuestos> ExtraerImpuesto(List<org.ventura.cpe.generico.dto.hb.TransaccionImpuestos> lstImpuesto) throws VenturaExcepcion {
//
//        List<TransaccionImpuestos> lstExImpuesto = new ArrayList<>();
//
//        try {
//
//            for (int i = 0; i < lstImpuesto.size(); i++) {
//                TransaccionImpuestos transaccionImpuestos = new TransaccionImpuestos(lstImpuesto.get(i).getTransaccionImpuestosPK().getFEId(), lstImpuesto.get(i).getTransaccionImpuestosPK().getLineId());
//                transaccionImpuestos.setMoneda(lstImpuesto.get(i).getMoneda());
//                transaccionImpuestos.setMonto(lstImpuesto.get(i).getMonto());
//                transaccionImpuestos.setPorcentaje(lstImpuesto.get(i).getPorcentaje());
//                transaccionImpuestos.setTierRange(lstImpuesto.get(i).getTierRange());
//                transaccionImpuestos.setTipoAfectacion(lstImpuesto.get(i).getTipoAfectacion());
//                transaccionImpuestos.setTipoTributo(lstImpuesto.get(i).getTipoTributo());
//
//                lstExImpuesto.add(transaccionImpuestos);
//            }
//
//        } catch (Exception e) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de Impuestos");
//        }
//
//        return lstExImpuesto;
//
//    }
//
//    public static List<TransaccionLineasBillref> extraerBillref(List<org.ventura.cpe.generico.dto.hb.TransaccionLineasBillref> lstBillRef) throws VenturaExcepcion {
//
//        List<TransaccionLineasBillref> lstExBill = new ArrayList<>();
//        try {
//
//            for (int i = 0; i < lstBillRef.size(); i++) {
//                TransaccionLineasBillref tlb = new TransaccionLineasBillref(lstBillRef.get(i).getTransaccionLineasBillrefPK().getFEId(), lstBillRef.get(i).getTransaccionLineasBillrefPK().getNroOrden(), lstBillRef.get(i).getTransaccionLineasBillrefPK().getLineId());
//
//                tlb.setAdtDocRefId(lstBillRef.get(i).getAdtDocRefId());
//                tlb.setAdtDocRefSchemaId(lstBillRef.get(i).getAdtDocRefSchemaId());
//                tlb.setInvDocRefDocTypeCode(lstBillRef.get(i).getInvDocRefDocTypeCode());
//                tlb.setInvDocRefId(lstBillRef.get(i).getInvDocRefId());
//
//                lstExBill.add(tlb);
//
//            }
//
//        } catch (Exception e) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de extraerBillref");
//        }
//        return lstExBill;
//    }
//
//    public static List<TransaccionContractdocref> ExtraerContractDocRef(List<org.ventura.cpe.generico.dto.hb.TransaccionContractdocref> lstContract) throws VenturaExcepcion {
//
//        List<TransaccionContractdocref> lstExContract = new ArrayList<>();
//
//        try {
//            for (int i = 0; i < lstContract.size(); i++) {
//
//                TransaccionContractdocref tcExContract = new TransaccionContractdocref();
//
//                int idUser = UsuariocamposJC.getIdByNombre(lstContract.get(i).getNombreCampo());
//                Usuariocampos usuariocampos = UsuariocamposJC.findUsuariocampos(idUser);
//                if (usuariocampos == null) {
//                    usuariocampos.setNombre(lstContract.get(i).getNombreCampo());
//                    try {
//                        UsuariocamposJC.create(usuariocampos);
//                    } catch (Exception ex) {
//                        System.out.println(ex.getMessage());
//                    }
//                }
//
//                tcExContract.setUsuariocampos(usuariocampos);
//                tcExContract.setValor(lstContract.get(i).getValor());
//
//                lstExContract.add(tcExContract);
//
//            }
//        } catch (Exception e) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de ContractDocRef");
//        }
//
//        return lstExContract;
//
//    }
//
//    public static List<TransaccionTotales> ExtraerTotales(List<org.ventura.cpe.generico.dto.hb.TransaccionTotales> lstTotales) throws VenturaExcepcion {
//
//        List<TransaccionTotales> lstExTotales = new ArrayList<>();
//
//        try {
//            for (int i = 0; i < lstTotales.size(); i++) {
//                TransaccionTotales transaccionTotales = new TransaccionTotales(lstTotales.get(i).getTransaccionTotalesPK().getFEId(), lstTotales.get(i).getTransaccionTotalesPK().getId());
//
//                transaccionTotales.setMonto(lstTotales.get(i).getMonto());
//                transaccionTotales.setPrcnt(lstTotales.get(i).getPrcnt());
//
//                lstExTotales.add(transaccionTotales);
//
//            }
//        } catch (Exception e) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de Totales");
//        }
//
//        return lstExTotales;
//    }
//
//    public static List<TransaccionPropiedades> ExtraerPropiedades(List<org.ventura.cpe.generico.dto.hb.TransaccionPropiedades> lstPropiedades) throws VenturaExcepcion {
//        List<TransaccionPropiedades> lstExPropiedades = new ArrayList<>();
//
//        try {
//
//            for (int i = 0; i < 10; i++) {
//
//                TransaccionPropiedades tp = new TransaccionPropiedades(lstPropiedades.get(i).getTransaccionPropiedadesPK().getFEId(), lstPropiedades.get(i).getTransaccionPropiedadesPK().getId());
//                tp.setValor(lstPropiedades.get(i).getValor());
//
//                lstExPropiedades.add(tp);
//            }
//
//        } catch (Exception e) {
//
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de Propiedades");
//        }
//
//        return lstExPropiedades;
//    }
//
//    public static List<TransaccionDocrefers> ExtraerDocRefers(List<org.ventura.cpe.generico.dto.hb.TransaccionDocrefers> lstDocRefers) throws VenturaExcepcion {
//
//        List<TransaccionDocrefers> lstExDocRefers = new ArrayList<>();
//
//        try {
//
//            for (int i = 0; i < lstDocRefers.size(); i++) {
//
//                TransaccionDocrefers docrefers = new TransaccionDocrefers(lstDocRefers.get(i).getTransaccionDocrefersPK().getFEId(), lstDocRefers.get(i).getTransaccionDocrefersPK().getLineId());
//                docrefers.setId(lstDocRefers.get(i).getId());
//                docrefers.setTipo(lstDocRefers.get(i).getTipo());
//
//                lstExDocRefers.add(docrefers);
//
//            }
//
//        } catch (Exception e) {
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de DocRefers");
//        }
//
//        return lstExDocRefers;
//
//    }
//
//    public static List<TransaccionComprobantePago> ExtraerComprobantePago(List<org.ventura.cpe.generico.dto.hb.TransaccionComprobantePago> lstComprobante) throws VenturaExcepcion {
//
//        List<TransaccionComprobantePago> lstExComprobante = new ArrayList<>();
//
//        try {
//
//            for (int i = 0; i < lstComprobante.size(); i++) {
//
//                TransaccionComprobantePago transaccionComprobantePago = new TransaccionComprobantePago(lstComprobante.get(i).getTransaccionComprobantePagoPK().getFEId(), lstComprobante.get(i).getTransaccionComprobantePagoPK().getNroOrden());
//
//                transaccionComprobantePago.setCPFecha(lstComprobante.get(i).getCPFecha());
//                transaccionComprobantePago.setCPImporte(lstComprobante.get(i).getCPImporte());
//                transaccionComprobantePago.setCPImporteTotal(lstComprobante.get(i).getCPImporteTotal());
//                transaccionComprobantePago.setCPMoneda(lstComprobante.get(i).getCPMoneda());
//                transaccionComprobantePago.setCPMonedaMontoNeto(lstComprobante.get(i).getCPMonedaMontoNeto());
//                transaccionComprobantePago.setDOCFechaEmision(lstComprobante.get(i).getDOCFechaEmision());
//                transaccionComprobantePago.setDOCImporte(lstComprobante.get(i).getDOCImporte());
//                transaccionComprobantePago.setDOCMoneda(lstComprobante.get(i).getDOCMoneda());
//                transaccionComprobantePago.setDOCNumero(lstComprobante.get(i).getDOCNumero());
//                transaccionComprobantePago.setDOCTipo(lstComprobante.get(i).getDOCTipo());
//                transaccionComprobantePago.setPagoFecha(lstComprobante.get(i).getPagoFecha());
//                transaccionComprobantePago.setPagoImporteSR(lstComprobante.get(i).getPagoImporteSR());
//                transaccionComprobantePago.setPagoMoneda(lstComprobante.get(i).getPagoMoneda());
//                transaccionComprobantePago.setPagoNumero(lstComprobante.get(i).getPagoNumero());
//                transaccionComprobantePago.setTCFactor(lstComprobante.get(i).getTCFactor());
//                transaccionComprobantePago.setTCFecha(lstComprobante.get(i).getTCFecha());
//                transaccionComprobantePago.setTCMonedaObj(lstComprobante.get(i).getTCMonedaObj());
//                transaccionComprobantePago.setTCMonedaRef(lstComprobante.get(i).getTCMonedaRef());
//
//                lstExComprobante.add(transaccionComprobantePago);
//
//            }
//
//        } catch (Exception e) {
//
//            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " Ocurrio un problema en la extraccion generica de Comprobante R/P");
//        }
//
//        return lstExComprobante;
//
//    }

}
