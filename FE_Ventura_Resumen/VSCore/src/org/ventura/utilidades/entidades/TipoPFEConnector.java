/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.entidades;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author VSUser
 */
public class TipoPFEConnector {

    private static LinkedList<TipoPFEConnector> listaPFEConector = null;
    private String valor;
    private String descripcion;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.valor);
        return hash;
    }

    public TipoPFEConnector(String valor, String descripcion) {
        this.valor = valor;
        this.descripcion = descripcion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public static List<TipoPFEConnector> listarTipoPFEConnector() {
        if (listaPFEConector == null) {
            listaPFEConector = new LinkedList<>();
            listaPFEConector.add(new TipoPFEConnector("1", "Efact"));
            listaPFEConector.add(new TipoPFEConnector("2", "SunatConnector"));
        }
        return listaPFEConector;
    }

    public static TipoPFEConnector getByValor(String valor) {
        for (TipoPFEConnector lista2 : listaPFEConector) {
            if(lista2.valor.compareTo(valor)==0){
                return lista2;
            }
        }
        return new TipoPFEConnector("0", "No Definido");
    }
    
     @Override
    public String toString() {
        return descripcion;
    }
    
     @Override
    public boolean equals(Object o) {
        if(o instanceof TipoConector){
            TipoPFEConnector ts = (TipoPFEConnector)o;
            if(ts.valor==this.valor) return true;
        }
        return false;
    }

    

}
