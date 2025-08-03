package org.ventura.cpe.core.domain;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "ENVIO_DOCUMENTO")
public class EnvioDocumento {

    @Id
    @NonNull
    @Column(name = "FE_Id")
    private String idDocumento;

    @Column(name = "Cantidad_Envio")
    private Integer cantidadEnvio;

    //@NotNull(message = "La clave de la sociedad no puede ser nula.")
    @Column(name = "key_sociedad")
    private String keySociedad;

    @Override
    public String toString() {
        return "EnvioDocumento{" +
                "fEId='" + idDocumento + '\'' +
                ", fEErrores=" + cantidadEnvio +
                ", keySociedad='" + keySociedad + '\'' +
                '}';
    }
}
