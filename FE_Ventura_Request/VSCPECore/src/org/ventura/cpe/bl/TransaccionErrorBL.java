/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.TransaccionErrorJC;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.log.LoggerTrans;

import java.util.List;
import java.util.logging.Level;

/**
 * @author Percy
 */
public class TransaccionErrorBL {

    public static boolean EliminarCorregidos(List<Transaccion> disponibles) {
        String feid = "";
        try {
            for (Transaccion tc : disponibles) {
                feid = tc.getFEId();
                TransaccionErrorJC.destroy(feid);
            }
            return true;
        } catch (NonexistentEntityException ex) {
            return false;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "EliminarCorregidos: [{0}] - {1}", new Object[]{feid, ex.getMessage()});
            return false;
        }
    }
}
