/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.TransaccionResumenJC;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.log.LoggerTrans;

import java.util.logging.Level;

/**
 *
 * @author VS-LT-06
 */
public class TransaccionResumenBL {

    public static boolean MarcarEnviado(TransaccionResumen tr) {

        try {
            tr.setEstado("K");
            TransaccionResumenJC.edit(tr);
            return true;

        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return false;
        }

    }
    
}