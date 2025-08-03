package ventura.soluciones.commons.handler;

/**
 * Esta clase contiene metodo para obtener el nombre de un documento segun el
 * formato de SUNAT.
 *
 */
public class DocumentNameHandler {

    private static DocumentNameHandler instance = null;

    private final String EE_ZIP = ".zip";
    /**
     * Constructor privado para evitar instancias.
     */
    private DocumentNameHandler() {
    }

    /**
     * Este metodo obtiene una instancia de la clase DocumentNameHandler.
     *
     * @return Retorna una instancia de la clase DocumentNameHandler.
     */
    public static synchronized DocumentNameHandler getInstance() {
        if (null == instance) {
            instance = new DocumentNameHandler();
        }
        return instance;
    } // newInstance

    /**
     * Este metodo obtiene el nombre de un documento de tipo RESUMEN DIARIO.
     *
     * @param senderRUC Numero de RUC del emisor.
     * @param docIdentifier Identificador del documento.
     * @return Retorna el nombre del documento de tipo RESUMEN DIARIO.
     */
    public String getSummaryDocumentName(String senderRUC, String docIdentifier) {
        return senderRUC + "-" + docIdentifier;
    } // getSummaryDocumentName

    /**
     * Este metodo retorna el nombre del documento concatenando el valor .zip
     *
     * @param documentName El nombre del documento.
     * @return Retorna el nombre del documento concatenando el valor .zip
     */
    public String getZipName(String documentName) {
        return documentName + EE_ZIP;
    } // formatZipName

} // DocumentNameHandler
