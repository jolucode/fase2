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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Yosmel
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "RESUMENDIARIO_CONFIG")
@XmlRootElement
public class ResumendiarioConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id")
    private String id;

    @Id
    @Column(name = "fecha")
    private String fecha;

    @Column(name = "hora")
    private String hora;

    @Override
    public String toString() {
        return "ResumendiarioConfig[ fecha=" + fecha + " ]";
    }


}
