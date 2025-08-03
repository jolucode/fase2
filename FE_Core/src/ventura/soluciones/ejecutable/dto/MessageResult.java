package ventura.soluciones.ejecutable.dto;

import java.time.LocalDateTime;

public class MessageResult {

    private String message;

    private MessageType type;

    private LocalDateTime fecha;

    public MessageResult(String mensaje, MessageType type, LocalDateTime fecha) {
        this.message = mensaje;
        this.fecha = fecha;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "MessageResult{" +
                "message='" + message + '\'' +
                ", type=" + type +
                ", fecha=" + fecha +
                '}';
    }
}