package org.ventura.wsclient.handler;


import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ventura.wsclient.exception.ValidationException;
import ventura.soluciones.commons.config.IUBLConfig;
import ventura.soluciones.commons.exception.error.IVenturaError;

/**
 * Esta clase contiene metodos para validar los documento UBL.
 * 
 */
public class ValidationHandler {

    private final Logger logger = Logger.getLogger(ValidationHandler.class);

    private String docUUID;

    /**
     * Constructor privado para evitar instancias.
     *
     * @param docUUID UUID que identifica al documento.
     */
    private ValidationHandler(String docUUID) {
        this.docUUID = docUUID;
    } //ValidationHandler

    /**
     * Este metodo crea una nueva instancia de la clase ValidationHandler.
     *
     * @param docUUID UUID que identifica al documento.
     * @return Retorna una nueva instancia de la clase ValidationHandler.
     */
    public static synchronized ValidationHandler newInstance(String docUUID) {
        return new ValidationHandler(docUUID);
    } //newInstance

    public void checkBasicInformation3(String docIdentifier, String senderIdentifier, String issueDate) throws ValidationException {
        if (logger.isDebugEnabled()) {
            logger.debug("+checkBasicInformation2() [" + this.docUUID + "]");
        }
        /*
         * Validando identificador del documento
         */
        if (StringUtils.isBlank(docIdentifier)) {
            throw new ValidationException(IVenturaError.ERROR_529);
        }
        if (!docIdentifier.startsWith(IUBLConfig.VOIDED_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.SUMMARY_SERIE_PREFIX) && !docIdentifier.startsWith(IUBLConfig.VOIDED_SERIE_PREFIX_CPE)) {
            throw new ValidationException(IVenturaError.ERROR_530);
        }

        /*
         * Validando RUC del emisor electronico
         */
        if (IUBLConfig.DOC_RUC_LENGTH != senderIdentifier.length()) {
            throw new ValidationException(IVenturaError.ERROR_519);
        }
        try {
            Long.valueOf(senderIdentifier);
        } catch (Exception e) {
            throw new ValidationException(IVenturaError.ERROR_520);
        }

        /*
         * Validando la fecha de emision
         */
        if (null == issueDate) {
            throw new ValidationException(IVenturaError.ERROR_521);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-checkBasicInformation2() [" + this.docUUID + "]");
        }
    } //checkBasicInformation2

} //ValidationHandler