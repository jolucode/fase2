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
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author VSUser
 */
@Entity
@Table(name = "GUIA_REMISION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "GuiaRemision.findAll", query = "SELECT g FROM GuiaRemision g"),
    @NamedQuery(name = "GuiaRemision.findById", query = "SELECT g FROM GuiaRemision g WHERE g.id = :id"),
    @NamedQuery(name = "GuiaRemision.findByGRSerie", query = "SELECT g FROM GuiaRemision g WHERE g.gRSerie = :gRSerie"),
    @NamedQuery(name = "GuiaRemision.findByGRCorrelativo", query = "SELECT g FROM GuiaRemision g WHERE g.gRCorrelativo = :gRCorrelativo"),
    @NamedQuery(name = "GuiaRemision.findByGRNumeracion", query = "SELECT g FROM GuiaRemision g WHERE g.gRNumeracion = :gRNumeracion"),
    @NamedQuery(name = "GuiaRemision.findByGRFecEmision", query = "SELECT g FROM GuiaRemision g WHERE g.gRFecEmision = :gRFecEmision"),
    @NamedQuery(name = "GuiaRemision.findByGRTipoDoc", query = "SELECT g FROM GuiaRemision g WHERE g.gRTipoDoc = :gRTipoDoc"),
    @NamedQuery(name = "GuiaRemision.findByGRObervaci\u00f3n", query = "SELECT g FROM GuiaRemision g WHERE g.gRObervaci\u00f3n = :gRObervaci\u00f3n"),
    @NamedQuery(name = "GuiaRemision.findByGRBSerie", query = "SELECT g FROM GuiaRemision g WHERE g.gRBSerie = :gRBSerie"),
    @NamedQuery(name = "GuiaRemision.findByGRBCorrelativo", query = "SELECT g FROM GuiaRemision g WHERE g.gRBCorrelativo = :gRBCorrelativo"),
    @NamedQuery(name = "GuiaRemision.findByGRBNumeracion", query = "SELECT g FROM GuiaRemision g WHERE g.gRBNumeracion = :gRBNumeracion"),
    @NamedQuery(name = "GuiaRemision.findByGRBCodTipoId", query = "SELECT g FROM GuiaRemision g WHERE g.gRBCodTipoId = :gRBCodTipoId"),
    @NamedQuery(name = "GuiaRemision.findByGRBTipoDoc", query = "SELECT g FROM GuiaRemision g WHERE g.gRBTipoDoc = :gRBTipoDoc"),
    @NamedQuery(name = "GuiaRemision.findByDOCRNumDoc", query = "SELECT g FROM GuiaRemision g WHERE g.dOCRNumDoc = :dOCRNumDoc"),
    @NamedQuery(name = "GuiaRemision.findByDOCRCodTipoDoc", query = "SELECT g FROM GuiaRemision g WHERE g.dOCRCodTipoDoc = :dOCRCodTipoDoc"),
    @NamedQuery(name = "GuiaRemision.findByDRNumDocId", query = "SELECT g FROM GuiaRemision g WHERE g.dRNumDocId = :dRNumDocId"),
    @NamedQuery(name = "GuiaRemision.findByDRTipoDocId", query = "SELECT g FROM GuiaRemision g WHERE g.dRTipoDocId = :dRTipoDocId"),
    @NamedQuery(name = "GuiaRemision.findByDRDenominacion", query = "SELECT g FROM GuiaRemision g WHERE g.dRDenominacion = :dRDenominacion"),
    @NamedQuery(name = "GuiaRemision.findByDDNumDocId", query = "SELECT g FROM GuiaRemision g WHERE g.dDNumDocId = :dDNumDocId"),
    @NamedQuery(name = "GuiaRemision.findByDDTipoDocId", query = "SELECT g FROM GuiaRemision g WHERE g.dDTipoDocId = :dDTipoDocId"),
    @NamedQuery(name = "GuiaRemision.findByDDDenominacion", query = "SELECT g FROM GuiaRemision g WHERE g.dDDenominacion = :dDDenominacion"),
    @NamedQuery(name = "GuiaRemision.findByDTNumDocId", query = "SELECT g FROM GuiaRemision g WHERE g.dTNumDocId = :dTNumDocId"),
    @NamedQuery(name = "GuiaRemision.findByDTTipoDocId", query = "SELECT g FROM GuiaRemision g WHERE g.dTTipoDocId = :dTTipoDocId"),
    @NamedQuery(name = "GuiaRemision.findByDTDenominacion", query = "SELECT g FROM GuiaRemision g WHERE g.dTDenominacion = :dTDenominacion"),
    @NamedQuery(name = "GuiaRemision.findByDEMotivTras", query = "SELECT g FROM GuiaRemision g WHERE g.dEMotivTras = :dEMotivTras"),
    @NamedQuery(name = "GuiaRemision.findByDEDesMotv", query = "SELECT g FROM GuiaRemision g WHERE g.dEDesMotv = :dEDesMotv"),
    @NamedQuery(name = "GuiaRemision.findByDEIndTransb", query = "SELECT g FROM GuiaRemision g WHERE g.dEIndTransb = :dEIndTransb"),
    @NamedQuery(name = "GuiaRemision.findByDEPesoBTotal", query = "SELECT g FROM GuiaRemision g WHERE g.dEPesoBTotal = :dEPesoBTotal"),
    @NamedQuery(name = "GuiaRemision.findByDEUnidMedida", query = "SELECT g FROM GuiaRemision g WHERE g.dEUnidMedida = :dEUnidMedida"),
    @NamedQuery(name = "GuiaRemision.findByDENumBultos", query = "SELECT g FROM GuiaRemision g WHERE g.dENumBultos = :dENumBultos"),
    @NamedQuery(name = "GuiaRemision.findByDEModalidadTras", query = "SELECT g FROM GuiaRemision g WHERE g.dEModalidadTras = :dEModalidadTras"),
    @NamedQuery(name = "GuiaRemision.findByDEFecIniTras", query = "SELECT g FROM GuiaRemision g WHERE g.dEFecIniTras = :dEFecIniTras"),
    @NamedQuery(name = "GuiaRemision.findByTTPNumRuc", query = "SELECT g FROM GuiaRemision g WHERE g.tTPNumRuc = :tTPNumRuc"),
    @NamedQuery(name = "GuiaRemision.findByTTPTipoDocId", query = "SELECT g FROM GuiaRemision g WHERE g.tTPTipoDocId = :tTPTipoDocId"),
    @NamedQuery(name = "GuiaRemision.findByTTPDenominacion", query = "SELECT g FROM GuiaRemision g WHERE g.tTPDenominacion = :tTPDenominacion"),
    @NamedQuery(name = "GuiaRemision.findByVTPNumPlaca", query = "SELECT g FROM GuiaRemision g WHERE g.vTPNumPlaca = :vTPNumPlaca"),
    @NamedQuery(name = "GuiaRemision.findByCTPNumDocId", query = "SELECT g FROM GuiaRemision g WHERE g.cTPNumDocId = :cTPNumDocId"),
    @NamedQuery(name = "GuiaRemision.findByCTPTipoDocId", query = "SELECT g FROM GuiaRemision g WHERE g.cTPTipoDocId = :cTPTipoDocId"),
    @NamedQuery(name = "GuiaRemision.findByDPLUbigeo", query = "SELECT g FROM GuiaRemision g WHERE g.dPLUbigeo = :dPLUbigeo"),
    @NamedQuery(name = "GuiaRemision.findByDPLDireccion", query = "SELECT g FROM GuiaRemision g WHERE g.dPLDireccion = :dPLDireccion"),
    @NamedQuery(name = "GuiaRemision.findByMINumContenedor", query = "SELECT g FROM GuiaRemision g WHERE g.mINumContenedor = :mINumContenedor"),
    @NamedQuery(name = "GuiaRemision.findByDPPUbigeo", query = "SELECT g FROM GuiaRemision g WHERE g.dPPUbigeo = :dPPUbigeo"),
    @NamedQuery(name = "GuiaRemision.findByDPPDireccion", query = "SELECT g FROM GuiaRemision g WHERE g.dPPDireccion = :dPPDireccion"),
    @NamedQuery(name = "GuiaRemision.findByPEDCodPuerto", query = "SELECT g FROM GuiaRemision g WHERE g.pEDCodPuerto = :pEDCodPuerto")})
public class GuiaRemision implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    @Column(name = "GR_Serie")
    private String gRSerie;
    @Column(name = "GR_Correlativo")
    private String gRCorrelativo;
    @Column(name = "GR_Numeracion")
    private String gRNumeracion;
    @Column(name = "GR_FecEmision")
    private String gRFecEmision;
    @Column(name = "GR_TipoDoc")
    private String gRTipoDoc;
    @Column(name = "GR_Obervaci\u00f3n")
    private String gRObervación;
    @Column(name = "GRB_Serie")
    private String gRBSerie;
    @Column(name = "GRB_Correlativo")
    private String gRBCorrelativo;
    @Column(name = "GRB_Numeracion")
    private String gRBNumeracion;
    @Column(name = "GRB_CodTipoId")
    private String gRBCodTipoId;
    @Column(name = "GRB_TipoDoc")
    private String gRBTipoDoc;
    @Column(name = "DOCR_NumDoc")
    private String dOCRNumDoc;
    @Column(name = "DOCR_CodTipoDoc")
    private String dOCRCodTipoDoc;
    @Column(name = "DR_NumDocId")
    private String dRNumDocId;
    @Column(name = "DR_TipoDocId")
    private String dRTipoDocId;
    @Column(name = "DR_Denominacion")
    private String dRDenominacion;
    @Column(name = "DD_NumDocId")
    private String dDNumDocId;
    @Column(name = "DD_TipoDocId")
    private String dDTipoDocId;
    @Column(name = "DD_Denominacion")
    private String dDDenominacion;
    @Column(name = "DT_NumDocId")
    private String dTNumDocId;
    @Column(name = "DT_TipoDocId")
    private String dTTipoDocId;
    @Column(name = "DT_Denominacion")
    private String dTDenominacion;
    @Column(name = "DE_MotivTras")
    private String dEMotivTras;
    @Column(name = "DE_DesMotv")
    private String dEDesMotv;
    @Column(name = "DE_IndTransb")
    private Short dEIndTransb;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "DE_PesoBTotal")
    private BigDecimal dEPesoBTotal;
    @Column(name = "DE_UnidMedida")
    private String dEUnidMedida;
    @Column(name = "DE_NumBultos")
    private BigDecimal dENumBultos;
    @Column(name = "DE_ModalidadTras")
    private String dEModalidadTras;
    @Column(name = "DE_FecIniTras")
    private String dEFecIniTras;
    @Column(name = "TTP_NumRuc")
    private String tTPNumRuc;
    @Column(name = "TTP_TipoDocId")
    private String tTPTipoDocId;
    @Column(name = "TTP_Denominacion")
    private String tTPDenominacion;
    @Column(name = "VTP_NumPlaca")
    private String vTPNumPlaca;
    @Column(name = "CTP_NumDocId")
    private String cTPNumDocId;
    @Column(name = "CTP_TipoDocId")
    private String cTPTipoDocId;
    @Column(name = "DPL_Ubigeo")
    private String dPLUbigeo;
    @Column(name = "DPL_Direccion")
    private String dPLDireccion;
    @Column(name = "MI_NumContenedor")
    private String mINumContenedor;
    @Column(name = "DPP_Ubigeo")
    private String dPPUbigeo;
    @Column(name = "DPP_Direccion")
    private String dPPDireccion;
    @Column(name = "PED_CodPuerto")
    private String pEDCodPuerto;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "guiaRemision")
    private List<GuiaRemisionLinea> guiaRemisionLineaList;

    public GuiaRemision() {
    }

    public GuiaRemision(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGRSerie() {
        return gRSerie;
    }

    public void setGRSerie(String gRSerie) {
        this.gRSerie = gRSerie;
    }

    public String getGRCorrelativo() {
        return gRCorrelativo;
    }

    public void setGRCorrelativo(String gRCorrelativo) {
        this.gRCorrelativo = gRCorrelativo;
    }

    public String getGRNumeracion() {
        return gRNumeracion;
    }

    public void setGRNumeracion(String gRNumeracion) {
        this.gRNumeracion = gRNumeracion;
    }

    public String getGRFecEmision() {
        return gRFecEmision;
    }

    public void setGRFecEmision(String gRFecEmision) {
        this.gRFecEmision = gRFecEmision;
    }

    public String getGRTipoDoc() {
        return gRTipoDoc;
    }

    public void setGRTipoDoc(String gRTipoDoc) {
        this.gRTipoDoc = gRTipoDoc;
    }

    public String getGRObervación() {
        return gRObervación;
    }

    public void setGRObervación(String gRObervación) {
        this.gRObervación = gRObervación;
    }

    public String getGRBSerie() {
        return gRBSerie;
    }

    public void setGRBSerie(String gRBSerie) {
        this.gRBSerie = gRBSerie;
    }

    public String getGRBCorrelativo() {
        return gRBCorrelativo;
    }

    public void setGRBCorrelativo(String gRBCorrelativo) {
        this.gRBCorrelativo = gRBCorrelativo;
    }

    public String getGRBNumeracion() {
        return gRBNumeracion;
    }

    public void setGRBNumeracion(String gRBNumeracion) {
        this.gRBNumeracion = gRBNumeracion;
    }

    public String getGRBCodTipoId() {
        return gRBCodTipoId;
    }

    public void setGRBCodTipoId(String gRBCodTipoId) {
        this.gRBCodTipoId = gRBCodTipoId;
    }

    public String getGRBTipoDoc() {
        return gRBTipoDoc;
    }

    public void setGRBTipoDoc(String gRBTipoDoc) {
        this.gRBTipoDoc = gRBTipoDoc;
    }

    public String getDOCRNumDoc() {
        return dOCRNumDoc;
    }

    public void setDOCRNumDoc(String dOCRNumDoc) {
        this.dOCRNumDoc = dOCRNumDoc;
    }

    public String getDOCRCodTipoDoc() {
        return dOCRCodTipoDoc;
    }

    public void setDOCRCodTipoDoc(String dOCRCodTipoDoc) {
        this.dOCRCodTipoDoc = dOCRCodTipoDoc;
    }

    public String getDRNumDocId() {
        return dRNumDocId;
    }

    public void setDRNumDocId(String dRNumDocId) {
        this.dRNumDocId = dRNumDocId;
    }

    public String getDRTipoDocId() {
        return dRTipoDocId;
    }

    public void setDRTipoDocId(String dRTipoDocId) {
        this.dRTipoDocId = dRTipoDocId;
    }

    public String getDRDenominacion() {
        return dRDenominacion;
    }

    public void setDRDenominacion(String dRDenominacion) {
        this.dRDenominacion = dRDenominacion;
    }

    public String getDDNumDocId() {
        return dDNumDocId;
    }

    public void setDDNumDocId(String dDNumDocId) {
        this.dDNumDocId = dDNumDocId;
    }

    public String getDDTipoDocId() {
        return dDTipoDocId;
    }

    public void setDDTipoDocId(String dDTipoDocId) {
        this.dDTipoDocId = dDTipoDocId;
    }

    public String getDDDenominacion() {
        return dDDenominacion;
    }

    public void setDDDenominacion(String dDDenominacion) {
        this.dDDenominacion = dDDenominacion;
    }

    public String getDTNumDocId() {
        return dTNumDocId;
    }

    public void setDTNumDocId(String dTNumDocId) {
        this.dTNumDocId = dTNumDocId;
    }

    public String getDTTipoDocId() {
        return dTTipoDocId;
    }

    public void setDTTipoDocId(String dTTipoDocId) {
        this.dTTipoDocId = dTTipoDocId;
    }

    public String getDTDenominacion() {
        return dTDenominacion;
    }

    public void setDTDenominacion(String dTDenominacion) {
        this.dTDenominacion = dTDenominacion;
    }

    public String getDEMotivTras() {
        return dEMotivTras;
    }

    public void setDEMotivTras(String dEMotivTras) {
        this.dEMotivTras = dEMotivTras;
    }

    public String getDEDesMotv() {
        return dEDesMotv;
    }

    public void setDEDesMotv(String dEDesMotv) {
        this.dEDesMotv = dEDesMotv;
    }

    public Short getDEIndTransb() {
        return dEIndTransb;
    }

    public void setDEIndTransb(Short dEIndTransb) {
        this.dEIndTransb = dEIndTransb;
    }

    public BigDecimal getDEPesoBTotal() {
        return dEPesoBTotal;
    }

    public void setDEPesoBTotal(BigDecimal dEPesoBTotal) {
        this.dEPesoBTotal = dEPesoBTotal;
    }

    public String getDEUnidMedida() {
        return dEUnidMedida;
    }

    public void setDEUnidMedida(String dEUnidMedida) {
        this.dEUnidMedida = dEUnidMedida;
    }

    public BigDecimal getDENumBultos() {
        return dENumBultos;
    }

    public void setDENumBultos(BigDecimal dENumBultos) {
        this.dENumBultos = dENumBultos;
    }

    public String getDEModalidadTras() {
        return dEModalidadTras;
    }

    public void setDEModalidadTras(String dEModalidadTras) {
        this.dEModalidadTras = dEModalidadTras;
    }

    public String getDEFecIniTras() {
        return dEFecIniTras;
    }

    public void setDEFecIniTras(String dEFecIniTras) {
        this.dEFecIniTras = dEFecIniTras;
    }

    public String getTTPNumRuc() {
        return tTPNumRuc;
    }

    public void setTTPNumRuc(String tTPNumRuc) {
        this.tTPNumRuc = tTPNumRuc;
    }

    public String getTTPTipoDocId() {
        return tTPTipoDocId;
    }

    public void setTTPTipoDocId(String tTPTipoDocId) {
        this.tTPTipoDocId = tTPTipoDocId;
    }

    public String getTTPDenominacion() {
        return tTPDenominacion;
    }

    public void setTTPDenominacion(String tTPDenominacion) {
        this.tTPDenominacion = tTPDenominacion;
    }

    public String getVTPNumPlaca() {
        return vTPNumPlaca;
    }

    public void setVTPNumPlaca(String vTPNumPlaca) {
        this.vTPNumPlaca = vTPNumPlaca;
    }

    public String getCTPNumDocId() {
        return cTPNumDocId;
    }

    public void setCTPNumDocId(String cTPNumDocId) {
        this.cTPNumDocId = cTPNumDocId;
    }

    public String getCTPTipoDocId() {
        return cTPTipoDocId;
    }

    public void setCTPTipoDocId(String cTPTipoDocId) {
        this.cTPTipoDocId = cTPTipoDocId;
    }

    public String getDPLUbigeo() {
        return dPLUbigeo;
    }

    public void setDPLUbigeo(String dPLUbigeo) {
        this.dPLUbigeo = dPLUbigeo;
    }

    public String getDPLDireccion() {
        return dPLDireccion;
    }

    public void setDPLDireccion(String dPLDireccion) {
        this.dPLDireccion = dPLDireccion;
    }

    public String getMINumContenedor() {
        return mINumContenedor;
    }

    public void setMINumContenedor(String mINumContenedor) {
        this.mINumContenedor = mINumContenedor;
    }

    public String getDPPUbigeo() {
        return dPPUbigeo;
    }

    public void setDPPUbigeo(String dPPUbigeo) {
        this.dPPUbigeo = dPPUbigeo;
    }

    public String getDPPDireccion() {
        return dPPDireccion;
    }

    public void setDPPDireccion(String dPPDireccion) {
        this.dPPDireccion = dPPDireccion;
    }

    public String getPEDCodPuerto() {
        return pEDCodPuerto;
    }

    public void setPEDCodPuerto(String pEDCodPuerto) {
        this.pEDCodPuerto = pEDCodPuerto;
    }

    @XmlTransient
    public List<GuiaRemisionLinea> getGuiaRemisionLineaList() {
        return guiaRemisionLineaList;
    }

    public void setGuiaRemisionLineaList(List<GuiaRemisionLinea> guiaRemisionLineaList) {
        this.guiaRemisionLineaList = guiaRemisionLineaList;
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
        if (!(object instanceof GuiaRemision)) {
            return false;
        }
        GuiaRemision other = (GuiaRemision) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.GuiaRemision[ id=" + id + " ]";
    }
    
}
