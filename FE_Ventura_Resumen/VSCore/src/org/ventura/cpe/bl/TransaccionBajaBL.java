/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.TransaccionBajaJC;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.excepciones.VenturaExcepcion;

/**
 *
 * @author VSUser
 */
public class TransaccionBajaBL {
    
    
    public static boolean ActualizarIDBaja(Transaccion tc) throws VenturaExcepcion {
        if (tc.getFETipoTrans().compareTo("E") == 0) {
            return true;
        }
        String serie = tc.getDOCSerie() + "-" + tc.getDOCCodigo();
        String numero = tc.getDOCNumero();
        String idbaja = tc.getANTICIPOId();
        TransaccionBajaJC.idBaja();
        return true;
    }
    
}
