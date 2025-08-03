package ventura.soluciones.sunatws.client.production.cpr;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import ventura.soluciones.sunatws.client.production.cpr.service.CPRService;
import ventura.soluciones.sunatws.client.production.cpr.service.CPRService_Service;
import ventura.soluciones.sunatws.consumer.Consumer;
import ventura.soluciones.sunatws.factory.ISunatClient;
import ventura.soluciones.sunatws.security.HeaderHandlerResolver;


/**
 * Esta clase implementa algunos metodos de la interfaz ISunatClient. Ademas,
 * realiza la seguridad (WS Security) establecida por Sunat.
 * 
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public abstract class ProductionCPRWSClient implements ISunatClient {
	
	private static final Logger logger = Logger.getLogger(ProductionCPRWSClient.class);
	
	protected Consumer consumer;
	protected boolean printOption;
	
	/**
	 * Este metodo define el objeto Consumidor que contiene los datos del emisor 
	 * electronico (RUC, Usuario secundario SOL y Clave secundaria SOL).
	 */
	@Override
	public void setConsumer(Consumer consumer)
	{
		if (logger.isDebugEnabled()) {logger.debug("+-setConsumer()");}
		this.consumer = consumer;
	} //setConsumer
	
	/**
	 * Este metodo habilita o desabilita la opcion de mostrar la trama 
	 * SOAP (Consulta y respuesta).
	 */
	@Override
	public void printSOAP(boolean printOption)
	{
		this.printOption = printOption;
	} //printSOAP
	
	/**
	 * Este metodo implementa la seguridad establecida por Sunat, para
	 * el envio de documentos electronicos.
	 *  
	 * @return
	 * 		Retorna el objeto BilService de la opcion de 'Production Client'.
	 * @throws JAXBException
	 */
	
	protected CPRService getSecurityPort() throws JAXBException
	{
		CPRService_Service service = new CPRService_Service();
		HeaderHandlerResolver handlerResolver = new HeaderHandlerResolver(consumer);
		
		service.setHandlerResolver(handlerResolver);
		return service.getBillServicePort();
	}
}
