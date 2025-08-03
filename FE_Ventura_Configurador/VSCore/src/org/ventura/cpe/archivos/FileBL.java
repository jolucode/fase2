/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.archivos;

import org.ventura.cpe.dao.DocumentoFileDAO;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author VSUser
 */
public class FileBL extends DocumentoINF {
 
    public boolean setActualizarEstadoEnvioSUNAT(Transaccion tc, int estado) throws org.ventura.cpe.excepciones.VenturaExcepcion {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean CargarParametro(Document doc, XPath xPath) {
        Node nodo;

        try {
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPRutaArchivos", doc.getDocumentElement(), XPathConstants.NODE);
            this.setERPRutaArchivos(nodo.getTextContent());
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPNombreSociedad", doc.getDocumentElement(), XPathConstants.NODE);
            if(nodo == null){
                this.setERPNombreSociedad("An�nimo");
            }else{
                this.setERPNombreSociedad(nodo.getTextContent());
            }
        } catch (XPathExpressionException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se encontro la ruta. {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public String Conectar() {
        String retorno = "";
        String rutaDocumentos = getERPRutaArchivos();
        try {
            retorno = DocumentoFileDAO.validarRutaArchivos(rutaDocumentos);
            if(retorno.equals("OK")){
                retorno = getERPNombreSociedad();
            }else{
                retorno = "ERR: No existe ruta de Documentos a Procesar";
            }
        } catch (VenturaExcepcion ex) {
            Logger.getLogger(FileBL.class.getName()).log(Level.SEVERE, null, ex);
            retorno = "ERR: "+ex.getMessage();
        }
        return retorno;
    }

    @Override
    public void Desconectar() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}