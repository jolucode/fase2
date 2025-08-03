/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author VsUser
 */
@Entity
@Table(name = "BDS_MAESTRAS")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "BdsMaestras.findAll", query = "SELECT b FROM BdsMaestras b")
        , @NamedQuery(name = "BdsMaestras.findByBDId", query = "SELECT b FROM BdsMaestras b WHERE b.bDId = :bDId")
        , @NamedQuery(name = "BdsMaestras.findByBDNombre", query = "SELECT b FROM BdsMaestras b WHERE b.bDNombre = :bDNombre")
        , @NamedQuery(name = "BdsMaestras.findByBDUsuario", query = "SELECT b FROM BdsMaestras b WHERE b.bDUsuario = :bDUsuario")
        , @NamedQuery(name = "BdsMaestras.findByBDClave", query = "SELECT b FROM BdsMaestras b WHERE b.bDClave = :bDClave")
        , @NamedQuery(name = "BdsMaestras.findByERPUsuario", query = "SELECT b FROM BdsMaestras b WHERE b.eRPUsuario = :eRPUsuario")
        , @NamedQuery(name = "BdsMaestras.findByERPClave", query = "SELECT b FROM BdsMaestras b WHERE b.eRPClave = :eRPClave")
        , @NamedQuery(name = "BdsMaestras.findByRucSociedad", query = "SELECT b FROM BdsMaestras b WHERE b.rucSociedad = :rucSociedad")
        , @NamedQuery(name = "BdsMaestras.findByRutaCD", query = "SELECT b FROM BdsMaestras b WHERE b.rutaCD = :rutaCD")
        , @NamedQuery(name = "BdsMaestras.findByPasswordCD", query = "SELECT b FROM BdsMaestras b WHERE b.passwordCD = :passwordCD")
        , @NamedQuery(name = "BdsMaestras.findByUsuarioSec", query = "SELECT b FROM BdsMaestras b WHERE b.usuarioSec = :usuarioSec")
        , @NamedQuery(name = "BdsMaestras.findByPasswordSec", query = "SELECT b FROM BdsMaestras b WHERE b.passwordSec = :passwordSec")
        , @NamedQuery(name = "BdsMaestras.findByUsuarioGuias", query = "SELECT b FROM BdsMaestras b WHERE b.usuarioGuias = :usuarioGuias")
        , @NamedQuery(name = "BdsMaestras.findByPasswordGuias", query = "SELECT b FROM BdsMaestras b WHERE b.passwordGuias = :passwordGuias")
        , @NamedQuery(name = "BdsMaestras.findByWebServicePublicacion", query = "SELECT b FROM BdsMaestras b WHERE b.webservicePublicacion = :webservicePublicacion")
        , @NamedQuery(name = "BdsMaestras.findByUsuarioWebService", query = "SELECT b FROM BdsMaestras b WHERE b.usuarioWebService = :usuarioWebService")
        , @NamedQuery(name = "BdsMaestras.findByPasswordWebService", query = "SELECT b FROM BdsMaestras b WHERE b.passwordWebService = :passwordWebService")
        , @NamedQuery(name = "BdsMaestras.findByLogoSociedad", query = "SELECT b FROM BdsMaestras b WHERE b.logoSociedad = :logoSociedad")
        , @NamedQuery(name = "BdsMaestras.findByTipoERP", query = "SELECT b FROM BdsMaestras b WHERE b.tipoERP = :tipoERP")
        , @NamedQuery(name = "BdsMaestras.findByTipoIntegracionSunat", query = "SELECT b FROM BdsMaestras b WHERE b.tipoIntegracionSunat = :tipoIntegracionSunat")
        , @NamedQuery(name = "BdsMaestras.findByTipoIntegracionGuias", query = "SELECT b FROM BdsMaestras b WHERE b.tipoIntegracionGuias = :tipoIntegracionGuias")
        , @NamedQuery(name = "BdsMaestras.findByClientID", query = "SELECT b FROM BdsMaestras b WHERE b.clientID = :clientID")
        , @NamedQuery(name = "BdsMaestras.findBySecretID", query = "SELECT b FROM BdsMaestras b WHERE b.secretID = :secretID")
        , @NamedQuery(name = "BdsMaestras.findByScope", query = "SELECT b FROM BdsMaestras b WHERE b.scope = :scope")
        , @NamedQuery(name = "BdsMaestras.findByPosicion", query = "SELECT b FROM BdsMaestras b WHERE b.posicion = :posicion")
        , @NamedQuery(name = "BdsMaestras.findByRutaArchivos", query = "SELECT b FROM BdsMaestras b WHERE b.rutaArchivos = :rutaArchivos")
        })
public class BdsMaestras implements Serializable {





    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "BD_Id")
    private String bDId;

    @Basic(optional = false)
    @Column(name = "BD_Servidor")
    private String bDServidor;

    @Basic(optional = false)
    @Column(name = "Tipo_ERP")
    private String tipoERP;

    @Basic(optional = false)
    @Column(name = "Ruta_Archivos")
    private String rutaArchivos;

    @Basic(optional = false)
    @Column(name = "BD_Nombre")
    private String bDNombre;

    @Basic(optional = false)
    @Column(name = "BD_Usuario")
    private String bDUsuario;

    @Basic(optional = false)
    @Column(name = "BD_Clave")
    private String bDClave;

    @Basic(optional = false)
    @Column(name = "Tipo_Servidor")
    private String tipoServidor;

    @Basic(optional = false)
    @Column(name = "ERP_Usuario")
    private String eRPUsuario;

    @Basic(optional = false)
    @Column(name = "ERP_Clave")
    private String eRPClave;

    @Basic(optional = false)
    @Column(name = "Ruc_Sociedad")
    private String rucSociedad;

    @Basic(optional = false)
    @Column(name = "Ruta_CD")
    private String rutaCD;

    @Basic(optional = false)
    @Column(name = "Password_CD")
    private String passwordCD;

    @Basic(optional = false)
    @Column(name = "Usuario_Sec")
    private String usuarioSec;

    @Basic(optional = false)
    @Column(name = "Password_Sec")
    private String passwordSec;
    


    @Basic(optional = false)
    @Column(name = "Logo_Sociedad")
    private String logoSociedad;

    @Basic(optional = false)
    @Column(name = "Servidor_Licencia")
    private String servidorLicencia;

    @Basic(optional = false)
    @Column(name = "Tipo_Integracion_Sunat")
    private String tipoIntegracionSunat;

    @Basic(optional = false)
    @Column(name = "TipoIntegracionGuias")
    private String tipoIntegracionGuias;
    
    @Basic(optional = false)
    @Column(name = "ClientId")
    private String clientID;
    
    @Basic(optional = false)
    @Column(name = "ClientSecret")
    private String secretID;

    @Basic(optional = false)
    @Column(name = "Usuario_Guias")
    private String usuarioGuias;

    @Basic(optional = false)
    @Column(name = "Password_Guias")
    private String passwordGuias;

    @Basic(optional = false)
    @Column(name = "Scope")
    private String scope;

    @Basic(optional = false)
    @Column(name = "Posicion")
    private Integer posicion;

    //Datos nuevo Web URL
    @Basic(optional = false)
    @Column(name = "Web_Service_Publicacion")
    private String webservicePublicacion;

    @Basic(optional = false)
    @Column(name = "Usuario_Web_Service")
    private String usuarioWebService;

    @Basic(optional = false)
    @Column(name = "Password_Web_Service")
    private String passwordWebService;


    public BdsMaestras() {
    }

    public BdsMaestras(String bDId) {
        this.bDId = bDId;
    }

    public BdsMaestras(String bDId, String bDServidor, String bDNombre, String bDUsuario, String bDClave, String eRPUsuario,
                       String eRPClave, String rucSociedad, String rutaCD, String passwordCD, String usuarioSec, String passwordSec,  String usuarioGuias, String passwordGuias, String logoSociedad,
                       String webservicePublicacion,String usuarioWebService, String passwordWebService,String tipoERP,String rutaArchivos,String tipoIntegracionSunat, String tipoIntegracionGuias,String clientID,String secretID,String scope, Integer posicion) {
        this.bDId = bDId;
        this.bDServidor = bDServidor;
        this.bDNombre = bDNombre;
        this.bDUsuario = bDUsuario;
        this.bDClave = bDClave;
        this.eRPUsuario = eRPUsuario;
        this.eRPClave = eRPClave;
        this.rucSociedad = rucSociedad;
        this.rutaCD = rutaCD;
        this.passwordCD = passwordCD;
        this.usuarioSec = usuarioSec;
        this.passwordSec = passwordSec;
        // Datos Guias
        this.tipoIntegracionGuias = tipoIntegracionGuias;
        this.usuarioGuias = usuarioGuias;
        this.passwordGuias = passwordGuias;
        this.logoSociedad = logoSociedad;
        //Datos nuevos
        this.webservicePublicacion = webservicePublicacion;
        this.usuarioWebService = usuarioWebService;
        this.passwordWebService = passwordWebService;
        this.tipoERP = tipoERP;
        this.rutaArchivos = rutaArchivos;
        this.clientID = clientID;
        this.secretID = secretID;
        this.scope = scope;
        this.posicion = posicion;
        this.tipoIntegracionSunat = tipoIntegracionSunat;
    }


    public String getBDId() {
        return bDId;
    }

    public void setBDId(String bDId) {
        this.bDId = bDId;
    }

    public String getTipoERP() {
        return tipoERP;
    }

    public void setTipoERP(String tipoERP) {
        this.tipoERP = tipoERP;
    }
    public String getRutaArchivos() {
        return rutaArchivos;
    }

    public void setRutaArchivos(String rutaArchivos) {
        this.rutaArchivos = rutaArchivos;
    }
    public String getBDNombre() {
        return bDNombre;
    }

    public void setBDNombre(String bDNombre) {
        this.bDNombre = bDNombre;
    }

    public String getBDUsuario() {
        return bDUsuario;
    }

    public void setBDUsuario(String bDUsuario) {
        this.bDUsuario = bDUsuario;
    }

    public String getBDClave() {
        return bDClave;
    }

    public void setBDClave(String bDClave) {
        this.bDClave = bDClave;
    }

    public String getERPUsuario() {
        return eRPUsuario;
    }

    public void setERPUsuario(String eRPUsuario) {
        this.eRPUsuario = eRPUsuario;
    }

    public String getERPClave() {
        return eRPClave;
    }

    public void setERPClave(String eRPClave) {
        this.eRPClave = eRPClave;
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

    public String getLogoSociedad() {
        return logoSociedad;
    }

    public void setLogoSociedad(String logoSociedad) {
        this.logoSociedad = logoSociedad;
    }

    public String getbDId() {
        return bDId;
    }

    public String getServidorLicencia() {
        return servidorLicencia;
    }

    public void setServidorLicencia(String servidorLicencia) {
        this.servidorLicencia = servidorLicencia;
    }

    public void setbDId(String bDId) {
        this.bDId = bDId;
    }

    public String getbDServidor() {
        return bDServidor;
    }

    public void setbDServidor(String bDServidor) {
        this.bDServidor = bDServidor;
    }

    public String getbDNombre() {
        return bDNombre;
    }

    public void setbDNombre(String bDNombre) {
        this.bDNombre = bDNombre;
    }

    public String getbDUsuario() {
        return bDUsuario;
    }

    public void setbDUsuario(String bDUsuario) {
        this.bDUsuario = bDUsuario;
    }

    public String getbDClave() {
        return bDClave;
    }

    public void setbDClave(String bDClave) {
        this.bDClave = bDClave;
    }

    public String geteRPUsuario() {
        return eRPUsuario;
    }

    public void seteRPUsuario(String eRPUsuario) {
        this.eRPUsuario = eRPUsuario;
    }

    public String geteRPClave() {
        return eRPClave;
    }

    public void seteRPClave(String eRPClave) {
        this.eRPClave = eRPClave;
    }

    public String getPasswordWebService() {
        return passwordWebService;
    }

    public void setPasswordWebService(String passwordWebService) {
        this.passwordWebService = passwordWebService;
    }
    public String getUsuarioWebService() {
        return usuarioWebService;
    }

    public void setUsuarioWebService(String usuarioWebService) {
        this.usuarioWebService = usuarioWebService;
    }
    public String getWebservicePublicacion() {
        return webservicePublicacion;
    }
    public void setWebservicePublicacion(String webservicePublicacion) {
        this.webservicePublicacion = webservicePublicacion;
    }
     /**
     * @return the tipoIntegracionSunat
     */
    public String getTipoIntegracionSunat() {
        return tipoIntegracionSunat;
    }

    /**
     * @param tipoIntegracionSunat the tipoIntegracionSunat to set
     */
    public void setTipoIntegracionSunat(String tipoIntegracionSunat) {
        this.tipoIntegracionSunat = tipoIntegracionSunat;
    }
    
    public String getTipoIntegracionGuias() {
        return tipoIntegracionGuias;
    }

    public void setTipoIntegracionGuias(String tipoIntegracionGuias) {
        this.tipoIntegracionGuias = tipoIntegracionGuias;
    }
    
    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }
    
    public String getSecretID() {
        return secretID;
    }

    public void setSecretID(String secretID) {
        this.secretID = secretID;
    }
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * @return the posicion
     */
    public Integer getPosicion() {
        return posicion;
    }

    /**
     * @param posicion the posicion to set
     */
    public void setPosicion(Integer posicion) {
        this.posicion = posicion;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bDId != null ? bDId.hashCode() : 0);
        return hash;
    }

    public String getTipoServidor() {
        return tipoServidor;
    }

    public void setTipoServidor(String tipoServidor) {
        this.tipoServidor = tipoServidor;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof BdsMaestras)) {
            return false;
        }
        BdsMaestras other = (BdsMaestras) object;
        if ((this.bDId == null && other.bDId != null) || (this.bDId != null && !this.bDId.equals(other.bDId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.BdsMaestras[ bDId=" + bDId + " ]";
    }

}
