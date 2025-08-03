/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.sunat;

import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.TransaccionRespuesta.Sunat;
import org.ventura.cpe.excepciones.VenturaExcepcion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author VSUser
 */
public class test {

    public static void main(String[] args) {

        FileInputStream fileInputStream = null;

        File file = new File("D:\\R-20101200125-RA-20160719-00003.zip");

        byte[] bFile = new byte[(int) file.length()];

        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bFile);
            fileInputStream.close();

            RespuestaSunat.AmbienteSunat = 2;

            Sunat sunatResponse = RespuestaSunat.SetRespuestaSUNAT(bFile, 2, "01");
            TransaccionRespuesta tr = new TransaccionRespuesta();
            tr.setCodigo(TransaccionRespuesta.RQT_EMITIDO_APROBADO);
            tr.setIdentificador(sunatResponse.getId());
            tr.setCodigoWS(sunatResponse.getCodigo());
            tr.setSunat(sunatResponse);
            tr.setMensaje(sunatResponse.getMensaje());

            System.out.println("Done");
            System.out.println(tr.getSunat().toString());
        } catch (IOException | VenturaExcepcion e) {
            System.out.println("Error " + e.getMessage());
        }

    }

}
