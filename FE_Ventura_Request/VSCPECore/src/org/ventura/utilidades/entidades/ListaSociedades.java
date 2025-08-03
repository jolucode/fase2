/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.entidades;

/**
 * @author VSUSER
 */
public class ListaSociedades {


    public String rucSociedad;

    public byte[] rutaCD;

    public String passwordCD;

    public String usuarioSec;

    public String passwordSec;

    public String idDB;

    public String logoSociedad;


    //GUIAS
    public String tipoIntegracionGuias;
    public String clientId;
    public String secretId;
    public String usuarioGuias;
    public String passwordGuias;
    public String scope;




    public ListaSociedades(String rucSociedad, byte[] rutaCD, String passwordCD, String usuarioSec, String passwordSec, String logoSociedad
                           ,String tipoIntegracionGuias, String clientId, String secretId, String usuarioGuias, String passwordGuias,  String  scope) {

        this.rutaCD = rutaCD;
        this.rucSociedad = rucSociedad;
        this.passwordCD = passwordCD;
        this.usuarioSec = usuarioSec;
        this.passwordSec = passwordSec;
        this.logoSociedad = logoSociedad;

        //GUIAS
        this.tipoIntegracionGuias = tipoIntegracionGuias;
        this.clientId = clientId;
        this.secretId = secretId;
        this.usuarioGuias = usuarioGuias;
        this.passwordGuias = passwordGuias;
        this.scope = scope;

    }

    public String getRucSociedad() {
        return rucSociedad;
    }

    public byte[] getRutaCD() {
        return rutaCD;
    }

    public void setRutaCD(byte[] rutaCD) {
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

    public String getUsuarioGuias() {
        return usuarioGuias;
    }

    public void setUsuarioGuias(String usuarioGuias) {
        this.usuarioGuias = usuarioGuias;
    }

    public String getPasswordGuias() {
        return passwordGuias;
    }

    public void setPasswordGuias(String passwordGuias) {
        this.passwordGuias = passwordGuias;
    }

    public String getTipoIntegracionGuias() {
        return tipoIntegracionGuias;
    }

    public void setTipoIntegracionGuias(String tipoIntegracionGuias) {
        this.tipoIntegracionGuias = tipoIntegracionGuias;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }
}
