/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.TransaccionJC;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.log.LoggerTrans;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Percy
 */
public class TransaccionBL {

    private static final SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");

    public static Transaccion ListarTransaccionCola(String id) {
        try {
            Transaccion t = TransaccionJC.findTransaccionCola(id);
            return t;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            throw ex;
        }
    }

    public static List<Transaccion> ListarAll() {
        try {
            List<Transaccion> l = TransaccionJC.findAll();
            return l;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            throw ex;
        }
    }

    public static boolean Eliminar(Transaccion tc) {
        try {
            TransaccionJC.destroy(tc.getFEId());
        } catch (NonexistentEntityException nee) {
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
        return true;
    }

}