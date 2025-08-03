package org.ventura.soluciones.sunatwsconsult.config;

public interface IWSConsultConfig {

    /**
     * Nombre de tipo de clientes WS.
     */
    public static final String TEST_CLIENT = "test";
    public static final String PRODUCTION_CLIENT = "production";


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

}
// URL Production-WS
//IWSConsultConfig
