/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.entidades;

/**
 *
 * @author VSUSER
 */
public class ListaSociedades {
    
    public  String rucSociedad ;
    public  byte[] rutaCD;
    public  String passwordCD ;
    public  String usuarioSec ;
    public  String passwordSec ;
    public  String idDB ;
    public  String logoSociedad;
    public ListaSociedades(String rucSociedad , byte[] rutaCD,String passwordCD, String usuarioSec, String passwordSec, String logoSociedad) {
        this.rutaCD = rutaCD;
        this.rucSociedad = rucSociedad;
        this.passwordCD = passwordCD;
        this.usuarioSec = usuarioSec;
        this.passwordSec =  passwordSec;
        this.logoSociedad = logoSociedad;
    }

    public  String getRucSociedad() {
        return rucSociedad;
    }

    public  byte[] getRutaCD() {
        return rutaCD;
    }

    public void setRutaCD( byte[] rutaCD) {
        this.rutaCD = rutaCD;
    }

    public String getPasswordCD() {
        return passwordCD;
    }

    public void setPasswordCD(String passwordCD) {
        this.passwordCD = passwordCD;
    }

    public String getUsuarioSec() {
        return usuarioSec;
    }

    public void setUsuarioSec(String usuarioSec) {
        this.usuarioSec = usuarioSec;
    }

    public String getPasswordSec() {
        return passwordSec;
    }

    public void setPasswordSec(String passwordSec) {
        this.passwordSec = passwordSec;
    }

    public String getIdDB() {
        return idDB;
    }

    public void setIdDB(String idDB) {
        this.idDB = idDB;
    }
    public String getLogoSociedad() {
        return logoSociedad;
    }

    public void setLogoSociedad(String logoSociedad) {
        this.logoSociedad = logoSociedad;
    }
}
