/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.sb1;

import com.sap.smb.sbo.api.ICompany;
import com.sap.smb.sbo.api.SBOCOMConstants;
import com.sap.smb.sbo.api.SBOCOMException;
import com.sap.smb.sbo.api.SBOCOMUtil;
import org.ventura.cpe.bl.TransaccionBL;
import org.ventura.cpe.dao.DocumentoDAO;
import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.loaderbl.Configuracion;
import org.ventura.cpe.log.LoggerTrans;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author VSUser
 */
public class DocumentoBL extends DocumentoINF {

    public static ICompany Sociedad;

    public static HashMap<String, ICompany> mapSociedades = new HashMap<>();

    @Override
    public boolean ActualizarRespuestaSUNAT(Transaccion tc, String respuesta, boolean aprobado, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        System.out.println("Entro al metodo ActualizarRespuestaSUNAT");
        String estado = "";
        if (tc.getFETipoTrans().equalsIgnoreCase("E") && (tc.getDOCCodigo().equalsIgnoreCase("03") || tc.getDOCCodigo().equalsIgnoreCase("07") || tc.getDOCCodigo().equalsIgnoreCase("08")) && tc.getDOCSerie().substring(0, 1).equalsIgnoreCase("B")) {
            LoggerTrans.getCDMainLogger().log(Level.INFO, "Documento :{0} Tipo de Transaccion :{1} Estado : K", new Object[]{tc.getDOCCodigo(), tc.getFETipoTrans()});
            estado = "K";
        } else {

            if (tc.getFETipoTrans().equalsIgnoreCase("E")) {
                if (aprobado) {
                    estado = "R";
                    LoggerTrans.getCDMainLogger().log(Level.INFO, "Documento :{0} Tipo de Transaccion :{1} Estado : R", new Object[]{tc.getDOCCodigo(), tc.getFETipoTrans()});
                } else {
                    estado = "Z";
                }
                LoggerTrans.getCDMainLogger().log(Level.INFO, "Documento :{0} Tipo de Transaccion :{1} Estado : Z", new Object[]{tc.getDOCCodigo(), tc.getFETipoTrans()});
            } else {
                if (tc.getFETipoTrans().equalsIgnoreCase("B")) {
                    if (aprobado) {
                        estado = "P";
                        LoggerTrans.getCDMainLogger().log(Level.INFO, "Documento :{0} Tipo de Transaccion :{1} Estado : P", new Object[]{tc.getDOCCodigo(), tc.getFETipoTrans()});
                    } else {
                        estado = "J";
                        LoggerTrans.getCDMainLogger().log(Level.INFO, "Documento :{0} Tipo de Transaccion :{1} Estado : J", new Object[]{tc.getDOCCodigo(), tc.getFETipoTrans()});
                    }
                } else {
                    if (tc.getFETipoTrans().equalsIgnoreCase("RDB")) {
                        if (aprobado) {
                            estado = "R";
                            LoggerTrans.getCDMainLogger().log(Level.INFO, "Documento :{0} Tipo de Transaccion :{1} Estado : R", new Object[]{tc.getDOCCodigo(), tc.getFETipoTrans()});
                        } else {
                            estado = "Z";
                            LoggerTrans.getCDMainLogger().log(Level.INFO, "Documento :{0} Tipo de Transaccion :{1} Estado : Z", new Object[]{tc.getDOCCodigo(), tc.getFETipoTrans()});
                        }
                    }
                }
            }
            System.out.println("Documento " + tc.getDOCCodigo() + " Estado " + estado);
        }
        try {
            return DocumentoDAO.ActualizarMensaje(tc, respuesta, estado, Sociedad, connection);
        } catch (SBOCOMException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Map<String, String> AgregarAnexos(Transaccion tc, byte[] xml, byte[] pdf, byte[] cdr, Boolean borrador, ICompany Sociedad, Connection connection) {
        try {
            return DocumentoDAO.AgregarAnexos(tc, xml, pdf, cdr, tc.getDOCFechaEmision(), borrador, Sociedad,connection);

        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "AgregarAnexos: {0}", ex.getMessage());

        } catch (IllegalArgumentException | SecurityException ex) {
            Logger.getLogger(DocumentoBL.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean PonerEnSeguimiento(Transaccion tc, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        try {
            String estado;
            estado = (tc.getFETipoTrans().compareTo("B") == 0 ? "S" : "S");
            return DocumentoDAO.ActualizarEstado(tc, estado, Sociedad, connection);
        } catch (VenturaExcepcion e) {
            TransaccionBL.Eliminar(tc);
            try {
                return DocumentoDAO.ActualizarMensaje(tc, e.getMessage(), "N", Sociedad, connection);
            } catch (SBOCOMException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean CargarParametro(Configuracion configuracion) {
        try {
            this.setSERVIDOR_LICENCIAS(configuracion.getErp().getServidorLicencias().normalValue());
            this.setBD_TIPO(configuracion.getErp().getTipoServidor().normalValue());
            this.setSERVIDOR_BASEDATOS(configuracion.getErp().getServidorBD().normalValue());
            this.setBD_NOMBRE(configuracion.getErp().getBaseDeDatos().normalValue());
            this.setBD_USER(configuracion.getErp().getUser().normalValue());
            this.setBD_PASS(configuracion.getErp().getPassword().normalValue());
            this.setERP_USER(configuracion.getErp().getErpUser().normalValue());
            this.setERP_PASS(configuracion.getErp().getErpPass().normalValue());
            this.setERPRutaArchivos(configuracion.getErp().getErpRutaArchivos().normalValue());
            this.setERPNombreSociedad(configuracion.getErp().getErpNombreSociedad().normalValue());
            this.setListBdsMaestras(HBPersistencia.LISTBDANADIDAS);
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se encontr? la ruta. {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String Conectar() {
        String msj = "";
        try {
            Desconectar();
            if (Sociedad == null || !Sociedad.isConnected()) {
                Sociedad = SBOCOMUtil.newCompany();
                Sociedad.setLicenseServer(this.getSERVIDOR_LICENCIAS());
                Sociedad.setServer(this.getSERVIDOR_BASEDATOS());
                Sociedad.setDbServerType(Integer.parseInt(this.getBD_TIPO()));
                Sociedad.setCompanyDB(this.getBD_NOMBRE());
                Sociedad.setDbUserName(this.getBD_USER());
                Sociedad.setDbPassword(this.getBD_PASS());
                Sociedad.setUserName(this.getERP_USER());
                Sociedad.setPassword(this.getERP_PASS());

                Sociedad.setLanguage(SBOCOMConstants.BoSuppLangs_ln_English);
                Sociedad.setUseTrusted(Boolean.FALSE);

                int ret = Sociedad.connect();
                if (ret == 0) {
                    mapSociedades.put(Sociedad.getCompanyDB(), Sociedad);
                    msj = Sociedad.getCompanyName();
                } else {
                    msj = "ERR: (" + ret + ")" + Sociedad.getLastErrorDescription();
                    LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se pudo conectar {0}", msj);
                }
            }
            if (this.getListBdsMaestras() != null) {
                for (int i = 0; i < this.getListBdsMaestras().size(); i++) {
                    //if (listSociedades == null || listSociedades.size() == 0 || !listSociedades.get(i).isConnected()) {
                    ICompany sociedad = SBOCOMUtil.newCompany();
                    sociedad.setLicenseServer(this.getSERVIDOR_LICENCIAS());
                    sociedad.setServer(this.getSERVIDOR_BASEDATOS());
                    sociedad.setDbServerType(Integer.parseInt(this.getBD_TIPO()));
                    sociedad.setCompanyDB(this.getListBdsMaestras().get(i).getBDNombre());
                    sociedad.setDbUserName(this.getListBdsMaestras().get(i).getBDUsuario());
                    sociedad.setDbPassword(this.getListBdsMaestras().get(i).getBDClave());
                    sociedad.setUserName(this.getListBdsMaestras().get(i).getERPUsuario());
                    sociedad.setPassword(this.getListBdsMaestras().get(i).getERPClave());

                    sociedad.setLanguage(SBOCOMConstants.BoSuppLangs_ln_English);
                    sociedad.setUseTrusted(Boolean.FALSE);

                    int ret = sociedad.connect();
                    if (ret == 0) {
                        mapSociedades.put(sociedad.getCompanyDB(), sociedad);
                    } else {
                        msj = "ERR: (" + ret + ")" + sociedad.getLastErrorDescription();
                        LoggerTrans.getCDMainLogger().log(Level.SEVERE, "ERR: No se pudo conectar {0}", msj);
                    }
                    //}
                }
            }
        } catch (NumberFormatException ex) {
            msj = "ERR: (-10000)" + ex.getMessage();
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se pudo conectar {0}", ex.getMessage());
        }
        return msj;
    }

    public ICompany ConectarSociedad() {
        String msj = "";
        try {
            Desconectar();
            Sociedad = SBOCOMUtil.newCompany();
            Sociedad.setLicenseServer(this.getSERVIDOR_LICENCIAS());
            Sociedad.setServer(this.getSERVIDOR_BASEDATOS());
            Sociedad.setDbServerType(Integer.parseInt(this.getBD_TIPO()));
            Sociedad.setCompanyDB(this.getBD_NOMBRE());
            Sociedad.setDbUserName(this.getBD_USER());
            Sociedad.setDbPassword(this.getBD_PASS());
            Sociedad.setUserName(this.getERP_USER());
            Sociedad.setPassword(this.getERP_PASS());

            Sociedad.setLanguage(SBOCOMConstants.BoSuppLangs_ln_Spanish);
            Sociedad.setUseTrusted(Boolean.FALSE);

            int ret = Sociedad.connect();
            if (ret == 0) {
                mapSociedades.put(Sociedad.getCompanyDB(), Sociedad);
                return Sociedad;
            } else {
                msj = "ERR: (" + ret + ")" + Sociedad.getLastErrorDescription();
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se pudo conectar {0}", msj);
                return null;
            }
        } catch (NumberFormatException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se pudo conectar {0}", ex.getMessage());
            return null;
        }
    }

    @Override
    public void Desconectar() {
        if (Sociedad != null && Sociedad.isConnected()) {
            Sociedad.disconnect();
            Sociedad = null;
        }
        if (mapSociedades != null && mapSociedades.size() > 0) {
            Set<String> keys = mapSociedades.keySet();
            for (String key : keys) {
                mapSociedades.get(key).disconnect();
            }
            mapSociedades.clear();
        }
        System.gc();
    }

    @Override
    public boolean ActualizarMensaje(Transaccion tc, String mensaje, String estado, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        try {
            return DocumentoDAO.ActualizarMensaje(tc, mensaje, estado, Sociedad, connection);
        } catch (SBOCOMException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean capturarCodigo(Transaccion tc, String codigoBarra, String digestValue, ICompany Sociedad, Connection connection) {
        return DocumentoDAO.CapturarCodigo(tc, codigoBarra, digestValue, Sociedad, connection);
    }

}
