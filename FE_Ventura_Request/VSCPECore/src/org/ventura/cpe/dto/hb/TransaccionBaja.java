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
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_BAJA")
@XmlRootElement
@NamedStoredProcedureQuery(
        name = "bpvs_FE_Correlativo_Baja",
        procedureName = "bpvs_FE_Correlativo_Baja"
)

@NamedQueries({
        @NamedQuery(name = "TransaccionBaja.findAll", query = "SELECT t FROM TransaccionBaja t"),
        @NamedQuery(name = "TransaccionBaja.findByFecha", query = "SELECT t FROM TransaccionBaja t WHERE t.fecha = :fecha"),
        @NamedQuery(name = "TransaccionBaja.findById", query = "SELECT t FROM TransaccionBaja t WHERE t.id = :id"),
        @NamedQuery(name = "TransaccionBaja.findBySerie", query = "SELECT t FROM TransaccionBaja t WHERE t.serie = :serie")})

public class TransaccionBaja implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "fecha")
    private Long fecha;

    @Basic(optional = false)
    @Column(name = "ID")
    private long id;

    @Column(name = "serie")
    private String serie;

    public TransaccionBaja() {
    }

    public TransaccionBaja(Long fecha) {
        this.fecha = fecha;
    }

    public TransaccionBaja(Long fecha, long id) {
        this.fecha = fecha;
        this.id = id;
    }

    public Long getFecha() {
        return fecha;
    }

    public void setFecha(Long fecha) {
        this.fecha = fecha;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fecha != null ? fecha.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionBaja)) {
            return false;
        }
        TransaccionBaja other = (TransaccionBaja) object;
        return !((this.fecha == null && other.fecha != null) || (this.fecha != null && !this.fecha.equals(other.fecha)));
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionBaja[ fecha=" + fecha + " ]";
    }

}
