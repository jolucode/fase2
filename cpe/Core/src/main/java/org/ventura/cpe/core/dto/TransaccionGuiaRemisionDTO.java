package org.ventura.cpe.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ventura.cpe.core.domain.Transaccion;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class TransaccionGuiaRemisionDTO {

    private String fEId;

    private String codigoPuerto;

    private String numeroContenedor;

    private String tipoDocConductor;

    private String documentoConductor;

    private String placaVehiculo;

    private String rUCTransporista;

    private String tipoDOCTransportista;

    private String nombreRazonTransportista;

    private Date fechaInicioTraslado;

    private String modalidadTraslado;

    private BigDecimal numeroBultos;

    private String unidadMedida;

    private BigDecimal peso;

    private String indicadorTransbordoProgramado;

    private String codigoMotivo;

    private String descripcionMotivo;

    private String licenciaConducir;

    private String direccionPartida;

    private String ubigeoPartida;

    private Transaccion transaccion;
}
