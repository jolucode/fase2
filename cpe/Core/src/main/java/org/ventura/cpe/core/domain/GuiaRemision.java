/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yosmel
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "GUIA_REMISION")
@XmlRootElement
public class GuiaRemision implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    @EqualsAndHashCode.Include
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

    @Column(name = "GR_Observacion")
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

    @Override
    public String toString() {
        return "GuiaRemision[ id=" + id + " ]";
    }

}
