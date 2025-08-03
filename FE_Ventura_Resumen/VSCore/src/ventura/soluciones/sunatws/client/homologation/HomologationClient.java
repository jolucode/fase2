package ventura.soluciones.sunatws.client.homologation;

import javax.activation.DataHandler;

import org.apache.log4j.Logger;

import pe.gob.sunat.service.StatusResponse;

/**
 * Esta clase implementa los metodos faltantes de la interfaz ISunatClient, relacionados
 * a los metodos extraidos del servicio web de Homologacion.
 * 
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public class HomologationClient extends HomologationWSClient
{
	private static final Logger logger = Logger.getLogger(HomologationClient.class);
	
	
	/**
	 * Este metodo envia una 'Factura', 'Boleta', 'Nota de Credito' o 'Nota de Debito' al
	 * servicio web de Sunat, retornando un CDR de respuesta.
	 */
	@Override
	public byte[] sendBill(String fileName, DataHandler contentFile) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendBill() [HOMOLOGATION] fileName: " + fileName + " contentFile: " + contentFile);}
		
		byte[] cdrResponse = getSecurityPort().sendBill(fileName, contentFile);
		
		if (logger.isDebugEnabled()) {logger.debug("-sendBill() [HOMOLOGATION]");}
		return cdrResponse;
	} //sendBill
	
	/**
	 * Este metodo envia una 'Comunicacion de Baja' o 'Resumen Diario' al servicio web de la
	 * Sunat, retornando un numero de ticket.
	 */
	@Override
	public String sendSummary(String fileName, DataHandler contentFile)	throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendSummary() [HOMOLOGATION] fileName: " + fileName + " contentFile: " + contentFile);}
		
		String response = getSecurityPort().sendSummary(fileName, contentFile);
		
		if (logger.isDebugEnabled()) {logger.debug("-sendSummary() [HOMOLOGATION]");}
		return response;
	} //sendSummary
	
	/**
	 * Este metodo no se implementa para esta clase, debido a que el servicio web de Homologacion
	 * no cuenta con este meodo.
	 */
	@Override
	public String sendPack(String fileName, DataHandler contentFile) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendPack() [HOMOLOGATION] Este metodo no se implementa para esta clase.");}
		return null;
	} //sendPack
	
	/**
	 * Este metodo envia el ticket al servicio web de la Sunat, retornando un objeto que contiene
	 * el codigo de respuesta y el CDR de respuesta.
	 */
	@Override
	public StatusResponse getStatus(String ticket) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+getStatus() [HOMOLOGATION] ticket: " + ticket);}
		
		StatusResponse response = getSecurityPort().getStatus(ticket);
		
		if (logger.isDebugEnabled()) {logger.debug("-getStatus() [HOMOLOGATION]");}
		return response;
	} //getStatus
	
} //HomologationClient
