package ventura.soluciones.sunatwsconsult.factory;

import pe.gob.sunat.service.consult.StatusResponse;
import ventura.soluciones.sunatwsconsult.consumer.Consumer;

/**
 * Esta interfaz contiene los metodos a implementar para acceder al 
 * servicio web de la Sunat de tipo 'test', 'homologation'.
 * 
 * @author Jose Manuel Lucas Barrera (josemlucasb@gmail.com)
 */
public interface ISunatClient
{
	
	public abstract StatusResponse getStatus(String rucComprobante, String tipoComprobante, String serieComprobante, Integer numeroComprobante) throws Exception;
	
	public abstract StatusResponse getStatusCdr(String rucComprobante, String tipoComprobante, String serieComprobante, Integer numeroComprobante) throws Exception;
	
	
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
