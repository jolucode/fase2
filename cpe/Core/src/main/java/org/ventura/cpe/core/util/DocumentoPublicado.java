package org.ventura.cpe.core.util;

import org.springframework.util.Base64Utils;
import org.ventura.cpe.core.domain.PublicardocWs;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DocumentoPublicado {

//    "idDocumento", "numSerie", "fecEmisionDoc", 'estadoSunat', 'estadoWeb', 'tipoDoc', "tipoTransaccion", "total", "docPdf", "docXml", "docCdr", "rucClient", "monedaTransaccion"
    //['direccionClient', 'estadoCliente', 'rutaImagenClient'

    private String usuarioSesion;

    private String claveSesion;

    private String rucClient;

    private String nombreClient;

    private String email;

    private String numSerie;

    private String fecEmisionDoc;

    private String tipoDoc;

    private String total;

    private String docPdf;

    private String docXml;

    private String docCdr;

    private String estadoSunat;

    private String monedaTransaccion;

    private String emailEmisor;

    private String tipoTransaccion;

    private String correoSecundario;

    private String rsRuc;

    private String rsDescripcion;

    private String serie;

    public DocumentoPublicado(String usuarioSesion, String password, PublicardocWs docPublicar) {
        this.usuarioSesion = usuarioSesion;
        this.claveSesion = password;
        rucClient = docPublicar.getSNDocIdentidadNro();
        nombreClient = docPublicar.getSNRazonSocial();
        email = docPublicar.getSNEMail();
        String docId = docPublicar.getDOCId();
        numSerie = docPublicar.getRSRuc() + "-" + docId;
        String[] split = docId.split("[-]");
        serie = split.length > 0 ? split[0] : "";
        LocalDate fechaEmision = docPublicar.getDOCFechaEmision();
        fecEmisionDoc = fechaEmision.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        tipoDoc = docPublicar.getDOCCodigo();
        total = docPublicar.getDOCMontoTotal().setScale(2, RoundingMode.HALF_UP).toString();
        estadoSunat = docPublicar.getEstadoSUNAT() + "";
        monedaTransaccion = docPublicar.getDOCMONCodigo();
        emailEmisor = docPublicar.getEMailEmisor();
        tipoTransaccion = docPublicar.getFETipoTrans();
        correoSecundario = docPublicar.getSNEMailSecundario();
        rsRuc = docPublicar.getRSRuc();
        rsDescripcion = docPublicar.getRSDescripcion();
        docPdf = docPublicar.getRutaPDF();
        docXml = docPublicar.getRutaXML();
        docCdr = docPublicar.getRutaZIP();
    }

    public void encodeFiles() {
        encodeFileToBase64Binary(docPdf).ifPresent(base64 -> docPdf = base64);
        encodeFileToBase64Binary(docXml).ifPresent(base64 -> docXml = base64);
        encodeFileToBase64Binary(docCdr).ifPresent(base64 -> docCdr = base64);
    }

    public String getUsuarioSesion() {
        return usuarioSesion;
    }

    public void setUsuarioSesion(String usuarioSesion) {
        this.usuarioSesion = usuarioSesion;
    }

    public String getClaveSesion() {
        return claveSesion;
    }

    public void setClaveSesion(String claveSesion) {
        this.claveSesion = claveSesion;
    }

    public String getRucClient() {
        return rucClient;
    }

    public void setRucClient(String rucClient) {
        this.rucClient = rucClient;
    }

    public String getNombreClient() {
        return nombreClient;
    }

    public void setNombreClient(String nombreClient) {
        this.nombreClient = nombreClient;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumSerie() {
        return numSerie;
    }

    public void setNumSerie(String numSerie) {
        this.numSerie = numSerie;
    }

    public String getFecEmisionDoc() {
        return fecEmisionDoc;
    }

    public void setFecEmisionDoc(String fecEmisionDoc) {
        this.fecEmisionDoc = fecEmisionDoc;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDocPdf() {
        return docPdf;
    }

    public void setDocPdf(String docPdf) {
        this.docPdf = docPdf;
    }

    public String getDocXml() {
        return docXml;
    }

    public void setDocXml(String docXml) {
        this.docXml = docXml;
    }

    public String getDocCdr() {
        return docCdr;
    }

    public void setDocCdr(String docCdr) {
        this.docCdr = docCdr;
    }

    public String getEstadoSunat() {
        return estadoSunat;
    }

    public void setEstadoSunat(String estadoSunat) {
        this.estadoSunat = estadoSunat;
    }

    public String getMonedaTransaccion() {
        return monedaTransaccion;
    }

    public void setMonedaTransaccion(String monedaTransaccion) {
        this.monedaTransaccion = monedaTransaccion;
    }

    public String getEmailEmisor() {
        return emailEmisor;
    }

    public void setEmailEmisor(String emailEmisor) {
        this.emailEmisor = emailEmisor;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public String getCorreoSecundario() {
        return correoSecundario;
    }

    public void setCorreoSecundario(String correoSecundario) {
        this.correoSecundario = correoSecundario;
    }

    public String getRsRuc() {
        return rsRuc;
    }

    public void setRsRuc(String rsRuc) {
        this.rsRuc = rsRuc;
    }

    public String getRsDescripcion() {
        return rsDescripcion;
    }

    public void setRsDescripcion(String rsDescripcion) {
        this.rsDescripcion = rsDescripcion;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    private Optional<String> encodeFileToBase64Binary(String fileName) {
        try {
            Path path = Paths.get(fileName);
            byte[] bytes = Files.readAllBytes(path);
            return Optional.of(Base64Utils.encodeToString(bytes));
        } catch (IOException | NullPointerException e) {
            return Optional.empty();
        }
    }
}
