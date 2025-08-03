/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.core.dto;

import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Yosmel
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicacionDocumentoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fEId;

    private String nombreConsumidor;

    private String emailConsumidor;

    private String numeroSerie;

    private String tipoDocumento;

    private BigDecimal total;

    private String estadoSunat;

    private String moneda;

    private String emailEmisor;

    private String rutaPdf;

    private String rutaXml;

    private String rutaCdr;

    private Integer estado;

    private String msjError;

    private String ruc;

    private String fechaEmision;

    @Override
    public String toString() {
        return "PublicacionDocumento[ fEId=" + fEId + " ]";
    }
}
