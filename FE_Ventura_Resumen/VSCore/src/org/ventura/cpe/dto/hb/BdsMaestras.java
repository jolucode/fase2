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
 * @author Jhony
 */
@Entity
@Table(name = "BDS_MAESTRAS")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "BdsMaestras.findAll", query = "SELECT b FROM BdsMaestras b")
        , @NamedQuery(name = "BdsMaestras.findByBDId", query = "SELECT b FROM BdsMaestras b WHERE b.bDId = :bDId")
        , @NamedQuery(name = "BdsMaestras.findByBDSevidor", query = "SELECT b FROM BdsMaestras b WHERE b.bDSevidor = :bDSevidor")
        , @NamedQuery(name = "BdsMaestras.findByBDNombre", query = "SELECT b FROM BdsMaestras b WHERE b.bDNombre = :bDNombre")
        , @NamedQuery(name = "BdsMaestras.findByBDUsuario", query = "SELECT b FROM BdsMaestras b WHERE b.bDUsuario = :bDUsuario")
        , @NamedQuery(name = "BdsMaestras.findByBDClave", query = "SELECT b FROM BdsMaestras b WHERE b.bDClave = :bDClave")
        , @NamedQuery(name = "BdsMaestras.findByERPUsuario", query = "SELECT b FROM BdsMaestras b WHERE b.eRPUsuario = :eRPUsuario")
        , @NamedQuery(name = "BdsMaestras.findByERPClave", query = "SELECT b FROM BdsMaestras b WHERE b.eRPClave = :eRPClave")
        , @NamedQuery(name = "BdsMaestras.findByRucSociedad", query = "SELECT b FROM BdsMaestras b WHERE b.rucSociedad = :rucSociedad")
        , @NamedQuery(name = "BdsMaestras.findByRutaCD", query = "SELECT b FROM BdsMaestras b WHERE b.rutaCD = :rutaCD")
        , @NamedQuery(name = "BdsMaestras.findByPasswordCD", query = "SELECT b FROM BdsMaestras b WHERE b.passwordCD = :passwordCD")
        , @NamedQuery(name = "BdsMaestras.findByUsuarioSec", query = "SELECT b FROM BdsMaestras b WHERE b.usuarioSec = :usuarioSec")
        , @NamedQuery(name = "BdsMaestras.findByPasswordSec", query = "SELECT b FROM BdsMaestras b WHERE b.passwordSec = :passwordSec")})
public class BdsMaestras implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "BD_Id")
    private String bDId;

    @Basic(optional = false)
    @Column(name = "BD_Servidor")
    private String bDSevidor;

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


    public BdsMaestras() {
    }

    public BdsMaestras(String bDId) {
        this.bDId = bDId;
    }

    public BdsMaestras(String bDId, String bDSevidor, String bDNombre, String bDUsuario, String bDClave, String eRPUsuario, String eRPClave,
                       String rucSociedad, String rutaCD, String passwordCD, String usuarioSec, String passwordSec) {
        this.bDId = bDId;
        this.bDSevidor = bDSevidor;
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
    }

    public String getBDId() {
        return bDId;
    }

    public void setBDId(String bDId) {
        this.bDId = bDId;
    }

    public String getBDSevidor() {
        return bDSevidor;
    }

    public void setBDSevidor(String bDSevidor) {
        this.bDSevidor = bDSevidor;
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

    public String getLogoSociedad() {
        return logoSociedad;
    }

    public void setLogoSociedad(String logoSociedad) {
        this.logoSociedad = logoSociedad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bDId != null ? bDId.hashCode() : 0);
        return hash;
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
