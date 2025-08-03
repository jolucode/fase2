/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.test;

import org.ventura.cpe.dao.controller.TransaccionJC;
import org.ventura.cpe.dao.exceptions.NonexistentEntityException;
import org.ventura.cpe.dto.hb.Transaccion;

import java.io.*;

/**
 *
 * @author VSUser
 */
public class EliminarAutomatico {

    public static void main(String[] args) {
        BufferedReader br = null;
        try {
            String ruta = System.getProperty("user.dir") + File.separator + "src" + File.separator + "org" + File.separator + "ventura" + File.separator + "cpe" + File.separator + "dao" + File.separator + "test" + File.separator + "Eliminar.txt";

            String sCurrentLine;
            br = new BufferedReader(new FileReader(ruta));

            while ((sCurrentLine = br.readLine()) != null) {
                Transaccion trans = TransaccionJC.findTransaccionCola(sCurrentLine);

                TransaccionJC.destroy(sCurrentLine);
                System.out.println("Se elimino correctamente");

            }

        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (NonexistentEntityException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }

    }

}
