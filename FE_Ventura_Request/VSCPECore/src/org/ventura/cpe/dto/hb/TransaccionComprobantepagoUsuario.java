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
 * @author VS-LT-06
 */
@Entity
@Table(name = "TRANSACCION_COMPROBANTEPAGO_USUARIO")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "TransaccionComprobantepagoUsuario.findAll", query = "SELECT t FROM TransaccionComprobantepagoUsuario t"),
        @NamedQuery(name = "TransaccionComprobantepagoUsuario.findById", query = "SELECT t FROM TransaccionComprobantepagoUsuario t WHERE t.transaccionComprobantepagoUsuarioPK.id = :id"),
        @NamedQuery(name = "TransaccionComprobantepagoUsuario.findByFEId", query = "SELECT t FROM TransaccionComprobantepagoUsuario t WHERE t.transaccionComprobantepagoUsuarioPK.fEId = :fEId"),
        @NamedQuery(name = "TransaccionComprobantepagoUsuario.findByNroOrden", query = "SELECT t FROM TransaccionComprobantepagoUsuario t WHERE t.transaccionComprobantepagoUsuarioPK.nroOrden = :nroOrden"),
        @NamedQuery(name = "TransaccionComprobantepagoUsuario.findByValor", query = "SELECT t FROM TransaccionComprobantepagoUsuario t WHERE t.valor = :valor")})
public class TransaccionComprobantepagoUsuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected TransaccionComprobantepagoUsuarioPK transaccionComprobantepagoUsuarioPK;

    @Column(name = "Valor")
    private String valor;

    @JoinColumn(name = "Id", referencedColumnName = "Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Usuariocampos usuariocampos;

    @JoinColumns({
            @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false),
            @JoinColumn(name = "NroOrden", referencedColumnName = "NroOrden", insertable = false, updatable = false)})
    @ManyToOne(optional = false)
    private TransaccionComprobantePago transaccionComprobantePago;

    public TransaccionComprobantepagoUsuario() {
    }

    public TransaccionComprobantepagoUsuario(TransaccionComprobantepagoUsuarioPK transaccionComprobantepagoUsuarioPK) {
        this.transaccionComprobantepagoUsuarioPK = transaccionComprobantepagoUsuarioPK;
    }

    public TransaccionComprobantepagoUsuario(int id, String fEId, int nroOrden) {
        this.transaccionComprobantepagoUsuarioPK = new TransaccionComprobantepagoUsuarioPK(id, fEId, nroOrden);
    }

    public TransaccionComprobantepagoUsuarioPK getTransaccionComprobantepagoUsuarioPK() {
        return transaccionComprobantepagoUsuarioPK;
    }

    public void setTransaccionComprobantepagoUsuarioPK(TransaccionComprobantepagoUsuarioPK transaccionComprobantepagoUsuarioPK) {
        this.transaccionComprobantepagoUsuarioPK = transaccionComprobantepagoUsuarioPK;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Usuariocampos getUsuariocampos() {
        return usuariocampos;
    }

    public void setUsuariocampos(Usuariocampos usuariocampos) {
        this.usuariocampos = usuariocampos;
    }

    public TransaccionComprobantePago getTransaccionComprobantePago() {
        return transaccionComprobantePago;
    }

    public void setTransaccionComprobantePago(TransaccionComprobantePago transaccionComprobantePago) {
        this.transaccionComprobantePago = transaccionComprobantePago;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionComprobantepagoUsuarioPK != null ? transaccionComprobantepagoUsuarioPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionComprobantepagoUsuario)) {
            return false;
        }
        TransaccionComprobantepagoUsuario other = (TransaccionComprobantepagoUsuario) object;
        if ((this.transaccionComprobantepagoUsuarioPK == null && other.transaccionComprobantepagoUsuarioPK != null) || (this.transaccionComprobantepagoUsuarioPK != null && !this.transaccionComprobantepagoUsuarioPK.equals(other.transaccionComprobantepagoUsuarioPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionComprobantepagoUsuario[ transaccionComprobantepagoUsuarioPK=" + transaccionComprobantepagoUsuarioPK + " ]";
    }

}
