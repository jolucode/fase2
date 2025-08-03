/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.core.entidades;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author VSUser
 */
public class TipoServidorBDIntermedia {
    
     private static LinkedList<TipoServidorBDIntermedia> lista = null;
    
    private String valor;

    public static LinkedList<TipoServidorBDIntermedia> getLista() {
        return lista;
    }

    public static void setLista(LinkedList<TipoServidorBDIntermedia> lista) {
        TipoServidorBDIntermedia.lista = lista;
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
    private String descripcion;

    public TipoServidorBDIntermedia(String valor, String descripcion) {
        this.valor = valor;
        this.descripcion = descripcion;
    }
    
    public static List<TipoServidorBDIntermedia> listarTodos(){
        if(lista==null){
            lista = new LinkedList<>();
            lista.add(new TipoServidorBDIntermedia("10","MSSQL"));
            lista.add(new TipoServidorBDIntermedia("11","MYSQL"));
        }        
        return lista;
    }
    
    public static TipoServidorBDIntermedia getByValor(String valor){
        for (TipoServidorBDIntermedia lista1 : lista) {
            if(lista1.valor.compareTo(valor)==0) return lista1;
        }
        return new TipoServidorBDIntermedia("0","No definido");
    }

    @Override
    public String toString() {
        return descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TipoServidorBDIntermedia){
            TipoServidorBDIntermedia ts = (TipoServidorBDIntermedia)o;
            if(ts.valor==this.valor) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.valor);
        return hash;
    }

    
}
