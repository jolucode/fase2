package org.ventura.cpe.core.erp.sapbo;

import com.sap.smb.sbo.api.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.ventura.cpe.core.domain.Transaccion;
import org.ventura.cpe.core.erp.interfaces.ERPConnection;
import org.ventura.cpe.core.exception.VenturaExcepcion;

import java.util.Optional;

@Slf4j
@Component
public class SBOConnection implements ERPConnection<ICompany> {

    @Value("${configuracion.erp.servidorLicencias}")
    private String servidorLicencia;

    @Value("${configuracion.erp.servidorBD}")
    private String servidorBaseDatos;

    @Value("${configuracion.erp.tipoServidor}")
    private String tipoServidor;

    @Value("${configuracion.erp.baseDeDatos}")
    private String database;

    @Value("${configuracion.erp.user}")
    private String databaseUsername;

    @Value("${configuracion.erp.password}")
    private String databasePassword;

    @Value("${configuracion.erp.erpUser}")
    private String erpUsername;

    @Value("${configuracion.erp.erpPass}")
    private String erpPassword;

    @Override
    public Optional<ICompany> connectToErp() {
        ICompany sociedad = SBOCOMUtil.newCompany();
        sociedad.setLicenseServer(servidorLicencia);
        sociedad.setServer(servidorBaseDatos);
        //Integer serverType = getBdType(tipoServidor);
        sociedad.setDbServerType(Integer.parseInt(tipoServidor));
        sociedad.setCompanyDB(database);
        sociedad.setDbUserName(databaseUsername);
        sociedad.setDbPassword(databasePassword);
        sociedad.setUserName(erpUsername);
        sociedad.setPassword(erpPassword);

        sociedad.setLanguage(SBOCOMConstants.BoSuppLangs_ln_Spanish_La);
        sociedad.setUseTrusted(Boolean.FALSE);
        log.info("Conectando a sociedad...");
        int retorno = sociedad.connect();
        if (retorno == 0) {
            log.info("Conectado a sociedad {}", sociedad.getCompanyName());
            return Optional.of(sociedad);
        } else {
            String msj = "ERROR: (" + retorno + ")" + sociedad.getLastErrorDescription();
            log.error("No se pudo conectar {}", msj);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ICompany> connectToErp(String servidorLicencia, String servidorBaseDatos, String tipoServer, String database, String databaseUsername, String databasePassword, String erpUsername, String erpPassword) {
        ICompany sociedadNew = SBOCOMUtil.newCompany();
        sociedadNew.setLicenseServer(servidorLicencia);
        sociedadNew.setServer(servidorBaseDatos);
        sociedadNew.setDbServerType(getBdType(tipoServer));
        sociedadNew.setCompanyDB(database);
        sociedadNew.setDbUserName(databaseUsername);
        sociedadNew.setDbPassword(databasePassword);
        sociedadNew.setUserName(erpUsername);
        sociedadNew.setPassword(erpPassword);

        sociedadNew.setLanguage(SBOCOMConstants.BoSuppLangs_ln_Spanish_La);
        sociedadNew.setUseTrusted(Boolean.FALSE);
        int retorno = sociedadNew.connect();
        if (retorno == 0) {
            log.info("Conectado a sociedad {}", sociedadNew.getCompanyName());
            return Optional.of(sociedadNew);
        } else {
            String msj = "ERROR: (" + retorno + ")" + sociedadNew.getLastErrorDescription();
            log.error("No se pudo conectar {}", msj);
            return Optional.empty();
        }
    }

    @Override
    public boolean actualizarMensaje(Transaccion tc, String mensaje, String estado, ICompany sociedad) throws VenturaExcepcion {
        try {
            if (tc.getDOCCodigo().equalsIgnoreCase("20")) {
                IPayments pge = SBOCOMUtil.getPayments(sociedad, Integer.parseInt(tc.getFEObjectType()), tc.getFEDocEntry());
                if (pge == null) {
                    throw new VenturaExcepcion("No se ha encontrado el registro");
                }
                pge.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                pge.getUserFields().getFields().item("U_VS_CDRRSM").setValue(mensaje);
                int ret = pge.update();
                if (ret != 0) {
                    log.info(sociedad.getLastErrorDescription());
                    throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                }
            } else {
                IDocuments doc = SBOCOMUtil.getDocuments(sociedad, Integer.parseInt(tc.getFEObjectType()), tc.getFEDocEntry());
                if (doc == null) {
                    throw new VenturaExcepcion("No se ha encontrado el registro");
                }
                doc.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                doc.getUserFields().getFields().item("U_VS_CDRRSM").setValue(mensaje);
                int ret = doc.update();
                if (ret != 0) {
                    throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                }
            }
            return true;
        } catch (NumberFormatException | SBOCOMException | VenturaExcepcion ex) {
            throw new VenturaExcepcion("[ObjType=" + tc.getFEObjectType() + ",DocEntry=" + tc.getFEDocEntry() + "]:Error al actualizar U_VS_CDRRSM='" + mensaje + "'. " + ex.getMessage(), ex);
        } finally {
            System.gc();
        }
    }

    public void ponerEnSeguimiento(Transaccion transaccion) throws VenturaExcepcion {
        this.actualizarEstado(transaccion, "", null);
    }

    @Override
    public boolean actualizarEstado(Transaccion transaccion, String estado, ICompany sociedad) throws VenturaExcepcion {
        Integer docEntry = transaccion.getFEDocEntry();
        String tipoTrans = transaccion.getFETipoTrans(), docCodigo = transaccion.getDOCCodigo();
        int objType = Integer.parseInt(transaccion.getFEObjectType());
        try {
            estado = Optional.ofNullable(estado).orElse("S");
            if (docCodigo.equalsIgnoreCase("20")) {
                IPayments ip = SBOCOMUtil.getPayments(sociedad, objType, docEntry);
                if (ip == null) {
                    throw new VenturaExcepcion("No se ha encontrado el registro");
                }
                log.info("Gey_Payment");
                if (tipoTrans == null || tipoTrans.isEmpty()) {
                    throw new VenturaExcepcion("El Tipo de transaccion es vacio o nulo");
                }
                ip.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                log.info("Estado" + estado + "DocEntry" + docEntry + "Objectype" + objType + "TipoTransaccion" + tipoTrans);
                int ret = ip.update();
                if (ret != 0) {
                    throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                }
            } else {
                IDocuments doc = SBOCOMUtil.getDocuments(sociedad, objType, docEntry);
                if (doc == null) {
                    throw new VenturaExcepcion("No se ha encontrado el registro");
                }
                if (tipoTrans == null || tipoTrans.isEmpty()) {
                    throw new VenturaExcepcion("El Tipo de transaccion esta vacia o nula");
                }
                doc.getUserFields().getFields().item("U_VS_FESTAT").setValue(estado);
                int ret = doc.update();
                if (ret != 0) {
                    throw new VenturaExcepcion(sociedad.getLastErrorDescription());
                }
            }
            return true;
        } catch (NumberFormatException | SBOCOMException | VenturaExcepcion ex) {
            throw new VenturaExcepcion("[ObjType=" + objType + ",DocEntry=" + docEntry + "]:Error al actualizar ESTADO='" + estado + "'. " + ex.getMessage() + "Tipo de Documento" + docCodigo, ex);
        } finally {
            System.gc();
        }
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
