package org.ventura.soluciones.sunatws.factory.impl;

import org.apache.log4j.Logger;
import org.ventura.cpe.core.exception.ConfigurationException;
import org.ventura.cpe.core.exception.IVenturaError;
import org.ventura.soluciones.sunatws.client.homologation.HomologationClient;
import org.ventura.soluciones.sunatws.client.production.ProductionClient;
import org.ventura.soluciones.sunatws.client.test.TestClient;
import org.ventura.soluciones.sunatws.config.ISunatConfig;
import org.ventura.soluciones.sunatws.factory.ISunatClient;
import org.ventura.soluciones.sunatws.factory.SunatClientFactory;

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
            sunatClient = new TestClient();

        } else if (clientName.equalsIgnoreCase(ISunatConfig.HOMOLOGATION_CLIENT)) {
            if (logger.isInfoEnabled()) {
                logger.info("getSunatClient() Create a HOMOLOGATION_CLIENT.");
            }
            sunatClient = new HomologationClient();
        } else if (clientName.equalsIgnoreCase(ISunatConfig.PRODUCTION_CLIENT)) {
            if (logger.isInfoEnabled()) {
                logger.info("getSunatClient() Create a PRODUCTION_CLIENT.");
            }

            sunatClient = new ProductionClient();
            if (logger.isInfoEnabled()) {
                logger.info("getSunatClient() Se creo correctamente el ProductionClient().");
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
