package org.ventura.cpe.core.publicacion;

public class PublicacionResponse {

    private String mensaje;

    private String error;

    public PublicacionResponse() {
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
