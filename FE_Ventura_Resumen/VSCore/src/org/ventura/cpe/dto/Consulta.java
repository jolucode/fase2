/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto;

import org.ventura.cpe.dto.hb.TransaccionUsucampos;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Percy
 */
public class Consulta {
    private static List<TransaccionUsucampos> propiedades;
    public static List<TransaccionUsucampos> getPropiedades() {
        if(propiedades==null){
            propiedades = new LinkedList();
        }
        return propiedades;
    }
}
