package org;

import org.apache.commons.codec.binary.Base64;

public class Prueba {

    private static final int shift = 5;

    public static void main(String[] args) {
        String msj = "";
        String cadena = "\\\\DESKTOP-FHTRSLI\\FE_Ventura\\anexo";
        String mensaje = Base64.encodeBase64String(cadena.getBytes());
        for (int i = 0; i < mensaje.length(); i++) {
            msj += String.valueOf((char) (((int) mensaje.charAt(i)) + shift));
        }
        msj = Base64.encodeBase64String(msj.getBytes());
        System.out.println(msj);
        try {
            mensaje = new String(Base64.decodeBase64(msj.getBytes()));
            for (int i = 0; i < mensaje.length(); i++) {
                msj += String.valueOf((char) (((int) mensaje.charAt(i)) - shift));
            }
            msj = new String(Base64.decodeBase64(msj.getBytes()));
            System.out.println(msj);
        } catch (Exception ex) {
        }
    }
}
