package org.ventura.cpe.core.util;

public class ConnectedCompany {

    private String rucSociedad;

    private String rutaCD;

    private String passwordCD;

    private String usuarioSec;

    private String passwordSec;

    private String databaseName;

    private String logoSociedad;

    public ConnectedCompany() {
    }

    public ConnectedCompany(String rucSociedad, String rutaCD, String passwordCD, String usuarioSec, String passwordSec, String databaseName, String logoSociedad) {
        this.rucSociedad = rucSociedad;
        this.rutaCD = rutaCD;
        this.passwordCD = passwordCD;
        this.usuarioSec = usuarioSec;
        this.passwordSec = passwordSec;
        this.databaseName = databaseName;
        this.logoSociedad = logoSociedad;
    }

    public String getRucSociedad() {
        return rucSociedad;
    }

    public void setRucSociedad(String rucSociedad) {
        this.rucSociedad = rucSociedad;
    }

    public String getRutaCD() {
        return rutaCD;
    }

    public void setRutaCD(String rutaCD) {
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

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getLogoSociedad() {
        return logoSociedad;
    }

    public void setLogoSociedad(String logoSociedad) {
        this.logoSociedad = logoSociedad;
    }
}
