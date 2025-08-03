/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.core.entidades;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author VSUser
 */
public class TipoConector {

    private static LinkedList<TipoConector> listaConector = null;

    private int valor;

    private String descripcion;

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public TipoConector(int valor, String descripcion) {
        this.valor = valor;
        this.descripcion = descripcion;
    }

    public static List<TipoConector> listarConector() {
        if (listaConector == null) {
            listaConector = new LinkedList<>();
            listaConector.add(new TipoConector(1, "SAP B1 ONE 9"));
            listaConector.add(new TipoConector(2, "ERP GENERICO BD"));
            listaConector.add(new TipoConector(3, "ERP GENERICO ARCHIVOS"));
        }
        return listaConector;
    }

    public static TipoConector getByValor(int valor) {
        for (TipoConector lista1 : listaConector) {
            if (lista1.valor == valor) {
                return lista1;
            }
        }
        return new TipoConector(0, "No Definido");
    }

    @Override
    public String toString() {
        return descripcion;
    }
    
     @Override
    public boolean equals(Object o) {
        if(o instanceof TipoConector){
            TipoConector ts = (TipoConector)o;
            if(ts.valor==this.valor) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.valor;
        return hash;
    }
    
}
