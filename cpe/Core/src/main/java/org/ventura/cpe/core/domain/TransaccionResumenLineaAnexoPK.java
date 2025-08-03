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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Yosmel
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Embeddable
public class TransaccionResumenLineaAnexoPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "Id_Transaccion")
    private String idTransaccion;

    @Basic(optional = false)
    @Column(name = "Id_Linea")
    private int idLinea;

    @Override
    public String toString() {
        return "TransaccionResumenLineaAnexoPK[ idTransaccion=" + idTransaccion + ", idLinea=" + idLinea + " ]";
    }
}
