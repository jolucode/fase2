package ventura.soluciones.ejecutable;

import java.time.LocalDateTime;

public class Prueba {

    private String error;

    private String type;

    private LocalDateTime fecha;

    public Prueba(String error, String type, LocalDateTime fecha) {
        this.error = error;
        this.type = type;
        this.fecha = fecha;
    }

    public Prueba() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
