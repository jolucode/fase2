/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.loaderbl;

import org.ventura.cpe.bl.BdsMaestrasBL;
import org.ventura.cpe.dao.DocumentoDAO;
import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.TransaccionResumenProp;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.utilidades.entidades.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;

/**
 * @author Jhony Monzalve
 */
public class Loader {

    public static int TipConector;

    public static String version = "Version 2.01.0.0004";

    public static boolean CargarParametros() {
        try {
            String sRutaConfigReal = System.getProperty("user.dir");
            String[] sRutaConfigGeneral = sRutaConfigReal.split("[\\\\/]", -1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";
            File file = new File(sRutaConfigReal);
            JAXBContext jaxbContext = JAXBContext.newInstance(Configuracion.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Configuracion configuracion = (Configuracion) jaxbUnmarshaller.unmarshal(file);
            boolean resultado = true;
            resultado = resultado && Repositorio(configuracion);
            resultado = resultado && ERP(configuracion);
            resultado = resultado && GestorTiempo(configuracion);
            resultado = resultado && Directorio(configuracion);
            resultado = resultado && Tiempos(configuracion);
            resultado = resultado && PuertoVS(configuracion);
            resultado = resultado && Resumen(configuracion);
            resultado = resultado && VersionUBL(configuracion);
            resultado = resultado && Sunat(configuracion);
            return resultado;
        } catch (JAXBException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros de config.xml. {0}", ex.getMessage());
            return false;
        }
    }

    public static boolean CargarParametroErp() {
        try {
            String sRutaConfigReal = System.getProperty("user.dir");
            String[] sRutaConfigGeneral = sRutaConfigReal.split("[\\\\/]", -1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";
            File file = new File(sRutaConfigReal);
            JAXBContext jaxbContext = JAXBContext.newInstance(org.ventura.cpe.loaderbl.Configuracion.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Configuracion configuracion = (org.ventura.cpe.loaderbl.Configuracion) jaxbUnmarshaller.unmarshal(file);
            return ERPConnection(configuracion);
        } catch (JAXBException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros de config.xml. {0}", ex.getMessage());
            return false;
        }
    }

    public static Optional<Configuracion> loadParametroErp() {
        try {
            String sRutaConfigReal = System.getProperty("user.dir");
            String[] sRutaConfigGeneral = sRutaConfigReal.split("[\\\\/]", -1);
            sRutaConfigReal = "";
            for (int i = 0; i < sRutaConfigGeneral.length - 1; i++) {
                sRutaConfigReal = sRutaConfigReal + sRutaConfigGeneral[i] + File.separator;
            }
            sRutaConfigReal = sRutaConfigReal + "Config.xml";
            File file = new File(sRutaConfigReal);
            JAXBContext jaxbContext = JAXBContext.newInstance(org.ventura.cpe.loaderbl.Configuracion.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Configuracion configuracion = (org.ventura.cpe.loaderbl.Configuracion) jaxbUnmarshaller.unmarshal(file);
            return Optional.ofNullable(configuracion);
        } catch (JAXBException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros de config.xml. {0}", ex.getMessage());
            return Optional.empty();
        }
    }


    public static boolean ERPConnection(Configuracion configuracion) {
        try {
            DocumentoINF.Conector.CargarParametro(configuracion);
        } catch (DOMException | NumberFormatException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de ERP : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Resumen(Configuracion configuracion) {
        try {
            //Tiempos
            TransaccionResumenProp.fechaResumen = configuracion.getResumenDiario().getFecha().normalValue();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = simpleDateFormat.parse(configuracion.getResumenDiario().getHora().normalValue());
            TransaccionResumenProp.Hora = date;
        } catch (ParseException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Resumen : . {0}", ex.getMessage());
            return false;
        } catch (VenturaExcepcion venturaExcepcion) {
            venturaExcepcion.printStackTrace();
            return false;
        }
        return true;

    }

    public static boolean GestorTiempo(Configuracion configuracion) {
        try {
            GestorTiempo.periodo = configuracion.getGestorTiempo().getTiempo().normalValue();
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Gestor de Transacciones.  {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Impresion(Configuracion configuracion) {
        try {
            //Directorio
            VariablesGlobales.Impresion = configuracion.getImpresion().getPdf().normalValue();
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Impresion : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    private static boolean Repositorio(Configuracion configuracion) {
        Node nodo;
        try {
            HBPersistencia.TIPOSERVIDOR = configuracion.getRepositorio().getTipoServidor().normalValue();
            HBPersistencia.SERVIDOR = configuracion.getRepositorio().getServidorBD().normalValue();
            HBPersistencia.PUERTO = configuracion.getRepositorio().getPuerto().normalValue();
            HBPersistencia.BASEDATOS = configuracion.getRepositorio().getBaseDatos().normalValue();
            if (HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("10")) {
                HBPersistencia.CONNSTRING = "jdbc:sqlserver://" + HBPersistencia.SERVIDOR + ":" + HBPersistencia.PUERTO + ";databaseName=" + HBPersistencia.BASEDATOS + "";
            } else {
                if (HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("11")) {
                    HBPersistencia.CONNSTRING = "jdbc:mysql://" + HBPersistencia.SERVIDOR + ":" + HBPersistencia.PUERTO + "/" + HBPersistencia.BASEDATOS;
                }
            }
            HBPersistencia.USER = configuracion.getRepositorio().getUser().normalValue();
            HBPersistencia.PASSWORD = configuracion.getRepositorio().getPassword().normalValue();
            //CARGA DE BD'S AÑADIDAS
            HBPersistencia.LISTBDANADIDAS = BdsMaestrasBL.listarBDAnadidas();

        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Repositorio : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean ERP(Configuracion configuracion) {
        try {

            TipoServidor.TipoServidorQuery = configuracion.getErp().getTipoServidor().normalValue();
            TipConector = Integer.parseInt(configuracion.getErp().getTipoConector().normalValue());
            switch (TipConector) {
                case 1:
                    DocumentoINF.Conector = new org.ventura.cpe.sb1.DocumentoBL();
                    DocumentoINF.GetConsultarResumenDiario = new org.ventura.cpe.sb1.DocumentoBL();
                    DocumentoINF.GetEnviarConsulta = new org.ventura.cpe.sb1.DocumentoBL();
                    DocumentoINF.GetEnviarResumenDiario = new org.ventura.cpe.sb1.DocumentoBL();
                    DocumentoINF.GetEnviarTransaccion = new org.ventura.cpe.sb1.DocumentoBL();
                    break;
                case 2:
                    DocumentoINF.Conector = new org.ventura.cpe.generico.DocumentoGENBL();
                    break;
                case 3:
                    DocumentoINF.Conector = new org.ventura.cpe.archivos.FileBL();
                    DocumentoINF.GetConsultarResumenDiario = new org.ventura.cpe.archivos.FileBL();
                    DocumentoINF.GetEnviarConsulta = new org.ventura.cpe.archivos.FileBL();
                    DocumentoINF.GetEnviarResumenDiario = new org.ventura.cpe.archivos.FileBL();
                    DocumentoINF.GetEnviarTransaccion = new org.ventura.cpe.archivos.FileBL();
                    break;
                default:
                    throw new VenturaExcepcion("El valor " + TipConector + " no es valido para el tipo conector");
            }
            DocumentoINF.Conector.CargarParametro(configuracion);
            DocumentoINF.GetConsultarResumenDiario.CargarParametro(configuracion);
            DocumentoINF.GetEnviarConsulta.CargarParametro(configuracion);
            DocumentoINF.GetEnviarTransaccion.CargarParametro(configuracion);
            DocumentoINF.GetEnviarResumenDiario.CargarParametro(configuracion);

        } catch (NumberFormatException | VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de ERP : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Directorio(Configuracion configuracion) {
        try {
            //Directorio
            Directorio.ADJUNTOS = configuracion.getDirectorio().getAdjuntos().normalValue();
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Directorio : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Tiempos(Configuracion configuracion) {
        Node nodo;
        try {
            //Tiempos
            TransaccionRespuesta.RQTIMEOUT = Integer.parseInt(configuracion.getTiempos().getRqTimeOut().normalValue()) * 1000;
            TransaccionRespuesta.RQINTERVAL = Integer.parseInt(configuracion.getTiempos().getRqInterval().normalValue());
            TransaccionRespuesta.RSTIMEOUT = Integer.parseInt(configuracion.getTiempos().getRsTimeOut().normalValue()) * 1000;
            TransaccionRespuesta.RSINTERVAL = Integer.parseInt(configuracion.getTiempos().getRsInterval().normalValue());
        } catch (NumberFormatException | VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Tiempos : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean PuertoVS(Configuracion configuracion) {
        try {
            PuertoVSCPE.PuertoAbierto_Configurador = Integer.parseInt(configuracion.getGeneral().getPuertoConfigurador().normalValue());
            PuertoVSCPE.PuertoAbierto_Request = Integer.parseInt(configuracion.getGeneral().getPuertoRequest().normalValue());
            PuertoVSCPE.PuertoAbierto_Response = Integer.parseInt(configuracion.getGeneral().getPuertoResponse().normalValue());
            PuertoVSCPE.PuertoAbierto_Resumen = Integer.parseInt(configuracion.getGeneral().getPuertoResumen().normalValue());
            PuertoVSCPE.PuertoAbierto_PublicWS = Integer.parseInt(configuracion.getGeneral().getPuertoPublicador().normalValue());
            PuertoVSCPE.PuertoAbierto_Extractor = Integer.parseInt(configuracion.getGeneral().getPuertoExtractor().normalValue());
        } catch (NumberFormatException | VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Puertos : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean VersionUBL(Configuracion configuracion) {
        try {
            TipoVersionUBL.factura = configuracion.getVersionUBL().getFactura().normalValue();
            TipoVersionUBL.boleta = configuracion.getVersionUBL().getBoleta().normalValue();
            TipoVersionUBL.notacredito = configuracion.getVersionUBL().getNotaCredito().normalValue();
            TipoVersionUBL.notadebito = configuracion.getVersionUBL().getNotaDebito().normalValue();
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros del Gestor de Transacciones.  {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Sunat(Configuracion configuracion) {
        try {
            String content = configuracion.getSunat().getIntegracionWS().normalValue();
            DocumentoDAO.isSunat = "SUNAT".equalsIgnoreCase(content);
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Sunat : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }
}
