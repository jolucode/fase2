package org.ventura.cpe.core.services;

import java.net.URL;
import java.net.URLConnection;

public class InternetConnectionUtils {

    public static boolean validarConexionInternet() {
        try {
            URL ruta = new URL("https://www.google.com");
            URLConnection rutaC = ruta.openConnection();
            rutaC.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
