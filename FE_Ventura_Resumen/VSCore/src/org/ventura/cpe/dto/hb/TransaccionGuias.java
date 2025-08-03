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
 *
 * @author VS-LT-06
 */
@Entity
@Table(name = "TRANSACCION_GUIAS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TransaccionGuias.findAll", query = "SELECT t FROM TransaccionGuias t")})
public class TransaccionGuias implements Serializable {
    @Column(name = "CodigoTransporte")
    private String codigoTransporte;
    @Column(name = "PesoTransporte")
    private String pesoTransporte;
    @Column(name = "Unidad")
    private String unidad;
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TransaccionGuiasPK transaccionGuiasPK;
    @Column(name = "Origen_Ubigeo")
    private String origenUbigeo;
    @Column(name = "Origen_Calle")
    private String origenCalle;
    @Column(name = "Origen_Distrito")
    private String origenDistrito;
    @Column(name = "Origen_Provincia")
    private String origenProvincia;
    @Column(name = "Origen_Departamento")
    private String origenDepartamento;
    @Column(name = "Origen_Pais")
    private String origenPais;
    @Column(name = "Destino_Ubigeo")
    private String destinoUbigeo;
    @Column(name = "Destino_Calle")
    private String destinoCalle;
    @Column(name = "Destino_Distrito")
    private String destinoDistrito;
    @Column(name = "Destino_Provincia")
    private String destinoProvincia;
    @Column(name = "Destino_Departamento")
    private String destinoDepartamento;
    @Column(name = "Destino_Pais")
    private String destinoPais;
    @Column(name = "Transportista_DocIdentidad")
    private String transportistaDocIdentidad;
    @Column(name = "Transportista_DocIdTipo")
    private String transportistaDocIdTipo;
    @Column(name = "Transportista_RazonSocial")
    private String transportistaRazonSocial;
    @Column(name = "Conductor_DocumentoId")
    private String conductorDocumentoId;
    @Column(name = "Vehiculo_Matricula")
    private String vehiculoMatricula;
    @Column(name = "Vehiculo_Autorizacion")
    private String vehiculoAutorizacion;
    @Column(name = "Vehiculo")
    private String vehiculo;
    @JoinColumn(name = "FE_Id", referencedColumnName = "FE_Id", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Transaccion transaccion;

    public TransaccionGuias() {
    }

    public TransaccionGuias(TransaccionGuiasPK transaccionGuiasPK) {
        this.transaccionGuiasPK = transaccionGuiasPK;
    }

    public TransaccionGuias(String fEId, int nroOrden) {
        this.transaccionGuiasPK = new TransaccionGuiasPK(fEId, nroOrden);
    }

    public TransaccionGuiasPK getTransaccionGuiasPK() {
        return transaccionGuiasPK;
    }

    public void setTransaccionGuiasPK(TransaccionGuiasPK transaccionGuiasPK) {
        this.transaccionGuiasPK = transaccionGuiasPK;
    }

    public String getOrigenUbigeo() {
        return origenUbigeo;
    }

    public void setOrigenUbigeo(String origenUbigeo) {
        this.origenUbigeo = origenUbigeo;
    }

    public String getOrigenCalle() {
        return origenCalle;
    }

    public void setOrigenCalle(String origenCalle) {
        this.origenCalle = origenCalle;
    }

    public String getOrigenDistrito() {
        return origenDistrito;
    }

    public void setOrigenDistrito(String origenDistrito) {
        this.origenDistrito = origenDistrito;
    }

    public String getOrigenProvincia() {
        return origenProvincia;
    }

    public void setOrigenProvincia(String origenProvincia) {
        this.origenProvincia = origenProvincia;
    }

    public String getOrigenDepartamento() {
        return origenDepartamento;
    }

    public void setOrigenDepartamento(String origenDepartamento) {
        this.origenDepartamento = origenDepartamento;
    }

    public String getOrigenPais() {
        return origenPais;
    }

    public void setOrigenPais(String origenPais) {
        this.origenPais = origenPais;
    }

    public String getDestinoUbigeo() {
        return destinoUbigeo;
    }

    public void setDestinoUbigeo(String destinoUbigeo) {
        this.destinoUbigeo = destinoUbigeo;
    }

    public String getDestinoCalle() {
        return destinoCalle;
    }

    public void setDestinoCalle(String destinoCalle) {
        this.destinoCalle = destinoCalle;
    }

    public String getDestinoDistrito() {
        return destinoDistrito;
    }

    public void setDestinoDistrito(String destinoDistrito) {
        this.destinoDistrito = destinoDistrito;
    }

    public String getDestinoProvincia() {
        return destinoProvincia;
    }

    public void setDestinoProvincia(String destinoProvincia) {
        this.destinoProvincia = destinoProvincia;
    }

    public String getDestinoDepartamento() {
        return destinoDepartamento;
    }

    public void setDestinoDepartamento(String destinoDepartamento) {
        this.destinoDepartamento = destinoDepartamento;
    }

    public String getDestinoPais() {
        return destinoPais;
    }

    public void setDestinoPais(String destinoPais) {
        this.destinoPais = destinoPais;
    }

    public String getTransportistaDocIdentidad() {
        return transportistaDocIdentidad;
    }

    public void setTransportistaDocIdentidad(String transportistaDocIdentidad) {
        this.transportistaDocIdentidad = transportistaDocIdentidad;
    }

    public String getTransportistaDocIdTipo() {
        return transportistaDocIdTipo;
    }

    public void setTransportistaDocIdTipo(String transportistaDocIdTipo) {
        this.transportistaDocIdTipo = transportistaDocIdTipo;
    }

    public String getTransportistaRazonSocial() {
        return transportistaRazonSocial;
    }

    public void setTransportistaRazonSocial(String transportistaRazonSocial) {
        this.transportistaRazonSocial = transportistaRazonSocial;
    }

    public String getConductorDocumentoId() {
        return conductorDocumentoId;
    }

    public void setConductorDocumentoId(String conductorDocumentoId) {
        this.conductorDocumentoId = conductorDocumentoId;
    }

    public String getVehiculoMatricula() {
        return vehiculoMatricula;
    }

    public void setVehiculoMatricula(String vehiculoMatricula) {
        this.vehiculoMatricula = vehiculoMatricula;
    }

    public String getVehiculoAutorizacion() {
        return vehiculoAutorizacion;
    }

    public void setVehiculoAutorizacion(String vehiculoAutorizacion) {
        this.vehiculoAutorizacion = vehiculoAutorizacion;
    }

    public String getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(String vehiculo) {
        this.vehiculo = vehiculo;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (transaccionGuiasPK != null ? transaccionGuiasPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TransaccionGuias)) {
            return false;
        }
        TransaccionGuias other = (TransaccionGuias) object;
        if ((this.transaccionGuiasPK == null && other.transaccionGuiasPK != null) || (this.transaccionGuiasPK != null && !this.transaccionGuiasPK.equals(other.transaccionGuiasPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.ventura.cpe.dto.hb.TransaccionGuias[ transaccionGuiasPK=" + transaccionGuiasPK + " ]";
    }

    public String getCodigoTransporte() {
        return codigoTransporte;
    }

    public void setCodigoTransporte(String codigoTransporte) {
        this.codigoTransporte = codigoTransporte;
    }

    public String getPesoTransporte() {
        return pesoTransporte;
    }

    public void setPesoTransporte(String pesoTransporte) {
        this.pesoTransporte = pesoTransporte;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
    
}
