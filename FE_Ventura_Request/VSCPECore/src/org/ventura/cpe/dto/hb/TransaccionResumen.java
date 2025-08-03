/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.List;

/**
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_RESUMEN")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "TransaccionResumen.findPendiente", query = "SELECT t FROM TransaccionResumen t WHERE t.estado IN('G','K','D','A')"),
        @NamedQuery(name = "TransaccionResumen.findAllConsulta", query = "SELECT t FROM TransaccionResumen t WHERE t.estado='R'"),
        @NamedQuery(name = "TransaccionResumen.findByIdTransaccion", query = "SELECT t FROM TransaccionResumen t WHERE t.idTransaccion = :idTransaccion"),
        @NamedQuery(name = "TransaccionResumen.findByNumeroRuc", query = "SELECT t FROM TransaccionResumen t WHERE t.numeroRuc = :numeroRuc"),
        @NamedQuery(name = "TransaccionResumen.findByDocIdentidadTipo", query = "SELECT t FROM TransaccionResumen t WHERE t.docIdentidadTipo = :docIdentidadTipo"),
        @NamedQuery(name = "TransaccionResumen.findByRazonSocial", query = "SELECT t FROM TransaccionResumen t WHERE t.razonSocial = :razonSocial"),
        @NamedQuery(name = "TransaccionResumen.findByNombreComercial", query = "SELECT t FROM TransaccionResumen t WHERE t.nombreComercial = :nombreComercial"),
        @NamedQuery(name = "TransaccionResumen.findByPersonContacto", query = "SELECT t FROM TransaccionResumen t WHERE t.personContacto = :personContacto"),
        @NamedQuery(name = "TransaccionResumen.findByEMail", query = "SELECT t FROM TransaccionResumen t WHERE t.eMail = :eMail"),
        @NamedQuery(name = "TransaccionResumen.findByDIRPais", query = "SELECT t FROM TransaccionResumen t WHERE t.dIRPais = :dIRPais"),
        @NamedQuery(name = "TransaccionResumen.findByDIRDepartamento", query = "SELECT t FROM TransaccionResumen t WHERE t.dIRDepartamento = :dIRDepartamento"),
        @NamedQuery(name = "TransaccionResumen.findByDIRProvincia", query = "SELECT t FROM TransaccionResumen t WHERE t.dIRProvincia = :dIRProvincia"),
        @NamedQuery(name = "TransaccionResumen.findByDIRDistrito", query = "SELECT t FROM TransaccionResumen t WHERE t.dIRDistrito = :dIRDistrito"),
        @NamedQuery(name = "TransaccionResumen.findByDIRNomCalle", query = "SELECT t FROM TransaccionResumen t WHERE t.dIRNomCalle = :dIRNomCalle"),
        @NamedQuery(name = "TransaccionResumen.findByDIRNroCasa", query = "SELECT t FROM TransaccionResumen t WHERE t.dIRNroCasa = :dIRNroCasa"),
        @NamedQuery(name = "TransaccionResumen.findByDIRUbigeo", query = "SELECT t FROM TransaccionResumen t WHERE t.dIRUbigeo = :dIRUbigeo"),
        @NamedQuery(name = "TransaccionResumen.findByFechaEmision", query = "SELECT t FROM TransaccionResumen t WHERE t.fechaEmision = :fechaEmision"),
        @NamedQuery(name = "TransaccionResumen.findByFechaGeneracion", query = "SELECT t FROM TransaccionResumen t WHERE t.fechaGeneracion = :fechaGeneracion"),
        @NamedQuery(name = "TransaccionResumen.findByEstado", query = "SELECT t FROM TransaccionResumen t WHERE t.estado = :estado"),
        @NamedQuery(name = "TransaccionResumen.findByNumeroTicket", query = "SELECT t FROM TransaccionResumen t WHERE t.numeroTicket = :numeroTicket")})
public class TransaccionResumen implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccionResumen")
    private List<TransaccionResumenLineaAnexo> transaccionResumenLineaAnexoList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "transaccionResumen")
    private TransaccionResumenLineaAnexo transaccionResumenLineaAnexo;

    @Id
    @Basic(optional = false)
    @Column(name = "Id_Transaccion")
    private String idTransaccion;

    @Column(name = "Numero_Ruc")
    private String numeroRuc;

    @Column(name = "DocIdentidad_Tipo")
    private String docIdentidadTipo;

    @Column(name = "RazonSocial")
    private String razonSocial;

    @Column(name = "NombreComercial")
    private String nombreComercial;

    @Column(name = "PersonContacto")
    private String personContacto;

    @Column(name = "EMail")
    private String eMail;

    @Column(name = "DIR_Pais")
    private String dIRPais;

    @Column(name = "DIR_Departamento")
    private String dIRDepartamento;

    @Column(name = "DIR_Provincia")
    private String dIRProvincia;

    @Column(name = "DIR_Distrito")
    private String dIRDistrito;

    @Lob
    @Column(name = "DIR_Direccion")
    private String dIRDireccion;

    @Column(name = "DIR_NomCalle")
    private String dIRNomCalle;

    @Column(name = "DIR_NroCasa")
    private String dIRNroCasa;

    @Column(name = "DIR_Ubigeo")
    private String dIRUbigeo;

    @Column(name = "Fecha_Emision")
    private String fechaEmision;

    @Column(name = "Fecha_Generacion")
    private String fechaGeneracion;

    @Column(name = "Estado")
    private String estado;

    @Column(name = "Numero_Ticket")
    private String numeroTicket;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccionResumen")
    private List<TransaccionResumenLinea> transaccionResumenLineaList;

    public TransaccionResumen() {
    }

    public TransaccionResumen(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getNumeroRuc() {
        return numeroRuc;
    }

    public void setNumeroRuc(String numeroRuc) {
        this.numeroRuc = numeroRuc;
    }

    public String getDocIdentidadTipo() {
        return docIdentidadTipo;
    }

    public void setDocIdentidadTipo(String docIdentidadTipo) {
        this.docIdentidadTipo = docIdentidadTipo;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getPersonContacto() {
        return personContacto;
    }

    public void setPersonContacto(String personContacto) {
        this.personContacto = personContacto;
    }

    public String getEMail() {
        return eMail;
    }

    public void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public String getDIRPais() {
        return dIRPais;
    }

    public void setDIRPais(String dIRPais) {
        this.dIRPais = dIRPais;
    }

    public String getDIRDepartamento() {
        return dIRDepartamento;
    }

    public void setDIRDepartamento(String dIRDepartamento) {
        this.dIRDepartamento = dIRDepartamento;
    }

    public String getDIRProvincia() {
        return dIRProvincia;
    }

    public void setDIRProvincia(String dIRProvincia) {
        this.dIRProvincia = dIRProvincia;
    }

    public String getDIRDistrito() {
        return dIRDistrito;
    }

    public void setDIRDistrito(String dIRDistrito) {
        this.dIRDistrito = dIRDistrito;
    }

    public String getDIRDireccion() {
        return dIRDireccion;
    }

    public void setDIRDireccion(String dIRDireccion) {
        this.dIRDireccion = dIRDireccion;
    }

    public String getDIRNomCalle() {
        return dIRNomCalle;
    }

    public void setDIRNomCalle(String dIRNomCalle) {
        this.dIRNomCalle = dIRNomCalle;
    }

    public String getDIRNroCasa() {
        return dIRNroCasa;
    }

    public void setDIRNroCasa(String dIRNroCasa) {
        this.dIRNroCasa = dIRNroCasa;
    }

    public String getDIRUbigeo() {
        return dIRUbigeo;
    }

    public void setDIRUbigeo(String dIRUbigeo) {
        this.dIRUbigeo = dIRUbigeo;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNumeroTicket() {
        return numeroTicket;
    }

    public void setNumeroTicket(String numeroTicket) {
        this.numeroTicket = numeroTicket;
    }

    @XmlTransient
    public List<TransaccionResumenLinea> getTransaccionResumenLineaList() {
        return transaccionResumenLineaList;
    }

    public void setTransaccionResumenLineaList(List<TransaccionResumenLinea> transaccionResumenLineaList) {
        this.transaccionResumenLineaList = transaccionResumenLineaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTransaccion != null ? idTransaccion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionResumen)) {
            return false;
        }
        TransaccionResumen other = (TransaccionResumen) object;
        if ((this.idTransaccion == null && other.idTransaccion != null) || (this.idTransaccion != null && !this.idTransaccion.equals(other.idTransaccion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionResumen[ idTransaccion=" + idTransaccion + " ]";
    }

    public TransaccionResumenLineaAnexo getTransaccionResumenLineaAnexo() {
        return transaccionResumenLineaAnexo;
    }

    public void setTransaccionResumenLineaAnexo(TransaccionResumenLineaAnexo transaccionResumenLineaAnexo) {
        this.transaccionResumenLineaAnexo = transaccionResumenLineaAnexo;
    }

    @XmlTransient
    public List<TransaccionResumenLineaAnexo> getTransaccionResumenLineaAnexoList() {
        return transaccionResumenLineaAnexoList;
    }

    public void setTransaccionResumenLineaAnexoList(List<TransaccionResumenLineaAnexo> transaccionResumenLineaAnexoList) {
        this.transaccionResumenLineaAnexoList = transaccionResumenLineaAnexoList;
    }


}
