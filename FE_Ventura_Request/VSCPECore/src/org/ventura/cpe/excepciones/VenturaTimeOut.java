/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.excepciones;

/**
 * @author Percy
 */
public class VenturaTimeOut extends Exception {

    public VenturaTimeOut() {

    }

    public VenturaTimeOut(Throwable thrwbl) {
        super(thrwbl);
    }

    public VenturaTimeOut(String mensaje) {
        super(mensaje);
    }

    public VenturaTimeOut(String mensaje, Exception ex) {
        super(mensaje, ex.getCause());
    }
}
