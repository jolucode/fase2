/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.generico.dto.excepciones;

/**
 *
 * @author Percy
 */
public class VenturaExcepcion extends Exception{

    public VenturaExcepcion() {
    }

    public VenturaExcepcion(Throwable thrwbl) {
        super(thrwbl);
    }
    
    public VenturaExcepcion(String mensaje) {
        super(mensaje);
    }
       
    public VenturaExcepcion(String mensaje,Exception ex) {
        super(mensaje,ex.getCause());
    }
    
}
