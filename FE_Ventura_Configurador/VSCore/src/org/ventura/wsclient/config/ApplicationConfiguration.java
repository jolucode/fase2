package org.ventura.wsclient.config;

import org.apache.log4j.Logger;
import org.ventura.wsclient.config.configuration.Configuracion;
import org.ventura.wsclient.handler.PropertiesHandler;
import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.commons.exception.error.IVenturaError;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Esta clase carga el archivo de configuracion 'config.xml'.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class ApplicationConfiguration {

    private final Logger logger = Logger.getLogger(ApplicationConfiguration.class);

    /* Patron SINGLETON */
    private static ApplicationConfiguration instance = null;

    private final String CONFIG_PATH = PropertiesHandler.getPropertyByKey("application.config.path");
    private String CONFIG_PATH2 = "";
    private Configuracion configuration = null;

    /**
     * Constructor privado para evitar instancias. Carga el archivo de
     * configuracion 'config.xml'.
     *
     * @throws ConfigurationException
     */
    private ApplicationConfiguration() throws ConfigurationException {
        if (logger.isDebugEnabled()) {
            logger.debug("+ApplicationConfiguration() constructor");
        }
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuracion.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            String sRutaConfigReal = System.getProperty("user.dir");
            String[] sRutaConfigGeneral = sRutaConfigReal.split("[\\\\/]",-1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length-1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";
            CONFIG_PATH2 = sRutaConfigReal;
            this.configuration = (Configuracion) unmarshaller.unmarshal(new java.io.File(CONFIG_PATH2));
            if (logger.isInfoEnabled()) {
                logger.info("ApplicationConfiguration() Ruta_configuracion: " + CONFIG_PATH2 + " Config_objeto: " + configuration);
            }
        } catch (Exception e) {
            logger.error("ApplicationConfiguration() ERROR: " + IVenturaError.ERROR_453.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_453.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-ApplicationConfiguration() constructor");
        }
    } //ApplicationConfiguration

    /**
     * Este metodo obtiene una instancia de la clase. Ademas, se utiliza el
     * patron SINGLETON (Solo existira una instancia de esta clase).
     *
     * @return Retorna la instancia de la clase ApplicationConfiguration.
     * @throws ConfigurationException
     */
    public static synchronized ApplicationConfiguration getInstance() throws ConfigurationException {
        if (null == instance) {
            instance = new ApplicationConfiguration();
        }
        return instance;
    } //getInstance

    /**
     * Este metodo retorna el objeto JAXB que representas el archivo de
     * configuracion de la aplicacion.
     *
     * @return Retorna el archivo de configuracion de la aplicacion.
     */
    public Configuracion getConfiguration() {
        return this.configuration;
    } //getConfiguration

} //Configuracion
