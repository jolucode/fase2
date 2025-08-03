package ventura.soluciones.sunatwsfe.factory.impl;

import org.apache.log4j.Logger;

import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.commons.exception.error.IVenturaError;
import ventura.soluciones.sunatwsfe.client.production.ProductionClient;
import ventura.soluciones.sunatwsfe.client.test.TestClient;
import ventura.soluciones.sunatwsfe.config.ISunatConfig;
import ventura.soluciones.sunatwsfe.factory.ISunatClient;
import ventura.soluciones.sunatwsfe.factory.SunatClientFactory;


/**
 * Esta clase implementa la clase SunatClientFactory y realiza el "FACTORY" para la
 * obtencion de un cliente de servicio web, llamandolo por su nombre.
 * 
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public final class SunatClientFactoryImpl extends SunatClientFactory
{
	private static final Logger logger = Logger.getLogger(SunatClientFactoryImpl.class); 
	
	
	/**
	 * Este metodo obtiene un objeto de interfaz ISunatClient en base al nombre del cliente.
	 */
	@Override
	public ISunatClient getSunatClient(String clientName,String documentType) throws ConfigurationException
	{
		if (logger.isDebugEnabled()) {logger.debug("+getSunatClient() clientName: " + clientName);}
		ISunatClient sunatClient = null;
		
		if (clientName.equalsIgnoreCase(ISunatConfig.TEST_CLIENT))
		{
				sunatClient = new TestClient();
			
		}
		
		else if (clientName.equalsIgnoreCase(ISunatConfig.PRODUCTION_CLIENT))
		{
			if (logger.isInfoEnabled()) {logger.info("getSunatClient() Create a PRODUCTION_CLIENT.");}
			
				sunatClient = new ProductionClient();
				if (logger.isInfoEnabled()) {logger.info("getSunatClient() Se creo correctamente el ProductionClient().");}	
			
		}
		else
		{
			logger.error("getSunatClient() "+ IVenturaError.ERROR_151.getMessage());
			throw new ConfigurationException(IVenturaError.ERROR_151.getMessage());
		}
		if (logger.isDebugEnabled()) {logger.debug("-getSunatClient()");}
		return sunatClient;
	} //getSunatClient
	
} //SunatClientFactoryImpl
