/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.utilidades.config;

import com.json.parsers.JSONParser;
import org.ventura.cpe.bl.BdsMaestrasBL;
import org.ventura.cpe.bl.ReglasIdiomaDocBL;
import org.ventura.cpe.dao.conexion.ConexionMSSQL;
import org.ventura.cpe.dto.TransaccionResumenProp;
import org.ventura.cpe.dto.hb.BdsMaestras;
import org.ventura.cpe.dto.hb.ReglasIdiomasDoc;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.utilidades.encriptacion.Criptor;
import org.ventura.utilidades.entidades.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.awt.*;
import java.io.*;
import java.sql.Connection;
import java.util.List;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Percy
 */
public class FrmConfig extends javax.swing.JFrame {

    /**
     * Creates new form FrmConfig
     */

    DefaultTableModel modeloBD;

    Document doc;

    private static ResourceBundle rs;

    private HashMap<String, String> hmpDatosTipoDoc = new HashMap<>();

    private HashMap<String, String> hmpDatosCamposFE = new HashMap<>();

    private HashMap<String, String> hmpDatosCondicionales = new HashMap<>();

    private int countReglasVisor = 1;

    private List<ConfiguradorBD> listBDConfig = new ArrayList<>();

    private static ReglasIdiomasDoc instance1 = null;

    public static ReglasIdiomasDoc getInstance1() {
        if (instance1 == null) {
            instance1 = new ReglasIdiomasDoc();
        }
        return instance1;
    }

    public static void setRs(ResourceBundle rs) {
        FrmConfig.rs = rs;
    }

    public FrmConfig() {
        initComponents();
        this.setLocationRelativeTo(null);

        try {
            setIconImage(ImageIO.read(ClassLoader.getSystemResourceAsStream("org/ventura/utilidades/iconos/llave24.png")));
        } catch (Exception ex) {
            LoggerTrans.getCDMainLogger().log(Level.WARNING, "{0}. {1}", new Object[]{"No es posible cargar el ícono", ex.getMessage()});
        }

        List<TipoServidor> listServidor = TipoServidor.listarTodos();
        for (TipoServidor item : listServidor) {
            cmbBDTipoServidor.addItem(item);
        }

        List<TipoConector> listConector = TipoConector.listarTodos();
        for (TipoConector item : listConector) {
            cmbTipoConector.addItem(item);
        }

        List<TipoPFEConnector> listPFEConnector = TipoPFEConnector.listarTodos();
        for (TipoPFEConnector item : listPFEConnector) {
            cmbTipoConectorSunat.setEditable(true);
            cmbTipoConectorSunat.addItem(item);
        }

        List<TipoServidorBDIntermedia> listServidorBDIntermedia = TipoServidorBDIntermedia.listarTodos();
        for (TipoServidorBDIntermedia item : listServidorBDIntermedia) {
            cmbBDIntermediaTipoServidor.addItem(item);
        }

        List<TipoIntegracionWS> listIntegracionWS = TipoIntegracionWS.listarTodos();
        for (TipoIntegracionWS item : listIntegracionWS) {
            cmbTipoIntegracionWS.addItem(item);
        }
        
        List<TipoIntegracionWS> listIntegracionGuias = TipoIntegracionWS.listarTodos();
        for (TipoIntegracionWS item : listIntegracionGuias) {
            cmbTipoIntegracionGuias.addItem(item);
        }

        setLocation((getGraphicsConfiguration().getBounds().width - this.getWidth()) / 2,
                (getGraphicsConfiguration().getBounds().height - this.getHeight()) / 2);

        CargarParametros();
        txtProxyUser.setEnabled(false);
        txtProxyPass.setEnabled(false);
        jTblReglasIdioma.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTblBDMaestras.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    }

    public void cargarCombosTablaReglasIdioma() {
        try {
            /*CARGA DE COMBO DE TIPOS DE DOCUMENTOS*/
            hmpDatosTipoDoc = obtenerHashDatosProperties("tipoDocumentos");
            String[] sDatosTipoDoc = obtenerArrayDatosProperties(hmpDatosTipoDoc);
            JComboBox jCmbTipoDoc = new JComboBox(sDatosTipoDoc);
            jCmbTipoDoc.setFont(new Font("Tahoma", Font.PLAIN, 10));
            TableColumn tcTipoDoc = jTblReglasIdioma.getColumnModel().getColumn(1);
            TableCellEditor tceTipoDoc = new DefaultCellEditor(jCmbTipoDoc);
            tcTipoDoc.setCellEditor(tceTipoDoc);
            /*CARGA DE COMBO DE CAMPOS DE FE*/
            HashMap<String, String> hashMapCamposFE = obtenerHashDatosProperties("camposFE");
            String[] sDatosCamposFE = obtenerArrayDatosProperties(hashMapCamposFE);
            JComboBox jCmbCamposFE = new JComboBox(sDatosCamposFE);
            jCmbCamposFE.setEditable(true);
            jCmbCamposFE.setFont(new Font("Tahoma", Font.PLAIN, 10));
            TableColumn tcCamposFE = jTblReglasIdioma.getColumnModel().getColumn(2);
            TableCellEditor tceCamposFE = new DefaultCellEditor(jCmbCamposFE);
            tcCamposFE.setCellEditor(tceCamposFE);
            /*CARGA DE COMBO DE CONDICIONALES*/
            hmpDatosCondicionales = obtenerHashDatosProperties("condicionales");
            String[] sDatosCondicionales = obtenerArrayDatosProperties(hmpDatosCondicionales);
            JComboBox jCmbCondicionales = new JComboBox(sDatosCondicionales);
            jCmbCondicionales.setFont(new Font("Tahoma", Font.PLAIN, 10));
            TableColumn tcCondicionales = jTblReglasIdioma.getColumnModel().getColumn(3);
            TableCellEditor tceCondicionales = new DefaultCellEditor(jCmbCondicionales);
            tcCondicionales.setCellEditor(tceCondicionales);
            /*CARGA DEL CHECKBOX PARA ELIMINACION DE REGISTRO*/
            JCheckBox check = new JCheckBox();
            jTblReglasIdioma.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(check));
            jTblReglasIdioma.getColumnModel().getColumn(7).setCellRenderer(new Render_CheckBox());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarInicialesBdAnadidas() {
        try {
            /*CARGA DEL CHECKBOX PARA ELIMINACION DE REGISTRO DE BD'S AÑADIDAS*/
            JCheckBox checkBd = new JCheckBox();
            jTblBDMaestras.getColumnModel().getColumn(27).setCellEditor(new DefaultCellEditor(checkBd));
            jTblBDMaestras.getColumnModel().getColumn(27).setCellRenderer(new Render_CheckBox());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Clase para manejar el TableCellRenderer, que permitirá mostrar el JCheckBox
    class Render_CheckBox extends JCheckBox implements TableCellRenderer {

        private final JComponent component = new JCheckBox();

        public Render_CheckBox() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            //Color de fondo de la celda
            ((JCheckBox) component).setBackground(new Color(98, 138, 183));
            //obtiene valor boolean y coloca valor en el JCheckBox
            boolean b = false;
            if (value instanceof Boolean) {
                b = (Boolean) value;
            }
            ((JCheckBox) component).setSelected(b);
            return ((JCheckBox) component);
        }
    }

    public HashMap<String, String> obtenerHashDatosProperties(String llave) {
        HashMap<String, String> hashTemp = new HashMap<>();
        String[] datos = rs.getString(llave).split(",");
        try {
            for (int i = 0; i < datos.length; i++) {
                String[] campo = datos[i].split(":");
                hashTemp.put(campo[0], campo[1]);
            }
        } catch (Exception e) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar archivo properties. {0}", e.getMessage());
            return null;
        }
        return hashTemp;
    }

    public String[] obtenerArrayDatosProperties(HashMap<String, String> hashMap) {
        String[] sDatosTipoDoc = new String[hashMap.size()];
        Iterator<String> sKeyMapTipoDoc = hashMap.keySet().iterator();
        int i = 0;
        while (sKeyMapTipoDoc.hasNext()) {
            sDatosTipoDoc[i] = hashMap.get(sKeyMapTipoDoc.next());
            i++;
        }
        return sDatosTipoDoc;
    }

    public void obtenerRegistrosReglaBD() {
        List<ReglasIdiomasDoc> listReglasIdiomasDoc = ReglasIdiomaDocBL.listarReglas();
        if (listReglasIdiomasDoc != null) {
            DefaultTableModel dtm;
            dtm = (DefaultTableModel) jTblReglasIdioma.getModel();
            for (ReglasIdiomasDoc reglaIdiomasDoc : listReglasIdiomasDoc) {
                dtm.addRow(new Object[]{countReglasVisor,
                        buscarValorPorLlave(reglaIdiomasDoc.getFETipoDOC(), 1),
                        buscarValorPorLlave(reglaIdiomasDoc.getFECampoDOC(), 2),
                        buscarValorPorLlave(reglaIdiomasDoc.getFEOperador(), 3),
                        reglaIdiomasDoc.getFEValorComparativo(),
                        "-->",
                        reglaIdiomasDoc.getFEDOCFinal(),
                        false});
                countReglasVisor++;
            }
        }
    }

    private String buscarValorPorLlave(String llave, int hmp) {
        String valRetorno = "";
        switch (hmp) {
            case 1:
                valRetorno = hmpDatosTipoDoc.get(llave);
                break;
            case 2:
                valRetorno = hmpDatosCamposFE.get(llave);
                break;
            case 3:
                valRetorno = hmpDatosCondicionales.get(llave);
                break;
            default:
                break;
        }
        if (valRetorno == null) {
            return llave;
        }
        return valRetorno;
    }

    public void limpiarTablaReglas() {
        DefaultTableModel dtm;
        dtm = (DefaultTableModel) jTblReglasIdioma.getModel();
        while (dtm.getRowCount() > 0) {
            dtm.removeRow(0);
        }
        countReglasVisor = 1;
    }

    public void limpiarTablaBdAnadidas() {
        DefaultTableModel dtm;
        dtm = (DefaultTableModel) jTblBDMaestras.getModel();
        while (dtm.getRowCount() > 0) {
            dtm.removeRow(0);
        }
    }

    public void obtenerRegistrosConfigBD(List<ConfiguradorBD> listConfiguradorBD) {
        if (listConfiguradorBD != null) {
            DefaultTableModel dtm;
            dtm = (DefaultTableModel) jTblReglasIdioma.getModel();
            for (ConfiguradorBD configuradorBD : listConfiguradorBD) {
                dtm.addRow(new Object[]{configuradorBD.getBD_Id(),
                        configuradorBD.getBD_Servidor(),
                        configuradorBD.getBD_Nombre(),
                        configuradorBD.getBD_Usuario(),
                        configuradorBD.getBD_Clave(),
                        configuradorBD.getERP_Usuario(),
                        configuradorBD.getERP_Clave(),
                        false});
            }
        }
    }

    public void obtenerRegistrosBDsAnadidas() {
        List<BdsMaestras> listBdsAñadidas = BdsMaestrasBL.listarBDAnadidas();
        if (listBdsAñadidas != null) {
            DefaultTableModel dtm;
            dtm = (DefaultTableModel) jTblBDMaestras.getModel();
            for (BdsMaestras bdsAñadidas : listBdsAñadidas) {
                dtm.addRow(new Object[]{bdsAñadidas.getBDId(),
                        bdsAñadidas.getbDServidor(),
                        bdsAñadidas.getBDNombre(),
                        bdsAñadidas.getBDUsuario(),
                        bdsAñadidas.getBDClave(),
                        bdsAñadidas.getERPUsuario(),
                        bdsAñadidas.getERPClave(),
                        bdsAñadidas.getRucSociedad(),
                        bdsAñadidas.getRutaCD(),
                        bdsAñadidas.getPasswordCD(),
                        bdsAñadidas.getUsuarioSec(),
                        bdsAñadidas.getPasswordSec(),
                        bdsAñadidas.getUsuarioGuias(),
                        bdsAñadidas.getPasswordGuias(),
                        bdsAñadidas.getWebservicePublicacion(),
                        bdsAñadidas.getUsuarioWebService(),
                        bdsAñadidas.getPasswordWebService(),
                        bdsAñadidas.getLogoSociedad(),
                        bdsAñadidas.getServidorLicencia(),
                        bdsAñadidas.getTipoServidor(),
                        bdsAñadidas.getTipoERP(),
                        bdsAñadidas.getRutaArchivos(),
                        bdsAñadidas.getTipoIntegracionSunat(),
                        bdsAñadidas.getTipoIntegracionGuias(),
                        bdsAñadidas.getClientID(),
                        bdsAñadidas.getSecretID(),
                        bdsAñadidas.getScope(),
                        false});
            }
        }
    }

    private static FrmConfig instance = null;

    public static FrmConfig getInstance() {
        if (instance == null) {
            instance = new FrmConfig();
        }
        return instance;
    }

    public void vaciarJtxtResumen() {

        try {
            txtFecha.setText("");
            TransaccionResumenProp.fechaResumen = "";

            Node nodo;

            String p = "";
            XPath xPath = XPathFactory.newInstance().newXPath();

            nodo = (Node) xPath.evaluate("/Configuracion/ResumenDiario/Fecha", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtFecha.getText());

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            String sRutaConfigReal = System.getProperty("user.dir");
            String sRutaConfigGeneral[] = sRutaConfigReal.split("[\\\\/]", -1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";
            Result output = new StreamResult(new File(sRutaConfigReal));
            Source input = new DOMSource(doc);

            transformer.transform(input, output);
        } catch (XPathExpressionException | TransformerException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al actualizar parámetros del Resumen Diario. {0}", ex.getMessage());
        }

    }

    private String buscarLlavePorValor(String valor, int hmp) {
        Entry<String, String> e;
        Iterator<Entry<String, String>> it;
        switch (hmp) {
            case 1:
                it = hmpDatosTipoDoc.entrySet().iterator();
                while (it.hasNext()) {
                    e = it.next();
                    if (e.getValue().equals(valor)) {
                        return e.getKey();
                    }
                }
                break;
            case 2:
                it = hmpDatosCamposFE.entrySet().iterator();
                while (it.hasNext()) {
                    e = it.next();
                    if (e.getValue().equals(valor)) {
                        return e.getKey();
                    }
                }
                break;
            case 3:
                it = hmpDatosCondicionales.entrySet().iterator();
                while (it.hasNext()) {
                    e = it.next();
                    if (e.getValue().equals(valor)) {
                        return e.getKey();
                    }
                }
                break;
            default:
                break;
        }
        return valor;
    }

    private List<ReglasIdiomasDoc> obtenerReglasTabla() {
        List<ReglasIdiomasDoc> listReglasIdioma = new ArrayList<>();
        DefaultTableModel dtm;
        ReglasIdiomasDoc reglaIdiomaDoc;
        dtm = (DefaultTableModel) jTblReglasIdioma.getModel();
        int registrosEnTablaReglasIdioma = dtm.getRowCount();
        for (int i = 0; i < registrosEnTablaReglasIdioma; i++) {
            if (!dtm.getValueAt(i, 2).toString().equals("") && dtm.getValueAt(i, 2) != null
                    && !dtm.getValueAt(i, 3).toString().equals("") && dtm.getValueAt(i, 3) != null
                    && !dtm.getValueAt(i, 4).toString().equals("") && dtm.getValueAt(i, 4) != null
                    && !dtm.getValueAt(i, 6).toString().equals("") && dtm.getValueAt(i, 6) != null) {
                reglaIdiomaDoc = new ReglasIdiomasDoc();
                reglaIdiomaDoc.setFETipoDOC(buscarLlavePorValor(dtm.getValueAt(i, 1).toString(), 1));
                reglaIdiomaDoc.setFECampoDOC(buscarLlavePorValor(dtm.getValueAt(i, 2).toString(), 2));
                reglaIdiomaDoc.setFEOperador(buscarLlavePorValor(dtm.getValueAt(i, 3).toString(), 3));
                reglaIdiomaDoc.setFEValorComparativo(dtm.getValueAt(i, 4).toString());
                reglaIdiomaDoc.setFEDOCFinal(dtm.getValueAt(i, 6).toString());
                listReglasIdioma.add(reglaIdiomaDoc);
            }
        }
        return listReglasIdioma;
    }

    private List<BdsMaestras> obtenerBdAñadidasTabla(List<BdsMaestras> bdsMaestras) {
        List<BdsMaestras> listBdsAñadidas = new ArrayList<>();
        DefaultTableModel dtm;
        BdsMaestras bdMaestra;
        dtm = (DefaultTableModel) jTblBDMaestras.getModel();
        int registrosEnTablaBdsAñadidas = dtm.getRowCount();
        for (int i = 0; i < registrosEnTablaBdsAñadidas; i++) {
            if (!dtm.getValueAt(i, 1).toString().equals("") && dtm.getValueAt(i, 1) != null
                    && !dtm.getValueAt(i, 2).toString().equals("") && dtm.getValueAt(i, 2) != null
                    && !dtm.getValueAt(i, 3).toString().equals("") && dtm.getValueAt(i, 3) != null
                    && !dtm.getValueAt(i, 4).toString().equals("") && dtm.getValueAt(i, 4) != null
                    && !dtm.getValueAt(i, 5).toString().equals("") && dtm.getValueAt(i, 5) != null
                    && !dtm.getValueAt(i, 6).toString().equals("") && dtm.getValueAt(i, 6) != null
                    && !dtm.getValueAt(i, 7).toString().equals("") && dtm.getValueAt(i, 7) != null
                    && !dtm.getValueAt(i, 8).toString().equals("") && dtm.getValueAt(i, 8) != null
                    && !dtm.getValueAt(i, 9).toString().equals("") && dtm.getValueAt(i, 9) != null
                    && !dtm.getValueAt(i, 10).toString().equals("") && dtm.getValueAt(i, 10) != null
                    && !dtm.getValueAt(i, 11).toString().equals("") && dtm.getValueAt(i, 11) != null
                    && !dtm.getValueAt(i, 12).toString().equals("") && dtm.getValueAt(i, 12) != null
                    && !dtm.getValueAt(i, 13).toString().equals("") && dtm.getValueAt(i, 13) != null
                    && !dtm.getValueAt(i, 14).toString().equals("") && dtm.getValueAt(i, 14) != null
                    && !dtm.getValueAt(i, 15).toString().equals("") && dtm.getValueAt(i, 15) != null
                    && !dtm.getValueAt(i, 16).toString().equals("") && dtm.getValueAt(i, 16) != null
                    && !dtm.getValueAt(i, 17).toString().equals("") && dtm.getValueAt(i, 17) != null
                    && !dtm.getValueAt(i, 18).toString().equals("") && dtm.getValueAt(i, 18) != null
                    && !dtm.getValueAt(i, 19).toString().equals("") && dtm.getValueAt(i, 19) != null
                    && !dtm.getValueAt(i, 20).toString().equals("") && dtm.getValueAt(i, 20) != null
                    && !dtm.getValueAt(i, 21).toString().equals("") && dtm.getValueAt(i, 21) != null
                    && !dtm.getValueAt(i, 22).toString().equals("") && dtm.getValueAt(i, 22) != null
                    && !dtm.getValueAt(i, 23).toString().equals("") && dtm.getValueAt(i, 23) != null
                    && !dtm.getValueAt(i, 24).toString().equals("") && dtm.getValueAt(i, 24) != null
                    && !dtm.getValueAt(i, 25).toString().equals("") && dtm.getValueAt(i, 25) != null
                    && !dtm.getValueAt(i, 26).toString().equals("") && dtm.getValueAt(i, 26) != null
                    )
            {
                bdMaestra = new BdsMaestras();
                bdMaestra.setBDId((i + 1) + "");
                bdMaestra.setPosicion((i + 1));
                bdMaestra.setbDServidor(dtm.getValueAt(i, 1).toString());
                bdMaestra.setBDNombre(dtm.getValueAt(i, 2).toString());
                bdMaestra.setBDUsuario(dtm.getValueAt(i, 3).toString());
                bdMaestra.setBDClave(dtm.getValueAt(i, 4).toString());
                bdMaestra.setERPUsuario(dtm.getValueAt(i, 5).toString());
                bdMaestra.setERPClave(dtm.getValueAt(i, 6).toString());
                bdMaestra.setRucSociedad(dtm.getValueAt(i, 7).toString());
                bdMaestra.setRutaCD(dtm.getValueAt(i, 8).toString());
                bdMaestra.setPasswordCD(dtm.getValueAt(i, 9).toString());
                bdMaestra.setUsuarioSec(dtm.getValueAt(i, 10).toString());
                bdMaestra.setPasswordSec(dtm.getValueAt(i, 11).toString());
                //Datos guías
                bdMaestra.setUsuarioGuias(dtm.getValueAt(i, 12).toString());
                bdMaestra.setPasswordGuias(dtm.getValueAt(i, 13).toString());
              
                //Datos nuevos
                 bdMaestra.setWebservicePublicacion(dtm.getValueAt(i, 14).toString());
                 bdMaestra.setUsuarioWebService(dtm.getValueAt(i, 15).toString());
                 bdMaestra.setPasswordWebService(dtm.getValueAt(i, 16).toString());
                 bdMaestra.setLogoSociedad(dtm.getValueAt(i, 17).toString());
                 
                 // Datos no en tabla 
                 bdMaestra.setServidorLicencia(dtm.getValueAt(i, 18).toString());
                 bdMaestra.setTipoServidor(dtm.getValueAt(i, 19).toString());
                 bdMaestra.setTipoERP(dtm.getValueAt(i, 20).toString());
                 bdMaestra.setRutaArchivos(dtm.getValueAt(i, 21).toString());
                 bdMaestra.setTipoIntegracionSunat(dtm.getValueAt(i, 22).toString());
                 bdMaestra.setTipoIntegracionGuias(dtm.getValueAt(i, 23).toString());
                 bdMaestra.setClientID(dtm.getValueAt(i, 24).toString());
                 bdMaestra.setSecretID(dtm.getValueAt(i, 25).toString());
                 bdMaestra.setScope(dtm.getValueAt(i, 26).toString());
                Optional<BdsMaestras> optional = buscarExistenciaBD(bdsMaestras, bdMaestra);
                if (optional.isPresent()) {
                    BdsMaestras maestra = optional.get();
                    bdMaestra.setServidorLicencia(maestra.getServidorLicencia());
                    bdMaestra.setTipoServidor(maestra.getTipoServidor());
                    bdMaestra.setTipoERP(maestra.getTipoERP());
                    bdMaestra.setRutaArchivos(maestra.getRutaArchivos());
                    bdMaestra.setTipoIntegracionSunat(maestra.getTipoIntegracionSunat());
                    bdMaestra.setTipoIntegracionGuias(maestra.getTipoIntegracionGuias());
                    bdMaestra.setClientID(maestra.getClientID());
                    bdMaestra.setSecretID(maestra.getSecretID());
                    bdMaestra.setScope(maestra.getScope());
                    //bdMaestra.setbDServidor(maestra.getbDServidor());
                }
                listBdsAñadidas.add(bdMaestra);
            }
        }
        return listBdsAñadidas;
    }

    private Optional<BdsMaestras> buscarExistenciaBD(List<BdsMaestras> bdsMaestras, BdsMaestras bdsMaestra) {
        for (BdsMaestras maestra : bdsMaestras) {
            if (maestra.getBDNombre().equalsIgnoreCase(bdsMaestra.getBDNombre()))
                return Optional.ofNullable(maestra);
        }
        return Optional.empty();
    }

    private int validarRegistrosTablaReglas() {
        DefaultTableModel dtm = (DefaultTableModel) jTblReglasIdioma.getModel();
        int nroLineasTablaReglas = dtm.getRowCount();
        int nroColumnasTablaReglas = dtm.getColumnCount();
        for (int i = 0; i < nroLineasTablaReglas; i++) {
            for (int j = 1; j < nroColumnasTablaReglas; j++) {
                if (dtm.getValueAt(i, j) == null || dtm.getValueAt(i, j).equals("")) {
                    JOptionPane.showMessageDialog(this, "Se debe de seleccionar o registrar dato en el campo: " + dtm.getColumnName(j) + " del registro " + (i + 1));
                    return 0;
                }
            }
        }
        if (nroLineasTablaReglas == 0) {
            return 1;
        }
        return 2;
    }

    private int validarRegistrosTablaAnadirBD() {
        DefaultTableModel dtm = (DefaultTableModel) jTblBDMaestras.getModel();
        int nroLineasTablaAñadirBD = dtm.getRowCount();
        int nroColumnasTablaReglas = dtm.getColumnCount();
        for (int i = 0; i < nroLineasTablaAñadirBD; i++) {
            for (int j = 1; j < nroColumnasTablaReglas - 1; j++) {
                if (dtm.getValueAt(i, j) == null || dtm.getValueAt(i, j).equals("")) {
                    JOptionPane.showMessageDialog(this, "Se debe de seleccionar o registrar dato en el campo: " + dtm.getColumnName(j) + " del registro " + (i + 1));
                    return 0;
                }
            }
        }
        if (nroLineasTablaAñadirBD == 0) {
            return 1;
        }
        return 2;
    }

    private boolean guardarReglasBD() throws Exception {
        try {
            List<ReglasIdiomasDoc> listReglasIdioma = obtenerReglasTabla();
            ReglasIdiomaDocBL.Crear(listReglasIdioma);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    private boolean guardarBDAñadidas(List<BdsMaestras> bdsMaestras) throws Exception {
        try {
            List<BdsMaestras> listBdAñadidas = obtenerBdAñadidasTabla(bdsMaestras);
            BdsMaestrasBL.Crear(listBdAñadidas);
            return true;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return false;
    }

    private boolean limpiarReglasBD() {
        List<ReglasIdiomasDoc> listKeyReglasBD = ReglasIdiomaDocBL.listarKeyReglasBD();
        boolean retorno = false;
        if (listKeyReglasBD != null) {
            if (listKeyReglasBD.size() > 0) {
                retorno = ReglasIdiomaDocBL.destroyAllReglas(listKeyReglasBD);
            } else {
                retorno = true;
            }
        }
        return retorno;
    }

    private boolean limpiarBDAnadidas() {
        List<BdsMaestras> listKeyBDAnadidas = BdsMaestrasBL.listarKeyBDAnadidas();
        boolean retorno = false;
        if (listKeyBDAnadidas != null) {
            if (listKeyBDAnadidas.size() > 0) {
                retorno = BdsMaestrasBL.destroyAllBDAnadidas(listKeyBDAnadidas);
            } else {
                retorno = true;
            }
        }
        return retorno;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chkSunat1 = new javax.swing.JCheckBox();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtSAPServer = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtSAPUsuario = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtSAPPassword = new javax.swing.JPasswordField();
        jLabel32 = new javax.swing.JLabel();
        cmbTipoConector = new javax.swing.JComboBox();
        jLblRutaArchivos = new javax.swing.JLabel();
        jTxfRutaArchivos = new javax.swing.JTextField();
        jTxfNombreSociedad = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cmbBDTipoServidor = new javax.swing.JComboBox();
        txtBDNombreServidor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtBDUsuario = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBaseDatos = new javax.swing.JTextField();
        txtBDPassword = new javax.swing.JPasswordField();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        txtPersistBDServidor = new javax.swing.JTextField();
        txtPersistBaseDatos = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        txtPersistBDPassword = new javax.swing.JPasswordField();
        txtPersistBDUsuario = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        lblCadena = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        txtPersistPuerto = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        cmbBDIntermediaTipoServidor = new javax.swing.JComboBox();
        jLabel35 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        chbProxyAutenUsar = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        txtProxyServer = new javax.swing.JTextField();
        txtProxyPuerto = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        chbProxyUsar = new javax.swing.JCheckBox();
        jLabel22 = new javax.swing.JLabel();
        txtProxyUser = new javax.swing.JTextField();
        jLabel31 = new javax.swing.JLabel();
        txtProxyPass = new javax.swing.JPasswordField();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtRQTimeOut = new javax.swing.JTextField();
        txtRQInterval = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        txtRSInterval = new javax.swing.JTextField();
        txtRSTimeOut = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        btnAdjuntos = new javax.swing.JButton();
        txtDirAdjuntos = new javax.swing.JPasswordField();
        jPanel11 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        txtHoraResumen = new javax.swing.JTextField();
        txtFecha = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        txtRutaCertificado = new javax.swing.JTextField();
        txtClaveCertificado = new javax.swing.JPasswordField();
        btnAdjuntoCertificado = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        txtUserSunat = new javax.swing.JTextField();
        txtPassSunat = new javax.swing.JPasswordField();
        jLabel52 = new javax.swing.JLabel();
        cmbTipoIntegracionWS = new javax.swing.JComboBox();
        jPanel19 = new javax.swing.JPanel();
        jLabel45 = new javax.swing.JLabel();
        txtTiempoActualizacion = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        cmbTipoConectorSunat = new javax.swing.JComboBox();
        jLabel40 = new javax.swing.JLabel();
        jPanel27 = new javax.swing.JPanel();
        jLabel53 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        txtPassGuias = new javax.swing.JPasswordField();
        txtUserGuias = new javax.swing.JTextField();
        cmbTipoIntegracionGuias = new javax.swing.JComboBox();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        txtSecretID = new javax.swing.JPasswordField();
        txtClientID = new javax.swing.JTextField();
        txtScope = new javax.swing.JTextField();
        chkPDF = new javax.swing.JCheckBox();
        chkSunat = new javax.swing.JCheckBox();
        chkValidacion = new javax.swing.JCheckBox();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jPanel18 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        txtUserWS = new javax.swing.JTextField();
        txtPathWS = new javax.swing.JTextField();
        txtPassWS = new javax.swing.JPasswordField();
        jPanel21 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTxfTiempoEsperaPublic = new javax.swing.JTextField();
        jTxfIntervaloRepeticionPublic = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jTxfTiempoPublicacionPublic = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        cmbImpresion = new javax.swing.JComboBox();
        jPanel22 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTblReglasIdioma = new javax.swing.JTable();
        jBtnAgregar = new javax.swing.JButton();
        jBtnEliminar = new javax.swing.JButton();
        jChkSeleccionarTodo = new javax.swing.JCheckBox();
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTblBDMaestras = new javax.swing.JTable();
        jBtnAgregarBD = new javax.swing.JButton();
        jBtnEliminarBD = new javax.swing.JButton();
        jChkSeleccionarTodoBD = new javax.swing.JCheckBox();
        jPanel13 = new javax.swing.JPanel();
        jPanel26 = new javax.swing.JPanel();
        jLabel46 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        txtversionfactura = new javax.swing.JTextField();
        txtversionboleta = new javax.swing.JTextField();
        jLabel54 = new javax.swing.JLabel();
        txtversioncredito = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        txtversiondebito = new javax.swing.JTextField();
        btnGuardar = new javax.swing.JButton();
        btnCerrar = new javax.swing.JButton();

        chkSunat1.setText("Uso Sunat");

        setTitle("Configuración");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("ERP"));

        jLabel3.setText("Servidor Licencias");

        jLabel6.setText("Usuario");

        jLabel7.setText("Contraseña");

        jLabel32.setText("Tipo ERP");

        cmbTipoConector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbTipoConectorMouseClicked(evt);
            }
        });
        cmbTipoConector.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTipoConectorItemStateChanged(evt);
            }
        });
        cmbTipoConector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipoConectorActionPerformed(evt);
            }
        });

        jLblRutaArchivos.setText("Ruta Archivos");

        jLabel50.setText("Nombre Sociedad");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(19, 19, 19))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLblRutaArchivos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel50, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTxfNombreSociedad)
                    .addComponent(txtSAPUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, 665, Short.MAX_VALUE)
                    .addComponent(txtSAPPassword)
                    .addComponent(txtSAPServer)
                    .addComponent(cmbTipoConector, javax.swing.GroupLayout.Alignment.TRAILING, 0, 665, Short.MAX_VALUE)
                    .addComponent(jTxfRutaArchivos))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(cmbTipoConector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtSAPServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtSAPUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtSAPPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxfRutaArchivos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLblRutaArchivos))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTxfNombreSociedad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel50))
                .addContainerGap(31, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Servidor Base Datos"));

        jLabel1.setText("Tipo servidor");

        cmbBDTipoServidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBDTipoServidorActionPerformed(evt);
            }
        });

        jLabel2.setText("Servidor Base Datos");

        jLabel4.setText("Usuario");

        jLabel5.setText("Contraseña");

        jLabel8.setText("Base Datos");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmbBDTipoServidor, 0, 671, Short.MAX_VALUE)
                    .addComponent(txtBDNombreServidor)
                    .addComponent(txtBDUsuario)
                    .addComponent(txtBaseDatos)
                    .addComponent(txtBDPassword, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbBDTipoServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtBDNombreServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtBaseDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtBDUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtBDPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(83, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Fuente Datos", jPanel3);

        jLabel9.setText("Servidor");

        txtPersistBDServidor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPersistBDServidorKeyReleased(evt);
            }
        });

        txtPersistBaseDatos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPersistBaseDatosKeyReleased(evt);
            }
        });

        jLabel10.setText("Base Datos");

        jLabel11.setText("Usuario");

        jLabel12.setText("Contraseña");

        jLabel13.setText("Cadena");

        lblCadena.setText("jdbc:sqlserver://:;databaseName=");

        jLabel16.setText("Puerto");

        txtPersistPuerto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtPersistPuertoKeyReleased(evt);
            }
        });

        jButton1.setText("Crear Base Datos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        cmbBDIntermediaTipoServidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbBDIntermediaTipoServidorActionPerformed(evt);
            }
        });

        jLabel35.setText("Tipo Servidor");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(31, 31, 31))
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblCadena, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtPersistBDPassword, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPersistBDUsuario, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPersistBaseDatos, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPersistPuerto, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtPersistBDServidor, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbBDIntermediaTipoServidor, javax.swing.GroupLayout.Alignment.LEADING, 0, 667, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmbBDIntermediaTipoServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel35))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(txtPersistBDServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(txtPersistPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(txtPersistBaseDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(txtPersistBDUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(txtPersistBDPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(lblCadena, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(246, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Repositorio", jPanel4);

        chbProxyAutenUsar.setText("Usar autentificación de proxy");
        chbProxyAutenUsar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbProxyAutenUsarItemStateChanged(evt);
            }
        });
        chbProxyAutenUsar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbProxyAutenUsarActionPerformed(evt);
            }
        });

        jLabel14.setText("Servidor");

        txtProxyServer.setEnabled(false);

        txtProxyPuerto.setEnabled(false);

        jLabel17.setText(":");

        chbProxyUsar.setText("Usar servidor proxy");
        chbProxyUsar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbProxyUsarItemStateChanged(evt);
            }
        });

        jLabel22.setText("Usuario");

        txtProxyUser.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtProxyUserKeyReleased(evt);
            }
        });

        jLabel31.setText("Contraseña");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chbProxyAutenUsar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtProxyServer, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtProxyPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtProxyUser, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE)
                                    .addComponent(txtProxyPass))))
                        .addGap(0, 473, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addComponent(chbProxyUsar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(txtProxyServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProxyPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(18, 18, 18)
                .addComponent(chbProxyAutenUsar)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(txtProxyUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel31)
                    .addComponent(txtProxyPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(337, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(17, 17, 17)
                    .addComponent(chbProxyUsar)
                    .addContainerGap(470, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Proxy", jPanel5);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Envío de Transacciones"));

        jLabel23.setText("Tiempo de espera");

        jLabel24.setText("Intervalo de repetición");

        txtRQTimeOut.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtRQInterval.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel27.setText("segundos");

        jLabel28.setText("segundos");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txtRQInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(txtRQTimeOut, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)))
                .addGap(343, 343, 343))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtRQTimeOut)
                        .addComponent(jLabel27))
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtRQInterval)))
                .addGap(0, 15, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Consulta de Documentos"));

        jLabel25.setText("Tiempo de espera");

        jLabel26.setText("Intervalo de repetición");

        txtRSInterval.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtRSTimeOut.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel30.setText("segundos");

        jLabel29.setText("segundos");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(36, 36, 36)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtRSTimeOut)
                    .addComponent(txtRSInterval))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(331, 331, 331))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtRSTimeOut)
                    .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtRSInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Directorios"));

        jLabel15.setText("Adjuntos");
        jLabel15.setToolTipText("* Especifique la ruta donde almacenar los documentos devueltos por SUNAT como CDR, XML, etc.");

        btnAdjuntos.setText("...");
        btnAdjuntos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdjuntosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtDirAdjuntos)
                .addGap(18, 18, 18)
                .addComponent(btnAdjuntos, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel15)
                .addComponent(btnAdjuntos)
                .addComponent(txtDirAdjuntos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Resumen Diario"));

        jLabel34.setText("Hora");

        txtHoraResumen.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtFecha.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel36.setText("Fecha (yyyy-MM-dd)");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .addComponent(txtHoraResumen))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHoraResumen))
                .addGap(17, 17, 17))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(152, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("General", jPanel7);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Certificado"));

        jLabel41.setText("Ruta");

        jLabel42.setText("Clave");

        btnAdjuntoCertificado.setText("...");
        btnAdjuntoCertificado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAdjuntoCertificadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel42, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                    .addComponent(jLabel41, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addComponent(txtRutaCertificado, javax.swing.GroupLayout.PREFERRED_SIZE, 575, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnAdjuntoCertificado, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtClaveCertificado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel41)
                    .addComponent(txtRutaCertificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdjuntoCertificado))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel42)
                    .addComponent(txtClaveCertificado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 14, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Sunat"));

        jLabel43.setText("Usuario");

        jLabel44.setText("Clave");

        jLabel52.setText("Tipo de Integración WS:");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel43, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel52))
                .addGap(42, 42, 42)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(txtUserSunat, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
                        .addComponent(txtPassSunat))
                    .addComponent(cmbTipoIntegracionWS, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(cmbTipoIntegracionWS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUserSunat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassSunat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44))
                .addContainerGap())
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Gestor de Transacciones"));

        jLabel45.setText("Tiempo Actualizacion");

        txtTiempoActualizacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTiempoActualizacionActionPerformed(evt);
            }
        });

        jLabel37.setText("segundos");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTiempoActualizacion, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(txtTiempoActualizacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Connector"));

        jLabel40.setText("Tipo Conector");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addComponent(cmbTipoConectorSunat, javax.swing.GroupLayout.PREFERRED_SIZE, 622, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel40)
                    .addComponent(cmbTipoConectorSunat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder("Guias SUNAT "));

        jLabel53.setText("Tipo de Integración Guias:");

        jLabel56.setText("Usuario");

        jLabel57.setText("Clave");

        jLabel60.setText("Client ID");

        jLabel61.setText("Secret ID");

        jLabel62.setText("Scope");

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                        .addComponent(jLabel61, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtSecretID, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(cmbTipoIntegracionGuias, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                        .addComponent(jLabel56, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtUserGuias, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel27Layout.createSequentialGroup()
                        .addComponent(jLabel57, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtPassGuias, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jLabel62, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtScope, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel27Layout.createSequentialGroup()
                        .addComponent(jLabel60, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtClientID, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(cmbTipoIntegracionGuias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(txtUserGuias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(txtPassGuias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel60)
                    .addComponent(txtClientID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel61)
                    .addComponent(txtSecretID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel62)
                    .addComponent(txtScope, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        chkPDF.setText("Generar PDF sin respuesta");
        chkPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkPDFActionPerformed(evt);
            }
        });

        chkSunat.setText("Uso Sunat");
        chkSunat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkSunatActionPerformed(evt);
            }
        });

        chkValidacion.setText("Validaciones");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(chkValidacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(91, 91, 91)
                        .addComponent(chkSunat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(83, 83, 83)
                        .addComponent(chkPDF, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(89, 89, 89))))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkValidacion)
                    .addComponent(chkSunat)
                    .addComponent(chkPDF))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(109, 109, 109))
        );

        jTabbedPane1.addTab("Conector", jPanel6);

        jLayeredPane1.setBackground(new java.awt.Color(204, 204, 204));

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Web Service"));

        jLabel20.setText("Usuario");

        jLabel21.setText("Clave");

        jLabel33.setText("Ubicación");

        txtUserWS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserWSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPathWS, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtPassWS, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtUserWS, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(txtUserWS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(txtPassWS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(txtPathWS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Tiempos del Publicador"));

        jLabel18.setText("Tiempo de espera");

        jLabel19.setText("Intervalo de repetición");

        jLabel39.setText("segundos");

        jLabel47.setText("segundos");

        jLabel48.setText("Tiempo de Publicación");

        jLabel49.setText("horas");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTxfTiempoEsperaPublic)
                    .addComponent(jTxfIntervaloRepeticionPublic)
                    .addComponent(jTxfTiempoPublicacionPublic, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel39, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                    .addComponent(jLabel47, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel49, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxfTiempoEsperaPublic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxfIntervaloRepeticionPublic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel47))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel48, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTxfTiempoPublicacionPublic, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(215, Short.MAX_VALUE))
        );

        jLayeredPane1.setLayer(jPanel18, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Publicador", jLayeredPane1);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Formato PDF"));

        jLabel38.setText("Cod. Barra");

        cmbImpresion.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Codigo QR", "PDF 417", "Valor Resumen" }));

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel38, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmbImpresion, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(335, 335, 335))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(cmbImpresion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(403, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Impresion", jPanel16);

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("Reglas"));

        jTblReglasIdioma.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTblReglasIdioma.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nro.", "Tipo Doc.", "Campos FE", "Operador", "Valor", "", "Documento", ""
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTblReglasIdioma.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTblReglasIdiomaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTblReglasIdioma);
        if (jTblReglasIdioma.getColumnModel().getColumnCount() > 0) {
            jTblReglasIdioma.getColumnModel().getColumn(0).setMinWidth(40);
            jTblReglasIdioma.getColumnModel().getColumn(0).setMaxWidth(40);
            jTblReglasIdioma.getColumnModel().getColumn(1).setMinWidth(250);
            jTblReglasIdioma.getColumnModel().getColumn(1).setMaxWidth(250);
            jTblReglasIdioma.getColumnModel().getColumn(2).setMinWidth(100);
            jTblReglasIdioma.getColumnModel().getColumn(2).setMaxWidth(100);
            jTblReglasIdioma.getColumnModel().getColumn(3).setMinWidth(58);
            jTblReglasIdioma.getColumnModel().getColumn(3).setMaxWidth(58);
            jTblReglasIdioma.getColumnModel().getColumn(4).setMinWidth(100);
            jTblReglasIdioma.getColumnModel().getColumn(4).setMaxWidth(100);
            jTblReglasIdioma.getColumnModel().getColumn(5).setMinWidth(25);
            jTblReglasIdioma.getColumnModel().getColumn(5).setPreferredWidth(10);
            jTblReglasIdioma.getColumnModel().getColumn(5).setMaxWidth(25);
            jTblReglasIdioma.getColumnModel().getColumn(6).setMinWidth(235);
            jTblReglasIdioma.getColumnModel().getColumn(6).setMaxWidth(235);
            jTblReglasIdioma.getColumnModel().getColumn(7).setMinWidth(25);
            jTblReglasIdioma.getColumnModel().getColumn(7).setMaxWidth(25);
        }

        jBtnAgregar.setText("AGREGAR");
        jBtnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAgregarActionPerformed(evt);
            }
        });

        jBtnEliminar.setText("ELIMINAR");
        jBtnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEliminarActionPerformed(evt);
            }
        });

        jChkSeleccionarTodo.setText("Selec. Todo");
        jChkSeleccionarTodo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jChkSeleccionarTodoMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jBtnEliminar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jBtnAgregar))
                        .addGap(6, 6, 6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                        .addComponent(jChkSeleccionarTodo)
                        .addContainerGap())))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addComponent(jBtnAgregar)
                        .addGap(18, 18, 18)
                        .addComponent(jBtnEliminar)
                        .addGap(18, 18, 18)
                        .addComponent(jChkSeleccionarTodo)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
                .addGap(52, 52, 52))
        );

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Reglas Formato", jPanel22);

        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder("Añadir más BD's"));

        jTblBDMaestras.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jTblBDMaestras.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nro.", "Servidor BD", "Nombre de BD", "Usuario BD", "Clave BD", "Usuario ERP", "Clave ERP", "Ruc Sociedad", "Ruta Certificado Digital", "Password CD", "Usuario Secundario", "Password Secundario", "Usuario Guias", "Password Guias", "Portal Web", "Web Usuario", "Web Clave", "Logo Sociedad", "Servidor Licencia", "Tipo Servidor", "Tipo ERP", "Ruta Archivos", "Tipo Integración Sunat", "Tipo Integracion Guias", "ClientID", "SecretID", "Scope", ""
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTblBDMaestras.setMaximumSize(new java.awt.Dimension(833, 0));
        jTblBDMaestras.setMinimumSize(new java.awt.Dimension(833, 0));
        jTblBDMaestras.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTblBDMaestrasMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTblBDMaestras);
        if (jTblBDMaestras.getColumnModel().getColumnCount() > 0) {
            jTblBDMaestras.getColumnModel().getColumn(0).setMinWidth(40);
            jTblBDMaestras.getColumnModel().getColumn(0).setMaxWidth(40);
            jTblBDMaestras.getColumnModel().getColumn(1).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(1).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(2).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(2).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(3).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(3).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(4).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(4).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(5).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(5).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(6).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(6).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(7).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(7).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(8).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(8).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(10).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(10).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(11).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(11).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(12).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(12).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(13).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(13).setMaxWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(14).setMinWidth(150);
            jTblBDMaestras.getColumnModel().getColumn(14).setMaxWidth(150);
        }

        jBtnAgregarBD.setText("AGREGAR");
        jBtnAgregarBD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnAgregarBDActionPerformed(evt);
            }
        });

        jBtnEliminarBD.setText("ELIMINAR");
        jBtnEliminarBD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBtnEliminarBDActionPerformed(evt);
            }
        });

        jChkSeleccionarTodoBD.setText("Selec. Todo");
        jChkSeleccionarTodoBD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jChkSeleccionarTodoBDMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jBtnEliminarBD)
                    .addComponent(jBtnAgregarBD)
                    .addComponent(jChkSeleccionarTodoBD))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jBtnAgregarBD)
                .addGap(49, 49, 49)
                .addComponent(jBtnEliminarBD)
                .addGap(55, 55, 55)
                .addComponent(jChkSeleccionarTodoBD)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Añadir BD´s", jPanel24);

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder("Envío de Transacciones"));

        jLabel46.setText("Factura");

        jLabel51.setText("Boleta");

        txtversionfactura.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        txtversionboleta.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel54.setText("Notas de Credito");

        txtversioncredito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel55.setText("Notas de Debito");

        txtversiondebito.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                        .addGap(35, 35, 35))
                    .addGroup(jPanel26Layout.createSequentialGroup()
                        .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtversiondebito, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtversioncredito, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtversionboleta, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtversionfactura, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(343, 495, Short.MAX_VALUE))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtversionfactura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel51, javax.swing.GroupLayout.DEFAULT_SIZE, 24, Short.MAX_VALUE)
                    .addComponent(txtversionboleta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtversioncredito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtversiondebito, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39))
        );

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(295, Short.MAX_VALUE))
        );

        jPanel26.getAccessibleContext().setAccessibleName("Versión de documentos");

        jTabbedPane1.addTab("Version UBL", jPanel13);

        btnGuardar.setText("Guardar");
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCerrarActionPerformed(evt);
            }
        });

        jLayeredPane2.setLayer(jTabbedPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(btnGuardar, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane2.setLayer(btnCerrar, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane2Layout = new javax.swing.GroupLayout(jLayeredPane2);
        jLayeredPane2.setLayout(jLayeredPane2Layout);
        jLayeredPane2Layout.setHorizontalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addGap(299, 299, 299)
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 835, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1))
        );
        jLayeredPane2Layout.setVerticalGroup(
            jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jLayeredPane2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuardar)
                    .addComponent(btnCerrar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Proxy");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        Node nodo;
        try {
            String p = "";
            XPath xPath = XPathFactory.newInstance().newXPath();

            // Gestor Tiempo
            nodo = (Node) xPath.evaluate("/Configuracion/GestorTiempo/Tiempo", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.setTextContent(txtTiempoActualizacion.getText());

            /**
             * *******************************ERP*********************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/ERP/TipoConector", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(((TipoConector) cmbTipoConector.getSelectedItem()).getValor()));

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ServidorLicencias", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtSAPServer.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/TipoServidor", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(((TipoServidor) cmbBDTipoServidor.getSelectedItem()).getValor()));

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ServidorBD", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtBDNombreServidor.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/BaseDeDatos", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtBaseDatos.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/User", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtBDUsuario.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/Password", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtBDPassword.getPassword())));

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPUser", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtSAPUsuario.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPPass", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtSAPPassword.getPassword())));

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPRutaArchivos", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent() != null && !"".equals(nodo.getTextContent())) {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                nodo.setTextContent(jTxfRutaArchivos.getText());
            } else {
                nodo.setTextContent("");
            }

            nodo = (Node) xPath.evaluate("/Configuracion/ERP/ERPNombreSociedad", doc.getDocumentElement(), XPathConstants.NODE);
            if (nodo.getTextContent() != null && !"".equals(nodo.getTextContent())) {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                nodo.setTextContent(jTxfNombreSociedad.getText());
            } else {
                nodo.setTextContent("");
            }

            /**
             * *******************************************USO
             * SUNAT********************************************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/UsoSunat/WS", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(chkSunat.isSelected()));

            nodo = (Node) xPath.evaluate("/Configuracion/UsoSunat/PDF", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(chkPDF.isSelected()));

            nodo = (Node) xPath.evaluate("/Configuracion/UsoSunat/Validacion", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(chkValidacion.isSelected()));

            /**
             * ******************************************REPOSITORIO********************************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/TipoServidor", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(((TipoServidorBDIntermedia) cmbBDIntermediaTipoServidor.getSelectedItem()).getValor()));

            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/ServidorBD", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtPersistBDServidor.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/Puerto", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtPersistPuerto.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/BaseDatos", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtPersistBaseDatos.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/User", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtPersistBDUsuario.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Repositorio/Password", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtPersistBDPassword.getPassword())));

            /**
             * *******************************************PROXY****************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/UsarProxy", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.setTextContent(String.valueOf(chbProxyAutenUsar.isSelected()));

            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Servidor", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.setTextContent(txtProxyServer.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Puerto", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.setTextContent(txtProxyPuerto.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/RequAuth", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.setTextContent(String.valueOf(chbProxyAutenUsar.isSelected()));

            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/User", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtProxyUser.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Proxy/Pass", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtProxyPass.getPassword())));

            /**
             * **************************************DIRECTORIO****************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/Directorio/Adjuntos", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtDirAdjuntos.getPassword())));

            /**
             * ************************************TIEMPOS*****************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RQTimeOut", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtRQTimeOut.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RQInterval", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtRQInterval.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RSTimeOut", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtRSTimeOut.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Tiempos/RSInterval", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtRSInterval.getText());

            /**
             * ********************************WEBSERVICE*********************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSClave", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtPassWS.getPassword())));

            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSLocation", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.setTextContent(txtPathWS.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSUsuario", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtUserWS.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSTiempoEsperaPublic", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(jTxfTiempoEsperaPublic.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSIntervaloRepeticionPublic", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.setTextContent(jTxfIntervaloRepeticionPublic.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/WebService/WSTiempoPublicacionPublic", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(jTxfTiempoPublicacionPublic.getText());

            /**
             * **************************EMISOR
             * ELECTRONICO*************************
             */
            String pathJAR = System.getProperty("user.dir");
            String sRutaConfigGeneral[] = pathJAR.split("[\\\\/]", -1);
            pathJAR = "";
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                pathJAR = pathJAR + sRutaConfigGeneral[i] + File.separator;
            }

            nodo = (Node) xPath.evaluate("/Configuracion/EmisorElectronico/RS", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent("205454844881/SUNAT");

            nodo = (Node) xPath.evaluate("/Configuracion/EmisorElectronico/SenderLogo", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\COMPANY_LOGO.jpg");

            /**
             * *********************CERTIFICADO
             * DIGITAL****************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/CertificadoDigital/RutaCertificado", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtRutaCertificado.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/CertificadoDigital/PasswordCertificado", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtClaveCertificado.getPassword())));

            nodo = (Node) xPath.evaluate("/Configuracion/CertificadoDigital/ProveedorKeystore", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent("SunJSSE");

            nodo = (Node) xPath.evaluate("/Configuracion/CertificadoDigital/TipoKeystore", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent("Pkcs12");

            /**
             * ***************************CODIGO
             * BARRA******************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/Impresion/PDF", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(cmbImpresion.getSelectedItem().toString());

            /**
             * ***************************PDF******************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/Pdf/PerceptionReportPath", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\PdfPlantilla\\Template_Default\\perceptionDocument.jrxml");

            nodo = (Node) xPath.evaluate("/Configuracion/Pdf/RetentionReportPath", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\PdfPlantilla\\Template_Default\\retentionDocument.jrxml");

            nodo = (Node) xPath.evaluate("/Configuracion/Pdf/InvoiceReportPath", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\PdfPlantilla\\Template_Default\\invoiceDocument.jrxml");

            nodo = (Node) xPath.evaluate("/Configuracion/Pdf/BoletaReportPath", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\PdfPlantilla\\Template_Default\\boletaDocument.jrxml");

            nodo = (Node) xPath.evaluate("/Configuracion/Pdf/CreditNoteReportPath", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\PdfPlantilla\\Template_Default\\creditNoteDocument.jrxml");

            nodo = (Node) xPath.evaluate("/Configuracion/Pdf/DebitNoteReportPath", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\PdfPlantilla\\Template_Default\\debitNoteDocument.jrxml");

            nodo = (Node) xPath.evaluate("/Configuracion/Pdf/LegendSubReportPath", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\PdfPlantilla\\Template_Default\\legendReport.jasper");

            nodo = (Node) xPath.evaluate("/Configuracion/Pdf/RemissionGuideReportPath", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(pathJAR + "Recursos\\PdfPlantilla\\Template_Default\\remissionguideDocument.jrxml");

            /**
             * ************************CONECTOR
             * SUNAT*****************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/PFEConnector/Tipo", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(((TipoPFEConnector) cmbTipoConectorSunat.getSelectedItem()).getValor()));

            /**
             * ************************RESUMEN
             * DIARIO*****************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/ResumenDiario/Fecha", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtFecha.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/ResumenDiario/Hora", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtHoraResumen.getText());

            /**
             * ************************PUERTOS DE
             * CONEXION*****************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Configurador", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(PuertoVSCPE.PuertoAbierto_Configurador + "");

            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Request", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(PuertoVSCPE.PuertoAbierto_Request + "");

            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Response", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(PuertoVSCPE.PuertoAbierto_Response + "");

            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Resumen", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(PuertoVSCPE.PuertoAbierto_Resumen + "");

            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_PublicWS", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(PuertoVSCPE.PuertoAbierto_PublicWS + "");

            nodo = (Node) xPath.evaluate("/Configuracion/General/PuertoVS_Extractor", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(PuertoVSCPE.PuertoAbierto_Extractor + "");

            /**
             * ************************SUNAT*****************************
             */
            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/ClienteSunat", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            if (nodo.getTextContent() != null && !"".equals(nodo.getTextContent())) {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                nodo.setTextContent(p);
            } else {
                nodo.setTextContent("test");
            }
            
            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/Ambiente", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            if (nodo.getTextContent() != null && !"".equals(nodo.getTextContent())) {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                nodo.setTextContent(p);
            } else {
                nodo.setTextContent("2");
            }

            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/MostrarSoap", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            if (nodo.getTextContent() != null && !"".equals(nodo.getTextContent())) {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                nodo.setTextContent(p);
            } else {
                nodo.setTextContent("true");
            }

            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/IntegracionWS", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(((TipoIntegracionWS) cmbTipoIntegracionWS.getSelectedItem()).getValor()));

            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/Usuario/UsuarioSol", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtUserSunat.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/Sunat/Usuario/ClaveSol", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtPassSunat.getPassword())));
            
            /*******************    CONEXIÓN GUIAS     ***************************/
            nodo = (Node) xPath.evaluate("/Configuracion/SunatGuias/IntegracionGuias", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(String.valueOf(((TipoIntegracionWS) cmbTipoIntegracionGuias.getSelectedItem()).getValor()));

            nodo = (Node) xPath.evaluate("/Configuracion/SunatGuias/Usuario/UsuarioGuias", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtUserGuias.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/SunatGuias/Usuario/ClaveGuias", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtPassGuias.getPassword())));
            
            nodo = (Node) xPath.evaluate("/Configuracion/SunatGuias/Usuario/ClientID", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtClientID.getText());
            
            nodo = (Node) xPath.evaluate("/Configuracion/SunatGuias/Usuario/SecretID", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("true");
            nodo.setTextContent(Criptor.Encriptar(new String(txtSecretID.getPassword())));
            
            nodo = (Node) xPath.evaluate("/Configuracion/SunatGuias/Usuario/Scope", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtScope.getText());

            /*******************    VERSION UBL     ***************************/

            nodo = (Node) xPath.evaluate("/Configuracion/VersionUBL/Factura", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtversionfactura.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/VersionUBL/Boleta", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtversionboleta.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/VersionUBL/NotaCredito", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtversioncredito.getText());

            nodo = (Node) xPath.evaluate("/Configuracion/VersionUBL/NotaDebito", doc.getDocumentElement(), XPathConstants.NODE);
            nodo.getAttributes().getNamedItem("encriptado").setNodeValue("false");
            nodo.setTextContent(txtversiondebito.getText());

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            pathJAR = pathJAR + "Config.xml";
            Result output = new StreamResult(new File(pathJAR));
            Source input = new DOMSource(doc);

            transformer.transform(input, output);
            //GUARDAR REGLAS CREADAS
            int bandValidarReglas = validarRegistrosTablaReglas();
            boolean bandGuardarReglas = false;
            if (bandValidarReglas == 1) {
                bandGuardarReglas = limpiarReglasBD();
            } else if (bandValidarReglas == 2) {
                limpiarReglasBD();
                bandGuardarReglas = guardarReglasBD();
            }
            //GUARDAR NUEVAS BD SAP PARA EXTRACCIONES
            //HQUISPE
            int bandValidarBdsAnadidas = validarRegistrosTablaAnadirBD();
            boolean bandGuardarBDsAñadidas = false;
            if (bandValidarBdsAnadidas == 1) {
                bandGuardarBDsAñadidas = limpiarBDAnadidas();
            } else if (bandValidarBdsAnadidas == 2) {
                List<BdsMaestras> bdsMaestras = BdsMaestrasBL.listarBDAnadidas();
                limpiarBDAnadidas();
                bandGuardarBDsAñadidas = guardarBDAñadidas(bdsMaestras);
            }
            if (bandGuardarReglas && bandGuardarBDsAñadidas) {
                JOptionPane.showMessageDialog(this, "Datos actualizados correctamente");
                this.dispose();
            }
            jChkSeleccionarTodo.setSelected(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros. {0}", ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage());
            this.dispose();
        }

    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

    }//GEN-LAST:event_formWindowClosing

    private void btnAdjuntosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdjuntosActionPerformed
        String dirAdjuntos = txtDirAdjuntos.getText();
        JFileChooser fc = new JFileChooser(dirAdjuntos);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setMultiSelectionEnabled(false);
        int ret = fc.showDialog(this, "Seleccionar");
        if (ret == JFileChooser.APPROVE_OPTION) {
            txtDirAdjuntos.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_btnAdjuntosActionPerformed

    private void txtTiempoActualizacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTiempoActualizacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTiempoActualizacionActionPerformed

    private void chkPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkPDFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkPDFActionPerformed

    private void chkSunatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSunatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chkSunatActionPerformed

    private void btnAdjuntoCertificadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdjuntoCertificadoActionPerformed
        String dirAdjuntos = txtRutaCertificado.getText();
        JFileChooser fc = new JFileChooser(dirAdjuntos);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        int ret = fc.showDialog(this, "Seleccionar");
        if (ret == JFileChooser.APPROVE_OPTION) {
            txtRutaCertificado.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_btnAdjuntoCertificadoActionPerformed

    private void txtProxyUserKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProxyUserKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtProxyUserKeyReleased

    private void chbProxyUsarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbProxyUsarItemStateChanged
        if (chbProxyUsar.isSelected()) {
            txtProxyServer.setEnabled(true);
            txtProxyPuerto.setEnabled(true);
        } else {
            txtProxyServer.setEnabled(false);
            txtProxyPuerto.setEnabled(false);
        }
    }//GEN-LAST:event_chbProxyUsarItemStateChanged

    private void chbProxyAutenUsarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbProxyAutenUsarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chbProxyAutenUsarActionPerformed

    private void chbProxyAutenUsarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbProxyAutenUsarItemStateChanged
        if (chbProxyAutenUsar.isSelected() && chbProxyUsar.isSelected()) {
            txtProxyUser.setEnabled(true);
            txtProxyPass.setEnabled(true);
        } else {
            txtProxyUser.setEnabled(false);
            txtProxyPass.setEnabled(false);
        }
    }//GEN-LAST:event_chbProxyAutenUsarItemStateChanged

    private void cmbBDIntermediaTipoServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBDIntermediaTipoServidorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbBDIntermediaTipoServidorActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Connection con = null;
        ConexionMSSQL.BASEDATOS = txtPersistBaseDatos.getText();
        ConexionMSSQL.PUERTO = txtPersistPuerto.getText();
        ConexionMSSQL.PASS = new String(txtPersistBDPassword.getPassword());
        ConexionMSSQL.PUERTO = txtPersistPuerto.getText();
        ConexionMSSQL.USER = txtPersistBDUsuario.getText();
        ConexionMSSQL.SERVIDOR = txtPersistBDServidor.getText();
        con = ConexionMSSQL.conectar();
        if (con != null) {
            ConexionMSSQL.desconectar(con);
            System.out.println("Si existe");
            int n = JOptionPane.showConfirmDialog(
                    null,
                    "Desea eliminar su base de datos?",
                    "Ventura Soluciones ADVERTENCIA",
                    JOptionPane.YES_NO_OPTION);
            if (n != JOptionPane.YES_OPTION) {
            } else {
                ConexionMSSQL.BASEDATOS = "master";
                con = ConexionMSSQL.conectar();
                if (con != null) {
                    if (ConexionMSSQL.ejecutarBaseDatos("USE MASTER", con) == 1) {
                        ConexionMSSQL.desconectar(con);
                        InputStream inputStream = getClass().getResourceAsStream("/org/ventura/cpe/recursos/bpvs_FE_BaseDatos.sql");
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        String line = "";
                        String batch = "";
                        try {
                            while ((line = br.readLine()) != null) {
                                if (line.compareTo("GO") == 0) {
                                    System.out.println(batch);
                                    ConexionMSSQL.ejecutarBaseDatos(batch, con);
                                    batch = "";
                                } else {
                                    batch += line + " ";
                                }
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(FrmConfig.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(
                                    null, ex, "Ventura Soluciones", JOptionPane.ERROR_MESSAGE);
                        }
                        ConexionMSSQL.BASEDATOS = txtPersistBaseDatos.getText();
                        con = ConexionMSSQL.conectar();
                        if (con != null) {

                            if (ConexionMSSQL.ejecutarBaseDatos("USE VSCPEBD", con) == 1) {
                                ConexionMSSQL.desconectar(con);
                                InputStream inputStream1 = getClass().getResourceAsStream("/org/ventura/cpe/recursos/bpvs_FE_procedure.sql");
                                BufferedReader br1 = new BufferedReader(new InputStreamReader(inputStream1));
                                String line1 = "";
                                String batch1 = "";
                                try {
                                    while ((line1 = br1.readLine()) != null) {
                                        if (line1.compareTo("GO") == 0) {
                                            System.out.println(batch1);
                                            ConexionMSSQL.ejecutarBaseDatos(batch1, con);
                                            batch1 = "";
                                        } else {
                                            batch1 += line1 + " ";
                                        }
                                    }
                                } catch (IOException ex) {
                                    Logger.getLogger(FrmConfig.class.getName()).log(Level.SEVERE, null, ex);
                                    JOptionPane.showMessageDialog(
                                            null, ex, "Ventura Soluciones", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                            JOptionPane.showMessageDialog(
                                    null, "Se ha creado correctamente la base de datos VSCPE", "Ventura Soluciones", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(
                                    null, "No se ha podido conectar", "Ventura Soluciones", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null, "No se ha podido conectar", "Ventura Soluciones", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            ConexionMSSQL.BASEDATOS = "master";
            con = ConexionMSSQL.conectar();
            ConexionMSSQL.desconectar(con);
            InputStream inputStream = getClass().getResourceAsStream("/org/ventura/cpe/recursos/bpvs_FE_BaseDatos.sql");
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String batch = "";
            try {
                while ((line = br.readLine()) != null) {
                    if (line.compareTo("GO") == 0) {
                        System.out.println(batch);
                        ConexionMSSQL.ejecutarBaseDatos(batch, con);
                        batch = "";
                    } else {
                        batch += line + " ";
                    }
                }
                JOptionPane.showMessageDialog(
                        null, "Se creo correctamente la base de datos", "Ventura Soluciones", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                Logger.getLogger(FrmConfig.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        null, ex, "Ventura Soluciones", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtPersistPuertoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPersistPuertoKeyReleased
        construirCadena();
    }//GEN-LAST:event_txtPersistPuertoKeyReleased

    private void txtPersistBaseDatosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPersistBaseDatosKeyReleased
        construirCadena();
    }//GEN-LAST:event_txtPersistBaseDatosKeyReleased

    private void txtPersistBDServidorKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPersistBDServidorKeyReleased
        construirCadena();
    }//GEN-LAST:event_txtPersistBDServidorKeyReleased

    private void cmbTipoConectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipoConectorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbTipoConectorActionPerformed

    private void txtUserWSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserWSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUserWSActionPerformed

    private void jBtnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAgregarActionPerformed
        DefaultTableModel dtm = null;
        dtm = (DefaultTableModel) jTblReglasIdioma.getModel();
        dtm.addRow(new Object[]{"", "", "", "", "", "-->", "", false});
    }//GEN-LAST:event_jBtnAgregarActionPerformed

    private void jTblReglasIdiomaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTblReglasIdiomaMouseClicked

    }//GEN-LAST:event_jTblReglasIdiomaMouseClicked

    private void jBtnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarActionPerformed
        DefaultTableModel dtm = null;
        dtm = (DefaultTableModel) jTblReglasIdioma.getModel();
        List<Integer> listKeyEliminar = keyReglasSelected(dtm, 7);
        if (!listKeyEliminar.isEmpty()) {
            for (Integer keyEliminar : listKeyEliminar) {
                for (int i = 0; i < dtm.getRowCount(); i++) {
                    if (dtm.getValueAt(i, 0).equals(keyEliminar)) {
                        dtm.removeRow(i);
                        i = dtm.getRowCount();
                    }
                }
            }
        }
        jChkSeleccionarTodo.setSelected(false);
    }//GEN-LAST:event_jBtnEliminarActionPerformed

    private void jChkSeleccionarTodoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jChkSeleccionarTodoMouseClicked
        if (jChkSeleccionarTodo.isSelected()) {
            for (int i = 0; i < jTblReglasIdioma.getRowCount(); i++) {
                jTblReglasIdioma.getModel().setValueAt(true, i, 7);
            }
        } else {
            for (int i = 0; i < jTblReglasIdioma.getRowCount(); i++) {
                jTblReglasIdioma.getModel().setValueAt(false, i, 7);
            }
        }
    }//GEN-LAST:event_jChkSeleccionarTodoMouseClicked

    private void cmbTipoConectorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbTipoConectorMouseClicked

    }//GEN-LAST:event_cmbTipoConectorMouseClicked

    private void cmbTipoConectorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTipoConectorItemStateChanged
        switch (cmbTipoConector.getSelectedIndex()) {
            case 0:
                txtSAPServer.setEnabled(true);
                txtSAPUsuario.setEnabled(true);
                txtSAPPassword.setEnabled(true);
                jTxfRutaArchivos.setEnabled(true);
                jTxfNombreSociedad.setEnabled(false);
                break;
            case 1:
                txtSAPServer.setEnabled(true);
                txtSAPUsuario.setEnabled(true);
                txtSAPPassword.setEnabled(true);
                jTxfRutaArchivos.setEnabled(true);
                jTxfNombreSociedad.setEnabled(true);
                break;
            default:
                txtSAPServer.setEnabled(false);
                txtSAPUsuario.setEnabled(false);
                txtSAPPassword.setEnabled(false);
                jTxfRutaArchivos.setEnabled(true);
                jTxfNombreSociedad.setEnabled(true);
                break;
        }
    }//GEN-LAST:event_cmbTipoConectorItemStateChanged

    private void jTblBDMaestrasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTblBDMaestrasMouseClicked

    }//GEN-LAST:event_jTblBDMaestrasMouseClicked

    private void jBtnAgregarBDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnAgregarBDActionPerformed

        frmBDSecundario frm = new frmBDSecundario(this,true);
        frm.pack();
        frm.setLocationRelativeTo(null);
        frm.setVisible(true);
        BdsMaestras o =  frm.getBdsMaestras();
        if(o !=null)
        {
            DefaultTableModel dtm = null;
            dtm = (DefaultTableModel) jTblBDMaestras.getModel();

            dtm.addRow(new Object[]{o.getBDId(),
                                        o.getbDServidor(),
                                        o.getBDNombre(),
                                        o.getBDUsuario(),
                                        o.getBDClave(),
                                        o.getERPUsuario(),
                                        o.geteRPClave(),
                                        o.getRucSociedad(),
                                        o.getRutaCD(),
                                        o.getPasswordCD(),
                                        o.getUsuarioSec(),
                                        o.getPasswordSec(),
                                        o.getUsuarioGuias(),
                                        o.getPasswordGuias(),
                                        o.getWebservicePublicacion(),
                                        o.getUsuarioWebService(),
                                        o.getPasswordWebService(),
                                        o.getLogoSociedad(),
                                        o.getServidorLicencia(),
                                        o.getTipoServidor(),
                                        o.getTipoERP(),
                                        o.getRutaArchivos(),
                                        o.getTipoIntegracionSunat(),
                                        o.getTipoIntegracionGuias(),
                                        o.getClientID(),
                                        o.getSecretID(),
                                        o.getScope(),
                                        false});

        }
    }//GEN-LAST:event_jBtnAgregarBDActionPerformed

    private void jBtnEliminarBDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBtnEliminarBDActionPerformed
        DefaultTableModel dtm = null;
        dtm = (DefaultTableModel) jTblBDMaestras.getModel();
        List<Integer> listBdsEliminar = keyBdsSelected(dtm, 18);
        if (!listBdsEliminar.isEmpty()) {
            for (Integer keyEliminar : listBdsEliminar) {
                for (int i = 0; i < dtm.getRowCount(); i++) {
                    if (dtm.getValueAt(i, 0).equals(keyEliminar + "")) {
                        dtm.removeRow(i);
                        i = dtm.getRowCount();
                    }
                }
            }
        }
        jChkSeleccionarTodoBD.setSelected(false);
    }//GEN-LAST:event_jBtnEliminarBDActionPerformed

    private void jChkSeleccionarTodoBDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jChkSeleccionarTodoBDMouseClicked
        if (jChkSeleccionarTodoBD.isSelected()) {
            for (int i = 0; i < jTblBDMaestras.getRowCount(); i++) {
                jTblBDMaestras.getModel().setValueAt(true, i, 20);
            }
        } else {
            for (int i = 0; i < jTblBDMaestras.getRowCount(); i++) {
                jTblBDMaestras.getModel().setValueAt(false, i, 20);
            }
        }
    }//GEN-LAST:event_jChkSeleccionarTodoBDMouseClicked

    private void cmbBDTipoServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbBDTipoServidorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbBDTipoServidorActionPerformed

    private List<Integer> keyReglasSelected(DefaultTableModel dtm, int nroColumna) {
        List<Integer> listKeyEliminar = new ArrayList<>();
        for (int i = dtm.getRowCount() - 1; i > -1; i--) {
            if (dtm.getValueAt(i, nroColumna).equals(true)) {
                if (!dtm.getValueAt(i, 0).equals("") && dtm.getValueAt(i, 0) != null) {
                    listKeyEliminar.add((Integer) dtm.getValueAt(i, 0));
                } else {
                    dtm.removeRow(i);
                }
            }
        }
        return listKeyEliminar;
    }

    private List<Integer> keyBdsSelected(DefaultTableModel dtm, int nroColumna) {
        List<Integer> listKeyEliminar = new ArrayList<>();
        for (int i = dtm.getRowCount() - 1; i > -1; i--) {
            if (dtm.getValueAt(i, nroColumna).equals(true)) {
                if (!dtm.getValueAt(i, 0).equals("") && dtm.getValueAt(i, 0) != null) {
                    listKeyEliminar.add(Integer.parseInt((String) dtm.getValueAt(i, 0)));
                } else {
                    dtm.removeRow(i);
                }
            }
        }
        return listKeyEliminar;
    }

    static JSONParser parser = null;

    public String cargarFechaResumen() {
        return txtFecha.getText();
    }

    private void CargarParametros() {
        Node nodo;
        try {
            String p;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            String sRutaConfigReal = System.getProperty("user.dir");
            String sRutaConfigGeneral[] = sRutaConfigReal.split("[\\\\/]", -1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";

            File f = new File(sRutaConfigReal);
            if (!f.exists()) {
                f.createNewFile();
                doc = db.newDocument();
            } else {
                try {
                    doc = db.parse(f);
                } catch (IOException | SAXException ex) {
                    LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al leer config.xml. {0}", ex.getMessage());
                    int ret = JOptionPane.showConfirmDialog(this, "No es posible leer el archivo config.xml porque no tiene el formato correcto."
                                    + "\n¿Desea sobrescribir el archivo actual con uno nuevo?",
                            "Error en Config.xml", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (ret == JOptionPane.YES_OPTION) {
                        doc = db.newDocument();
                    } else {
                        dispose();
                        System.exit(0);
                    }
                }
            }

            //Gestor Tiempo
            nodo = getTag(doc, null, "/Configuracion/GestorTiempo/Tiempo", true);
            txtTiempoActualizacion.setText(nodo.getTextContent());

            /**
             * ****************************ERP***************************
             */
            nodo = getTag(doc, null, "/Configuracion/ERP/ServidorLicencias", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtSAPServer.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ERP/TipoServidor", true);
            try {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                cmbBDTipoServidor.getModel().setSelectedItem(TipoServidor.getByValor(p));
            } catch (DOMException ex) {
                cmbBDTipoServidor.setSelectedIndex(0);
            }

            nodo = getTag(doc, null, "/Configuracion/ERP/TipoConector", true);
            try {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                cmbTipoConector.getModel().setSelectedItem(TipoConector.getByValor(Integer.parseInt(p)));
            } catch (DOMException | NumberFormatException ex) {
                cmbTipoConector.setSelectedIndex(0);
            }

            nodo = getTag(doc, null, "/Configuracion/ERP/ServidorBD", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtBDNombreServidor.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ERP/BaseDeDatos", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtBaseDatos.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ERP/User", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtBDUsuario.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ERP/Password", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtBDPassword.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ERP/ERPUser", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtSAPUsuario.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ERP/ERPPass", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtSAPPassword.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ERP/ERPRutaArchivos", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            jTxfRutaArchivos.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ERP/ERPNombreSociedad", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            jTxfNombreSociedad.setText(p);

            /**
             * ******************************************** USO
             * SUNAT***************************************************
             */
            nodo = getTag(doc, null, "/Configuracion/UsoSunat/WS", true);
            chkSunat.setSelected(Boolean.parseBoolean(nodo.getTextContent()));

            nodo = getTag(doc, null, "/Configuracion/UsoSunat/PDF", true);
            chkPDF.setSelected(Boolean.parseBoolean(nodo.getTextContent()));

            nodo = getTag(doc, null, "/Configuracion/UsoSunat/Validacion", true);
            chkValidacion.setSelected(Boolean.parseBoolean(nodo.getTextContent()));

            /**
             * *********************REPOSITORIO******************************
             */
            nodo = getTag(doc, null, "/Configuracion/Repositorio/TipoServidor", true);
            try {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                cmbBDIntermediaTipoServidor.getModel().setSelectedItem(TipoServidorBDIntermedia.getByValor(p));
            } catch (DOMException | NumberFormatException ex) {
                cmbBDIntermediaTipoServidor.setSelectedIndex(0);
            }

            nodo = getTag(doc, null, "/Configuracion/Repositorio/ServidorBD", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPersistBDServidor.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Repositorio/Puerto", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPersistPuerto.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Repositorio/BaseDatos", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPersistBaseDatos.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Repositorio/User", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPersistBDUsuario.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Repositorio/Password", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPersistBDPassword.setText(p);
            construirCadena();

            /**
             * *****************************PROXY********************************
             */
            nodo = getTag(doc, null, "/Configuracion/Proxy/UsarProxy", true);
            chbProxyUsar.setSelected(Boolean.parseBoolean(nodo.getTextContent()));

            nodo = getTag(doc, null, "/Configuracion/Proxy/Servidor", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtProxyServer.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Proxy/Puerto", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtProxyPuerto.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Proxy/RequAuth", true);
            chbProxyAutenUsar.setSelected(Boolean.parseBoolean(nodo.getTextContent()));

            nodo = getTag(doc, null, "/Configuracion/Proxy/User", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtProxyUser.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Proxy/Pass", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtProxyPass.setText(p);

            /**
             * ****************************DIRECTORIO************************************
             */
            nodo = getTag(doc, null, "/Configuracion/Directorio/Adjuntos", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtDirAdjuntos.setText(p);

            /**
             * *************************TIEMPOS****************************************
             */
            nodo = getTag(doc, null, "/Configuracion/Tiempos/RQTimeOut", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtRQTimeOut.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Tiempos/RQInterval", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtRQInterval.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Tiempos/RSTimeOut", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtRSTimeOut.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Tiempos/RSInterval", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtRSInterval.setText(p);

            /**
             * ***********************************WEB
             * SERVICE*****************************************
             */
            nodo = getTag(doc, null, "/Configuracion/WebService/WSUsuario", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtUserWS.setText(p);

            nodo = getTag(doc, null, "/Configuracion/WebService/WSClave", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPassWS.setText(p);

            nodo = getTag(doc, null, "/Configuracion/WebService/WSLocation", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPathWS.setText(p);

            nodo = getTag(doc, null, "/Configuracion/WebService/WSTiempoEsperaPublic", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            jTxfTiempoEsperaPublic.setText(p);

            nodo = getTag(doc, null, "/Configuracion/WebService/WSIntervaloRepeticionPublic", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            jTxfIntervaloRepeticionPublic.setText(p);

            nodo = getTag(doc, null, "/Configuracion/WebService/WSTiempoPublicacionPublic", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            jTxfTiempoPublicacionPublic.setText(p);

            /**
             * ****************************EMISOR
             * ELECTRONICO**********************************************
             */
            nodo = getTag(doc, null, "/Configuracion/EmisorElectronico/RS", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/EmisorElectronico/SenderLogo", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Impresion/PDF", true);
            try {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                cmbImpresion.getModel().setSelectedItem(p);
            } catch (DOMException | NumberFormatException ex) {
                cmbImpresion.setSelectedIndex(0);
            }

            /**
             * *************************************PDF******************************
             */
            nodo = getTag(doc, null, "/Configuracion/Pdf/InvoiceReportPath", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Pdf/BoletaReportPath", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Pdf/CreditNoteReportPath", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Pdf/PerceptionReportPath", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Pdf/RetentionReportPath", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Pdf/DebitNoteReportPath", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Pdf/LegendSubReportPath", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Pdf/RemissionGuideReportPath", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            /**
             * **************************CERTIFICADO*****************************************
             */
            nodo = getTag(doc, null, "/Configuracion/CertificadoDigital/RutaCertificado", true);
            txtRutaCertificado.setText(nodo.getTextContent());

            nodo = getTag(doc, null, "/Configuracion/CertificadoDigital/PasswordCertificado", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtClaveCertificado.setText(p);

            nodo = getTag(doc, null, "/Configuracion/CertificadoDigital/ProveedorKeystore", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/CertificadoDigital/TipoKeystore", true);
            p = nodo.getTextContent();
            nodo.setTextContent(p);

            /**
             * *************************CONECTOR
             * SUNAT****************************************
             */
            nodo = getTag(doc, null, "/Configuracion/PFEConnector/Tipo", true);
            try {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                cmbTipoConectorSunat.getModel().setSelectedItem(TipoPFEConnector.getByValor(p));
            } catch (DOMException | NumberFormatException ex) {
                cmbTipoConectorSunat.setSelectedIndex(0);
            }
            /**
             * ****************************RESUMEN***********************************************
             */

            nodo = getTag(doc, null, "/Configuracion/ResumenDiario/Hora", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtHoraResumen.setText(p);

            nodo = getTag(doc, null, "/Configuracion/ResumenDiario/Fecha", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtFecha.setText(p);

            /**
             * ****************************PUERTOS DE
             * CONEXION****************************************
             */
            nodo = getTag(doc, null, "/Configuracion/General/PuertoVS_Configurador", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;

            nodo = getTag(doc, null, "/Configuracion/General/PuertoVS_Request", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;

            nodo = getTag(doc, null, "/Configuracion/General/PuertoVS_Response", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;

            nodo = getTag(doc, null, "/Configuracion/General/PuertoVS_Resumen", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;

            nodo = getTag(doc, null, "/Configuracion/General/PuertoVS_PublicWS", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;

            nodo = getTag(doc, null, "/Configuracion/General/PuertoVS_Extractor", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;

            /**
             * ************************SUNAT*****************************
             */
            nodo = getTag(doc, null, "/Configuracion/Sunat/MostrarSoap", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Sunat/ClienteSunat", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);
            

            nodo = getTag(doc, null, "/Configuracion/Sunat/Ambiente", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            nodo.setTextContent(p);
            
            

//            nodo = getTag(doc, null, "/Configuracion/Sunat/ClienteSunat", true);
//            p = nodo.getTextContent();
//            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
//            nodo.setTextContent(p);

//            nodo = getTag(doc, null, "/Configuracion/Sunat/MostrarSoap", true);
//            p = nodo.getTextContent();
//            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
//            nodo.setTextContent(p);

            nodo = getTag(doc, null, "/Configuracion/Sunat/IntegracionWS", true);
            try {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                cmbTipoIntegracionWS.getModel().setSelectedItem(TipoIntegracionWS.getByValor(p));
            } catch (DOMException | NumberFormatException ex) {
                cmbTipoConectorSunat.setSelectedIndex(0);
            }
            
            nodo = getTag(doc, null, "/Configuracion/Sunat/Usuario/UsuarioSol", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtUserSunat.setText(p);

            nodo = getTag(doc, null, "/Configuracion/Sunat/Usuario/ClaveSol", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPassSunat.setText(p);

            /*******************    CONEXIÓN GUIAS     ***************************/
            nodo = getTag(doc, null, "/Configuracion/SunatGuias/IntegracionGuias", true);
            try {
                p = nodo.getTextContent();
                p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
                cmbTipoIntegracionGuias.getModel().setSelectedItem(TipoIntegracionWS.getByValor(p));
            } catch (DOMException | NumberFormatException ex) {
                cmbTipoConectorSunat.setSelectedIndex(0);
            }
            nodo = getTag(doc, null, "/Configuracion/SunatGuias/Usuario/UsuarioGuias", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtUserGuias.setText(p);

            nodo = getTag(doc, null, "/Configuracion/SunatGuias/Usuario/ClaveGuias", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtPassGuias.setText(p);
           
            nodo = getTag(doc, null, "/Configuracion/SunatGuias/Usuario/ClientID", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtClientID.setText(p);
            
            nodo = getTag(doc, null, "/Configuracion/SunatGuias/Usuario/SecretID", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtSecretID.setText(p);
            
            nodo = getTag(doc, null, "/Configuracion/SunatGuias/Usuario/Scope", true);
            p = nodo.getTextContent();
            p = nodo.getAttributes().getNamedItem("encriptado").getNodeValue().compareTo("true") == 0 ? Criptor.Desencriptar(p) : p;
            txtScope.setText(p);

            /*************************  VERSION UBL  ************************/
            nodo = getTag(doc, null, "/Configuracion/VersionUBL/Factura", true);
            txtversionfactura.setText(nodo.getTextContent());

            nodo = getTag(doc, null, "/Configuracion/VersionUBL/Boleta", true);
            txtversionboleta.setText(nodo.getTextContent());

            nodo = getTag(doc, null, "/Configuracion/VersionUBL/NotaCredito", true);
            txtversioncredito.setText(nodo.getTextContent());

            nodo = getTag(doc, null, "/Configuracion/VersionUBL/NotaDebito", true);
            txtversiondebito.setText(nodo.getTextContent());


        } catch (ParserConfigurationException | IOException | HeadlessException | DOMException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros.  {0}", ex.getMessage());
            dispose();
        }
    }

    private void construirCadena() {
        String Sr = txtPersistBDServidor.getText();
        String Pt = txtPersistPuerto.getText();
        String Bd = txtPersistBaseDatos.getText();

        lblCadena.setText("jdbc:sqlserver://" + Sr + ":" + Pt + ";databaseName=" + Bd);
    }

    private Node getTag(Document doc, Node padre, String ruta, boolean encriptable) {
        Node nodo = null;
        String[] partes = ruta.substring(1).split("/");
        if (partes.length == 0) {
            return null;
        } else {
            String nombre = partes[0];
            NodeList nodos = (padre == null ? doc.getChildNodes() : padre.getChildNodes());
            //NodeList nodos = padre.getChildNodes();
            for (int i = 0; i < nodos.getLength(); i++) {
                Node n = nodos.item(i);
                if (n.getNodeType() == 1 && n.getNodeName().compareTo(nombre) == 0) {
                    nodo = n;
                    break;
                }
            }
            if (nodo == null) {
                //nodo = padre.appendChild(doc.createElement(nombre));
                if (padre == null) {
                    nodo = doc.appendChild(doc.createElement(nombre));
                } else {
                    nodo = padre.appendChild(doc.createElement(nombre));
                }
            }
            if (partes.length > 1) {
                ruta = ruta.substring(nombre.length() + 1);
                return getTag(doc, nodo, ruta, encriptable);
            } else {
                if (encriptable) {
                    Attr prop = (Attr) nodo.getAttributes().getNamedItem("encriptado");
                    if (prop == null) {
                        prop = doc.createAttribute("encriptado");
                        nodo.getAttributes().setNamedItem(prop);
                    }
                    if (prop.getNodeValue() == null || prop.getNodeValue().isEmpty()) {
                        prop.setNodeValue("false");
                    }
                }
                return nodo;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                } else {
                    javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmConfig.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmConfig().setVisible(true);
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdjuntoCertificado;
    private javax.swing.JButton btnAdjuntos;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JCheckBox chbProxyAutenUsar;
    private javax.swing.JCheckBox chbProxyUsar;
    private javax.swing.JCheckBox chkPDF;
    private javax.swing.JCheckBox chkSunat;
    private javax.swing.JCheckBox chkSunat1;
    private javax.swing.JCheckBox chkValidacion;
    private javax.swing.JComboBox cmbBDIntermediaTipoServidor;
    private javax.swing.JComboBox cmbBDTipoServidor;
    private javax.swing.JComboBox cmbImpresion;
    private javax.swing.JComboBox cmbTipoConector;
    private javax.swing.JComboBox cmbTipoConectorSunat;
    private javax.swing.JComboBox cmbTipoIntegracionGuias;
    private javax.swing.JComboBox cmbTipoIntegracionWS;
    private javax.swing.JButton jBtnAgregar;
    private javax.swing.JButton jBtnAgregarBD;
    private javax.swing.JButton jBtnEliminar;
    private javax.swing.JButton jBtnEliminarBD;
    private javax.swing.JButton jButton1;
    private javax.swing.JCheckBox jChkSeleccionarTodo;
    private javax.swing.JCheckBox jChkSeleccionarTodoBD;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLabel jLblRutaArchivos;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTblBDMaestras;
    private javax.swing.JTable jTblReglasIdioma;
    private javax.swing.JTextField jTxfIntervaloRepeticionPublic;
    private javax.swing.JTextField jTxfNombreSociedad;
    private javax.swing.JTextField jTxfRutaArchivos;
    private javax.swing.JTextField jTxfTiempoEsperaPublic;
    private javax.swing.JTextField jTxfTiempoPublicacionPublic;
    private javax.swing.JLabel lblCadena;
    private javax.swing.JTextField txtBDNombreServidor;
    private javax.swing.JPasswordField txtBDPassword;
    private javax.swing.JTextField txtBDUsuario;
    private javax.swing.JTextField txtBaseDatos;
    private javax.swing.JPasswordField txtClaveCertificado;
    private javax.swing.JTextField txtClientID;
    private javax.swing.JPasswordField txtDirAdjuntos;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtHoraResumen;
    private javax.swing.JPasswordField txtPassGuias;
    private javax.swing.JPasswordField txtPassSunat;
    private javax.swing.JPasswordField txtPassWS;
    private javax.swing.JTextField txtPathWS;
    private javax.swing.JPasswordField txtPersistBDPassword;
    private javax.swing.JTextField txtPersistBDServidor;
    private javax.swing.JTextField txtPersistBDUsuario;
    private javax.swing.JTextField txtPersistBaseDatos;
    private javax.swing.JTextField txtPersistPuerto;
    private javax.swing.JPasswordField txtProxyPass;
    private javax.swing.JTextField txtProxyPuerto;
    private javax.swing.JTextField txtProxyServer;
    private javax.swing.JTextField txtProxyUser;
    private javax.swing.JTextField txtRQInterval;
    private javax.swing.JTextField txtRQTimeOut;
    private javax.swing.JTextField txtRSInterval;
    private javax.swing.JTextField txtRSTimeOut;
    private javax.swing.JTextField txtRutaCertificado;
    private javax.swing.JPasswordField txtSAPPassword;
    private javax.swing.JTextField txtSAPServer;
    private javax.swing.JTextField txtSAPUsuario;
    private javax.swing.JTextField txtScope;
    private javax.swing.JPasswordField txtSecretID;
    private javax.swing.JTextField txtTiempoActualizacion;
    private javax.swing.JTextField txtUserGuias;
    private javax.swing.JTextField txtUserSunat;
    private javax.swing.JTextField txtUserWS;
    private javax.swing.JTextField txtversionboleta;
    private javax.swing.JTextField txtversioncredito;
    private javax.swing.JTextField txtversiondebito;
    private javax.swing.JTextField txtversionfactura;
    // End of variables declaration//GEN-END:variables
}
