/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.test;


import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import org.ventura.utilidades.encriptacion.Criptor;

/**
 *
 * @author VSUser
 */
public class testAll {

    public static void main(String[] args) throws ParseException {


        System.out.println(Criptor.Desencriptar("XUt9f19dTzdfXU9yZlx9cWg2fUxXWn1LVjZ9VlpwPkpbWlNJWFo+VA=="));
        
        System.out.println(Criptor.Encriptar(""));

        System.out.println(Criptor.Desencriptar("U39GfFJJXn9TWWxC"));

        System.out.println(Criptor.Desencriptar("XUt9WV9dTzdfXU9yZlx9cWg2fXJfXH1xXjZ9VlpxW0tWcEtZ"));

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
        int month = cal.get(Calendar.MONTH)+1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        System.out.println(year+"/"+month+"/"+day);
    }
    
    public void Buscar(){
    
        
    }
    
    
}


