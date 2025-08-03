/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.main;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.ventura.cpe.conversion.ConvertirArchivos;
import org.ventura.cpe.dao.controller.LogTransJC;
import org.ventura.cpe.dto.hb.LogTrans;
import org.ventura.cpe.dto.hb.PublicardocWs;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.wsclient.exception.ConectionSunatException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ventura.soluciones.commons.exception.error.IVenturaError;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ParameterMode;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import static org.ventura.cpe.loader.loader.*;

/**
 *
 * @author VSUser
 */
public class Interfaz {

    public static String WS_URL;
    public static String WS_USER;
    public static String WS_PASS;
    public static String WS_TIEMPOESPERA;
    public static String WS_INTERVALOREPETICION;
    public static String WS_TIEMPOPUBLICACION;
    public static String ADJUNTOS;

    public static String USERPROXY;
    public static String PASSPROXY;
    public static String HOSTNAME;
    public static String HOSTPORT;
    public static boolean USEPROXY;

    public static int valorPDF = 1;
    public static int valorXML = 2;
    public static int valorZIP = 4;
    public static int valorDATA = 8;
    public static int estadoBoolean = 0;
    private static String response = "";

    public static int PDF, XML, ZIP, DATA;

    private static LogTrans instance = null;

    public static LogTrans getInstance() {
        if (instance == null) {
            instance = new LogTrans();
        }
        return instance;
    }

    public static boolean validarConnectionInternet() throws ConectionSunatException {
        boolean bValidaConnection = false;
        try {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "Verificando conexion a internet...");
            URL url = new URL("http://www.google.com");
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();
            bValidaConnection = true;
        } catch (MalformedURLException ex) {
            //LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "ERR: (5000) No se pudo establecer conexion a internet");
            bValidaConnection = false;
            throw new ConectionSunatException(IVenturaError.ERROR_5000);
        } catch (IOException ex) {
            //LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "ERR: (5000) No se pudo establecer conexion a internet");
            bValidaConnection = false;
            throw new ConectionSunatException(IVenturaError.ERROR_5000);
        }
        return bValidaConnection;
    }
    
    /**
     *
     * facturacion, con los siguientes parametros:
     * @throws FileNotFoundException 1. rucConsumidor : Es la ruc del cliente
     * del Consumidor 2. nombreConsumidor : Es el nombre del cliente del
     * Consumidor 3. emailConsumidor : Es el email del cliente del Consumidor 4.
     * numeroSerie : Nro y serie del documento de facturacion. 5. fechaEmision :
     * Fecha de emision del documento de facturacion. 6. tipoDoc : El tipo de
     * documento : 01-> Factura, 03-> Boleta, 07-> Nota Credito, 08->Nota
     * Debito, BJ->Baja 7. total : Total del documento de facturacion 8. rutaPDF
     * : La ruta del pdf localmente 9. rutaXML : La ruta del xml localmente 10.
     * rutaZIP : La ruta del zip localmente 11. estadoSunat : El estado acordado
     * es el siguiente: V->Pendiente Respuesta SUNAT, D ->Aprobado pero
     * pendiente de Lectura, R->Rechazo Sunat, L->Leido 12. moneda : Moneda con
     * nomenclatura internacional.
     */
    public static boolean CargarParametros() {
        try {
            String sRutaConfigReal = System.getProperty("user.dir");
            String sRutaConfigGeneral[] = sRutaConfigReal.split("[\\\\/]",-1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length-1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(sRutaConfigReal);
            XPath xPath = XPathFactory.newInstance().newXPath();
            boolean resultado = true;
            resultado = resultado && DatosWS(doc, xPath);
            resultado = resultado && DatosPROXY(doc, xPath);
            resultado = resultado && Directorio(doc, xPath);
            return resultado;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parametros de config.xml. {0}", ex.getMessage());
            System.out.println(" no se conecto");
            return false;
        }
    }

    public static void publicar(PublicardocWs docPublicar) throws FileNotFoundException, IOException, Exception {

        String[] params = new String[17];
        params[0] = WS_USER;
        params[1] = WS_PASS;
        params[2] = docPublicar.getSNDocIdentidadNro();
        params[3] = docPublicar.getSNRazonSocial();
        params[4] = docPublicar.getSNEMail();
        params[5] = docPublicar.getDOCId();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        params[6] = df.format(docPublicar.getDOCFechaEmision());
        params[7] = docPublicar.getDOCCodigo();
        params[8] = docPublicar.getDOCMontoTotal().setScale(2).toString();
        params[9] = docPublicar.getRutaPDF();
        params[10] = docPublicar.getRutaXML();
        params[11] = docPublicar.getRutaZIP();
        params[12] = docPublicar.getEstadoSUNAT() + "";
        params[13] = docPublicar.getDOCMONCodigo();
        params[14] = docPublicar.getEMailEmisor();
        params[15] = docPublicar.getFETipoTrans();
        params[16] = docPublicar.getSNEMailSecundario();

        Date dt = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");

        if (params[9] != null && !params[9].isEmpty()) {
            String bytespdf = ConvertirArchivos.encodeFileToBase64Binary(params[9]);
            params[9] = bytespdf;
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se convirtio exitosamente el formato PDF ", new Object[]{docPublicar.getFEId()});
        }
        if (params[10] != null && !params[10].isEmpty()) {
            String bytesxml = ConvertirArchivos.encodeFileToBase64Binary(params[10]);
            params[10] = bytesxml;
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se convirtio exitosamente el formato XML ", new Object[]{docPublicar.getFEId()});
        }

        if (params[11] != null && !params[11].isEmpty()) {
            String byteszip = ConvertirArchivos.encodeFileToBase64Binary(params[11]);
            params[11] = byteszip;
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Se convirtio exitosamente el formato ZIP ", new Object[]{docPublicar.getFEId()});
        }

        for (int i = 0; i < params.length; i++) {
            if (params[i] == null || params[i].isEmpty()) {
                params[i] = " ";
            }
        }

        Service service = new Service();

        try {

            if (USEPROXY) {

                String url = WS_URL,
                        proxy = HOSTNAME,
                        port = HOSTPORT;
                URL server = new URL(url);
                Properties systemProperties = System.getProperties();
                systemProperties.setProperty("http.proxyHost", proxy);
                systemProperties.setProperty("http.proxyPort", port);
                HttpURLConnection connection = (HttpURLConnection) server.openConnection();
                connection.connect();
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + docPublicar.getFEId() + "] " + connection.getResponseCode() + " : " + connection.getResponseMessage());
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "[" + docPublicar.getFEId() + "] Uso del proxy para WS");
            }

            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(new URL(WS_URL));
            call.setOperationName("GetLocal");
            call.clearOperation();
            call.setOperationName(new QName("http://schemas.xmlsoap.org/wsdl/", "publicarDocumento"));
            QName QNAME_TYPE_STRING = new QName("string");
            call.addParameter("usuarioSesion", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("claveSesion", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("rucConsumidor", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("nombreConsumidor", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("emailConsumidor", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("numeroSerie", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("fechaEmision", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("tipoDoc", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("total", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("rutaPDF", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("rutaXML", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("rutaZIP", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("estadoSunat", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("moneda", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("emailEmisor", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("tipoTransaccion", QNAME_TYPE_STRING, ParameterMode.IN);
            call.addParameter("correoSecundario", QNAME_TYPE_STRING, ParameterMode.IN);
            
            
            call.setReturnType(QNAME_TYPE_STRING);
            System.out.println("Esperando respuesta de WS");
            response = (String) call.invoke(params);
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Respuesta de WS: {1} ", new Object[]{docPublicar.getFEId(), response});
            String request = call.getMessageContext().getRequestMessage().getSOAPPart().getEnvelope().getBody().toString().trim();
            String response1 = call.getMessageContext().getResponseMessage().getSOAPPart().getEnvelope().getBody().toString().trim();
            LogTrans objTran = getInstance();
            objTran.setConector("VS Connector");
            objTran.setFecha(new Date());
            objTran.setHora(Integer.parseInt(hourFormat.format(dt).replace(":", "")));
            objTran.setProceso(Thread.currentThread().getName());
            objTran.setTarea(new Object() {
            }.getClass().getEnclosingMethod().getName());
            objTran.setTramaEnvio(request);
            objTran.setTramaRespuesta(null);
            objTran.setDocId(docPublicar.getDOCId());
            LogTransJC.create(objTran);

            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Respuesta Saliente: {1} ", new Object[]{docPublicar.getFEId(), request});
            LogTrans objTran1 = getInstance();
            objTran1.setConector("VS Connector");
            objTran1.setFecha(new Date());
            objTran1.setHora(Integer.parseInt(hourFormat.format(dt).replace(":", "")));
            objTran1.setProceso(Thread.currentThread().getName());
            objTran1.setTarea(new Object() {
            }.getClass().getEnclosingMethod().getName());
            objTran1.setTramaEnvio(null);
            objTran1.setTramaRespuesta(response1);
            objTran1.setDocId(docPublicar.getDOCId());
            LogTransJC.create(objTran1);
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Respesta Saliente: {1} ", new Object[]{docPublicar.getFEId(), response1});

        } catch (NumberFormatException | IOException e) {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "[{0}] Respuesta de WS: {1} ", new Object[]{docPublicar.getFEId(), response});
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), e.getMessage()});

        }
    }

}
