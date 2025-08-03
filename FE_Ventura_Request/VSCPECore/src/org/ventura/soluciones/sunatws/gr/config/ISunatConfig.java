package org.ventura.soluciones.sunatws.gr.config;

/**
 * Esta interfaz contiene todas las constantes a utilizar dentro del
 * proyecto.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public interface ISunatConfig {

    /**
     * Nombre de tipo de clientes WS.
     */
    public static final String TEST_CLIENT = "test";
    public static final String HOMOLOGATION_CLIENT = "homologation";
    public static final String PRODUCTION_CLIENT = "production";


    /**
     * Prefijos con los que Sunat devuelve el codigo de una excepcion
     */
    public static final String SOAP_FAULT_CLIENT_EXP = "soap-env-Client";
    public static final String SOAP_FAULT_SERVER_EXP = "soap-env-Server";


    /**
     * Parametros para el WS Security.
     */
    public static final String WS_SECURITY_HEADER_PARENT_NAME = "Security";
    public static final String WS_SECURITY_HEADER_PARENT_BASE_PFX = "wsse";
    public static final String WS_SECURITY_HEADER_PARENT_VALUE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WS_SECURITY_SUB_UNTOKEN_NAME = "UsernameToken";
    public static final String WS_SECURITY_SUB_UNTOKEN_PFX = "xmlns:wsu";
    public static final String WS_SECURITY_SUB_UNTOKEN_VALUE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    public static final String WS_SECURITY_PARAM_USERNAME = "Username";
    public static final String WS_SECURITY_PARAM_PASSWORD = "Password";
    public static final String WS_SECURITY_COMMON_ATTRIBUTE_TYPE = "Type";
    public static final String WS_SECURITY_COMMON_ATRIBUTE_PWD_VALUE = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText";

} //ISunatConfig
