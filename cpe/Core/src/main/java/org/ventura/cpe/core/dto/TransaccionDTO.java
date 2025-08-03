package org.ventura.cpe.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
public class TransaccionDTO {

    private String fEId;

    private String tipoOperacionSunat;

    private String sNDIRPaisDescripcion;

    private String fechaDOCRef;

    private String docIdentidadNro;

    private String docIdentidadTipo;

    private String razonSocial;

    private String nombreComercial;

    private String personContacto;

    private String eMail;

    private String telefono;

    private String telefono1;

    private String web;

    private String dIRPais;

    private String dIRDepartamento;

    private String dIRProvincia;

    private String dIRDistrito;

    private String dIRDireccion;

    private String dIRNomCalle;

    private String dIRNroCasa;

    private String dIRUbigeo;

    private String dIRUrbanizacion;

    private String sNDocIdentidadNro;

    private String sNDocIdentidadTipo;

    private String sNRazonSocial;

    private String sNNombreComercial;

    private String sNEMail;

    private String sNEMailSecundario;

    private String sNSegundoNombre;

    private String sNDIRPais;

    private String sNDIRDepartamento;

    private String sNDIRProvincia;

    private String sNDIRDistrito;

    private String sNDIRDireccion;

    private String sNDIRNomCalle;

    private String sNDIRNroCasa;

    private String sNDIRUbigeo;

    private String sNDIRUrbanizacion;

    private String dOCSerie;

    private String dOCNumero;

    private String dOCId;

    private LocalDate dOCFechaEmision;

    private LocalDate dOCFechaVencimiento;

    private String dOCDscrpcion;

    private String dOCCodigo;

    private String dOCMONNombre;

    private String dOCMONCodigo;

    private BigDecimal dOCDescuento;

    private BigDecimal dOCPorDescuento;

    private BigDecimal dOCMontoTotal;

    private BigDecimal dOCDescuentoTotal;

    private BigDecimal dOCImporte;

    private BigDecimal dOCImporteTotal;

    private BigDecimal dOCMonPercepcion;

    private BigDecimal dOCPorPercepcion;

    private String dOCPorcImpuesto;

    private String aNTICIPOId;

    private String aNTICIPOTipo;

    private BigDecimal aNTICIPOMonto;

    private String aNTCIPOTipoDocID;

    private String aNTICIPONroDocID;

    private String sUNATTransact;

    private Integer fEDocEntry;

    private String fEObjectType;

    private String fEEstado;

    //@NotNull(message = "El campo tipo de transacci\u00fen no debe ser nula")
    private String fETipoTrans;

    private Integer fEDocNum;

    private String fEFormSAP;

    private String rEFDOCTipo;

    private String rEFDOCId;

    private String rEFDOCMotivCode;

    private String rEFDOCMotivDesc;

    private String fEComentario;

    private String fEErrCod;

    private String fEErrMsj;

    private Integer fEErrores;

    private Integer fEMaxSalto;

    private Integer fESaltos;

    private String dOCCondPago;

    private String rETRegimen;

    private String rETTasa;

    private String observaciones;

    private BigDecimal importePagado;

    private String monedaPagado;

    private BigDecimal dOCOtrosCargos;

    private BigDecimal dOCSinPercepcion;

    private BigDecimal dOCImpuestoTotal;

    private String cuentaDetraccion;

    private String codigoDetraccion;

    private BigDecimal porcDetraccion;

    private BigDecimal montoDetraccion;

    private String codigoPago;

    private TransaccionGuiaRemisionDTO transaccionGuiaRemision;

    //@NotNull(message = "La clave de la sociedad no puede ser nula.")
    private String keySociedad;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransaccionDTO that = (TransaccionDTO) o;
        return fEId.equals(that.fEId) && Objects.equals(keySociedad, that.keySociedad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fEId, keySociedad);
    }
}
