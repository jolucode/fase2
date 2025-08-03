/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.sunat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author VSUser
 */
public class Test2 {

    public static void main(String[] args) {

        String datos = "soap-env:Client.0100";
        List<Character> alfa = new ArrayList<>();
        List<Integer> numeros = new ArrayList<>();
        char[] toCharArray = datos.toCharArray();
        for (int i = 0; i < datos.length(); i++) {
            char caracter = toCharArray[i];
            if (Character.isDigit(caracter)) {
                numeros.add((int) caracter);
            } else {
                if (Character.isLetter(caracter)) {
                    alfa.add(caracter);
                }
            }
        }
        System.out.println("caracteres: " + alfa);
        System.out.println("numeros: " + numeros);

//        String faultcode = "soap-env:Server.0835";
//        int codeExcepcion;
//        try {
//            codeExcepcion=Integer.valueOf(faultcode);
//        } catch (NumberFormatException e) {
//            faultcode = faultcode.substring(faultcode.length()-4, faultcode.length());
//            System.out.println(faultcode);
//            codeExcepcion = Integer.valueOf(faultcode);
//        }
//        System.out.println(String.valueOf(codeExcepcion));
    }

}
