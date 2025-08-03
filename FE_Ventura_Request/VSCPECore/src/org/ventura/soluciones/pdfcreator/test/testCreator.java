/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.soluciones.pdfcreator.test;

import org.ventura.soluciones.pdfcreator.handler.PDFGenerateHandler;

/**
 * @author VS-LT-06
 */
public class testCreator {

    public static void main(String[] args) {
        PDFGenerateHandler.generatePDF417Code("ñañoñaaaá", "D:\\temp\\image.png", 400, 200, 1);


    }

}
