package ventura.soluciones.sunatws.factory;

import pe.gob.sunat.service.StatusResponse;
import ventura.soluciones.sunatws.consumer.Consumer;

import javax.activation.DataHandler;

/**
 * Esta interfaz contiene los metodos a implementar para acceder al 
 * servicio web de la Sunat de tipo 'test', 'homologation'.
 * 
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public interface ISunatClient
{
	
	/**
	 * Este metodo debe implementar el envio de una 'Factura', 'Boleta', 'Nota de Credito' 
	 * o 'Nota de Debito' al servicio web de la Sunat, retornando un CDR de respuesta. 
	 * 
	 * @param fileName
	 * 		El nombre del archivo adjunto.
	 * @param contentFile
	 * 		El archivo ZIP adjunto.
	 * @return
	 * 		Retorna el CDR de respuesta de la Sunat.
	 * @throws Exception
	 */
	public abstract byte[] sendBill(String fileName, DataHandler contentFile) throws Exception;
	
	/**
	 * Este metodo debe implementar el envio de una 'Comunicacion de Baja' o 'Resumen Diario' 
	 * al servicio web de la Sunat, retornando un numero de ticket.
	 * 
	 * @param fileName
	 * 		El nombre del archivo adjunto.
	 * @param contentFile
	 * 		El archivo ZIP adjunto.
	 * @return
	 * 		Retorna el ticket de respuesta.
	 * @throws Exception
	 */
	public abstract String sendSummary(String fileName, DataHandler contentFile) throws Exception;
	
	/**
	 * Este metodo debe implementar el envio de un documento al servicio web de la Sunat, retornando
	 * un numero de ticket.
	 * 
	 * @param fileName
	 * 		El nombre del archivo adjunto.
	 * @param contentFile
	 * 		El archivo ZIP adjunto.
	 * @return
	 * 		Retorna el ticket de respuesta.
	 * @throws Exception
	 */
	public abstract String sendPack(String fileName, DataHandler contentFile) throws Exception;
	
	/**
	 * Este metodo debe implementar el envio de un ticket al servicio web de la Sunat, retornando un 
	 * objeto que contiene el codigo de respuesta y el CDR de respuesta.
	 * 
	 * @param ticket
	 * 		El ticket de consulta.
	 * @return
	 * 		El objeto que contiene el codigo de respuesta y el CDR de respuesta.
	 * @throws Exception
	 */
	public abstract StatusResponse getStatus(String ticket) throws Exception;
	
	
	//*******************************************************************
	//*******************************************************************
	//*******************************************************************
	/**
	 * Este metodo debe implementar la definicion del objeto Consumidor que contiene los 
	 * datos del emisor electronico (RUC, Usuario secundario SOL y Clave secundaria SOL).
	 * 
	 * @param consumer
	 * 		El objeto consumidor.
	 */
	public abstract void setConsumer(Consumer consumer);
	
	/**
	 * Este metodo debe implementar la habilitacion o desabilitacion de la opcion de 
	 * mostrar la trama SOAP (Consulta y respuesta).
	 * 
	 * @param printOption
	 * 		Valor para habilitar (true) o desabilitar (false) la opcion de 
	 * 		mostrar la trama SOAP.
	 */
	public abstract void printSOAP(boolean printOption);
	
} //ISunatClient
