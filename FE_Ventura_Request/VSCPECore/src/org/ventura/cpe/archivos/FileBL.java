/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.archivos;

import com.sap.smb.sbo.api.ICompany;
import org.ventura.cpe.dao.DocumentoFileDAO;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.loaderbl.Configuracion;

import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ventura.cpe.log.LoggerTrans;

/**
 * @author VSUser
 */
public class FileBL extends DocumentoINF {

    @Override
    public boolean ActualizarMensaje(org.ventura.cpe.dto.hb.Transaccion tc, String mensaje, String estado, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean ActualizarRespuestaSUNAT(org.ventura.cpe.dto.hb.Transaccion tc, String respuesta, boolean aprobado, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean setActualizarEstadoEnvioSUNAT(Transaccion tc, int estado, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> AgregarAnexos(org.ventura.cpe.dto.hb.Transaccion tc, byte[] xml, byte[] pdf, byte[] cdr, Boolean borrador, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean PonerEnSeguimiento(org.ventura.cpe.dto.hb.Transaccion tc, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean CargarParametro(Configuracion configuracion) {
        try {
            this.setERPRutaArchivos(configuracion.getErp().getErpRutaArchivos().normalValue());
            this.setERPNombreSociedad(configuracion.getErp().getErpNombreSociedad().normalValue());
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se encontro la ruta. {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String Conectar() {
        String retorno = "";
        String rutaDocumentos = getERPRutaArchivos();
        try {
            retorno = DocumentoFileDAO.validarRutaArchivos(rutaDocumentos);
            if (retorno.equals("OK")) {
                retorno = getERPNombreSociedad();
            } else {
                retorno = "ERR: No existe ruta de Documentos a Procesar";
            }
        } catch (VenturaExcepcion ex) {
            Logger.getLogger(FileBL.class.getName()).log(Level.SEVERE, null, ex);
            retorno = "ERR: " + ex.getMessage();
        }
        return retorno;
    }

    @Override
    public void Desconectar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean capturarCodigo(Transaccion tc, String codigoBarra, String digestValue, ICompany Sociedad, Connection connection) throws VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ICompany ConectarSociedad() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
