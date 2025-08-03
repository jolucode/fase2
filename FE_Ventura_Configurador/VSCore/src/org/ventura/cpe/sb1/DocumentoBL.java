package org.ventura.cpe.sb1;

import com.sap.smb.sbo.api.ICompany;
import com.sap.smb.sbo.api.SBOCOMConstants;
import com.sap.smb.sbo.api.SBOCOMUtil;
import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.utilidades.encriptacion.Criptor;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

/**
 *
 * @author Jhony Monzalve
 */
public class DocumentoBL extends DocumentoINF {

    public static ICompany Sociedad;
    public static Set<ICompany> listSociedades = new HashSet<>();

    public DocumentoBL() {
        //Constructor de la clase DocumentoBL
    }

    @Override
    public boolean CargarParametro(Document doc, XPath xPath) {
        Node nodo;

        try {
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ServidorLicencias", doc.getDocumentElement(), XPathConstants.NODE);
            this.setSERVIDOR_LICENCIAS(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/TipoServidor", doc.getDocumentElement(), XPathConstants.NODE);
            this.setBD_TIPO(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ServidorBD", doc.getDocumentElement(), XPathConstants.NODE);
            this.setSERVIDOR_BASEDATOS(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/BaseDeDatos", doc.getDocumentElement(), XPathConstants.NODE);
            this.setBD_NOMBRE(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/User", doc.getDocumentElement(), XPathConstants.NODE);
            this.setBD_USER(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/Password", doc.getDocumentElement(), XPathConstants.NODE);
            this.setBD_PASS(nodo.getTextContent());
            this.setBD_PASS(nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(this.getBD_PASS()) : this.getBD_PASS());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPUser", doc.getDocumentElement(), XPathConstants.NODE);
            this.setERP_USER(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPPass", doc.getDocumentElement(), XPathConstants.NODE);
            this.setERP_PASS(nodo.getTextContent());
            this.setERP_PASS(nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(this.getERP_PASS()) : this.getERP_PASS());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPRutaArchivos", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                this.setERPRutaArchivos("");
            } else {
                this.setERPRutaArchivos(nodo.getTextContent());
            }
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPNombreSociedad", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo == null) {
                this.setERPNombreSociedad("");
            } else {
                this.setERPNombreSociedad(nodo.getTextContent());
            }
            this.setListBdsMaestras(HBPersistencia.LISTBDANADIDAS);
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se encontr? la ruta. {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String Conectar() {
        
        String msj = "";
        try {
            Desconectar();
            if (Sociedad == null || !Sociedad.isConnected()) {
                Sociedad = SBOCOMUtil.newCompany();
                Sociedad.setLicenseServer(this.getSERVIDOR_LICENCIAS());
                Sociedad.setServer(this.getSERVIDOR_BASEDATOS());
                Sociedad.setDbServerType(Integer.parseInt(this.getBD_TIPO()));
                Sociedad.setCompanyDB(this.getBD_NOMBRE());
                Sociedad.setDbUserName(this.getBD_USER());
                Sociedad.setDbPassword(this.getBD_PASS());
                Sociedad.setUserName(this.getERP_USER());
                Sociedad.setPassword(this.getERP_PASS());

                Sociedad.setLanguage(SBOCOMConstants.BoSuppLangs_ln_English);
                Sociedad.setUseTrusted(Boolean.FALSE);

                int ret = Sociedad.connect();
                if (ret == 0) {
                    msj = Sociedad.getCompanyName();
                } else {
                    msj = "ERR: (" + ret + ")" + Sociedad.getLastErrorDescription();
                    LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se pudo conectar {0}", msj);
                }
            }
            if (this.getListBdsMaestras() != null) {
                for (int i = 0; i < this.getListBdsMaestras().size(); i++) {
                    //if (listSociedades == null || listSociedades.size() == 0 || !listSociedades.get(i).isConnected()) {
                        ICompany sociedad = SBOCOMUtil.newCompany();
                        sociedad.setLicenseServer(this.getSERVIDOR_LICENCIAS());
                        sociedad.setServer(this.getSERVIDOR_BASEDATOS());
                        sociedad.setDbServerType(Integer.parseInt(this.getBD_TIPO()));
                        sociedad.setCompanyDB(this.getListBdsMaestras().get(i).getBDNombre());
                        sociedad.setDbUserName(this.getListBdsMaestras().get(i).getBDUsuario());
                        sociedad.setDbPassword(this.getListBdsMaestras().get(i).getBDClave());
                        sociedad.setUserName(this.getListBdsMaestras().get(i).getERPUsuario());
                        sociedad.setPassword(this.getListBdsMaestras().get(i).getERPClave());

                        sociedad.setLanguage(SBOCOMConstants.BoSuppLangs_ln_English);
                        sociedad.setUseTrusted(Boolean.FALSE);

                        int ret = sociedad.connect();
                        if(ret == 0){
                            listSociedades.add(sociedad);                            
                        }else{
                            msj = "ERR: (" + ret + ")" + sociedad.getLastErrorDescription();
                            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "ERR: No se pudo conectar {0}", msj);
                        }
                    //}
                }
            }
        } catch (Exception ex) {
            msj = "ERR: (-10000)" + ex.getMessage();
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se pudo conectar {0}", ex.getMessage());
            return msj;
        }
        return msj;
    }

    @Override
    public void Desconectar() {
        if (Sociedad != null && Sociedad.isConnected()) {
            Sociedad.disconnect();
            Sociedad = null;
        }
        if (listSociedades != null && listSociedades.size() > 0) {
            for (ICompany company : listSociedades) {
                company.disconnect();
                listSociedades.remove(company);
            }
            listSociedades.clear();
        }
        System.gc();
    }

}
