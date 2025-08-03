/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.main.config;

import org.ventura.cpe.bl.BdsMaestrasBL;
import org.ventura.cpe.dao.conexion.HBPersistencia;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.TransaccionResumenProp;
import org.ventura.cpe.dto.hb.BdsMaestras;
import org.ventura.cpe.ex.DocumentoINF;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.loaderbl.Configuracion;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.pfeconn.TransaccionPFE;
import org.ventura.soluciones.commons.exception.ConfigurationException;
import org.ventura.soluciones.signer.utils.CertificateUtils;
import org.ventura.utilidades.entidades.*;
import org.ventura.utilidades.sunat.RespuestaSunat;
import org.w3c.dom.DOMException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.ventura.cpe.dto.TransaccionRespuesta.WS_TIEMPOPUBLICACION;

/**
 * @author Percy
 */
public class Loader {

    public static int TipConector;

    public static String tipoConectorPFE;

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
            JAXBContext jaxbContext = JAXBContext.newInstance(org.ventura.cpe.loaderbl.Configuracion.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Configuracion configuracion = (org.ventura.cpe.loaderbl.Configuracion) jaxbUnmarshaller.unmarshal(file);
            boolean resultado;
            resultado = Repositorio(configuracion);
            resultado = resultado && ERP(configuracion);
            resultado = resultado && Conector(configuracion);
            resultado = resultado && Proxy(configuracion);
            //resultado = resultado && Propiedades(configuracion);
            resultado = resultado && GestorTiempo(configuracion);
            resultado = resultado && Ambiente__Sunat(configuracion);
            resultado = resultado && Directorio(configuracion);
            resultado = resultado && Tiempos(configuracion);
            resultado = resultado && Resumen(configuracion);
            resultado = resultado && Sunat(configuracion);
            resultado = resultado && PuertoVS(configuracion);
            resultado = resultado && Impresion(configuracion);
            resultado = resultado && DatosWS(configuracion);
            resultado = resultado && VersionUBL(configuracion);
            return resultado;
        } catch (JAXBException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros de config.xml. {0}", ex.getMessage());
            return false;
        } catch (VenturaExcepcion ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
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
            boolean resultado = true;
            resultado = resultado && ERPConnection(configuracion);
            return resultado;
        } catch (JAXBException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, "Error al cargar parámetros de config.xml. {0}", ex.getMessage());
            return false;
        }
    }

    public static boolean DatosWS(Configuracion configuracion) throws VenturaExcepcion {
        try {
            WS_TIEMPOPUBLICACION = configuracion.getWebService().getWsTiempoPublicacionPublic().normalValue();
        } catch (DOMException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "{0}: Se encontro un incidencia en el metodo {1}  con el siguiente mensaje {2}", new Object[]{new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage()});
            return false;
        }
        return true;
    }

    public static boolean GestorTiempo(Configuracion configuracion) throws VenturaExcepcion {
        GestorTiempo.periodo = configuracion.getGestorTiempo().getTiempo().normalValue();
        return true;
    }

    public static boolean Impresion(Configuracion configuracion) throws VenturaExcepcion {
        //Directorio
        VariablesGlobales.Impresion = configuracion.getImpresion().getPdf().normalValue();
        return true;
    }

    public static boolean Ambiente__Sunat(Configuracion configuracion) throws VenturaExcepcion {
        VariablesGlobales.Ambiente__Sunat = configuracion.getSunat().getClienteSunat().normalValue();
        return true;
    }

    private static boolean Repositorio(Configuracion configuracion) throws VenturaExcepcion {
        HBPersistencia.TIPOSERVIDOR = configuracion.getRepositorio().getTipoServidor().normalValue();
        HBPersistencia.SERVIDOR = configuracion.getRepositorio().getServidorBD().normalValue();
        HBPersistencia.PUERTO = configuracion.getRepositorio().getPuerto().normalValue();
        HBPersistencia.BASEDATOS = configuracion.getRepositorio().getBaseDatos().normalValue();
        boolean isSqlServer = HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("8") || HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("10");
        if (isSqlServer) {
            HBPersistencia.CONNSTRING = "jdbc:sqlserver://" + HBPersistencia.SERVIDOR + ":" + HBPersistencia.PUERTO + ";databaseName=" + HBPersistencia.BASEDATOS + "";
        } else {
            if (HBPersistencia.TIPOSERVIDOR.equalsIgnoreCase("11")) {
                HBPersistencia.CONNSTRING = "jdbc:mysql://" + HBPersistencia.SERVIDOR + ":" + HBPersistencia.PUERTO + "/" + HBPersistencia.BASEDATOS + "?serverTimezone=America/Lima&useSSL=false";
            }
        }
        HBPersistencia.USER = configuracion.getRepositorio().getUser().normalValue();
        HBPersistencia.PASSWORD = configuracion.getRepositorio().getPassword().normalValue();
//            CARGA DE BD'S AÑADIDAS
        HBPersistencia.LISTBDANADIDAS = BdsMaestrasBL.listarBDAnadidas();
        return true;
    }

    private static boolean Proxy(Configuracion configuracion) throws VenturaExcepcion {
        boolean usar, auten;
        String proxyHost, proxyPort;
        usar = Boolean.parseBoolean(configuracion.getProxy().getUsarProxy().normalValue());
        proxyHost = configuracion.getProxy().getServidor().normalValue();
        proxyPort = configuracion.getProxy().getPuerto().normalValue();
        auten = Boolean.parseBoolean(configuracion.getProxy().getRequAuth().normalValue());
        final String authUser = configuracion.getProxy().getUser().normalValue();
        String authPassword = configuracion.getProxy().getPass().normalValue();
        if (usar) {
            Properties systemProperties = System.getProperties();
            systemProperties.setProperty("http.proxyHost", proxyHost);
            systemProperties.setProperty("http.proxyPort", proxyPort);
            //System.setProperty("http.proxyUser", authUser);
            //System.setProperty("http.proxyPassword", authPassword);
//                Authenticator.setDefault(new Authenticator() {
//                    @Override
//                    public PasswordAuthentication getPasswordAuthentication() {
//                        return new PasswordAuthentication(
//                                authUser, authPassword1.toCharArray());
//                    }
//                });
        }
        return true;
    }

    public static boolean Conector(Configuracion configuracion) {
        try {
            tipoConectorPFE = configuracion.getPfeConnector().getTipo().normalValue();
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
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Conector : . {0}", ex.getMessage());
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

        } catch (DOMException | NumberFormatException | VenturaExcepcion ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de ERP : . {0}", ex.getMessage());
            return false;
        }
        return true;
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

    public static boolean Directorio(Configuracion configuracion) throws VenturaExcepcion {
        try {
            //Directorio
            Directorio.ADJUNTOS = configuracion.getDirectorio().getAdjuntos().normalValue();
            System.out.println(Directorio.ADJUNTOS);
        } catch (DOMException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Directorio : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Tiempos(Configuracion configuracion) throws VenturaExcepcion {
        try {
            //Tiempos
            TransaccionRespuesta.RQTIMEOUT = Integer.parseInt(configuracion.getTiempos().getRqTimeOut().normalValue()) * 1000;
            TransaccionRespuesta.RQINTERVAL = Integer.parseInt(configuracion.getTiempos().getRqInterval().normalValue());
            TransaccionRespuesta.RSTIMEOUT = Integer.parseInt(configuracion.getTiempos().getRsTimeOut().normalValue()) * 1000;
            TransaccionRespuesta.RSINTERVAL = Integer.parseInt(configuracion.getTiempos().getRsInterval().normalValue());
        } catch (DOMException | NumberFormatException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Tiempos : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean Resumen(Configuracion configuracion) throws VenturaExcepcion {
        try {
            //Tiempos
            TransaccionResumenProp.fechaResumen = configuracion.getResumenDiario().getFecha().normalValue();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date date = simpleDateFormat.parse(configuracion.getResumenDiario().getHora().normalValue());
            TransaccionResumenProp.Hora = date;
        } catch (DOMException | ParseException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Resumen : . {0}", ex.getMessage());
            return false;
        }
        return true;

    }

    public static boolean Sunat(Configuracion configuracion) throws VenturaExcepcion {
        try {
            RespuestaSunat.AmbienteSunat = Integer.parseInt(configuracion.getSunat().getAmbiente().normalValue());
            VariablesGlobales.UsoSunat = Boolean.parseBoolean(configuracion.getUsoSunat().getWs().normalValue());
            VariablesGlobales.UsoValidaciones = Boolean.parseBoolean(configuracion.getUsoSunat().getValidacion().normalValue());
            VariablesGlobales.UsoPdfSinRespuesta = Boolean.parseBoolean(configuracion.getUsoSunat().getPdf().normalValue());
            VariablesGlobales.rutaSunatTestWebservice = Optional.ofNullable(configuracion.getSunat().getRutaSunatTest().normalValue()).orElse("");
            VariablesGlobales.rutaSunatWebservice = Optional.ofNullable(configuracion.getSunat().getRutaSunatProd().normalValue()).orElse("");
            VariablesGlobales.rutaOseTestWebservice = Optional.ofNullable(configuracion.getSunat().getRutaOseTest().normalValue()).orElse("");
            VariablesGlobales.rutaOseWebservice = Optional.ofNullable(configuracion.getSunat().getRutaOseProd().normalValue()).orElse("");
            VariablesGlobales.rutaOseWebservice = Optional.ofNullable(configuracion.getSunat().getRutaOseProd().normalValue()).orElse("");
        } catch (DOMException | NumberFormatException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Sunat : . {0}", ex.getMessage());
            return false;
        }
        return true;
    }

    public static boolean PuertoVS(Configuracion configuracion) throws VenturaExcepcion {
        try {
            String config = configuracion.getGeneral().getPuertoConfigurador().normalValue();
            PuertoVSCPE.PuertoAbierto_Configurador = config.isEmpty() ? 7000 : Integer.parseInt(config);
            config = configuracion.getGeneral().getPuertoRequest().normalValue();
            PuertoVSCPE.PuertoAbierto_Request = config.isEmpty() ? 7001 : Integer.parseInt(config);
            config = configuracion.getGeneral().getPuertoResponse().normalValue();
            PuertoVSCPE.PuertoAbierto_Response = config.isEmpty() ? 7002 : Integer.parseInt(config);
            config = configuracion.getGeneral().getPuertoResumen().normalValue();
            PuertoVSCPE.PuertoAbierto_Resumen = config.isEmpty() ? 7003 : Integer.parseInt(config);
            config = configuracion.getGeneral().getPuertoPublicador().normalValue();
            PuertoVSCPE.PuertoAbierto_PublicWS = config.isEmpty() ? 7004 : Integer.parseInt(config);
            config = configuracion.getGeneral().getPuertoExtractor().normalValue();
            PuertoVSCPE.PuertoAbierto_Extractor = config.isEmpty() ? 7005 : Integer.parseInt(config);
        } catch (DOMException | NumberFormatException ex) {
            LoggerTrans.getCDMainLogger().log(Level.SEVERE, " No se cargo correctamente los parametros de Puertos : . {0}", ex.getMessage());
            return false;
        }
        return true;

    }

    public static boolean VersionUBL(Configuracion configuracion) throws VenturaExcepcion {
        try {
            TipoVersionUBL.factura = configuracion.getVersionUBL().getFactura().normalValue();
            TipoVersionUBL.boleta = configuracion.getVersionUBL().getBoleta().normalValue();
            TipoVersionUBL.notacredito = configuracion.getVersionUBL().getNotaCredito().normalValue();
            TipoVersionUBL.notadebito = configuracion.getVersionUBL().getNotaDebito().normalValue();

            byte[] certificate = CertificateUtils.getCertificateInBytes(configuracion.getCertificadoDigital().getRutaCertificado().normalValue());
            String certPassword = configuracion.getCertificadoDigital().getPasswordCertificado().normalValue();
            String sunatuser = configuracion.getSunat().getUsuario().getUsuarioSol().normalValue();
            String sunatPwd = configuracion.getSunat().getUsuario().getClaveSol().normalValue();
            String logo = configuracion.getEmisorElectronico().getSenderLogo().normalValue();

            //GUIAS
            String tipoTransaccion = configuracion.getSunatGuias().getIntegracionGuias().normalValue();
            String clientId = configuracion.getSunatGuias().getUsuario().getClientID().normalValue();
            String secretId = configuracion.getSunatGuias().getUsuario().getSecretID().normalValue();
            String usuarioGuias = configuracion.getSunatGuias().getUsuario().getUsuarioGuias().normalValue();
            String passwordGuias = configuracion.getSunatGuias().getUsuario().getClaveGuias().normalValue();
            String scope = configuracion.getSunatGuias().getUsuario().getScope().normalValue();

            List<BdsMaestras> bdsMaestras = BdsMaestrasBL.listarKeyBDAnadidas();
            VariablesGlobales.MapSociedades = new HashMap<>();
            String nombreBdPrimaria = configuracion.getErp().getBaseDeDatos().normalValue();

            VariablesGlobales.MapSociedades.put(nombreBdPrimaria, new ListaSociedades("", certificate, certPassword, sunatuser, sunatPwd, logo,
                    tipoTransaccion, clientId, secretId, usuarioGuias, passwordGuias ,scope));
            for (BdsMaestras maestra : bdsMaestras) {
                VariablesGlobales.MapSociedades.put(maestra.getBDNombre(),
                        new ListaSociedades(maestra.getRucSociedad(),
                                CertificateUtils.getCertificateInBytes(maestra.getRutaCD()),
                                maestra.getPasswordCD(),
                                maestra.getUsuarioSec(),
                                maestra.getPasswordSec(),
                                maestra.getLogoSociedad(),
                                maestra.getTipoIntegracionGuias(),
                                maestra.getClientId(),
                                maestra.getClientSecret(),
                                maestra.getUsuarioGuias(),
                                maestra.getPasswordGuias(),
                                maestra.getScope()
                        ));

            }
        } catch (ConfigurationException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

}
