package pe.gob.sunat.apireset;

import pe.gob.sunat.apireset.service.ClientJwtSunat;

import java.io.IOException;

public class Principal {

    public static void main(String[] args) throws IOException {
        ClientJwtSunat clientJwtSunat = new ClientJwtSunat();
        //clientJwtSunat.getJwtSunat();
        System.out.println("Solo soy pruebas");
    }


}
