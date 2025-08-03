/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.test;


import org.ventura.utilidades.encriptacion.Criptor;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * @author VSUser
 */
public class testAll {

    public static void main(String[] args) throws ParseException {


        System.out.println(Criptor.Desencriptar("Z29GcFI3Vn1ef1JC"));

//        BigDecimal bg = new BigDecimal("0.00");
//        for (int i = 0; i < 10; i++) {
//            bg = bg.add(new BigDecimal(i));
//            System.out.println(bg);
//        }

    }

    public static void imprimir() throws ParseException {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        System.out.println(year + "/" + month + "/" + day);
    }

    public void Buscar() {


    }


}


