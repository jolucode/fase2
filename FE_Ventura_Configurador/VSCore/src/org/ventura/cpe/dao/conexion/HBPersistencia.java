package org.ventura.cpe.dao.conexion;

import org.ventura.cpe.dto.hb.BdsMaestras;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.main.Interfaz;
import org.ventura.utilidades.encriptacion.Criptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.ventura.cpe.loader.loader.*;
import static org.ventura.cpe.loader.loader.Repositorio;

/**
 * @author Jhony Monzalve
 */
public class HBPersistencia {

    private static EntityManagerFactory emf = null;


    public static String CONNSTRING = "";

    public static String USER = "";

    public static String PASSWORD = "";

    public static String DRIVER = "";

    public static String PUERTO = "";

    public static String BASEDATOS = "";

    public static String SERVIDOR = "";

    public static String TIPOSERVIDOR = "";

    public static String PERSISTENCE = "VSCPEDAOPU";

    public static List<BdsMaestras> LISTBDANADIDAS;

    public static EntityManagerFactory getInstancia() {
        if (emf == null) {
            HBPersistencia.CargarParametros();
            boolean isSqlServer = TIPOSERVIDOR.equalsIgnoreCase("8") || TIPOSERVIDOR.equalsIgnoreCase("10") || TIPOSERVIDOR.equalsIgnoreCase("15");
            if (isSqlServer) {
                DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            } else {
                if (TIPOSERVIDOR.equalsIgnoreCase("11")) {
                    DRIVER = "com.mysql.cj.jdbc.Driver";
                }
            }

            //emf = Persistence.createEntityManagerFactory("VSCPEDTOPU");
            Map<String, String> dbProps = new HashMap();
            dbProps.put("javax.persistence.jdbc.url", CONNSTRING);
            dbProps.put("javax.persistence.jdbc.user", USER);
            dbProps.put("javax.persistence.jdbc.password", PASSWORD);
            dbProps.put("javax.persistence.jdbc.driver", DRIVER);
            dbProps.put("javax.persistence.schema-generation.database.action", "create");
            emf = Persistence.createEntityManagerFactory(PERSISTENCE, dbProps);
        }
        return emf;
    }

    static void CargarParametros() {
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
            Repositorio(doc, xPath);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parametros de config.xml. {0}", ex.getMessage());
            System.out.println(" no se conecto");
        }
    }
    public static boolean Repositorio(Document doc, XPath xPath) throws IOException {
        Node nodo;
        Node nodeServidorBd;

        try {
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/Password", doc.getDocumentElement(), XPathConstants.NODE);
            String passBd = nodo.getTextContent();
            HBPersistencia.PASSWORD = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(passBd) : passBd;


            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/TipoServidor", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.TIPOSERVIDOR = nodo.getTextContent();

            nodeServidorBd = (Node) xPath.evaluate("/Configuracion/Repositorio/ServidorBD", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.SERVIDOR = nodo.getTextContent();

            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/BaseDatos", doc.getDocumentElement(), XPathConstants.NODE);
            if (HBPersistencia.TIPOSERVIDOR.equals("11")) {

                HBPersistencia.CONNSTRING = "jdbc:mysql://" + nodeServidorBd.getTextContent() + "/" + nodo.getTextContent() + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            } else if (HBPersistencia.TIPOSERVIDOR.equals("10")) {
                HBPersistencia.CONNSTRING = "jdbc:sqlserver://" + nodeServidorBd.getTextContent() + ":1433;databaseName=" + nodo.getTextContent() + ";";
            }

            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/User", doc.getDocumentElement(), XPathConstants.NODE);
            HBPersistencia.USER = nodo.getTextContent();

        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se encontr? la ruta. {0}", ex.getMessage());
            return false;
        }
        return true;
    }
}
