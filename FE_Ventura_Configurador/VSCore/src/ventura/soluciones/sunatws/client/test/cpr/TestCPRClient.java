package ventura.soluciones.sunatws.client.test.cpr;

import org.apache.log4j.Logger;
import pe.gob.sunat.service.StatusResponse;

import javax.activation.DataHandler;

public class TestCPRClient extends TestCPRWSClient {
	
	private static final Logger logger = Logger.getLogger(TestCPRClient.class);
	

	/**
	 * Este metodo envia una 'Factura', 'Boleta', 'Nota de Credito' o 'Nota de Debito' al
	 * servicio web de Sunat, retornando un CDR de respuesta.
	 */
	@Override
	public byte[] sendBill(String fileName, DataHandler contentFile) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendBill() [TEST] fileName: " + fileName + " contentFile: " + contentFile);}
		
		byte[] cdrResponse = getSecurityPort().sendBill(fileName, contentFile);
		
		if (logger.isDebugEnabled()) {logger.debug("-sendBill() [TEST]");}
		return cdrResponse;
	} //sendBill
	
	/**
	 * Este metodo envia una 'Comunicacion de Baja' o 'Resumen Diario' al servicio web de la
	 * Sunat, retornando un numero de ticket.
	 */
	@Override
	public String sendSummary(String fileName, DataHandler contentFile) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendSummary() [TEST] fileName: " + fileName + " contentFile: " + contentFile);}
		
		String response = getSecurityPort().sendSummary(fileName, contentFile);
		
		if (logger.isDebugEnabled()) {logger.debug("-sendSummary() [TEST]");}
		return response;
	} //sendSummary
	
	/**
	 * Este metodo envia un documento al servicio web de la Sunat, retornando un numero de
	 * ticket.
	 */
	@Override
	public String sendPack(String fileName, DataHandler contentFile) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+sendPack() [TEST] fileName: " + fileName + " contentFile: " + contentFile);}
		
		String response = getSecurityPort().sendPack(fileName, contentFile);
		
		if (logger.isDebugEnabled()) {logger.debug("-sendPack() [TEST]");}
		return response;
	} //sendPack
	
	/**
	 * Este metodo envia el ticket al servicio web de la Sunat, retornando un objeto que contiene
	 * el codigo de respuesta y el CDR de respuesta.
	 */
	@Override
	public StatusResponse getStatus(String ticket) throws Exception
	{
		if (logger.isDebugEnabled()) {logger.debug("+getStatus() [TEST] ticket: " + ticket);}
		
		StatusResponse response = getSecurityPort().getStatus(ticket);
		
		if (logger.isDebugEnabled()) {logger.debug("-getStatus() [TEST]");}
		return response;
	} //getStatus

}
