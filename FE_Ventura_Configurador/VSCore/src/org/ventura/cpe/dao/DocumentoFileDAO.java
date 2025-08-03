/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao;

import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.excepciones.VenturaExcepcion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author VSUser
 */
public class DocumentoFileDAO {

    public static List<Transaccion> ExtraerTransacciones(String rutaDocumentos) throws VenturaExcepcion {
        List<Transaccion> trans = new ArrayList<>();
        try {
            System.out.println("");
        } catch (Exception e){
        
        }/*catch (VenturaExcepcion ve) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ve.getMessage()});
            throw ve;
        }*/ finally {
            System.gc();
        }
        return null;
    }

    public static String validarRutaArchivos(String rutaDocumentos) throws VenturaExcepcion {
        File file = new File(rutaDocumentos);
        if (file.isDirectory()) {
            return "OK";
        }
        return "FALSE";
    }

}
