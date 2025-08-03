package org.ventura.cpe.core.handler;

import org.ventura.cpe.core.config.IUBLConfig;

import java.text.MessageFormat;

/**
 * Esta clase contiene metodo para obtener el nombre de un documento segun el
 * formato de SUNAT.
 *
 * @author Yosmel Lopez Pimentel
 */
public final class DocumentNameHandler {

    private static final String DOCUMENT_PATTERN = "{0}-{1}-{2}";

    private static final String ANEXO_PATTERN = "{0}_{1}_{2}";

    private static final String CDR_RESPONSE_NAME = "R-{0}";

    private static final String EE_ZIP = ".zip";

    /**
     * Este metodo obtiene el nombre de un documento de tipo PERCEPCION.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento (Serie y correlativo).
     * @return Retorna el nombre del documento de tipo PERCEPCION.
     */
    public static String getPerceptionName(String senderRUC, String docIdentifier) {
        return MessageFormat.format(DOCUMENT_PATTERN, senderRUC, IUBLConfig.DOC_PERCEPTION_CODE, docIdentifier);
    } // getPerceptionName

    /**
     * Este metodo obtiene el nombre de un documento de tipo RETENCION.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento (Serie y correlativo).
     * @return Retorna el nombre del documento de tipo RETENCION.
     */
    public static String getRemissionGuideName(String senderRUC, String docIdentifier) {
        return MessageFormat.format(DOCUMENT_PATTERN, senderRUC, IUBLConfig.DOC_SENDER_REMISSION_GUIDE_CODE, docIdentifier);
    }

    /**
     * Este metodo obtiene el nombre de un documento de tipo RETENCION.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento (Serie y correlativo).
     * @return Retorna el nombre del documento de tipo RETENCION.
     */
    public static String getRetentionName(String senderRUC, String docIdentifier) {
        return MessageFormat.format(DOCUMENT_PATTERN, senderRUC, IUBLConfig.DOC_RETENTION_CODE, docIdentifier);
    }

    /**
     * Este metodo obtiene el nombre de un documento de tipo FACTURA.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento (Serie y correlativo).
     * @return Retorna el nombre del documento de tipo FACTURA.
     */
    public static String getInvoiceName(String senderRUC, String docIdentifier) {
        return MessageFormat.format(DOCUMENT_PATTERN, senderRUC, IUBLConfig.DOC_INVOICE_CODE, docIdentifier);
    }

    public static String getAnexoName(String serie, String correlativo, String sufijo) {
        return MessageFormat.format(ANEXO_PATTERN, serie, correlativo, sufijo);
    }

    /**
     * Este metodo obtiene el nombre de un documento de tipo BOLETA.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento (Serie y correlativo).
     * @return Retorna el nombre del documento de tipo BOLETA.
     */
    public static String getBoletaName(String senderRUC, String docIdentifier) {
        return MessageFormat.format(DOCUMENT_PATTERN, senderRUC, IUBLConfig.DOC_BOLETA_CODE, docIdentifier);
    }

    /**
     * Este metodo obtiene el nombre de un documento de tipo NOTA DE CREDITO.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento (Serie y correlativo).
     * @return Retorna el nombre del documento de tipo NOTA DE CREDITO.
     */
    public static String getCreditNoteName(String senderRUC, String docIdentifier) {
        return MessageFormat.format(DOCUMENT_PATTERN, senderRUC, IUBLConfig.DOC_CREDIT_NOTE_CODE, docIdentifier);
    }

    /**
     * Este metodo obtiene el nombre de un documento de tipo NOTA DE DEBITO.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento (Serie y correlativo).
     * @return Retorna el nombre del documento de tipo NOTA DE DEBITO.
     */
    public static String getDebitNoteName(String senderRUC, String docIdentifier) {
        return MessageFormat.format(DOCUMENT_PATTERN, senderRUC, IUBLConfig.DOC_DEBIT_NOTE_CODE, docIdentifier);
    }

    /**
     * Este metodo obtiene el nombre de un documento de tipo COMUNICACION DE
     * BAJA.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento.
     * @return Retorna el nombre del documento de tipo COMUNICACION DE BAJA.
     */
    public static String getVoidedDocumentName(String senderRUC, String docIdentifier) {
        return senderRUC + "-" + docIdentifier;
    }

    public static String getVoidedCPEDocumentName(String senderRUC, String docIdentifier) {
        return senderRUC + "-" + docIdentifier;
    }

    /**
     * Este metodo obtiene el nombre de un documento de tipo RESUMEN DIARIO.
     *
     * @param senderRUC     Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento.
     * @return Retorna el nombre del documento de tipo RESUMEN DIARIO.
     */
    public static String getSummaryDocumentName(String senderRUC, String docIdentifier) {
        return senderRUC + "-" + docIdentifier;
    }

    /**
     * Este metodo retorna el nombre del documento concatenando el valor .zip
     *
     * @param documentName El nombre del documento.
     * @return Retorna el nombre del documento concatenando el valor .zip
     */
    public static String getZipName(String documentName) {
        return documentName + EE_ZIP;
    }

    /**
     * Este metodo retorna el nombre del documento en formato de respuesta CDR.
     *
     * @param documentName El nombre del documento UBL.
     * @return Retorna el nombre de la constancia CDR de respuesta.
     */
    public static String getCDRConstancyName(String documentName) {
        return MessageFormat.format(CDR_RESPONSE_NAME, documentName);
    }

} // DocumentNameHandler
