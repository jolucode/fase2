/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author VSUser
 */
public class VSFactory {

    private static VSFactory instance = null;

    protected VSFactory() {

    }

    public static VSFactory getInstance() {

        if (instance == null) {
            instance = new VSFactory();
        }
        return instance;

    }

    public String GetQuery(int numero, String Tipo, String[] parametros) {
        if (Tipo.equalsIgnoreCase("4") || Tipo.equalsIgnoreCase("5") || Tipo.equalsIgnoreCase("6") || Tipo.equalsIgnoreCase("7") || Tipo.equalsIgnoreCase("8") || Tipo.equalsIgnoreCase("15")) {
            Tipo = "MSSQL";
        } else {
            if (Tipo.equalsIgnoreCase("9")) {
                Tipo = "HANA";
            }
        }
        String filename = "";
        String num = String.format("%03d", numero);
        filename = "/org/ventura/cpe/query/" + num + "_" + Tipo + ".sql";
        InputStream inputStream = getClass().getResourceAsStream(filename);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        try {
            line = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(VSFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        String queryString = String.format(line, parametros);
        System.out.println(queryString);
        return queryString;
    }

}
