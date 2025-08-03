package org.ventura.cpe.core.ws.response;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Yosmel
 */
public class TransaccionRespuesta {

    public static final int DEFAULT = -10000;

    public static final int SIN_CDRSUNAT = -20000;

    public static final int RSP_ERROR = 10;

    /**
     * Indica si el documento consultado está de BAJA.
     */
    public static final int RSP_BAJA = 11;

    public static final int RSP_ENVIANDO = 9;

    public static final int RSP_CORRECTO = 8;

    /**
     * Nuevas Variables
     */

    public static final int RQT_EMITDO_ESPERA = 100;

    public static final int RSP_EMITDO_ESPERA = 101;

    public static final int RQT_EMITIDO_APROBADO = 20;

    public static final int RQT_EMITIDO_RECHAZADO = 21;

    public static final int RQT_EMITIDO_EXCEPTION = 22;

    public static final int RQT_EMITIDO_ERROR = 23;

    public static final int RQT_EMITIDO_PENDIENTE_CDR = 24;

    public static final int RQT_EMITIDO_EXCEPTION_1033 = 99;

    public static final int RQT_EMITIDO_CDR_NULO = 399;

    public static final int RQT_BAJA_APROBADO = 30;

    public static final int RQT_BAJA_RECHAZADO = 31;

    public static final int RQT_BAJA_EXCEPCION = 32;

    public static final int RQT_BAJA_ERROR = 33;

    public static final int RSP_EMITIDO_APROBADO = 41;

    public static final int RSP_EMITIDO_RECHAZADO = 42;

    public static final int RSP_EMITIDO_EXCEPTION = 43;

    public static final int RSP_EMITIDO_ERROR = 44;

    public static final int RSP_BAJA_APROBADO = 50;

    public static final int RSP_BAJA_RECHAZADO = 51;

    public static final int RSP_BAJA_EXCEPCION = 52;

    public static final int RSP_BAJA_ERROR = 53;

    public static final int RSP_BAJA_EMITIDO_ANTERIORMENTE = 54;

    public static final int RSP_RSM_APROBADO = 200;

    public static final int RSP_RSM_RECHAZADO = 201;

    public static final int RSP_RESM_EXCEPCION = 202;

    /**
     * Indica el número máximo de reintentos antes de reducir la frecuencia de
     * envío.
     */
    public static final int MAX_ERRORES = 3;

    public static final int RQT_ERROR = 10;

    /**
     * Indica si el documento enviado ya está de BAJA.
     */
    public static final int RQT_BAJA = 11;

    public static final int RQT_CORRECTO = 8;

    public static int RQTIMEOUT;

    public static int RQINTERVAL;

    public static int RSTIMEOUT;

    public static int RSINTERVAL;

    public static String WS_TIEMPOPUBLICACION;

    private int codigo = DEFAULT;

    private int codigoWS = 0;

    private String hashCode;

    private String mensaje = "No enviado";

    private String estado;

    private String identificador;

    private String uuid;

    private byte[] pdf;

    private byte[] xml;

    private byte[] zip;

    private Sunat sunat;

    private String EmitioError;

    private String nroTique;

    public TransaccionRespuesta() {

    }

    public TransaccionRespuesta(int codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public String getEmitioError() {
        return EmitioError;
    }

    public void setEmitioError(String EmitioError) {
        this.EmitioError = EmitioError;
    }

    public String getNroTique() {
        return nroTique;
    }

    public void setNroTique(String nroTique) {
        this.nroTique = nroTique;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getHashCode() {
        return hashCode;
    }

    public void setHashCode(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the pdf
     */
    public byte[] getPdf() {
        return pdf;
    }

    /**
     * @param pdf the pdf to set
     */
    public void setPdf(byte[] pdf) {
        this.pdf = pdf;
    }

    /**
     * @return the xml
     */
    public byte[] getXml() {
        return xml;
    }

    /**
     * @param xml the xml to set
     */
    public void setXml(byte[] xml) {
        this.xml = xml;
    }

    /**
     * @return the zip
     */
    public byte[] getZip() {
        return zip;
    }

    /**
     * @param zip the zip to set
     */
    public void setZip(byte[] zip) {
        this.zip = zip;
    }

    /**
     * @return the codigoWS
     */
    public int getCodigoWS() {
        return codigoWS;
    }

    /**
     * @param codigoWS the codigoWS to set
     */
    public void setCodigoWS(int codigoWS) {
        this.codigoWS = codigoWS;
    }

    /**
     * @return the sunat
     */
    public Sunat getSunat() {
        return sunat;
    }

    /**
     * @param sunat the sunat to set
     */
    public void setSunat(Sunat sunat) {
        this.sunat = sunat;
    }

    public boolean hasAllDocuments() {
        return Optional.ofNullable(pdf).isPresent() && Optional.ofNullable(xml).isPresent() && Optional.ofNullable(zip).isPresent();
    }

    @Override
    public String toString() {
        return "TransaccionRespuesta{" +
                "codigo=" + codigo +
                ", codigoWS=" + codigoWS +
                ", hashCode='" + hashCode + '\'' +
                ", mensaje='" + mensaje + '\'' +
                ", estado='" + estado + '\'' +
                ", identificador='" + identificador + '\'' +
                ", uuid='" + uuid + '\'' +
                ", pdf=" + Arrays.toString(pdf) +
                ", xml=" + Arrays.toString(xml) +
                ", zip=" + Arrays.toString(zip) +
                ", sunat=" + sunat +
                ", EmitioError='" + EmitioError + '\'' +
                ", nroTique='" + nroTique + '\'' +
                '}';
    }

    public static class Observacion {

        private int codigo;

        private String message;

        public Observacion(int codigo, String message) {
            this.codigo = codigo;
            this.message = message;
        }

        public int getCodigo() {
            return codigo;
        }

        public void setCodigo(int codigo) {
            this.codigo = codigo;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public static class Sunat {

        private int codigo = -1;

        private String mensaje = "";

        private String id = "";

        private boolean aceptado = false;

        private List<Observacion> listaObs;

        private String emisor;

        public String getEmisor() {
            return emisor;
        }

        public void setEmisor(String emisor) {
            this.emisor = emisor;
        }

        public boolean isAceptado() {
            return aceptado;
        }

        /**
         * @return the codigo
         */
        public int getCodigo() {
            return codigo;
        }

        /**
         * @param codigo the codigo to set
         */
        public void setCodigo(int codigo) {
            this.codigo = codigo;
            aceptado = (this.codigo == 0);
        }

        /**
         * @return the mensaje
         */
        public String getMensaje() {
            return mensaje;
        }

        /**
         * @param mensaje the mensaje to set
         */
        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the List Observacoin
         */
        public List<Observacion> getListaObs() {
            return listaObs;
        }

        public void setListaObs(List<Observacion> listaObs) {
            this.listaObs = listaObs;
        }

        @Override
        public String toString() {
            return "[" + this.id + "]-(" + this.codigo + ") " + this.mensaje;
        }

    }
}
