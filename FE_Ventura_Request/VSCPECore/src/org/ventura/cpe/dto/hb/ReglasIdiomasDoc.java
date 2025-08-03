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
@Table(name = "REGLAS_IDIOMAS_DOC")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "ReglasIdiomasDoc.findAllReglas", query = "SELECT r FROM ReglasIdiomasDoc r")})
public class ReglasIdiomasDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "FEKey")
    private Integer fEKey;

    @Column(name = "FETipoDOC")
    private String fETipoDOC;

    @Column(name = "FECampoDOC")
    private String fECampoDOC;

    @Column(name = "FEOperador")
    private String fEOperador;

    @Column(name = "FEValorComparativo")
    private String fEValorComparativo;

    @Column(name = "FEDOCFinal")
    private String fEDOCFinal;

    public ReglasIdiomasDoc() {
    }

    public ReglasIdiomasDoc(Integer fEKey) {
        this.fEKey = fEKey;
    }

    public Integer getFEKey() {
        return fEKey;
    }

    public void setFEKey(Integer fEKey) {
        this.fEKey = fEKey;
    }

    public String getFETipoDOC() {
        return fETipoDOC;
    }

    public void setFETipoDOC(String fETipoDOC) {
        this.fETipoDOC = fETipoDOC;
    }

    public String getFECampoDOC() {
        return fECampoDOC;
    }

    public void setFECampoDOC(String fECampoDOC) {
        this.fECampoDOC = fECampoDOC;
    }

    public String getFEOperador() {
        return fEOperador;
    }

    public void setFEOperador(String fEOperador) {
        this.fEOperador = fEOperador;
    }

    public String getFEValorComparativo() {
        return fEValorComparativo;
    }

    public void setFEValorComparativo(String fEValorComparativo) {
        this.fEValorComparativo = fEValorComparativo;
    }

    public String getFEDOCFinal() {
        return fEDOCFinal;
    }

    public void setFEDOCFinal(String fEDOCFinal) {
        this.fEDOCFinal = fEDOCFinal;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (fEKey != null ? fEKey.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReglasIdiomasDoc)) {
            return false;
        }
        ReglasIdiomasDoc other = (ReglasIdiomasDoc) object;
        if ((this.fEKey == null && other.fEKey != null) || (this.fEKey != null && !this.fEKey.equals(other.fEKey))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.ReglasIdiomasDoc[ fEKey=" + fEKey + " ]";
    }

}
