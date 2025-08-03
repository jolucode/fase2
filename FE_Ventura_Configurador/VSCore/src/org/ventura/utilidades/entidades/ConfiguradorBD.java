package org.ventura.utilidades.entidades;

/**
 *
 * @author Jhony Monzalve
 */
public class ConfiguradorBD {
    private String BD_Id;
    private String BD_Servidor;
    private String BD_Nombre;
    private String BD_Usuario;
    private String BD_Clave;
    private String ERP_Usuario;
    private String ERP_Clave;

    public ConfiguradorBD() {
    }

    public String getBD_Id() {
        return BD_Id;
    }

    public void setBD_Id(String BD_Id) {
        this.BD_Id = BD_Id;
    }

    public String getBD_Servidor() {
        return BD_Servidor;
    }

    public void setBD_Servidor(String BD_Servidor) {
        this.BD_Servidor = BD_Servidor;
    }

    public String getBD_Nombre() {
        return BD_Nombre;
    }

    public void setBD_Nombre(String BD_Nombre) {
        this.BD_Nombre = BD_Nombre;
    }

    public String getBD_Usuario() {
        return BD_Usuario;
    }

    public void setBD_Usuario(String BD_Usuario) {
        this.BD_Usuario = BD_Usuario;
    }

    public String getBD_Clave() {
        return BD_Clave;
    }

    public void setBD_Clave(String BD_Clave) {
        this.BD_Clave = BD_Clave;
    }

    public String getERP_Usuario() {
        return ERP_Usuario;
    }

    public void setERP_Usuario(String ERP_Usuario) {
        this.ERP_Usuario = ERP_Usuario;
    }

    public String getERP_Clave() {
        return ERP_Clave;
    }

    public void setERP_Clave(String ERP_Clave) {
        this.ERP_Clave = ERP_Clave;
    }
}
