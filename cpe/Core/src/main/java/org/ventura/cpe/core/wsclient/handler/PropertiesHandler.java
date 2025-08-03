package org.ventura.cpe.core.wsclient.handler;

import org.apache.commons.lang3.StringUtils;

import java.util.ResourceBundle;

/**
 * Esta clase carga y maneja las propiedades del archivo 'homologation_sw.properties'.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class PropertiesHandler {

    private static final String PROPERTIES_PATH = "sunat_conn_client";

    private static ResourceBundle resource;


    /**
     * Carga las propiedades del archivo 'homologation_sw.properties'.
     */
    static {
        resource = ResourceBundle.getBundle(PROPERTIES_PATH);
    }


    /**
     * Este metodo obtiene el valor de la propiedad en base a
     * la llave de entrada.
     *
     * @param key Llave de entrada. (Nombre de la propiedad)
     * @return Retorna el valor de la propiedad.
     */
    public static String getPropertyByKey(String key) {
        String result = null;

        if (StringUtils.isNotBlank(key)) {
            result = resource.getString(key);
        }
        return result;
    } //getPropertyByKey

} //PropertiesHandler
