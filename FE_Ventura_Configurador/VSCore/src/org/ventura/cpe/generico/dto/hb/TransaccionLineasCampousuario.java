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
@Table(name = "TRANSACCION_LINEAS_CAMPOUSUARIO")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionLineasCampousuario.findAll", query = "SELECT t FROM TransaccionLineasCampousuario t"),
    @NamedQuery(name = "TransaccionLineasCampousuario.findByFEId", query = "SELECT t FROM TransaccionLineasCampousuario t WHERE t.transaccionLineasCampousuarioPK.fEId = :fEId"),
    @NamedQuery(name = "TransaccionLineasCampousuario.findByNroOrden", query = "SELECT t FROM TransaccionLineasCampousuario t WHERE t.transaccionLineasCampousuarioPK.nroOrden = :nroOrden"),
    @NamedQuery(name = "TransaccionLineasCampousuario.findByIdCampoUsuario", query = "SELECT t FROM TransaccionLineasCampousuario t WHERE t.transaccionLineasCampousuarioPK.idCampoUsuario = :idCampoUsuario"),
    @NamedQuery(name = "TransaccionLineasCampousuario.findByValor", query = "SELECT t FROM TransaccionLineasCampousuario t WHERE t.valor = :valor"),
    @NamedQuery(name = "TransaccionLineasCampousuario.findByNombreCampo", query = "SELECT t FROM TransaccionLineasCampousuario t WHERE t.nombreCampo = :nombreCampo")})
public class TransaccionLineasCampousuario implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionLineasCampousuarioPK transaccionLineasCampousuarioPK;
    @Column(name = "Valor")
    private String valor;
    @Column(name = "NombreCampo")
    private String nombreCampo;
    @JoinColumns({
        @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false),
        @JoinColumn(name = "NroOrden", referencedColumnName = "NroOrden", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TransaccionLineas transaccionLineas;

    public TransaccionLineasCampousuario() {
    }

    public TransaccionLineasCampousuario(TransaccionLineasCampousuarioPK transaccionLineasCampousuarioPK) {
        this.transaccionLineasCampousuarioPK = transaccionLineasCampousuarioPK;
    }

    public TransaccionLineasCampousuario(String fEId, int nroOrden, int idCampoUsuario) {
        this.transaccionLineasCampousuarioPK = new TransaccionLineasCampousuarioPK(fEId, nroOrden, idCampoUsuario);
    }

    public TransaccionLineasCampousuarioPK getTransaccionLineasCampousuarioPK() {
        return transaccionLineasCampousuarioPK;
    }

    public void setTransaccionLineasCampousuarioPK(TransaccionLineasCampousuarioPK transaccionLineasCampousuarioPK) {
        this.transaccionLineasCampousuarioPK = transaccionLineasCampousuarioPK;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getNombreCampo() {
        return nombreCampo;
    }

    public void setNombreCampo(String nombreCampo) {
        this.nombreCampo = nombreCampo;
    }

    public TransaccionLineas getTransaccionLineas() {
        return transaccionLineas;
    }

    public void setTransaccionLineas(TransaccionLineas transaccionLineas) {
        this.transaccionLineas = transaccionLineas;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionLineasCampousuarioPK != null ? transaccionLineasCampousuarioPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionLineasCampousuario)) {
            return false;
        }
        TransaccionLineasCampousuario other = (TransaccionLineasCampousuario) object;
        if ((this.transaccionLineasCampousuarioPK == null && other.transaccionLineasCampousuarioPK != null) || (this.transaccionLineasCampousuarioPK != null && !this.transaccionLineasCampousuarioPK.equals(other.transaccionLineasCampousuarioPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.generico.dto.hb.TransaccionLineasCampousuario[ transaccionLineasCampousuarioPK=" + transaccionLineasCampousuarioPK + " ]";
    }
    
}
