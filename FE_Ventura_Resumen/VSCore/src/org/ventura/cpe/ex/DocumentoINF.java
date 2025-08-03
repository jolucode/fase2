/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.ex;

import com.sap.smb.sbo.api.ICompany;
import org.ventura.cpe.dto.hb.BdsMaestras;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import java.util.List;

/**
 * @author VSUser
 */
public abstract class DocumentoINF {

    public static DocumentoINF Conector;

    public static DocumentoINF GetEnviarTransaccion;

    public static DocumentoINF GetEnviarConsulta;

    public static DocumentoINF GetEnviarResumenDiario;

    public static DocumentoINF GetConsultarResumenDiario;

    public static DocumentoINF getGetEnviarResumenDiario() {
        return GetEnviarResumenDiario;
    }

    private String SERVIDOR_LICENCIAS;

    private String BD_TIPO;

    private String SERVIDOR_BASEDATOS;

    private String BD_NOMBRE;

    private String BD_USER;

    private String BD_PASS;

    private String ERP_USER;

    private String ERP_PASS;

    private String ERPRutaArchivos;

    private String ERPNombreSociedad;

    private List<BdsMaestras> listBdsMaestras;

    public List<BdsMaestras> getListBdsMaestras() {
        return listBdsMaestras;
    }

    public void setListBdsMaestras(List<BdsMaestras> listBdsMaestras) {
        this.listBdsMaestras = listBdsMaestras;
    }


    public String getERPNombreSociedad() {
        return ERPNombreSociedad;
    }

    public void setERPNombreSociedad(String ERPNombreSociedad) {
        this.ERPNombreSociedad = ERPNombreSociedad;
    }

    public String getERPRutaArchivos() {
        return ERPRutaArchivos;
    }

    public void setERPRutaArchivos(String ERPRutaArchivos) {
        this.ERPRutaArchivos = ERPRutaArchivos;
    }

    public String getSERVIDOR_LICENCIAS() {
        return SERVIDOR_LICENCIAS;
    }

    public void setSERVIDOR_LICENCIAS(String SERVIDOR_LICENCIAS) {
        this.SERVIDOR_LICENCIAS = SERVIDOR_LICENCIAS;
    }

    public String getBD_TIPO() {
        return BD_TIPO;
    }

    public void setBD_TIPO(String BD_TIPO) {
        this.BD_TIPO = BD_TIPO;
    }

    public String getSERVIDOR_BASEDATOS() {
        return SERVIDOR_BASEDATOS;
    }

    public void setSERVIDOR_BASEDATOS(String SERVIDOR_BASEDATOS) {
        this.SERVIDOR_BASEDATOS = SERVIDOR_BASEDATOS;
    }

    public String getBD_NOMBRE() {
        return BD_NOMBRE;
    }

    public void setBD_NOMBRE(String BD_NOMBRE) {
        this.BD_NOMBRE = BD_NOMBRE;
    }

    public String getBD_USER() {
        return BD_USER;
    }

    public void setBD_USER(String BD_USER) {
        this.BD_USER = BD_USER;
    }

    public String getBD_PASS() {
        return BD_PASS;
    }

    public void setBD_PASS(String BD_PASS) {
        this.BD_PASS = BD_PASS;
    }

    public String getERP_USER() {
        return ERP_USER;
    }

    public void setERP_USER(String ERP_USER) {
        this.ERP_USER = ERP_USER;
    }

    public String getERP_PASS() {
        return ERP_PASS;
    }

    public void setERP_PASS(String ERP_PASS) {
        this.ERP_PASS = ERP_PASS;
    }

    public abstract boolean CargarParametro(Document doc, XPath xPath);

    public abstract String Conectar();

    public abstract void Desconectar();

    public abstract List<TransaccionResumen> ExtraerResumen(String fechaEmision, int correlativo, ICompany Sociedad) throws VenturaExcepcion;

}
