/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.dto.hb;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author VSUser
 */
@Embeddable
public class TransaccionResumenLineaPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "Id_Transaccion")
    private String idTransaccion;
    @Basic(optional = false)
    @Column(name = "Id_Linea")
    private int idLinea;

   

    public TransaccionResumenLineaPK() {
    }

    public TransaccionResumenLineaPK(String idTransaccion, int idLinea) {
        this.idTransaccion = idTransaccion;
        this.idLinea = idLinea;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public int getIdLinea() {
        return idLinea;
    }

    public void setIdLinea(int idLinea) {
        this.idLinea = idLinea;
    }

    

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionResumenLineaPK)) {
            return false;
        }
        TransaccionResumenLineaPK other = (TransaccionResumenLineaPK) object;
        if ((this.idTransaccion == null && other.idTransaccion != null) || (this.idTransaccion != null && !this.idTransaccion.equals(other.idTransaccion))) {
            return false;
        }
        if (this.idLinea != other.idLinea) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionResumenLineaPK[ idTransaccion=" + idTransaccion + ", idLinea=" + idLinea + " ]";
    }
    
}
