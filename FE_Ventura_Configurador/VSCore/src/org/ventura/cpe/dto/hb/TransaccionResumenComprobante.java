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
 *
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_RESUMEN_COMPROBANTE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionResumenComprobante.findAll", query = "SELECT t FROM TransaccionResumenComprobante t"),
    @NamedQuery(name = "TransaccionResumenComprobante.findById", query = "SELECT t FROM TransaccionResumenComprobante t WHERE t.id = :id"),
    @NamedQuery(name = "TransaccionResumenComprobante.findByDOCRuc", query = "SELECT t FROM TransaccionResumenComprobante t WHERE t.dOCRuc = :dOCRuc"),
    @NamedQuery(name = "TransaccionResumenComprobante.findByDOCTipo", query = "SELECT t FROM TransaccionResumenComprobante t WHERE t.dOCTipo = :dOCTipo"),
    @NamedQuery(name = "TransaccionResumenComprobante.findByDOCFechaEmision", query = "SELECT t FROM TransaccionResumenComprobante t WHERE t.dOCFechaEmision = :dOCFechaEmision"),
    @NamedQuery(name = "TransaccionResumenComprobante.findPendiente", query = "SELECT t FROM TransaccionResumenComprobante t WHERE t.dOCEstado <> 'R'"),
    @NamedQuery(name = "TransaccionResumenComprobante.findPendienteConsultar", query = "SELECT t FROM TransaccionResumenComprobante t WHERE t.dOCEstado <> 'R'"),
    @NamedQuery(name = "TransaccionResumenComprobante.findByDOCCorrelativo", query = "SELECT t FROM TransaccionResumenComprobante t WHERE t.dOCCorrelativo = :dOCCorrelativo")})
public class TransaccionResumenComprobante implements Serializable {
    @Column(name = "DOC_Estado")
    private String dOCEstado;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "DOC_Ruc")
    private String dOCRuc;
    @Column(name = "DOC_Tipo")
    private String dOCTipo;
    @Column(name = "DOC_FechaEmision")
    private String dOCFechaEmision;
    @Column(name = "DOC_Correlativo")
    private String dOCCorrelativo;

    public TransaccionResumenComprobante() {
    }

    public TransaccionResumenComprobante(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDOCRuc() {
        return dOCRuc;
    }

    public void setDOCRuc(String dOCRuc) {
        this.dOCRuc = dOCRuc;
    }

    public String getDOCTipo() {
        return dOCTipo;
    }

    public void setDOCTipo(String dOCTipo) {
        this.dOCTipo = dOCTipo;
    }

    public String getDOCFechaEmision() {
        return dOCFechaEmision;
    }

    public void setDOCFechaEmision(String dOCFechaEmision) {
        this.dOCFechaEmision = dOCFechaEmision;
    }

    public String getDOCCorrelativo() {
        return dOCCorrelativo;
    }

    public void setDOCCorrelativo(String dOCCorrelativo) {
        this.dOCCorrelativo = dOCCorrelativo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionResumenComprobante)) {
            return false;
        }
        TransaccionResumenComprobante other = (TransaccionResumenComprobante) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionResumenComprobante[ id=" + id + " ]";
    }

    public String getDOCEstado() {
        return dOCEstado;
    }

    public void setDOCEstado(String dOCEstado) {
        this.dOCEstado = dOCEstado;
    }
    
}
