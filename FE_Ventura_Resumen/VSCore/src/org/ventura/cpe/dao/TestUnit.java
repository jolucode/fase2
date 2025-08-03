/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dao;

import org.ventura.cpe.dao.controller.TransaccionBajaJC;

import static org.ventura.cpe.dao.conexion.HBPersistencia.*;

/**
 *
 * @author VSUser
 */
public class TestUnit {

    public static void main(String[] args) {

        try {
            
            /*
            String sr="localhost";
            String pt="3306";
            String bd="vscpebd";
            USER="root";
            PASSWORD="B1AdminB1Admin";
            TIPOSERVIDOR="11";
            CONNSTRING ="jdbc:mysql://" + sr + "/" + bd;
            */
            
            String sr="VS-LT-06";
            String pt="1433";
            String bd="vscpebd";
            USER="sa";
            PASSWORD="B1Admin";
            TIPOSERVIDOR="10";
            
            CONNSTRING  = "jdbc:sqlserver://"+ sr +":"+pt+";databaseName="+bd+";user="+USER+"; password="+PASSWORD+";";
            
            String lista="";
            lista = TransaccionBajaJC.idBaja();
            System.out.println(lista);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
