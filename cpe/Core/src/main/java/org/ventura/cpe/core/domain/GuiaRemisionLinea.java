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

/**
 * @author VSUser
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "GUIA_REMISION_LINEA")
@XmlRootElement
public class GuiaRemisionLinea implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected GuiaRemisionLineaPK guiaRemisionLineaPK;

    @Column(name = "BT_NumOrden")
    private Integer bTNumOrden;

    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "BT_Cantidad")
    private BigDecimal bTCantidad;

    @Column(name = "BT_UnidMedida")
    private String bTUnidMedida;

    @Column(name = "BT_Descripcion")
    private String bTDescripcion;

    @Column(name = "BT_Codigo")
    private String bTCodigo;

    @JoinColumn(name = "ID", referencedColumnName = "ID", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private GuiaRemision guiaRemision;

    @Override
    public String toString() {
        return "org.ventura.cpe.core.dto.hb.GuiaRemisionLinea[ guiaRemisionLineaPK=" + guiaRemisionLineaPK + " ]";
    }

}
