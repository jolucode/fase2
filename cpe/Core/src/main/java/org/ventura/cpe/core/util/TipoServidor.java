package org.ventura.cpe.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TipoServidor {

    private String nombre;

    private Integer numero;

    private Integer puerto;

    @Override
    public String toString() {
        return puerto + "";
    }
}
