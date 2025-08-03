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
@Table(name = "RESUMENDIARIO_CONFIG")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ResumendiarioConfig.findAll", query = "SELECT r FROM ResumendiarioConfig r"),
        @NamedQuery(name = "ResumendiarioConfig.findByFecha", query = "SELECT r FROM ResumendiarioConfig r WHERE r.fecha = :fecha"),
        @NamedQuery(name = "ResumendiarioConfig.findByHora", query = "SELECT r FROM ResumendiarioConfig r WHERE r.hora = :hora")})
public class ResumendiarioConfig implements Serializable {

    @Column(name = "id")
    private String id;

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "fecha")
    private String fecha;

    @Column(name = "hora")
    private String hora;

    public ResumendiarioConfig() {
    }

    public ResumendiarioConfig(String fecha) {
        this.fecha = fecha;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
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
        if (!(object instanceof ResumendiarioConfig)) {
            return false;
        }
        ResumendiarioConfig other = (ResumendiarioConfig) object;
        if ((this.fecha == null && other.fecha != null) || (this.fecha != null && !this.fecha.equals(other.fecha))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.ResumendiarioConfig[ fecha=" + fecha + " ]";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
