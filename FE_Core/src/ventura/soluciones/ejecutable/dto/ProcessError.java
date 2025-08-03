package ventura.soluciones.ejecutable.dto;

import java.time.LocalDateTime;

public class ProcessError {

    private String error;

    private String type;

    private LocalDateTime fecha;

    public ProcessError(String error, String type, LocalDateTime fecha) {
        this.error = error;
        this.fecha = fecha;
        this.type = type;
    }
}