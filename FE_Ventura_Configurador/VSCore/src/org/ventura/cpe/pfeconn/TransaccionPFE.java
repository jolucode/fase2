package org.ventura.cpe.pfeconn;

import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.excepciones.VenturaExcepcion;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author VSUser
 */
public abstract class TransaccionPFE {

    public static TransaccionPFE Conector;

    public abstract TransaccionRespuesta EnviarResumenDiario(TransaccionResumen tr) throws VenturaExcepcion;

    public abstract boolean Eco () throws VenturaExcepcion;

}
