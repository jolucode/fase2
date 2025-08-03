package org.ventura.cpe.core.util;

public class AnexoDocumento {

    private String documentoId;

    private String database;

    private int docEntry;

    private String pdfPath;

    private String xmlPath;

    private String cdrPath;

    public AnexoDocumento() {
    }

    public AnexoDocumento(String documentoId, String database, int docEntry) {
        this.documentoId = documentoId;
        this.database = database;
        this.docEntry = docEntry;
    }

    public AnexoDocumento(String documentoId, int docEntry, String pdfPath, String xmlPath, String cdrPath) {
        this.documentoId = documentoId;
        this.docEntry = docEntry;
        this.pdfPath = pdfPath;
        this.xmlPath = xmlPath;
        this.cdrPath = cdrPath;
    }

    public String getDocumentoId() {
        return documentoId;
    }

    public void setDocumentoId(String documentoId) {
        this.documentoId = documentoId;
    }

    public int getDocEntry() {
        return docEntry;
    }

    public void setDocEntry(int docEntry) {
        this.docEntry = docEntry;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }

    public String getXmlPath() {
        return xmlPath;
    }

    public void setXmlPath(String xmlPath) {
        this.xmlPath = xmlPath;
    }

    public String getCdrPath() {
        return cdrPath;
    }

    public void setCdrPath(String cdrPath) {
        this.cdrPath = cdrPath;
    }
}
