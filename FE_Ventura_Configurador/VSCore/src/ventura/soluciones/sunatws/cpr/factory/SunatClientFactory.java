package ventura.soluciones.sunatws.cpr.factory;

import org.apache.log4j.Logger;
import ventura.soluciones.commons.exception.ConfigurationException;
import ventura.soluciones.sunatws.cpr.factory.impl.SunatClientFactoryImpl;


/**
 * Esta clase abstracta realiza el "FACTORY" para la obtencion de un
 * cliente de servicio web, llamandolo por su nombre.
 * 
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public abstract class SunatClientFactory
{
	private static final Logger logger = Logger.getLogger(SunatClientFactory.class);
	
	/* Patron SINGLETON */
	private static SunatClientFactory instance = null;
	
	
	/**
	 * Este metodo retorna una instancia SunatClientFactoryImpl.
	 * 
	 * @return
	 * 		Retorna una instancia SunatClientFactoryImpl.
	 */
	public static synchronized SunatClientFactory getInstance()
	{
		if (null == instance)
		{
			if (logger.isDebugEnabled()) {logger.debug("+-getInstance()");}
			instance = new SunatClientFactoryImpl();
		}
		return instance;
	} //getInstance
	
	
	/**
	 * Este metodo implementara la obtencion de un objeto de interfaz ISunatClient
	 * en base al nombre del cliente.
	 * 
	 * @param clientName
	 * 		El nombre del cliente.
	 * @return
	 * 		Retorna un objeto que implementa la interfaz ISunatClient, seleccionandose
	 * 		en base al nombre del cliente.
	 * @throws ConfigurationException
	 */
	public abstract ISunatClient getSunatClient(String clientName,String documentType) throws ConfigurationException;
	
} //SunatClientFactory
