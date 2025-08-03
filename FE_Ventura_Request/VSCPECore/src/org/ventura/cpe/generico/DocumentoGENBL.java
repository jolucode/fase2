/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.generico;

import com.sap.smb.sbo.api.ICompany;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.loaderbl.Configuracion;

import java.sql.Connection;
import java.util.Map;

/**
 * @author VSUser
 */
public class DocumentoGENBL extends DocumentoINF {

    @Override
    public boolean ActualizarMensaje(Transaccion tc, String mensaje, String estado, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean ActualizarRespuestaSUNAT(Transaccion tc, String respuesta, boolean aprobado, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> AgregarAnexos(Transaccion tc, byte[] xml, byte[] pdf, byte[] cdr, Boolean borrador, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean PonerEnSeguimiento(Transaccion tc, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean CargarParametro(Configuracion configuracion) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String Conectar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void Desconectar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean capturarCodigo(Transaccion tc, String codigoBarra, String digestValue, ICompany Sociedad, Connection connection) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ICompany ConectarSociedad() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
