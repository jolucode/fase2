package ventura.soluciones.sunatwsconsult.factory.impl;

import org.apache.log4j.Logger;
import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.sunatwsconsult.client.production.ProductionClient;
import ventura.soluciones.sunatwsconsult.config.IWSConsultConfig;
import ventura.soluciones.sunatwsconsult.factory.ISunatClient;
import ventura.soluciones.sunatwsconsult.factory.SunatClientFactory;

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
	public ISunatClient getSunatClient(String clientName) throws ConfigurationException
	{
		if (logger.isDebugEnabled()) {logger.debug("+getSunatClient() clientName: " + clientName);}
		ISunatClient sunatClient = null;
		
		if (clientName.equalsIgnoreCase(IWSConsultConfig.PRODUCTION_CLIENT))
		{
			if (logger.isInfoEnabled()) {logger.info("getSunatClient() Create a PRODUCTION_CLIENT.");}
			sunatClient = new ProductionClient();
		}
		else if (clientName.equalsIgnoreCase(IWSConsultConfig.TEST_CLIENT))
		{	
			if (logger.isInfoEnabled()) {logger.info("getSunatClient() Create a TEST_CLIENT.");}
			//sunatClient = new TestClient();
		}
		if (logger.isDebugEnabled()) {logger.debug("-getSunatClient()");}
		return sunatClient;
	} //getSunatClient
	
} //SunatClientFactoryImpl
