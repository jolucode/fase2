/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.generica.dao.conexion;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VSUser
 */
public class Persistence {

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


    public static EntityManagerFactory getInstancia() {
        if (emf == null) {
            if (TIPOSERVIDOR.equalsIgnoreCase("10")) {
                DRIVER = "com.mysql.jdbc.Driver";
            } else {
                DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            }
            //emf = Persistence.createEntityManagerFactory("VSCPEDTOPU");
            Map<String, String> dbProps = new HashMap();
            dbProps.put("javax.persistence.jdbc.url", CONNSTRING);
            dbProps.put("javax.persistence.jdbc.user", USER);
            dbProps.put("javax.persistence.jdbc.password", PASSWORD);
            dbProps.put("javax.persistence.jdbc.driver", DRIVER);
            dbProps.put("javax.persistence.schema-generation.database.action", "create");
            emf = javax.persistence.Persistence.createEntityManagerFactory(PERSISTENCE, dbProps);
        }
        return emf;
    }

}
