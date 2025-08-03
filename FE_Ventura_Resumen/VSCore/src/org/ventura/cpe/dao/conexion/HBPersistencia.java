/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.conexion;

import org.ventura.cpe.dto.hb.BdsMaestras;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Percy
 */
public class HBPersistencia {

    private static EntityManagerFactory emf = null;
    public static String CONNSTRING = "";
    public static String USER = "";
    public static String PASSWORD = "";
    public static String DRIVER = "";
    public static String PUERTO = "";
    public static String BASEDATOS = "";
    public static String SERVIDOR = "";
    public static String TIPOSERVIDOR = "";
    public static String PERSISTENCE = "VSCPEDAOPU";
    public static List<BdsMaestras> LISTBDANADIDAS;

    public static EntityManagerFactory getInstancia() {
        if (emf == null) {
            if (TIPOSERVIDOR.equalsIgnoreCase("10") ||TIPOSERVIDOR.equalsIgnoreCase("10")) {
                DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            } else {
                if (TIPOSERVIDOR.equalsIgnoreCase("11")) {
                    DRIVER = "com.mysql.cj.jdbc.Driver";
                }
            }
            //emf = Persistence.createEntityManagerFactory("VSCPEDTOPU");
            Map<String, String> dbProps = new HashMap();
            dbProps.put("javax.persistence.jdbc.url", CONNSTRING);
            dbProps.put("javax.persistence.jdbc.user", USER);
            dbProps.put("javax.persistence.jdbc.password", PASSWORD);
            dbProps.put("javax.persistence.jdbc.driver", DRIVER);
            dbProps.put("javax.persistence.schema-generation.database.action", "create");
            emf = Persistence.createEntityManagerFactory(PERSISTENCE, dbProps);
        }
        return emf;
    }
}
