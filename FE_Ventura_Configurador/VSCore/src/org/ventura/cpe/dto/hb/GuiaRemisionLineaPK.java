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
public class GuiaRemisionLineaPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    @Basic(optional = false)
    @Column(name = "ID_Linea")
    private int iDLinea;

    public GuiaRemisionLineaPK() {
    }

    public GuiaRemisionLineaPK(String id, int iDLinea) {
        this.id = id;
        this.iDLinea = iDLinea;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIDLinea() {
        return iDLinea;
    }

    public void setIDLinea(int iDLinea) {
        this.iDLinea = iDLinea;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        hash += (int) iDLinea;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GuiaRemisionLineaPK)) {
            return false;
        }
        GuiaRemisionLineaPK other = (GuiaRemisionLineaPK) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if (this.iDLinea != other.iDLinea) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.GuiaRemisionLineaPK[ id=" + id + ", iDLinea=" + iDLinea + " ]";
    }
    
}
