package ventura.soluciones.sunatws.client.production.cpr;

import javax.activation.DataHandler;

import org.apache.log4j.Logger;

import pe.gob.sunat.service.StatusResponse;


public class ProductionCPRClient extends ProductionCPRWSClient {
	
	private static final Logger logger = Logger.getLogger(ProductionCPRClient.class);
	
	/**
	 * Este metodo envia una 'Factura', 'Boleta', 'Nota de Credito' o 'Nota de Debito' al
	 * servicio web de Sunat, retornando un CDR de respuesta.
	 */
	@Override
	public byte[] sendBill(String fileName, DataHandler contentFile) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendBill() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);}
		
		byte[] cdrResponse = getSecurityPort().sendBill(fileName, contentFile);
		
		if (logger.isDebugEnabled()) {logger.debug("-sendBill() [PRODUCTION]");}
		return cdrResponse;
	} //sendBill
	
	/**
	 * Este metodo envia una 'Comunicacion de Baja' o 'Resumen Diario' al servicio web de la
	 * Sunat, retornando un numero de ticket.
	 */
	@Override
	public String sendSummary(String fileName, DataHandler contentFile) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendSummary() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);}
		
		String response = getSecurityPort().sendSummary(fileName, contentFile);
		
		if (logger.isDebugEnabled()) {logger.debug("-sendSummary() [PRODUCTION]");}
		return response;
	} //sendSummary
	
	/**
	 * Este metodo envia un documento al servicio web de la Sunat, retornando un numero de
	 * ticket.
	 */
	@Override
	public String sendPack(String fileName, DataHandler contentFile) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendPack() [PRODUCTION] fileName: " + fileName + " contentFile: " + contentFile);}
		
		String response = getSecurityPort().sendPack(fileName, contentFile);
		
		if (logger.isDebugEnabled()) {logger.debug("-sendPack() [PRODUCTION]");}
		return response;
	} //sendPack
	
	/**
	 * Este metodo envia el ticket al servicio web de la Sunat, retornando un objeto que contiene
	 * el codigo de respuesta y el CDR de respuesta.
	 */
	@Override
	public StatusResponse getStatus(String ticket) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+getStatus() [PRODUCTION] ticket: " + ticket);}
		
		StatusResponse response = getSecurityPort().getStatus(ticket);
		
		if (logger.isDebugEnabled()) {logger.debug("-getStatus() [PRODUCTION]");}
		return response;
	} //getStatus

}
