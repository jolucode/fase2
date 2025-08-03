/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.main.config;

import org.ventura.cpe.archivos.FileBL;
import org.ventura.cpe.bl.BdsMaestrasBL;
import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.TransaccionResumenProp;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.pfeconn.TransaccionPFE;
import org.ventura.cpe.sb1.DocumentoBL;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.utilidades.entidades.GestorTiempo;
import org.ventura.utilidades.entidades.PuertoVSCPE;
import org.ventura.utilidades.entidades.TipoServidor;
import org.ventura.utilidades.entidades.VariablesGlobales;
import org.ventura.utilidades.sunat.RespuestaSunat;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

/**
 * @author Percy
 */
public class Loader {

    public static int TipConector;

    public static String tipoConectorPFE;

    public static String version = "Version 24.04.0007";

    public static boolean CargarParametros() {
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
            resultado = resultado && ERP(doc, xPath);
            resultado = resultado && Conector(doc, xPath);
            resultado = resultado && Proxy(doc, xPath);
            //resultado = resultado && Propiedades(doc, xPath);
            resultado = resultado && GestorTiempo(doc, xPath);
            resultado = resultado && Ambiente__Sunat(doc, xPath);
            resultado = resultado && Directorio(doc, xPath);
            resultado = resultado && Tiempos(doc, xPath);
            resultado = resultado && Resumen(doc, xPath);
            resultado = resultado && Sunat(doc, xPath);
            resultado = resultado && PuertoVS(doc, xPath);
            resultado = resultado && Impresion(doc, xPath);
            return resultado;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros de config.xml. {0}", ex.getMessage());
            return false;
        }
    }

    public static boolean GestorTiempo(Document doc, XPath xPath) {
        Node nodo;
        try {
            nodo = (Node) xPath.evaluate("/Configuracion/GestorTiempo/Tiempo", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Gestor de Transacciones ");
                return false;
            }
            GestorTiempo.periodo = nodo.getTextContent();
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Gestor de Transacciones.  {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Impresion(Document doc, XPath xPath) {
        Node nodo;
        try {
            //Directorio
            nodo = (Node) xPath.evaluate("/Configuracion/Impresion/PDF", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Impresion ");
                return false;
            }
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Impresion ");
                return false;
            }
            VariablesGlobales.Impresion = nodo.getTextContent();

        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Impresion : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Ambiente__Sunat(Document doc, XPath xPath) {

        Node nodo;
        try {
            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/ClienteSunat", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Ambiente Sunat (test_production) ");
                return false;
            }
            VariablesGlobales.Ambiente__Sunat = nodo.getTextContent();
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Ambiente Sunat (test_production)  {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    private static boolean Repositorio(Document doc, XPath xPath) {
        Node nodo;
        try {
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/TipoServidor", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Tipo de Servidor - Repositorio");
                return false;
            }
            HBPersistencia.TIPOSERVIDOR = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/ServidorBD", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Servidor BD - Repositorio");
                return false;
            }
            HBPersistencia.SERVIDOR = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/Puerto", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Puerto - Repositorio");
                return false;
            }
            HBPersistencia.PUERTO = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/BaseDatos", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de la BD - Repositorio");
                return false;
            }
            HBPersistencia.BASEDATOS = nodo.getTextContent();
            boolean isSqlServer = HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("8") || HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("10");
            if (isSqlServer) {
                HBPersistencia.CONNSTRING = "jdbc:sqlserver://" + HBPersistencia.SERVIDOR + ":" + HBPersistencia.PUERTO + ";databaseName=" + HBPersistencia.BASEDATOS + "";
            } else {
                if (HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("11")) {
                    HBPersistencia.CONNSTRING = "jdbc:mysql://" + HBPersistencia.SERVIDOR + ":" + HBPersistencia.PUERTO + "/" + HBPersistencia.BASEDATOS+ "?serverTimezone=America/Lima";
                }
            }
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/User", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Usuario - Repositorio");
                return false;
            }

            HBPersistencia.USER = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/Password", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Password - Repositorio");
                return false;
            }
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

    public static boolean Conector(Document doc, XPath xPath) {
        Node nodo;
        try {
            nodo = (Node) xPath.evaluate("/Configuracion/PFEConnector/Tipo", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del conector");
                return false;
            }
            tipoConectorPFE = nodo.getTextContent();
            switch (tipoConectorPFE) {
                case "1":
                    //TransaccionPFE.Conector = new org.ventura.cpe.efact.wsclient.TransaccionWS();
                    LoggerTrans.getCDMainLogger().log(Level.INFO, "Se conecto al servicio de Efact.");
                    break;
                case "2":
                    TransaccionPFE.Conector = new org.ventura.wsclient.TransaccionWS();

                    LoggerTrans.getCDMainLogger().log(Level.INFO, "Se conecto al servicio de Ventura Soluciones");
                    break;
                default:
                    throw new VenturaExcepcion("El valor " + tipoConectorPFE + " no es valido para el tipo conector");
            }
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Conector : . {0}", ex.getMessage());
            return false;
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Conector : . {0}", ex.getMessage());
        }
        return true;
    }

    public static boolean ERP(Document doc, XPath xPath) {
        Node nodo;
        try {

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/TipoServidor", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Tipo Servidor - ERP");
                return false;
            }

            TipoServidor.TipoServidorQuery = nodo.getTextContent();
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/TipoConector", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Tipo Conector - ERP");
                return false;
            }

            TipConector = Integer.parseInt(nodo.getTextContent());
            switch (TipConector) {
                case 1:
                    DocumentoINF.Conector = new DocumentoBL();
                    DocumentoINF.GetEnviarConsulta = new DocumentoBL();
                    DocumentoINF.GetEnviarTransaccion = new DocumentoBL();
                    break;
                case 2:
                    DocumentoINF.Conector = new org.ventura.cpe.generico.DocumentoGENBL();
                    break;
                case 3:
                    DocumentoINF.Conector = new FileBL();
                    DocumentoINF.GetEnviarConsulta = new FileBL();
                    DocumentoINF.GetEnviarTransaccion = new FileBL();
                    break;
                default:
                    throw new VenturaExcepcion("El valor " + TipConector + " no es valido para el tipo conector");
            }
            DocumentoINF.Conector.CargarParametro(doc, xPath);
            DocumentoINF.GetEnviarConsulta.CargarParametro(doc, xPath);
            DocumentoINF.GetEnviarTransaccion.CargarParametro(doc, xPath);

        } catch (XPathExpressionException | DOMException | NumberFormatException | VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de ERP : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Directorio(Document doc, XPath xPath) {
        Node nodo;
        try {
            //Directorio
            nodo = (Node) xPath.evaluate("/Configuracion/Directorio/Adjuntos", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Directorio");
                return false;
            }

            Directorio.ADJUNTOS = nodo.getTextContent();
            Directorio.ADJUNTOS = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(Directorio.ADJUNTOS) : Directorio.ADJUNTOS;
        } catch (XPathExpressionException | DOMException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Directorio : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Tiempos(Document doc, XPath xPath) {
        Node nodo;
        try {
            //Tiempos
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RQTimeOut", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de RQTimeOut");
                return false;
            }

            TransaccionRespuesta.RQTIMEOUT = Integer.parseInt(nodo.getTextContent()) * 1000;
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RQInterval", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de RQInterval");
                return false;
            }
            TransaccionRespuesta.RQINTERVAL = Integer.parseInt(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RSTimeOut", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de RSTimeOut");
                return false;
            }
            TransaccionRespuesta.RSTIMEOUT = Integer.parseInt(nodo.getTextContent()) * 1000;
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RSInterval", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de RSInterval");
                return false;
            }
            TransaccionRespuesta.RSINTERVAL = Integer.parseInt(nodo.getTextContent());
        } catch (XPathExpressionException | DOMException | NumberFormatException ex) {
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
            if (nodo.getTextContent().equalsIgnoreCase("")) {
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Hora - Resumen Diario");
                return false;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = simpleDateFormat.parse(nodo.getTextContent());
            TransaccionResumenProp.Hora = date;
        } catch (XPathExpressionException | DOMException | ParseException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Resumen : . {0}", ex.getMessage());
            return false;
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

            //nodo = (Node) xPath.evaluate("/Configuracion/Sunat/RutaConoseTest", doc.getDocumentElement(), XPathConstants.NODE);
            //VariablesGlobales.rutaConoseTestWebservice = nodo.getTextContent();

            //nodo = (Node) xPath.evaluate("/Configuracion/Sunat/RutaConoseProd", doc.getDocumentElement(), XPathConstants.NODE);
            //VariablesGlobales.rutaConoseWebservice = nodo.getTextContent();

        } catch (XPathExpressionException | DOMException | NumberFormatException ex) {
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
