/*

 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.query;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author Percy
 */
public class QueryLoader {

    /**
     * @param ruta      la ruta del archivo script con los comandos
     * @param separador indica secuencia de caracteres que separa un comando de otro. Ejm. para SQL Server se utiliza "GO"
     * @return Lista de queries de la clase Query
     */
    public static QueryList DesdeArchivo(String ruta, String separador) {
        QueryList ql = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(ruta));
            String linea = "";
            while ((linea = br.readLine()) != null) {

            }
        } catch (Exception ex) {

        } finally {

        }
        return ql;
    }
}
