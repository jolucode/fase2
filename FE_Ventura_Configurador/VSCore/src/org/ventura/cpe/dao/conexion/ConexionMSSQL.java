/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao.conexion;

import org.ventura.cpe.log.LoggerTrans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Percy
 */
public class ConexionMSSQL {

    public static String BASEDATOS = "";
    public static String SERVIDOR = "";
    public static String USER = "";
    public static String PASS = "";
    public static String PUERTO = "";

    public static Connection conectar() {
        try {
            String url = "jdbc:sqlserver:";
            if (!SERVIDOR.isEmpty()) {
                url += "//" + SERVIDOR;
            }
            if (!PUERTO.isEmpty()) {
                url += ":" + PUERTO;
            }
            if (!BASEDATOS.isEmpty()) {
                url += ";database=" + BASEDATOS;
            }

            Class.forName("com.mysql.jdbc.Driver");
            Connection _conn = DriverManager.getConnection(url, USER, PASS);

            return _conn;
        } catch (ClassNotFoundException | SQLException ex) {
            return null;
        }
    }

    public static boolean desconectar(Connection _conn) {
        try {
            if (_conn != null && _conn.isClosed()) {
                _conn.close();
                return true;
            }
            return false;
        } catch (SQLException ex) {
            LoggerTrans.getCDMainLogger().log(Level.OFF, ex.getMessage());
            return false;
        }
    }

    public static int ejecutarBaseDatos(String query, Connection _conn) {
        try {
            Statement stm = _conn.createStatement();
            int rs = stm.executeUpdate(query);

        } catch (SQLException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.OFF, ex.getMessage());
            return 0;
        }
        return 1;
    }

    public static boolean estarConectado(Connection _conn) {
        if (_conn != null) {
            try {
                if (!_conn.isClosed()) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(ConexionMSSQL.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return false;
    }

}
