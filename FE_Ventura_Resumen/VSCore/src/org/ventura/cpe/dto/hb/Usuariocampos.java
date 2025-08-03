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
 *
 * @author Percy
 */
@Entity
@Table(name = "USUARIOCAMPOS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Usuariocampos.findAll", query = "SELECT u FROM Usuariocampos u"),
    @NamedQuery(name = "Usuariocampos.findById", query = "SELECT u FROM Usuariocampos u WHERE u.id = :id"),
    @NamedQuery(name = "Usuariocampos.findByNombre", query = "SELECT u FROM Usuariocampos u WHERE u.nombre = :nombre")})
public class Usuariocampos implements Serializable {
    private List<TransaccionContractdocref> transaccionContractdocrefList;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "Id",insertable = false)
    private Integer id;
    @Column(name = "Nombre",unique = true)
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuariocampos")
    private List<TransaccionUsucampos> transaccionUsucamposList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "usuariocampos")
    private List<TransaccionLineasUsucampos> transaccionLineasUsucamposList;

    public Usuariocampos() {
    }

    public Usuariocampos(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @XmlTransient
    public List<TransaccionUsucampos> getTransaccionUsucamposList() {
        return transaccionUsucamposList;
    }

    public void setTransaccionUsucamposList(List<TransaccionUsucampos> transaccionUsucamposList) {
        this.transaccionUsucamposList = transaccionUsucamposList;
    }

    @XmlTransient
    public List<TransaccionLineasUsucampos> getTransaccionLineasUsucamposList() {
        return transaccionLineasUsucamposList;
    }

    public void setTransaccionLineasUsucamposList(List<TransaccionLineasUsucampos> transaccionLineasUsucamposList) {
        this.transaccionLineasUsucamposList = transaccionLineasUsucamposList;
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
        if (!(object instanceof Usuariocampos)) {
            return false;
        }
        Usuariocampos other = (Usuariocampos) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.Usuariocampos[ id=" + id + " ]";
    }

    @XmlTransient
    public List<TransaccionContractdocref> getTransaccionContractdocrefList() {
        return transaccionContractdocrefList;
    }

    public void setTransaccionContractdocrefList(List<TransaccionContractdocref> transaccionContractdocrefList) {
        this.transaccionContractdocrefList = transaccionContractdocrefList;
    }
}
