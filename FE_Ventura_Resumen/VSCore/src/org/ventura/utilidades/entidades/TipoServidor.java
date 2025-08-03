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
 * @author Percy
 */
public class TipoServidor {
    
    public static String TipoServidorQuery="";
    private static LinkedList<TipoServidor> lista = null;
    
    private String valor;

    public static LinkedList<TipoServidor> getLista() {
        return lista;
    }

    public static void setLista(LinkedList<TipoServidor> lista) {
        TipoServidor.lista = lista;
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

    public TipoServidor(String valor, String descripcion) {
        this.valor = valor;
        this.descripcion = descripcion;
    }
    
    public static List<TipoServidor> listarTodos(){
        if(lista==null){
            lista = new LinkedList<>();
            lista.add(new TipoServidor("4","Microsoft SQL 2005"));
            lista.add(new TipoServidor("6","Microsoft SQL 2008"));
            lista.add(new TipoServidor("7","Microsoft SQL 2012"));
            lista.add(new TipoServidor("8","Microsoft SQL 2014"));
            lista.add(new TipoServidor("10", "Microsoft SQL 2016"));
            lista.add(new TipoServidor("11", "Microsoft SQL 2017"));
            lista.add(new TipoServidor("15", "Microsoft SQL 2019"));
            lista.add(new TipoServidor("9","HANABD"));
            lista.add(new TipoServidor("10","Mysql"));
        }        
        return lista;
    }
    
    public static TipoServidor getByValor(String valor){
        for (TipoServidor lista1 : lista) {
            if(lista1.valor.compareTo(valor)==0) return lista1;
        }
        return new TipoServidor("0","No definido");
    }

    @Override
    public String toString() {
        return descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TipoServidor){
            TipoServidor ts = (TipoServidor)o;
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
