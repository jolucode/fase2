package org.ventura.cpe.publicador;

public class Main {

    public static void main(String[] args) {
        String valor = "Invalid address:  (to): aberio<br>" + "{\"mensaje\":\"Documento [20100029741-F002-00019696] registrado correctamente\"}";
        int indexOf = valor.indexOf("{");
        String substring = valor.substring(indexOf);
        System.out.println(substring);
    }
}
