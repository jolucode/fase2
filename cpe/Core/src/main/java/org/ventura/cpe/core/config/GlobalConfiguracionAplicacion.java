package org.ventura.cpe.core.config;

public class GlobalConfiguracionAplicacion {

    private final ConfiguracionAplicacion configuracion;

    public GlobalConfiguracionAplicacion(ConfiguracionAplicacion configuracion) {
        this.configuracion = configuracion;
    }

    public ConfiguracionAplicacion getConfiguracion() {
        return configuracion;
    }
}
