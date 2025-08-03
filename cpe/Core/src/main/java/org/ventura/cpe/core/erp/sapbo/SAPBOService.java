package org.ventura.cpe.core.erp.sapbo;

import com.sap.smb.sbo.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.ventura.cpe.core.config.AppProperties;
import org.ventura.cpe.core.domain.BdsMaestras;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.domain.TransaccionResumenLineaAnexo;
import org.ventura.cpe.core.entidades.ListaSociedades;
import org.ventura.cpe.core.exception.ConfigurationException;
import org.ventura.cpe.core.exception.SAPNotFoundRegistry;
import org.ventura.cpe.core.exception.VenturaExcepcion;
import org.ventura.cpe.core.repository.BdsMaestrasRepository;
import org.ventura.cpe.core.signer.utils.CertificateUtils;
import org.ventura.cpe.core.util.ConnectedCompany;
import org.ventura.cpe.core.util.TipoServidor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.sap.smb.sbo.api.SBOCOMUtil.getPayments;
import static com.sap.smb.sbo.api.SBOCOMUtil.runRecordsetQuery;
import static java.text.MessageFormat.format;

@Slf4j
@Component
@RequiredArgsConstructor
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SAPBOService {

    private static final HashMap<String, ICompany> sociedades = new HashMap<>();

    private static final HashMap<String, ListaSociedades> listaSociedades = new HashMap<>();

    private static final List<ConnectedCompany> sociedadesConectadas = new ArrayList<>();

    private HashMap<String, String> sociedadesName = new HashMap<>();

    private final SAPBOAnexoService sapboAnexoService;

    private final BdsMaestrasRepository bdsMaestrasRepository;

    private final SBOConnection sboConnection;

    private final AppProperties properties;

    private boolean isConnected = false;

    private List<TipoServidor> tipoServidores = Arrays.asList(new TipoServidor("SAP HANA", SBOCOMConstants.BoDataServerTypes_dst_HANADB, 30000), new TipoServidor("Microsoft SQL Server 2005", SBOCOMConstants.BoDataServerTypes_dst_MSSQL2005, 1433), new TipoServidor("Microsoft SQL Server 2008", SBOCOMConstants.BoDataServerTypes_dst_MSSQL2008, 1433), new TipoServidor("Microsoft SQL Server 2012", SBOCOMConstants.BoDataServerTypes_dst_MSSQL2012, 1433), new TipoServidor("Microsoft SQL Server 2014", SBOCOMConstants.BoDataServerTypes_dst_MSSQL2014, 1433), new TipoServidor("Microsoft SQL Server 2016", 10, 1433), new TipoServidor("Microsoft SQL Server 2017", 11, 1433), new TipoServidor("Microsoft SQL Server 2019", 12, 1433));

    public boolean conectarSociedad() {
        if (sociedades.isEmpty()) {
            Optional<ICompany> optional = sboConnection.connectToErp();
            optional.ifPresent(sociedad -> {
                try {
                    Path pathCertificado = Paths.get(properties.getCertificadoDigital().getRutaCertificado());
                    byte[] certificadoBytes = Files.readAllBytes(pathCertificado);
                    String passwordCertificado = properties.getCertificadoDigital().getPasswordCertificado();
                    String usuarioSol = properties.getSunat().getUsuario().getUsuarioSol();
                    String claveSol = properties.getSunat().getUsuario().getUsuarioSol();
                    String logo = properties.getEmisorElectronico().getSenderLogo();
                    String companyDB = sociedad.getCompanyDB();
                    sociedadesName.put(companyDB, sociedad.getCompanyName());
                    ListaSociedades sociedadLista = new ListaSociedades("", certificadoBytes, passwordCertificado, usuarioSol, claveSol, logo);
                    ConnectedCompany connectedCompany = new ConnectedCompany("", pathCertificado.toString(), passwordCertificado, usuarioSol, claveSol, companyDB, logo);
                    sociedadesConectadas.add(connectedCompany);
                    sociedades.put(companyDB, sociedad);
                    listaSociedades.put(companyDB, sociedadLista);
                    List<BdsMaestras> bdsMaestras = bdsMaestrasRepository.findAll();
                    isConnected = true;
                    bdsMaestras.forEach(bd -> {
                        Optional<ICompany> optionalICompany = sboConnection.connectToErp(bd.getServidorLicencia(), bd.getBDServidor(), bd.getTipoServidor(), bd.getBDNombre(), bd.getBDUsuario(), bd.getBDClave(), bd.getERPUsuario(), bd.getERPClave());
                        optionalICompany.ifPresent(iCompany -> {
                            try {
                                ListaSociedades sociedadListaNew = new ListaSociedades(bd.getRucSociedad(), CertificateUtils.getCertificateInBytes(bd.getRutaCD()), bd.getPasswordCD(), bd.getUsuarioSec(), bd.getPasswordCD(), bd.getLogoSociedad());
                                listaSociedades.put(iCompany.getCompanyDB(), sociedadListaNew);
                                sociedades.put(iCompany.getCompanyDB(), iCompany);
                                sociedadesName.put(iCompany.getCompanyDB(), iCompany.getCompanyName());
                                ConnectedCompany newCompany = new ConnectedCompany(bd.getRucSociedad(), bd.getRutaCD(), bd.getPasswordCD(), bd.getUsuarioSec(), bd.getPasswordCD(), iCompany.getCompanyDB(), bd.getLogoSociedad());
                                sociedadesConectadas.add(newCompany);
                            } catch (ConfigurationException | FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            sociedades.forEach((key, iCompany) -> {
                isConnected = iCompany.isConnected();
                if (isConnected) return;
            });
        }
        return isConnected;
    }

    public boolean actualizarEstado(Transaccion tc, String estado, ICompany sociedad) {
        return false;
    }

    public static HashMap<String, ICompany> getSociedades() {
        return sociedades;
    }

    public boolean ponerEnSeguimientoResumen(TransaccionResumenLineaAnexo resumenLineaAnexo, String keySociedad) throws VenturaExcepcion {
        String objectType = resumenLineaAnexo.getObjcType();
        String docEntry = resumenLineaAnexo.getDocEntry();
        ICompany sociedad = sociedades.get(keySociedad);
        try {
            //IDocuments doc = Optional.ofNullable(SBOCOMUtil.getDocuments(sociedad, Integer.parseInt(objectType), Integer.parseInt(docEntry))).orElseThrow(() -> new VenturaExcepcion("No se ha encontrado el registro"));
            IDocuments doc = SBOCOMUtil.getDocuments(sociedad, Integer.parseInt(objectType), Integer.parseInt(docEntry));
            if(doc == null){
                String queryOinv = "UPDATE OINV SET U_VS_FESTAT='H' WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOinv);
                return true;
            }
            doc.getUserFields().getFields().item("U_VS_FESTAT").setValue("H");
            int ret = doc.update();
            if (ret != 0) {
                throw new VenturaExcepcion(sociedad.getLastErrorDescription());
            }
            return true;
        } catch (NumberFormatException | SBOCOMException | VenturaExcepcion ex) {
            throw new VenturaExcepcion("[ObjType=" + objectType + ",DocEntry=" + docEntry + "]:Error al actualizar U_VS_FESTAT='H'" + ex.getMessage(), ex);
        }
    }

    public boolean ActualizarMensajeResumenDiario(String objectType, String docEntry, String estado, ICompany Sociedad) throws VenturaExcepcion {
        IDocuments doc;
        try {
            doc = SBOCOMUtil.getDocuments(Sociedad, Integer.parseInt(objectType), Integer.parseInt(docEntry));
            if (doc == null) {
                throw new VenturaExcepcion("No se ha encontrado el registro");
            }
            doc.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
            int ret = doc.update();
            if (ret != 0) {
                throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
            }
            return true;
        } catch (NumberFormatException | SBOCOMException | VenturaExcepcion ex) {
            throw new VenturaExcepcion("[ObjType=" + objectType + ",DocEntry=" + docEntry + "]:Error al actualizar U_VS_FESTAT='" + estado + "'. " + ex.getMessage(), ex);
        }
    }

    public void actualizarMensaje(Transaccion transaccion, String estado, String message) {
        ICompany iCompany = SAPBOService.sociedades.get(transaccion.getKeySociedad());
        actualizarMensaje(transaccion, estado, message, iCompany);
    }

    public void actualizarMensaje(Transaccion transaccion, String estado, String respuesta, ICompany sociedad) {
        String codigoDocumento = transaccion.getDOCCodigo();
        boolean isPago = "20".equals(codigoDocumento);
        try {
            if (isPago) {
                //actualizarMensajePago(transaccion, estado, respuesta, sociedad, connection);
                log.info("Actualizado el estado del pago a Seguimiento [S]");
                log.info("Falló actualizado de estado");
            } else {
                //actualizarMensajeDocumento(transaccion, estado, respuesta, sociedad);
                log.info("Falló actualizado de estado");
            }
        } catch (VenturaExcepcion ex) {
            log.info("No se pudo actualizar el estado del {} a seguimiento", isPago ? "pago" : "documento");
            log.error(ex.getLocalizedMessage(), ex);
        }
    }

    private void actualizarMensajeDocumento(Transaccion transaccion, String estado, String respuesta, ICompany sociedad, Connection connection) throws VenturaExcepcion, SBOCOMException {
        String objType = transaccion.getFEObjectType();
        int docEntry = transaccion.getFEDocEntry();
        if(connection != null){
            String tablaName = obtenerTablaObjectType(objType);
            if (tablaName == null) {
                throw new IllegalArgumentException("ObjectType not supported: " + objType);
            }else{
                String updateQuery = "UPDATE "+tablaName+" SET U_VS_FESTAT = '"+estado+"' ,U_VS_CDRRSM= '"+respuesta+"' WHERE \"DocEntry\" = ? AND \"ObjType\" = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, docEntry);
                    pstmt.setInt(2, Integer.parseInt(objType));
                    pstmt.executeUpdate();
                } catch (SQLException | NumberFormatException e) {
                    throw new RuntimeException("Error updating document state", e);
                }
            }
        }else{
            String docCodigo = transaccion.getDOCCodigo();
            try {
                IDocuments documents = SBOCOMUtil.getDocuments(sociedad, Integer.parseInt(objType), docEntry);
                if(documents != null){
                    IDocuments doc = Optional.ofNullable(documents).orElseThrow(() -> new SAPNotFoundRegistry(format("No se ha encontrado el registro por el DocEntry [{0}]", docEntry)));
                    doc.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                    doc.getUserFields().getFields().item("U_VS_CDRRSM").setValue(respuesta);
                    int ret = doc.update();
                    if (ret != 0) {
                        if (!forceUpdateEstadoDocumento(docEntry, docCodigo, estado, respuesta, sociedad)) {
                            throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                        }
                    }
                }else{
                    if (!forceUpdateEstadoDocumento(docEntry, docCodigo, estado, respuesta, sociedad)) {
                        throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                    }
                }
            } catch (NumberFormatException | SBOCOMException | VenturaExcepcion | SAPNotFoundRegistry ex) {
                if (ex instanceof SAPNotFoundRegistry) {
                    log.info(ex.getLocalizedMessage());
                    System.out.println(objType);
                    log.info("Hubo un error con libreria de sap");
                    if (!forceUpdateEstadoDocumento(docEntry, docCodigo, estado, respuesta, sociedad)) {
                        throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                    }
                } else throw new VenturaExcepcion("[ObjType=" + transaccion.getFEObjectType() + ", DocEntry=" + transaccion.getFEDocEntry() + "]: Error al actualizar U_VS_CDRRSM='" + respuesta + "'. " + ex.getMessage(), ex);
            }
        }
    }

    private String obtenerTablaObjectType(String tabla){
        switch (tabla){
            case "13": return "OINV";
            case "203": return "ODPI";
            case "14": return "ORIN";
            case "46": return "OVPM";
            case "15": return "ODLN";
            case "66": return "ORDN";
            case "67": return "OWTR";
            case "60": return "OIGE";
            case "59": return "OIGN";
            case "21": return "ORPD";
            case "20": return "OPDN";
            default: return null;
        }
    }

    private boolean forceUpdateEstadoDocumento(int docEntry, String tipoDocumento, String estado, String respuesta, ICompany sociedad) throws SBOCOMException {
        Optional<String> op = Optional.ofNullable(respuesta).map(s -> s.isEmpty() ? null : s);
        String campos = (op.isPresent() ? "U_VS_FESTAT='" + estado + "',U_VS_CDRRSM='" + respuesta + "'" : "U_VS_FESTAT='" + estado + "'");
        /** Harol 16-01-2024 se agrega case 08 para ND*/
        switch (tipoDocumento) {
            case "01":
            case "03":
            case "08":
            case "40":
                String queryOinv = "UPDATE OINV SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOinv);
                String queryOdpi = "UPDATE ODPI SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOdpi);
                break;
            case "07":
                String query = "UPDATE OVPM SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, query);
                String queryOrin = "UPDATE ORIN SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOrin);
                break;
            case "20":
                String queryOvpm = "UPDATE OVPM SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOvpm);
                break;
            case "09":
                String queryOdln = "UPDATE ODLN SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOdln);
                String queryOige = "UPDATE OIGE SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOige);
                String queryOign = "UPDATE OIGN SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOign);
                String queryOwtr = "UPDATE OWTR SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOwtr);
                String queryOrdn = "UPDATE ORDN SET " + campos + " WHERE \"DocEntry\"=" + docEntry;
                runRecordsetQuery(sociedad, queryOrdn);
                break;
        }
        return true;
    }


    private void actualizarMensajePago(Transaccion transaccion, String estado, String respuesta, ICompany sociedad, Connection connection) throws VenturaExcepcion, SBOCOMException {
        String objType = transaccion.getFEObjectType();
        Integer docEntry = transaccion.getFEDocEntry();
        if(connection != null){
            String tablaName = obtenerTablaObjectType(objType);
            if (tablaName == null) {
                throw new IllegalArgumentException("ObjectType not supported: " + objType);
            }else{
                String updateQuery = "UPDATE "+tablaName+" SET U_VS_FESTAT = '"+estado+"' ,U_VS_CDRRSM= '"+respuesta+"' WHERE \"DocEntry\" = ? AND \"ObjType\" = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setInt(1, docEntry);
                    pstmt.setInt(2, Integer.parseInt(objType));
                    pstmt.executeUpdate();
                } catch (SQLException | NumberFormatException e) {
                    throw new RuntimeException("Error updating document state", e);
                }
            }
        }
        try {
            IPayments iPayments = Optional.ofNullable(getPayments(sociedad, Integer.parseInt(objType), docEntry)).orElseThrow(() -> new SAPNotFoundRegistry("No se ha encontrado el registro"));
            iPayments.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
            if (!respuesta.isEmpty()) iPayments.getUserFields().getFields().item("U_VS_CDRRSM").setValue(respuesta);
            int ret = iPayments.update();
            if (ret != 0) {
                try {
                    if (!forceUpdateEstadoDocumento(docEntry, transaccion.getDOCCodigo(), estado, respuesta, sociedad)) {
                        throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                    }
                } catch (Exception e) {
                    throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                }
            } else {
                try {
                    String query = "UPDATE ODPI SET U_VS_FESTAT='S' WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, query);
                    Optional<String> op = Optional.of(respuesta).map(s -> s.isEmpty() ? null : s);
                    String queryOinv = "UPDATE ODPI SET " + (op.isPresent() ? "U_VS_FESTAT='S',U_VS_CDRRSM='" + respuesta + "'" : "U_VS_FESTAT='S'") + " WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOinv);
                    String queryOvpm = "UPDATE OVPM SET " + (op.isPresent() ? "U_VS_FESTAT='S',U_VS_CDRRSM='" + respuesta + "'" : "U_VS_FESTAT='S'") + " WHERE \"DocEntry\"=" + docEntry;
                    runRecordsetQuery(sociedad, queryOvpm);
                } catch (Exception e) {
                    throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                }
            }
        } catch (NumberFormatException | SBOCOMException | VenturaExcepcion | SAPNotFoundRegistry ex) {
            Optional<String> op = Optional.ofNullable(respuesta).map(s -> s.isEmpty() ? null : s);
            String query = "UPDATE ODPI SET U_VS_FESTAT='S' WHERE \"DocEntry\"=" + docEntry;
            runRecordsetQuery(sociedad, query);
            String queryOinv = "UPDATE ODPI SET " + (op.isPresent() ? "U_VS_FESTAT='S',U_VS_CDRRSM='" + respuesta + "'" : "U_VS_FESTAT='S'") + " WHERE \"DocEntry\"=" + docEntry;
            runRecordsetQuery(sociedad, queryOinv);
            String queryOvpm = "UPDATE OVPM SET " + (op.isPresent() ? "U_VS_FESTAT='S',U_VS_CDRRSM='" + respuesta + "'" : "U_VS_FESTAT='S'") + " WHERE \"DocEntry\"=" + docEntry;
            runRecordsetQuery(sociedad, queryOvpm);
            if (ex instanceof SAPNotFoundRegistry) {
                forceUpdateEstadoDocumento(docEntry, transaccion.getDOCCodigo(), estado, respuesta, sociedad);
            } else throw new VenturaExcepcion("[ObjType=" + transaccion.getFEObjectType() + ",DocEntry=" + transaccion.getFEDocEntry() + "]: Error al actualizar U_VS_CDRRSM='" + respuesta + "'. " + ex.getMessage(), ex);
        }
    }

    public void ponerEnSeguimiento(Transaccion transaccion, ICompany sociedad, Connection connection) {
        String codigoDocumento = transaccion.getDOCCodigo();
        try {
            if ("20".equals(codigoDocumento)) {
                actualizarMensajePago(transaccion, "S", "Pago en Seguimiento", sociedad, connection);
                log.info("Actualizado el estado del pago [{}] a seguimiento", transaccion.getFEId());
            } else {
                actualizarMensajeDocumento(transaccion, "S", "Documento en Seguimiento", sociedad, connection);
                log.info("Actualizado el estado del documento [{}] a Seguimiento [S]", transaccion.getFEId());
            }
        } catch (VenturaExcepcion | SBOCOMException ex) {
            log.info("No se pudo actualizar el estado del documento a seguimiento");
            System.out.println(ex.getLocalizedMessage());
            log.error(ex.getLocalizedMessage(), ex);
        }
    }

    public void capturarCodigo(Transaccion tc, String codBarra, String digestValue, ICompany Sociedad) {
        try {
            if (!tc.getDOCCodigo().equalsIgnoreCase("20")) {
                capturarCodigoDocumento(tc, codBarra, digestValue, Sociedad);
            } else {
                capturarCodigoPago(tc, codBarra, digestValue, Sociedad);
            }
        } catch (NumberFormatException ex) {
            log.error("Ocurrio un problemas {}", ex.getMessage());
        } finally {
            System.gc();
        }
    }

    public void capturarCodigo(Transaccion tc, String codBarra, String digestValue) {
        ICompany Sociedad = SAPBOService.sociedades.get(tc.getKeySociedad());
        try {
            if (!tc.getDOCCodigo().equalsIgnoreCase("20")) {
                capturarCodigoDocumento(tc, codBarra, digestValue, Sociedad);
            } else {
                capturarCodigoPago(tc, codBarra, digestValue, Sociedad);
            }
        } catch (NumberFormatException ex) {
            log.error("Ocurrio un problemas {}", ex.getMessage());
        } finally {
            System.gc();
        }
    }

    private void capturarCodigoPago(Transaccion transaccion, String codBarra, String digestValue, ICompany Sociedad) {
        String objType = transaccion.getFEObjectType();
        Integer docEntry = transaccion.getFEDocEntry();
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            try {
                IPayments pge = Optional.ofNullable(getPayments(Sociedad, Integer.parseInt(objType), docEntry)).orElseThrow(() -> new SAPNotFoundRegistry("No se ha encontrado el registro"));
                pge.getUserFields().getFields().item("U_VS_DIGESTVALUE").setValue(digestValue);
                pge.getUserFields().getFields().item("U_VS_CODBARRA").setValue(codBarra);
                int ret = pge.update();
                if (ret != 0) {
                    throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
                }
            } catch (SAPNotFoundRegistry | SBOCOMException | VenturaExcepcion ex) {
                log.error(ex.getLocalizedMessage(), ex);
                if (ex instanceof SAPNotFoundRegistry) {
                    try {
                        if (objType.equalsIgnoreCase("07")) {
                            String query = "UPDATE ORIN SET U_VS_DIGESTVALUE='" + digestValue + "',U_VS_CODBARRA='" + codBarra + " WHERE \"DocEntry\"=" + docEntry;
                            runRecordsetQuery(Sociedad, query);
                        } else {
                            String query = "UPDATE OVPM SET U_VS_DIGESTVALUE='" + digestValue + "',U_VS_CODBARRA='" + codBarra + " WHERE \"DocEntry\"=" + docEntry;
                            runRecordsetQuery(Sociedad, query);
                            String queryOinv = "UPDATE OINV SET " + "U_VS_DIGESTVALUE='" + digestValue + "',U_VS_CODBARRA='" + codBarra + " WHERE \"DocEntry\"=" + docEntry;
                            runRecordsetQuery(Sociedad, queryOinv);
                        }
                    } catch (SBOCOMException e) {
                        log.error(e.getLocalizedMessage(), e);
                    }
                }
            }
        });
    }

    public void capturarCodigoDocumento(Transaccion transaccion, String codBarra, String digestValue, ICompany Sociedad) {
        try {
            int objType = Integer.parseInt(transaccion.getFEObjectType());
            int docEntry = transaccion.getFEDocEntry();
            IDocuments doc = Optional.ofNullable(SBOCOMUtil.getDocuments(Sociedad, objType, docEntry)).orElseThrow(() -> new SAPNotFoundRegistry(format("No se ha encontrado el registro por el DocEntry [{0}]", docEntry)));
            doc.getUserFields().getFields().item("U_VS_DIGESTVALUE").setValue(digestValue);
            doc.getUserFields().getFields().item("U_VS_CODBARRA").setValue(codBarra);
            int ret = doc.update();
            if (ret != 0) {
                throw new VenturaExcepcion(Sociedad.getLastErrorDescription());
            }
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
    }

    public void ponerEnError(Transaccion transaccion, String errorMensaje, ICompany sociedad, Connection connection) {
        String codigoDocumento = transaccion.getDOCCodigo();
        try {
            if ("20".equals(codigoDocumento)) {
                actualizarMensajePago(transaccion, "E", errorMensaje, sociedad, connection);
                log.info("Actualizado el estado del pago a Error [E]");
            } else {
                actualizarMensajeDocumento(transaccion, "E", errorMensaje, sociedad, connection);
                log.info("Actualizado el estado del documento a Error [E]");
            }
        } catch (VenturaExcepcion | SBOCOMException ex) {
            log.info("No se pudo actualizar el estado del documento a error");
            log.error(ex.getLocalizedMessage(), ex);
        }
    }

    public boolean agregarAnexo(Transaccion transaccion, Path path, boolean borrador) {
        try {
            String keySociead = transaccion.getKeySociedad();
            ICompany sociedad = SAPBOService.sociedades.get(keySociead);
            return sapboAnexoService.agregarAnexo(transaccion, path, borrador, sociedad);
        } catch (SBOCOMException e) {
            log.error(e.getLocalizedMessage(), e);
            return false;
        }
    }

    public HashMap<String, String> getNameSociedades() {
        return sociedadesName;
    }

    public List<TipoServidor> getTipoServidores() {
        return tipoServidores;
    }

    public HashMap<String, ListaSociedades> getListaSociedades() {
        return listaSociedades;
    }

    public static void addCompany(ICompany sociedad, String databaseName) {
        sociedades.put(databaseName, sociedad);
    }

    public static List<ConnectedCompany> getSociedadesConectadas() {
        return sociedadesConectadas;
    }

    private Integer getBdType(String tipoServidor) {
        switch (tipoServidor) {
            case "1":
                return SBOCOMConstants.BoDataServerTypes_dst_MSSQL;
            case "2":
                return SBOCOMConstants.BoDataServerTypes_dst_DB_2;
            case "3":
                return SBOCOMConstants.BoDataServerTypes_dst_SYBASE;
            case "4":
                return SBOCOMConstants.BoDataServerTypes_dst_MSSQL2005;
            case "5":
                return SBOCOMConstants.BoDataServerTypes_dst_MAXDB;
            case "6":
                return SBOCOMConstants.BoDataServerTypes_dst_MSSQL2008;
            case "7":
                return SBOCOMConstants.BoDataServerTypes_dst_MSSQL2012;
            case "8":
                return SBOCOMConstants.BoDataServerTypes_dst_MSSQL2014;
            case "9":
                return SBOCOMConstants.BoDataServerTypes_dst_HANADB;
            case "10":
                return 10;
            case "11":
                return 11;
            default:
                return SBOCOMConstants.BoDataServerTypes_dst_MSSQL;
        }
    }
}
