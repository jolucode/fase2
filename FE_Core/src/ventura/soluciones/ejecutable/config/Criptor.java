/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ventura.soluciones.ejecutable.config;

import org.apache.commons.codec.binary.Base64;

public class Criptor {

    private static final int shift = 5;

    public static String Desencriptar(String mensaje) {
        String msj = "";
        mensaje = new String(Base64.decodeBase64(mensaje.getBytes()));
        for (int i = 0; i < mensaje.length(); i++) {
            msj += String.valueOf((char) (((int) mensaje.charAt(i)) - shift));
        }
        msj = new String(Base64.decodeBase64(msj.getBytes()));
        return msj;
    }
}
