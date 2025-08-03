package org.ventura.soluciones.commons.handler;

import oasis.names.specification.ubl.schema.xsd.applicationresponse_2.ApplicationResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentResponseType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ResponseType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NoteType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase HANDLER contiene los metodos para extraer la informacion de la
 * constancia de rercepcion (CDR) de Sunat.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class CDRResponseHandler {

    private final Logger logger = Logger.getLogger(CDRResponseHandler.class);

    /* Identicador del objeto */
    private String identifier;

    /**
     * Constructor privado basico para evitar la creacion de instancias usando
     * el constructor.
     */
    private CDRResponseHandler() {
    }

    /**
     * Constructor privado para evitar la creacion de instancias usando el
     * constructor.
     *
     * @param identifier Identificador del objeto CDRResponseHandler.
     */
    private CDRResponseHandler(String identifier) {
        this.identifier = identifier;
    } // CDRResponseHandler

    /**
     * Este metodo crea una nueva instancia de la clase CDRResponseHandler.
     *
     * @return Retorna una nueva instancia de la clase CDRResponseHandler.
     */
    public static synchronized CDRResponseHandler newInstance() {
        return new CDRResponseHandler();
    } // newInstance

    /**
     * Este metodo crea una nueva instancia de la clase CDRResponseHandler.
     *
     * @param identifier Identificador del objeto CDRResponseHandler.
     * @return Retorna una nueva instancia de la clase CDRResponseHandler.
     */
    public static synchronized CDRResponseHandler newInstance(String identifier) {
        return new CDRResponseHandler(identifier);
    } // newInstance

    /**
     * Este metodo retorna el codigo de respuesta del CDR.
     *
     * @param applicationResponse El objeto JAXB ApplicationResponseType.
     * @param docUUID             El UUID del documento.
     * @return Retorna el codigo de respuesta del CDR.
     */
    public int getCode(ApplicationResponseType applicationResponse,
                       String docUUID) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getCode()"
                    + (null != this.identifier ? " [" + this.identifier + "]"
                    : "") + " [" + docUUID + "]");
        }
        DocumentResponseType documentResponse = applicationResponse
                .getDocumentResponse().get(0);
        ResponseType response = documentResponse.getResponse();

        return Integer.parseInt(response.getResponseCode().getValue());
    } // getCode

    /**
     * Este metodo retorna el identificador de respuesta del CDR.
     *
     * @param applicationResponse El objeto JAXB ApplicationResponseType.
     * @param docUUID             El UUID del documento.
     * @return Retorna el identificador del CDR.
     */
    public String getID(ApplicationResponseType applicationResponse,
                        String docUUID) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getID()"
                    + (null != this.identifier ? " [" + this.identifier + "]"
                    : "") + " [" + docUUID + "]");
        }
        return applicationResponse.getID().getValue();
    } // getID

    /**
     * Este metodo retorna el mensaje de respuesta del CDR.
     *
     * @param applicationResponse El objeto JAXB ApplicationResponseType.
     * @param docUUID             El UUID del documento.
     * @return Retorna el mensaje de respuesta del CDR.
     */
    public String getMessage(ApplicationResponseType applicationResponse,
                             String docUUID) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getMessage()"
                    + (null != this.identifier ? " [" + this.identifier + "]"
                    : "") + " [" + docUUID + "]");
        }
        DocumentResponseType documentResponse = applicationResponse
                .getDocumentResponse().get(0);
        ResponseType response = documentResponse.getResponse();

        return response.getDescription().get(0).getValue();
    } // getDescription

    /**
     * Este metodo retorna las notas de observacion del CDR.
     *
     * @param applicationResponse El objeto JAXB ApplicationResponseType.
     * @param docUUID             El UUID del documento.
     * @return Retorna las notas de observacion del CDR.
     */
    public List<String> getNotes(ApplicationResponseType applicationResponse,
                                 String docUUID) {
        if (logger.isDebugEnabled()) {
            logger.debug("+-getNotes()"
                    + (null != this.identifier ? " [" + this.identifier + "]"
                    : "") + " [" + docUUID + "]");
        }
        List<String> messageList = new ArrayList<String>();

        List<NoteType> noteList = applicationResponse.getNote();
        if (null != noteList && 0 < noteList.size()) {
            for (NoteType note : noteList) {
                String[] parts = note.getValue().split("-");
                String message = parts[1].trim();

                messageList.add(message);
            }
        }

        return messageList;
    } // getNotes

    /**
     * Este metodo retorna las notas de observacion del CDR concatenadas en un
     * String.
     *
     * @param noteList Lista de notas del CDR.
     * @return Retorna las notas del CDR concatenadas en un String.
     */
    public String dumpNotes(List<String> noteList) {
        String dump = new String("");

        if (null != noteList && 0 < noteList.size()) {
            dump = "\n Sunat_notes: ";

            for (String note : noteList) {
                dump += "\n\t- " + note;
            }
        }
        return dump;
    } // dumpNotes

} // CDRResponseHandler
