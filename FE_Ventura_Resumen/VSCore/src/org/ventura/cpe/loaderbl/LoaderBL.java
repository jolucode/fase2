/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.loaderbl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.ventura.cpe.bl.BdsMaestrasBL;
import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.TransaccionResumenProp;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import static org.ventura.cpe.loaderbl.LoaderBL.Directorio;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.utilidades.entidades.PuertoVSCPE;
import org.ventura.utilidades.entidades.VariablesGlobales;
import org.ventura.utilidades.sunat.RespuestaSunat;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author VSUser
 */
public class LoaderBL {

    public static void CargarSeteoParametro() throws VenturaExcepcion {

        Runtime recolector = Runtime.getRuntime();

        try {
            String sRutaConfigReal = System.getProperty("user.dir");
            String[] sRutaConfigGeneral = sRutaConfigReal.split("[\\\\/]", -1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(sRutaConfigReal);
            XPath xPath = XPathFactory.newInstance().newXPath();
            boolean resultado = true;
            resultado = resultado && Repositorio(doc, xPath);
            resultado = resultado && Proxy(doc, xPath);
            resultado = resultado && Directorio(doc, xPath);
            resultado = resultado && Tiempos(doc, xPath);
            resultado = resultado && Resumen(doc, xPath);
            resultado = resultado && Sunat(doc, xPath);
            resultado = resultado && PuertoVS(doc, xPath);
            resultado = resultado && Impresion(doc, xPath);

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros de config.xml. {0}", ex.getMessage());

        }
        recolector.gc();

    }

    private static boolean Repositorio(Document doc, XPath xPath) {
        Node nodo;
        try {
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/TipoServidor", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.TIPOSERVIDOR = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/ServidorBD", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.SERVIDOR = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/Puerto", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.PUERTO = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/BaseDatos", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.BASEDATOS = nodo.getTextContent();

            if (HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("10")) {
                HBPersistencia.CONNSTRING = "jdbc:sqlserver://" + HBPersistencia.SERVIDOR + ":" + HBPersistencia.PUERTO + ";databaseName=" + HBPersistencia.BASEDATOS + "";
            } else {
                if (HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("11")) {
                    HBPersistencia.CONNSTRING = "jdbc:mysql://" + HBPersistencia.SERVIDOR + ":" + HBPersistencia.PUERTO + "/" + HBPersistencia.BASEDATOS;
                }
            }
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/User", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.USER = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/Password", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.PASSWORD = nodo.getTextContent();
            HBPersistencia.PASSWORD = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(HBPersistencia.PASSWORD) : HBPersistencia.PASSWORD;
            //CARGA DE BD'S AÑADIDAS
            HBPersistencia.LISTBDANADIDAS = BdsMaestrasBL.listarBDAnadidas();

        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Repositorio : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    private static boolean Proxy(Document doc, XPath xPath) {
        Node nodo;

        try {
            boolean usar, auten;
            String proxyHost, proxyPort;
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/UsarProxy", doc.getDocumentElement(), XPathConstants.NODE);
            usar = Boolean.parseBoolean(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Servidor", doc.getDocumentElement(), XPathConstants.NODE);
            proxyHost = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Puerto", doc.getDocumentElement(), XPathConstants.NODE);
            proxyPort = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/RequAuth", doc.getDocumentElement(), XPathConstants.NODE);
            auten = Boolean.parseBoolean(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/User", doc.getDocumentElement(), XPathConstants.NODE);
            final String authUser = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Pass", doc.getDocumentElement(), XPathConstants.NODE);
            String authPassword = nodo.getTextContent();
            final String authPassword1 = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(authPassword) : authPassword;
            if (usar) {
                Properties systemProperties = System.getProperties();
                systemProperties.setProperty("http.proxyHost", proxyHost);
                systemProperties.setProperty("http.proxyPort", proxyPort);

            }

        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Proxy : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Directorio(Document doc, XPath xPath) {
        Node nodo;
        try {
            //Directorio
            nodo = (Node) xPath.evaluate("/Configuracion/Directorio/Adjuntos", doc.getDocumentElement(), XPathConstants.NODE);
            Directorio.ADJUNTOS = nodo.getTextContent();
            Directorio.ADJUNTOS = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(Directorio.ADJUNTOS) : Directorio.ADJUNTOS;
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Directorio : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Impresion(Document doc, XPath xPath) {
        Node nodo;
        try {
            //Directorio
            nodo = (Node) xPath.evaluate("/Configuracion/Impresion/PDF", doc.getDocumentElement(), XPathConstants.NODE);
            VariablesGlobales.Impresion = nodo.getTextContent();
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Impresion : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Tiempos(Document doc, XPath xPath) {
        Node nodo;
        try {
            //Tiempos
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RQTimeOut", doc.getDocumentElement(), XPathConstants.NODE);
            TransaccionRespuesta.RQTIMEOUT = Integer.parseInt(nodo.getTextContent()) * 1000;
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RQInterval", doc.getDocumentElement(), XPathConstants.NODE);
            TransaccionRespuesta.RQINTERVAL = Integer.parseInt(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RSTimeOut", doc.getDocumentElement(), XPathConstants.NODE);
            TransaccionRespuesta.RSTIMEOUT = Integer.parseInt(nodo.getTextContent()) * 1000;
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RSInterval", doc.getDocumentElement(), XPathConstants.NODE);
            TransaccionRespuesta.RSINTERVAL = Integer.parseInt(nodo.getTextContent());
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Tiempos : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Resumen(Document doc, XPath xPath) {

        Node nodo;
        try {
            //Tiempos
            nodo = (Node) xPath.evaluate("/Configuracion/ResumenDiario/Fecha", doc.getDocumentElement(), XPathConstants.NODE);
            TransaccionResumenProp.fechaResumen = nodo.getTextContent();

            nodo = (Node) xPath.evaluate("/Configuracion/ResumenDiario/Hora", doc.getDocumentElement(), XPathConstants.NODE);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = simpleDateFormat.parse(nodo.getTextContent());
            TransaccionResumenProp.Hora = date;
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Resumen : . {0}", ex.getMessage());
            return false;
        } catch (ParseException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Resumen : . {0}", ex.getMessage());
        }
        return true;

    }

    public static boolean Sunat(Document doc, XPath xPath) {

        Node nodo;
        try {

            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/Ambiente", doc.getDocumentElement(), XPathConstants.NODE);
            RespuestaSunat.AmbienteSunat = Integer.parseInt(nodo.getTextContent());

            nodo = (Node) xPath.evaluate("/Configuracion/UsoSunat/WS", doc.getDocumentElement(), XPathConstants.NODE);
            VariablesGlobales.UsoSunat = Boolean.parseBoolean(nodo.getTextContent());

            nodo = (Node) xPath.evaluate("/Configuracion/UsoSunat/Validacion", doc.getDocumentElement(), XPathConstants.NODE);
            VariablesGlobales.UsoValidaciones = Boolean.parseBoolean(nodo.getTextContent());

            nodo = (Node) xPath.evaluate("/Configuracion/UsoSunat/PDF", doc.getDocumentElement(), XPathConstants.NODE);
            VariablesGlobales.UsoPdfSinRespuesta = Boolean.parseBoolean(nodo.getTextContent());
            
            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/RutaSunatTest", doc.getDocumentElement(), XPathConstants.NODE);
            VariablesGlobales.rutaSunatTestWebservice = nodo.getTextContent();

            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/RutaSunatProd", doc.getDocumentElement(), XPathConstants.NODE);
            VariablesGlobales.rutaSunatWebservice = nodo.getTextContent();

            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/RutaOseTest", doc.getDocumentElement(), XPathConstants.NODE);
            VariablesGlobales.rutaOseTestWebservice = nodo.getTextContent();

            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/RutaOseProd", doc.getDocumentElement(), XPathConstants.NODE);
            VariablesGlobales.rutaOseWebservice = nodo.getTextContent();
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Sunat : . {0}", ex.getMessage());
            return false;
        }
        return true;

    }

    public static boolean PuertoVS(Document doc, XPath xPath) {

        Node nodo;
        try {
            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Configurador", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                PuertoVSCPE.PuertoAbierto_Configurador = 7000;
            } else {
                if (!"".equals(nodo.getTextContent()) || nodo.getTextContent() != null) {
                    PuertoVSCPE.PuertoAbierto_Configurador = Integer.parseInt(nodo.getTextContent());
                } else {
                    PuertoVSCPE.PuertoAbierto_Configurador = 7000;
                }
            }
            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Request", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                PuertoVSCPE.PuertoAbierto_Request = 7001;
            } else {
                if (!"".equals(nodo.getTextContent()) || nodo.getTextContent() != null) {
                    PuertoVSCPE.PuertoAbierto_Request = Integer.parseInt(nodo.getTextContent());
                } else {
                    PuertoVSCPE.PuertoAbierto_Request = 7001;
                }
            }
            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Response", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                PuertoVSCPE.PuertoAbierto_Response = 7002;
            } else {
                if (!"".equals(nodo.getTextContent()) || nodo.getTextContent() != null) {
                    PuertoVSCPE.PuertoAbierto_Response = Integer.parseInt(nodo.getTextContent());
                } else {
                    PuertoVSCPE.PuertoAbierto_Response = 7002;
                }
            }
            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Resumen", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                PuertoVSCPE.PuertoAbierto_Resumen = 7003;
            } else {
                if (!"".equals(nodo.getTextContent()) || nodo.getTextContent() != null) {
                    PuertoVSCPE.PuertoAbierto_Resumen = Integer.parseInt(nodo.getTextContent());
                } else {
                    PuertoVSCPE.PuertoAbierto_Resumen = 7003;
                }
            }
            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_PublicWS", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                PuertoVSCPE.PuertoAbierto_PublicWS = 7004;
            } else {
                if (!"".equals(nodo.getTextContent()) || nodo.getTextContent() != null) {
                    PuertoVSCPE.PuertoAbierto_PublicWS = Integer.parseInt(nodo.getTextContent());
                } else {
                    PuertoVSCPE.PuertoAbierto_PublicWS = 7004;
                }
            }
            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Extractor", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                PuertoVSCPE.PuertoAbierto_Extractor = 7005;
            } else {
                if (!"".equals(nodo.getTextContent()) || nodo.getTextContent() != null) {
                    PuertoVSCPE.PuertoAbierto_Extractor = Integer.parseInt(nodo.getTextContent());
                } else {
                    PuertoVSCPE.PuertoAbierto_Extractor = 7005;
                }
            }

        } catch (XPathExpressionException | DOMException | NumberFormatException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Puertos : . {0}", ex.getMessage());
            return false;
        }
        return true;

    }

}
