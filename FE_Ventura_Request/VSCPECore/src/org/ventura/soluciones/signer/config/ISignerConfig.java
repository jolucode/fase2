package org.ventura.soluciones.signer.config;

/**
 * Esta interfaz contiene las constantes para el proceso
 * de firmado de los documentos UBL.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public interface ISignerConfig {

    /**
     * Prefijo del firmante
     */
    public static final String SIGNER_PREFIX = "signer";


    /**
     * Prefijos de tag's UBL.
     */
    public static final String UBL_TAG_UBLEXTENSION = "ext:UBLExtension";
    public static final String UBL_TAG_EXTENSIONCONTENT = "ext:ExtensionContent";


    /**
     * Propiedades del XML TRANSFORM
     */
    public static final String XML_TRANSFORM_KEYS_ENCODING = "UTF-8";
    public static final String XML_TRANSFORM_KEYS_METHOD = "xml";
    public static final String XML_TRANSFORM_KEYS_INDENT = "no";


    /**
     * Otros parametros de configuracion.
     */
    public static final String SIGN_CONTEXT_NAMESPACE_PREFIX = "ds";
    public static final String SIGNATURE_FACTORY_MECHANISM = "DOM";

} //ISignerConfig
