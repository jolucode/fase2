/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.TransaccionResumenLineaAnexoJC;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.dto.hb.TransaccionResumenLineaAnexo;
import org.ventura.cpe.dto.hb.TransaccionResumenLineaAnexoPK;

/**
 *
 * @author VS-LT-06
 */
public class TransaccionResumenLineaAnexoBL {

    public static void eliminarFilas(TransaccionResumenLineaAnexoPK trlapk,TransaccionResumen tr) {

        try {
            TransaccionResumenLineaAnexoJC.destroy(trlapk,tr);
        } catch (Exception e) {
            System.out.println("Ocurrió el siguiente incidente: " + e.getMessage());
        }

    }

}
