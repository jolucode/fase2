package org.ventura.wsclient.config;


public interface ISunatConnectorConfig {

    /**
     * Tabla:	Transaccion
     * Columna:	FE_TipoTrans
     */
    public static final String FE_TIPO_TRANS_EMISION = "E";
    public static final String FE_TIPO_TRANS_BAJA = "B";


    /**
     * Directorio de los documentos que se almacenan
     * en base al tipo de documento.
     */
    public static final String INVOICE_PATH = "factura";
    public static final String PERCEPTION_PATH = "percepcion";
    public static final String RETENTION_PATH = "retencion";
    public static final String REMISSION_GUIDE_PATH = "guiaremision";
    public static final String CARRIER_GUIDE_PATH = "transportista";
    public static final String BOLETA_PATH = "boleta";
    public static final String CREDIT_NOTE_PATH = "notacredito";
    public static final String DEBIT_NOTE_PATH = "notadebito";
    public static final String SUMMARY_DOCUMENT_PATH = "resumen";
    public static final String VOIDED_DOCUMENT_PATH = "baja";
    public static final String REVERSION_DOCUMENT_PATH = "reversion";


    /**
     * Extensiones de archivos
     */
    public static final String EE_XML = ".xml";
    public static final String EE_ZIP = ".zip";
    public static final String EE_PDF = ".pdf";


    /**
     * El formato de ENCODE.
     */
    public static final String ENCODING_UTF = "UTF-8";

    /**
     * Las llaves del objeto MAP que contiene la respuesta
     * del CDR
     */
    public static final String CDR_OBJECT_KEY = "CDR_OBJECT";
    public static final String CDR_BYTES_KEY = "CDR_BYTES";

} //ISunatConnectorConfig
