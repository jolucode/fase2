package org.ventura.cpe.core.util;

public class CodigoBarra {

    private String documentoId;

    private String barcodeValue;

    private String digestValue;

    public CodigoBarra() {
    }

    public CodigoBarra(String documentoId, String barcodeValue, String digestValue) {
        this.documentoId = documentoId;
        this.barcodeValue = barcodeValue;
        this.digestValue = digestValue;
    }

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }

    public String getBarcodeValue() {
        return barcodeValue;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
    }

    public String getDigestValue() {
        return digestValue;
    }

    public void setDigestValue(String digestValue) {
        this.digestValue = digestValue;
    }
}
