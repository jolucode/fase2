package org.ventura.soluciones.sunatws.cpr.factory.impl;

import org.apache.log4j.Logger;
import org.ventura.cpe.core.exception.ConfigurationException;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.soluciones.sunatws.cpr.client.production.ProductionCPEClient;
import org.ventura.soluciones.sunatws.cpr.client.test.TestCPEClient;
import org.ventura.soluciones.sunatws.cpr.config.ISunatConfig;
import org.ventura.soluciones.sunatws.cpr.factory.ISunatClient;
import org.ventura.soluciones.sunatws.cpr.factory.SunatClientFactory;


/**
 * Esta clase implementa la clase SunatClientFactory y realiza el "FACTORY" para la
 * obtencion de un cliente de servicio web, llamandolo por su nombre.
 *
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public final class SunatClientFactoryImpl extends SunatClientFactory {

    private static final Logger logger = Logger.getLogger(SunatClientFactoryImpl.class);


    /**
     * Este metodo obtiene un objeto de interfaz ISunatClient en base al nombre del cliente.
     */
    @Override
    public ISunatClient getSunatClient(String clientName, String documentType) throws ConfigurationException {
        if (logger.isDebugEnabled()) {
            logger.debug("+getSunatClient() clientName: " + clientName);
        }
        ISunatClient sunatClient = null;

        if (clientName.equalsIgnoreCase(ISunatConfig.TEST_CLIENT)) {
            sunatClient = new TestCPEClient();
            if (logger.isInfoEnabled()) {
                logger.info("getSunatClient() Create a TEST_CLIENT para PERCEPCIONES Y RETENCIONES.");
            }

        } else if (clientName.equalsIgnoreCase(ISunatConfig.PRODUCTION_CLIENT)) {
            sunatClient = new ProductionCPEClient();
            if (logger.isInfoEnabled()) {
                logger.info("getSunatClient() Create a PRODUCTION_CLIENT para PERCEPCIONES Y RETENCIONES.");
            }
        } else {
            logger.error("getSunatClient() " + IVenturaError.ERROR_151.getMessage());
            throw new ConfigurationException(IVenturaError.ERROR_151.getMessage());
        }
        if (logger.isDebugEnabled()) {
            logger.debug("-getSunatClient()");
        }
        return sunatClient;
    } //getSunatClient

} //SunatClientFactoryImpl
