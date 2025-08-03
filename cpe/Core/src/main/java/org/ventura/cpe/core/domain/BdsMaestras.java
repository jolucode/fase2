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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * @author Yomsel
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "BDS_MAESTRAS")
@XmlRootElement
public class BdsMaestras implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @EqualsAndHashCode.Include
    @Basic(optional = false)
    @Size(max = 100)
    @Column(name = "BD_Id")
    private String bDId;

    @Basic(optional = false)
    @Size(max = 100)
    @Column(name = "BD_Servidor")
    private String bDServidor;

    @Basic(optional = false)
    @Column(name = "Servidor_Licencia")
    private String servidorLicencia;

    @Basic(optional = false)
    @Column(name = "Tipo_Servidor")
    private String tipoServidor;

    @Size(max = 100)
    @Basic(optional = false)
    @Column(name = "BD_Nombre")
    private String bDNombre;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "BD_Usuario")
    private String bDUsuario;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "BD_Clave")
    private String bDClave;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "ERP_Usuario")
    private String eRPUsuario;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "ERP_Clave")
    private String eRPClave;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "Ruc_Sociedad")
    private String rucSociedad;

    @Size(max = 1000)
    @Basic(optional = false)
    @Column(name = "Ruta_Archivos")
    private String rutaArchivos;

    @Size(max = 200)
    @Basic(optional = false)
    @Column(name = "Ruta_CD")
    private String rutaCD;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "Password_CD")
    private String passwordCD;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "Usuario_Sec")
    private String usuarioSec;

    @Size(max = 255)
    @Basic(optional = false)
    @Column(name = "Tipo_ERP")
    private String tipoErp;

    @Size(max = 200)
    @Basic(optional = false)
    @Column(name = "Logo_Sociedad")
    private String logoSociedad;

    ///
    @Size(max = 255)
    @Basic(optional = false)
    @Column(name = "Password_Sec")
    private String passwordSec;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "Tipo_Integracion_Sunat")
    private String tipoIntegracionSunat;

    @Size(max = 500)
    @Basic(optional = false)
    @Column(name = "Web_Service_Publicacion")
    private String webServicePublicacion;

    @Size(max = 100)
    @Basic(optional = false)
    @Column(name = "Usuario_Web_Service")
    private String usuarioWebService;

    @Size(max = 255)
    @Basic(optional = false)
    @Column(name = "Password_Web_Service")
    private String passwordWebService;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "Cliente_Sunat")
    private String clienteSunat;

    // Guias
    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "TipoIntegracionGuias")
    private String tipoIntegracionGuias;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "ClientId")
    private String clientId;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "ClientSecret")
    private String clientSecret;

    @Size(max = 50)
    @Basic(optional = false)
    @Column(name = "Usuario_Guias")
    private String usuarioGuias;

    @Size(max = 255)
    @Basic(optional = false)
    @Column(name = "Password_Guias")
    private String passwordGuias;

    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "Scope")
    private String scope;


    @Override
    public String toString() {
        return "BdsMaestras[ bDId=" + bDId + " ]";
    }
}
