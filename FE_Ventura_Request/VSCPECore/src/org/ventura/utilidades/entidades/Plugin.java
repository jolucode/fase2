/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.entidades;

import javax.swing.table.DefaultTableModel;
import java.util.List;

/**
 * @author VSUser
 */
public class Plugin {

    String pnombre;

    DefaultTableModel pdatatable;

    String pruta;

    List<Propiedad> plistaPropiedad;

    String prutaLibreria;

    public String getPrutaLibreria() {
        return prutaLibreria;
    }

    public void setPrutaLibreria(String prutaLibreria) {
        this.prutaLibreria = prutaLibreria;
    }

    public String getPnombre() {
        return pnombre;
    }

    public void setPnombre(String pnombre) {
        this.pnombre = pnombre;
    }

    public DefaultTableModel getPdatatable() {
        return pdatatable;
    }

    public void setPdatatable(DefaultTableModel pdatatable) {
        this.pdatatable = pdatatable;
    }

    public String getPruta() {
        return pruta;
    }

    public void setPruta(String pruta) {
        this.pruta = pruta;
    }

    public List<Propiedad> getPlistaPropiedad() {
        return plistaPropiedad;
    }

    public void setPlistaPropiedad(List<Propiedad> plistaPropiedad) {
        this.plistaPropiedad = plistaPropiedad;
    }

}
