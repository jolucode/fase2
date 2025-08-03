package org.ventura.utilidades.entidades;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TipoIntegracionWS {
    private static LinkedList<TipoIntegracionWS> listaIntegracionWS = null;

    private String valor;
    private String descripcion;

    public TipoIntegracionWS(String valor, String descripcion) {
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

    public static List<TipoIntegracionWS> listarTodos() {
        if (listaIntegracionWS == null) {
            listaIntegracionWS = new LinkedList<>();
            listaIntegracionWS.add(new TipoIntegracionWS("SUNAT", "SUNAT"));
            listaIntegracionWS.add(new TipoIntegracionWS("OSE", "OSE"));
            listaIntegracionWS.add(new TipoIntegracionWS("CONOSE", "CONOSE"));

        }
        return listaIntegracionWS;
    }

    public static TipoIntegracionWS getByValor(String valor) {
        for (TipoIntegracionWS item : listaIntegracionWS) {
            if(item.valor.compareTo(valor) == 0) {
                return item;
            }
        }
        return new TipoIntegracionWS("-", "No Definido");
    }

    @Override
    public String toString() {
        return descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TipoIntegracionWS){
            TipoIntegracionWS obj = (TipoIntegracionWS)o;
            if(obj.valor == this.valor) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.valor);
        return hash;
    }

}
