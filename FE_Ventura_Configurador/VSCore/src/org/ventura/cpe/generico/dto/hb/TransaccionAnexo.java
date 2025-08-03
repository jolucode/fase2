/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.generico.dto.hb;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "TRANSACCION_ANEXO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionAnexo.findAll", query = "SELECT t FROM TransaccionAnexo t"),
    @NamedQuery(name = "TransaccionAnexo.findByFEId", query = "SELECT t FROM TransaccionAnexo t WHERE t.transaccionAnexoPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionAnexo.findByNroAnexo", query = "SELECT t FROM TransaccionAnexo t WHERE t.transaccionAnexoPK.nroAnexo = :nroAnexo"),
    @NamedQuery(name = "TransaccionAnexo.findByRuta", query = "SELECT t FROM TransaccionAnexo t WHERE t.ruta = :ruta"),
    @NamedQuery(name = "TransaccionAnexo.findByTipo", query = "SELECT t FROM TransaccionAnexo t WHERE t.tipo = :tipo"),
    @NamedQuery(name = "TransaccionAnexo.findByObservacion", query = "SELECT t FROM TransaccionAnexo t WHERE t.observacion = :observacion")})
public class TransaccionAnexo implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionAnexoPK transaccionAnexoPK;
    @Column(name = "Ruta")
    private String ruta;
    @Column(name = "Tipo")
    private String tipo;
    @Column(name = "Observacion")
    private String observacion;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionAnexo() {
    }

    public TransaccionAnexo(TransaccionAnexoPK transaccionAnexoPK) {
        this.transaccionAnexoPK = transaccionAnexoPK;
    }

    public TransaccionAnexo(String fEId, int nroAnexo) {
        this.transaccionAnexoPK = new TransaccionAnexoPK(fEId, nroAnexo);
    }

    public TransaccionAnexoPK getTransaccionAnexoPK() {
        return transaccionAnexoPK;
    }

    public void setTransaccionAnexoPK(TransaccionAnexoPK transaccionAnexoPK) {
        this.transaccionAnexoPK = transaccionAnexoPK;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionAnexoPK != null ? transaccionAnexoPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionAnexo)) {
            return false;
        }
        TransaccionAnexo other = (TransaccionAnexo) object;
        if ((this.transaccionAnexoPK == null && other.transaccionAnexoPK != null) || (this.transaccionAnexoPK != null && !this.transaccionAnexoPK.equals(other.transaccionAnexoPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.generico.dto.hb.TransaccionAnexo[ transaccionAnexoPK=" + transaccionAnexoPK + " ]";
    }
    
}
