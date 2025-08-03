package org.ventura.cpe.core.util;

public class TransactionResponse {

    private String documentoId;

    private String database;

    private String estado;

    private String message;

    public TransactionResponse() {
    }

    public TransactionResponse(String documentoId, String database, String estado, String message) {
        this.documentoId = documentoId;
        this.database = database;
        this.estado = estado;
        this.message = message;
    }

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
