package org.ventura.cpe.pfeconn;

import com.sap.smb.sbo.api.ICompany;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.wsclient.config.configuration.Configuracion;

import java.sql.Connection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author VSUser
 */
public abstract class TransaccionPFE {

    public static TransaccionPFE Conector;


    public abstract TransaccionRespuesta EnviarTransaccion(Transaccion ts, DocumentoINF doc, ICompany Sociedad, Configuracion configuration, Connection connection) throws VenturaExcepcion;

    public abstract boolean Eco() throws VenturaExcepcion;

}
