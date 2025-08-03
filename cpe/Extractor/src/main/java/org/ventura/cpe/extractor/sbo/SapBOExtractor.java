package org.ventura.cpe.extractor.sbo;

import com.sap.smb.sbo.api.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ventura.cpe.core.config.IUBLConfig;
import org.ventura.cpe.core.domain.*;
import org.ventura.cpe.core.erp.sapbo.SAPBOService;
import org.ventura.cpe.core.erp.sapbo.SBOObjectCreator;
import org.ventura.cpe.core.exception.VenturaExcepcion;
import org.ventura.cpe.core.repository.EnvioDocumentoRepository;
import org.ventura.cpe.core.repository.TransaccionRepository;
import org.ventura.cpe.core.repository.UsuariocamposRepository;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.function.Predicate;

import static com.sap.smb.sbo.api.SBOCOMUtil.runRecordsetQuery;
import static java.math.BigDecimal.valueOf;
import static org.ventura.cpe.core.config.IUBLConfig.TAX_TOTAL_BPT_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class SapBOExtractor {

    private final VSFactory factory;

    private final TransaccionRepository transaccionRepository;

    private final UsuariocamposRepository usuariocamposRepository;

    private final EnvioDocumentoRepository documentoRepository;

    private final SAPBOService sapboService;

    @Value("${configuracion.erp.servidorBD}")
    private String servidorBaseDatos;

    @Value("${configuracion.erp.baseDeDatos}")
    private String database;

    @Value("${configuracion.erp.user}")
    private String databaseUsername;

    @Value("${configuracion.erp.password}")
    private String databasePassword;

    @Value("${configuracion.erp.tipoServidor}")
    private String tipoServidor;

    @Value("${configuracion.jdbc.sqlServer}")
    private Boolean estadoSqlServer;

    @Value("${configuracion.jdbc.servidorBDJdbc}")
    private String servidorBDJdbc;

    @Value("${configuracion.jdbc.nameUserField}")
    private String nameUserField;

    @Value("${configuracion.jdbc.estadoJdbc}")
    private Boolean estadoJdbc;

    @Value("${configuracion.versionUBL.factura}")
    private String versionUBLFactura;

    @Value("${configuracion.versionUBL.boleta}")
    private String versionUBLBoleta;

    @Value("${configuracion.versionUBL.notaCredito}")
    private String versionUBLNotaCredito;

    @Value("${configuracion.versionUBL.notaDebito}")
    private String versionUBLNotaDebito;

    private BigDecimal descuentoTotal;

    private void insertarImpuestoBolsa(Set<TransaccionLineas> transaccionLineas, Transaccion transaccion) {
        char impuestoBolsa = 'I', itemBolsa = 'A';
        Optional<TransaccionLineas> lineasOptional = transaccionLineas.stream().filter(linea -> linea.getItemBolsa().equals(itemBolsa)).findAny();
        lineasOptional.ifPresent(lineaBolsa -> {
            Optional<TransaccionLineas> impuestoBolsaOptional = transaccionLineas.stream().filter(linea -> linea.getItemBolsa().equals(impuestoBolsa)).findAny();
            TransaccionImpuestosPK transaccionImpuestosPK = new TransaccionImpuestosPK();
            String feId = lineaBolsa.getTransaccionLineasPK().getFEId();
            transaccionImpuestosPK.setFEId(feId);
            int nroOrden = lineaBolsa.getTransaccionLineasPK().getNroOrden();
            int nroOrdenImpuesto = nroOrden;
            while (existeNumeroOrden(transaccion.getTransaccionImpuestosList(), nroOrdenImpuesto)) {
                nroOrdenImpuesto++;
            }
            transaccionImpuestosPK.setLineId(nroOrdenImpuesto);
            TransaccionImpuestos transaccionImpuesto = new TransaccionImpuestos();
            transaccionImpuesto.setTransaccionImpuestosPK(transaccionImpuestosPK);
            transaccionImpuesto.setAbreviatura(IUBLConfig.TAX_TOTAL_OTH_CODE);
            transaccionImpuesto.setMoneda(transaccion.getDOCMONCodigo());
            BigDecimal precioRefMonto = impuestoBolsaOptional.map(TransaccionLineas::getPrecioRefMonto).orElseGet(lineaBolsa::getPrecioRefMonto);
            BigDecimal totalBruto = impuestoBolsaOptional.map(TransaccionLineas::getTotalBruto).orElseGet(lineaBolsa::getTotalBruto);
            transaccionImpuesto.setMonto(precioRefMonto);
            transaccionImpuesto.setValorVenta(totalBruto);
            transaccionImpuesto.setPorcentaje(BigDecimal.valueOf(100));
            transaccionImpuesto.setTipoTributo(IUBLConfig.TAX_TOTAL_BPT_ID);
            transaccionImpuesto.setCodigo("C");
            transaccionImpuesto.setNombre(TAX_TOTAL_BPT_NAME);
            transaccion.getTransaccionImpuestosList().add(transaccionImpuesto);

            Optional<TransaccionImpuestos> impuestosOptional = transaccion.getTransaccionImpuestosList().stream().filter(impuestoTotal -> impuestoTotal.getNombre().isEmpty()).findAny();
            impuestosOptional.ifPresent(transaccion.getTransaccionImpuestosList()::remove);

            Optional<Integer> optionalNumeroOrden = lineaBolsa.getTransaccionLineaImpuestosList().stream().map(TransaccionLineaImpuestos::getTransaccionLineaImpuestosPK).map(TransaccionLineaImpuestosPK::getLineId).max(Integer::compareTo);
            int lineNumber = optionalNumeroOrden.orElseGet(lineaBolsa.getTransaccionLineaImpuestosList()::size) + 1;
            final TransaccionLineaImpuestos transaccionLineaImpuesto = new TransaccionLineaImpuestos();
            transaccionLineaImpuesto.setTransaccionLineaImpuestosPK(new TransaccionLineaImpuestosPK(feId, nroOrden, lineNumber));
            transaccionLineaImpuesto.setAbreviatura(IUBLConfig.TAX_TOTAL_OTH_CODE);
            transaccionLineaImpuesto.setMoneda(transaccion.getDOCMONCodigo());
            transaccionLineaImpuesto.setMonto(precioRefMonto);
            transaccionLineaImpuesto.setValorVenta(totalBruto);
            transaccionLineaImpuesto.setPorcentaje(BigDecimal.valueOf(100));
            transaccionLineaImpuesto.setTipoTributo(IUBLConfig.TAX_TOTAL_BPT_ID);
            transaccionLineaImpuesto.setCodigo("C");
            transaccionLineaImpuesto.setNombre(TAX_TOTAL_BPT_NAME);
            transaccionLineaImpuesto.setTransaccionLineas(lineaBolsa);
            lineaBolsa.getTransaccionLineaImpuestosList().add(transaccionLineaImpuesto);

            impuestoBolsaOptional.ifPresent(transaccion.getTransaccionLineas()::remove);

            Predicate<TransaccionImpuestos> predicate = impuesto -> impuesto.getPorcentaje().compareTo(valueOf(100)) == 0
                    && impuesto.getValorVenta().compareTo(totalBruto) == 0 && !TAX_TOTAL_BPT_NAME.equalsIgnoreCase(impuesto.getNombre());
            ArrayList<TransaccionImpuestos> transaccionImpuestos = new ArrayList<>(transaccion.getTransaccionImpuestosList());
            Optional<TransaccionImpuestos> optional = transaccionImpuestos.stream().filter(predicate).findAny();
            optional.ifPresent(transaccion.getTransaccionImpuestosList()::remove);
        });
    }

    private boolean existeNumeroOrden(Set<TransaccionImpuestos> transaccionImpuestos, final int numeroOrden) {
        return transaccionImpuestos.stream().map(TransaccionImpuestos::getTransaccionImpuestosPK).anyMatch(impuesto -> impuesto.getLineId() == numeroOrden);
    }

    private boolean checkIfDocumentoExceded(String feId, String keySociedad) {
        Optional<EnvioDocumento> optional = documentoRepository.findByIdDocumentoAndKeySociedad(feId, keySociedad);
        if (!optional.isPresent()) {
            EnvioDocumento envioDocumento = new EnvioDocumento();
            envioDocumento.setIdDocumento(feId);
            envioDocumento.setKeySociedad(keySociedad);
            envioDocumento.setCantidadEnvio(0);
            documentoRepository.saveAndFlush(envioDocumento);
            return false;
        }
        EnvioDocumento envioDocumento = optional.get();
        int cantidad = Optional.ofNullable(envioDocumento.getCantidadEnvio()).orElse(0);
        if (cantidad < 30) {
            envioDocumento.setCantidadEnvio(++cantidad);
            documentoRepository.saveAndFlush(envioDocumento);
            return false;
        }
        return true;
    }

    public void extraerTransacciones(ICompany sociedad) {
        try {
            String query = factory.getQuery(1, tipoServidor);
            log.info("Ejecutando {} en {}", query, sociedad.getCompanyName());
            IRecordset rs = runRecordsetQuery(sociedad, query);
            boolean existenTransacciones = false;
            Connection conn = null;
            if(estadoJdbc){
                conn = obtenerConexion(conn);
            }
            while (!rs.isEoF()) {
                insertarTransaccion(sociedad, rs,conn);
                existenTransacciones = true;
                rs.moveNext();
            }
            if (!existenTransacciones) log.info("No existen transacciones en la sociedad: {}", sociedad.getCompanyName());
        } catch (SBOCOMException | ConstraintViolationException e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            System.gc();
        }
    }

    @Transactional
    public void insertarTransaccion(ICompany sociedad, IRecordset rs , Connection conn) {
        try {
            log.info("Extrayendo las Transacciones de: {}", sociedad.getCompanyName());
            SBOObjectCreator<Transaccion> objectCreator = new SBOObjectCreator<>();
            Transaccion transaccion = new Transaccion();
            objectCreator.construirReflection(rs, transaccion);
            String feObjectType = transaccion.getFEObjectType();
            Integer docEntry = transaccion.getFEDocEntry();
            if (transaccion.getFEId() == null || transaccion.getFEId().isEmpty()) {
                throw new VenturaExcepcion("[ObjType=" + feObjectType + ", DocEntry=" + docEntry + "] El id de la transacci\u00f3n es vacio. Revise los campos Serie y Correlativo y vuelva intentarlo");
            }
            transaccion.setFEId("0" + transaccion.getFEId());

            if (checkIfDocumentoExceded(transaccion.getFEId(), sociedad.getCompanyDB())) {
                sapboService.ponerEnError(transaccion, "El documento excede los 30 envios por tanto no se extraera nuevamente", sociedad, conn);
                log.error("El documento excede la candidad de envios por tanto no se extraera nuevamente");
                return;
            }
            Optional<Transaccion> optionalTransaccion = transaccionRepository.findById(transaccion.getFEId());
            optionalTransaccion.ifPresent(transaccionRepository::delete);
            transaccion.setFEErrCod(transaccion.getFEEstado());
            descuentoTotal = transaccion.getDOCDescuento();
            transaccion.setTransaccionImpuestosList(extraerTransaccionImpuestos(transaccion.getFEId(), sociedad, feObjectType, docEntry + ""));
            transaccion.setTransaccionLineas(extraerTransaccionLineas(transaccion.getFEId(), sociedad, feObjectType, docEntry));
            //seteamos la transaccion de las cuotas
            transaccion.setTransaccionCuotas(extraerTranssacionCuotas(transaccion.getFEId(), sociedad, feObjectType, docEntry));
            //
            Set<TransaccionLineas> transaccionLineas = transaccion.getTransaccionLineas();
            insertarImpuestoBolsa(transaccionLineas, transaccion);
            List<Usuariocampos> usucamposList = usuariocamposRepository.findAll();
             List<TransaccionUsucampos> transaccionCamposUsuarios = objectCreator.getTransaccionCamposUsuarios(transaccion.getFEId(), rs.getFields(), usucamposList);
            transaccion.setTransaccionUsucamposList(transaccionCamposUsuarios);
            transaccion.setTransaccionContractdocrefs(extraerTransaccionContractDocRefs(transaccion.getFEId(), sociedad, feObjectType, docEntry + ""));
            transaccion.setTransaccionTotalesList(extraerTransaccionTotales(transaccion.getFEId(), sociedad, feObjectType, docEntry + ""));
            transaccion.setTransaccionGuiaRemision(extraerTransaccionGuias(transaccion.getFEId(), sociedad, feObjectType, docEntry + "",conn));
            transaccion.setTransaccionPropiedadesList(extraerTransaccionPropiedades(transaccion.getFEId(), sociedad, feObjectType, docEntry));
            transaccion.setTransaccionDocrefersList(ExtraerTransaccionDocReferes(transaccion.getFEId(), sociedad, feObjectType, docEntry + ""));
            transaccion.setTransaccionComprobantePagos(ExtraerTransaccionComprobantes(transaccion.getFEId(), sociedad, feObjectType, docEntry + ""));
            transaccion.setTransaccionAnticipoList(extraerTransaccionAnticipos(transaccion.getFEId(), sociedad, feObjectType, docEntry + ""));
            transaccion.setDOCDescuentoTotal(descuentoTotal);
            transaccion.setKeySociedad(sociedad.getCompanyDB());

            sapboService.ponerEnSeguimiento(transaccion, sociedad,conn);

            /** Harol 29-03-2024 Obtener Texto_Amplio*/
            if(estadoJdbc && !nameUserField.trim().isEmpty()) {
                String textoAmplio = obtenerTextoAmplio(13, transaccion.getFEDocEntry(),conn);
                if(textoAmplio != null && !textoAmplio.trim().isEmpty()){
                    log.info("Se obtuvo de bpvs_FE_ContractDocRefers {}: {}",nameUserField,textoAmplio);
                    List<Usuariocampos> usucamposList2 = usuariocamposRepository.findAll();
                    int usuId = 0;
                    for (Usuariocampos usu: usucamposList2){
                        if(usu.getNombre().equals(nameUserField)){
                            usuId = usu.getId();
                        }
                    }
                    Set<TransaccionContractdocref> tcdr = transaccion.getTransaccionContractdocrefs();
                    for (TransaccionContractdocref tcd : tcdr) {
                        if (tcd.getTransaccionContractdocrefPK().getUSUCMPId() == usuId) {
                            if (textoAmplio != null || !textoAmplio.trim().isEmpty()) {
                                tcd.setValor(textoAmplio);
                            }
                        }
                    }
                    transaccion.setTransaccionContractdocrefs(tcdr);
                }else{
                    log.info("JDBC - el campo {} no se pudo obtener de bpvs_FE_ContractDocRefers()",nameUserField);
                }
            }
            /** */
            transaccionRepository.saveAndFlush(transaccion);
            log.info("[{}] No: [{}] insertada con estado [{}]", transaccion.getFEFormSAP(), transaccion.getFEId(), transaccion.getFEEstado());
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        } finally {
            System.gc();
        }
    }
    private Connection obtenerConexion(Connection connection){
        String url = "jdbc:sap://" + servidorBDJdbc + "/?currentschema=" + database + "&encrypt=true&validateCertificate=false";
        if (estadoSqlServer) {
            url = "jdbc:sqlserver://" + servidorBDJdbc + ";databaseName=" + database;
        }
        Properties props = new Properties();
        props.setProperty("user", databaseUsername);
        props.setProperty("password", databasePassword);
        try {
            connection = DriverManager.getConnection(url, props);
            log.info("¡Conexión exitosa por JDBC a {}!", database);
        } catch (SQLException e) {
            log.info("¡Conexion Fallida por JDBC! - Error: [{}]", e.getMessage());
            connection = null;
            //e.printStackTrace();
        }
        return connection;
    }
    private BigDecimal obtenerNumeroBultos(int parametro1, int parametro2,String name,Connection conn){
        BigDecimal numeroBultos = BigDecimal.ZERO;;
        try{
            if (conn != null) {
                String sqlQuery = database+".\"bpvs_FE_Base_GuiaRemision\"(?, ?)}";
                if(estadoSqlServer){
                    sqlQuery = "dbo.\"bpvs_FE_Base_GuiaRemision\"(?, ?)}";
                }
                CallableStatement stmt = conn.prepareCall("{call "+sqlQuery);
                stmt.setInt(1, parametro1);
                stmt.setInt(2, parametro2);
                ResultSet rs = stmt.executeQuery();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                boolean columnaExiste = false;
                int tipoColumna = Types.NULL;
                for (int i = 1; i <= columnCount; i++) {
                    if (metaData.getColumnName(i).equalsIgnoreCase(name)) {
                        columnaExiste = true;
                        tipoColumna = metaData.getColumnType(i);
                        break;
                    }
                }
                if(columnaExiste){
                    while (rs.next()) {
                        if (tipoColumna == Types.INTEGER) {
                            int valorInt = rs.getInt(name);
                            numeroBultos = BigDecimal.valueOf(valorInt);
                        } else if (tipoColumna == Types.VARCHAR || tipoColumna == Types.CHAR) {
                            String valorString = rs.getString(name);
                            if (valorString != null && !valorString.trim().isEmpty()) {
                                numeroBultos = new BigDecimal(valorString.trim());
                            }
                        }
                    }
                }else{
                    log.info("obtenerNumeroBultos() JDBC valor {} no se encontro en bpvs_FE_Base_GuiaRemision",name);
                }

                rs.close();
                stmt.close();
            }else{
                log.info("obtenerNumeroBultos() JDBC - ¡Conexion fallida!");
            }
        } catch (SQLException e) {
            log.info("obtenerNumeroBultos() - Error: [{}]",e.getMessage());
            //e.printStackTrace();
        }

        return numeroBultos;
    }
    /** Harol 29-03-2024 conexion sql y obtencion de campo del store procude*/
    private String obtenerTextoAmplio(int parametro1, int parametro2, Connection conn){
        String texto_amplio = "";
        try{
            if (conn != null) {
                log.info("obtenerTextoAmplio() - ¡Conexion a HANA exitosa!");
                String sqlQuery = database+".\"bpvs_FE_ContractDocRefers\"(?, ?)}";
                if(estadoSqlServer){
                    sqlQuery = "dbo.\"bpvs_FE_ContractDocRefers\"(?, ?)}";
                }
                CallableStatement stmt = conn.prepareCall("{call "+sqlQuery);
                stmt.setInt(1, parametro1);
                stmt.setInt(2, parametro2);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String valor = rs.getString(nameUserField);
                    //String valor = rs.getString("nameUserField");
                    texto_amplio = valor;
                }
                rs.close();
                stmt.close();
            }else{
                log.info("obtenerTextoAmplio() - ¡Conexion a HANA fallida!");
            }
        } catch (SQLException e) {
            log.info("obtenerTextoAmplio() - Error: [{}]",e.getMessage());
            //e.printStackTrace();
        }
        return texto_amplio;
    }
    /** */
    private Set<TransaccionImpuestos> extraerTransaccionImpuestos(String feID, ICompany sociedad, String... params) throws VenturaExcepcion {
        Set<TransaccionImpuestos> impuestos = new HashSet<>();
        try {
            boolean isUbl21 = versionUBLFactura.equals("21") || versionUBLBoleta.equals("21") || versionUBLNotaCredito.equals("21") || versionUBLNotaDebito.equals("21");
            boolean isUbl20 = versionUBLFactura.equals("20") || versionUBLBoleta.equals("20") || versionUBLNotaCredito.equals("20") || versionUBLNotaDebito.equals("20");
            if (isUbl21 || isUbl20) {
                IRecordset rs = SBOCOMUtil.runRecordsetQuery(sociedad, factory.getQuery(isUbl21 ? 2 : 18, tipoServidor, params));
                while (!rs.isEoF()) {
                    TransaccionImpuestos impuesto = new TransaccionImpuestos(new TransaccionImpuestosPK(feID, rs.getFields().item("LineId").getValueInteger()));
                    impuesto.setMoneda(rs.getFields().item("Moneda").getValueString());
                    impuesto.setMonto(valueOf(rs.getFields().item("Monto").getValueDouble()));
                    impuesto.setPorcentaje(valueOf(rs.getFields().item("Porcentaje").getValueDouble()));
                    impuesto.setTipoTributo(rs.getFields().item("TipoTributo").getValueString());
                    impuesto.setTipoAfectacion(rs.getFields().item("TipoAfectacion").getValueString());
                    if (isUbl21) {
                        impuesto.setAbreviatura(rs.getFields().item("Abreviatura").getValueString());
                        impuesto.setCodigo(rs.getFields().item("Codigo").getValueString());
                        impuesto.setValorVenta(valueOf(rs.getFields().item("ValorVenta").getValueDouble()));
                        impuesto.setNombre(rs.getFields().item("Nombre").getValueString());
                    }
                    if (isUbl20) {
                        impuesto.setTierRange(rs.getFields().item("TierRange").getValueString());
                    }
                    impuestos.add(impuesto);
                    rs.moveNext();
                }
            }
            return impuestos;
        } catch (Exception ex) {
            throw new VenturaExcepcion("Impuesto de cabecera. " + ex.getMessage());
        }
    }

    private Set<TransaccionLineas> extraerTransaccionLineas(String feID, ICompany sociedad, String feObjectType, Integer docEntry) throws VenturaExcepcion {
        Set<TransaccionLineas> lineas = new HashSet<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(sociedad, factory.getQuery(3, tipoServidor, feObjectType, docEntry + ""));
            crearCamposUsuario(rs);
            while (!rs.isEoF()) {
                TransaccionLineas linea = new TransaccionLineas(new TransaccionLineasPK(feID, rs.getFields().item("NroOrden").getValueInteger()));
                linea.setCantidad(valueOf(rs.getFields().item("Cantidad").getValueDouble()));
                linea.setDSCTOMonto(valueOf(rs.getFields().item("DSCTO_Monto").getValueDouble()));
                linea.setDSCTOPorcentaje(valueOf(rs.getFields().item("DSCTO_Porcentaje").getValueDouble()));
                linea.setDescripcion(rs.getFields().item("Descripcion").getValueString());
                linea.setPrecioDscto(valueOf(rs.getFields().item("PrecioDscto").getValueDouble()));
                linea.setPrecioIGV(valueOf(rs.getFields().item("PrecioIGV").getValueDouble()));
                linea.setTotalLineaSinIGV(valueOf(rs.getFields().item("TotalLineaSinIGV").getValueDouble()));
                linea.setTotalLineaConIGV(valueOf(rs.getFields().item("TotalLineaConIGV").getValueDouble()));
                linea.setPrecioRefCodigo(rs.getFields().item("PrecioRef_Codigo").getValueString());
                linea.setPrecioRefMonto(valueOf(rs.getFields().item("PrecioRef_Monto").getValueDouble()));
                linea.setUnidad(rs.getFields().item("Unidad").getValueString());
                linea.setUnidadSunat(rs.getFields().item("UnidadSunat").getValueString());
                linea.setCodArticulo(rs.getFields().item("CodArticulo").getValueString());
                linea.setCodSunat(rs.getFields().item("CodSunat").getValueString());
                linea.setTotalBruto(valueOf(rs.getFields().item("TotalBruto").getValueDouble()));
                linea.setLineaImpuesto(valueOf(rs.getFields().item("LineaImpuesto").getValueDouble()));
                linea.setCodProdGS1(rs.getFields().item("CodProdGS1").getValueString());
                linea.setCodUbigeoOrigen(rs.getFields().item("CodUbigeoOrigen").getValueString());
                linea.setDirecOrigen(rs.getFields().item("DirecOrigen").getValueString());
                linea.setCodUbigeoDestino(rs.getFields().item("CodUbigeoDestino").getValueString());
                linea.setDirecDestino(rs.getFields().item("DirecDestino").getValueString());
                linea.setDetalleViaje(rs.getFields().item("DetalleViaje").getValueString());
                linea.setValorTransporte(valueOf(rs.getFields().item("ValorTransporte").getValueDouble()));
                linea.setValorCargaEfectiva(valueOf(rs.getFields().item("ValorCargaEfectiva").getValueDouble()));
                linea.setValorCargaUtil(valueOf(rs.getFields().item("ValorCargaUtil").getValueDouble()));
                linea.setConfVehicular(rs.getFields().item("ConfVehicular").getValueString());
                linea.setCUtilVehiculo(valueOf(rs.getFields().item("CUtilVehiculo").getValueDouble()));
                linea.setCEfectivaVehiculo(valueOf(rs.getFields().item("CEfectivaVehiculo").getValueDouble()));
                linea.setValorRefTM(valueOf(rs.getFields().item("ValorRefTM").getValueDouble()));
                linea.setValorPreRef(valueOf(rs.getFields().item("ValorPreRef").getValueDouble()));
                linea.setFactorRetorno(rs.getFields().item("FactorRetorno").getValueString());

                linea.setNombreEmbarcacion(rs.getFields().item("NombreEmbarcacion").getValueString());
                linea.setTipoEspeciaVendida(rs.getFields().item("TipoEspecieVendida").getValueString());
                linea.setLugarDescarga(rs.getFields().item("LugarDescarga").getValueString());
                linea.setFechaDescarga(rs.getFields().item("FechaDescarga").getValueDate());
                linea.setCantidadEspecieVendida(valueOf(rs.getFields().item("CantidadEspecieVendida").getValueDouble()));

                //GUIAS REST API SUNAT
                linea.setSubPartida(rs.getFields().item("SubPartida").getValueString());
                linea.setIndicadorBien(rs.getFields().item("IndicadorBien").getValueString());
                linea.setNumeracion(rs.getFields().item("Numeracion").getValueString());
                linea.setNumeroSerie(rs.getFields().item("NumeroSerie").getValueString());


                try {
                    Optional<Character> itmBolsaOptional = Optional.ofNullable(rs.getFields().item("ItmBolsa").getValueString()).map(s -> s.isEmpty() ? null : s.charAt(0));
                    linea.setItemBolsa(itmBolsaOptional.orElse('N'));
                } catch (Exception e) {
                    log.info("No se encuentra el Item de Bolsa.");
                    linea.setItemBolsa('N');
                }
                Integer resultado = (rs.getFields().item("NroOrden").getValueInteger() - 1);
                String numeroOrden = String.valueOf(resultado);
                linea.setTransaccionLineaImpuestosList(extraerTransaccionLineasImpuestos(linea.getTransaccionLineasPK().getNroOrden(), feID, sociedad, feObjectType, docEntry + "", numeroOrden));
                List<Usuariocampos> usuariocampos = usuariocamposRepository.findAll();
                SBOObjectCreator<Transaccion> objectCreator = new SBOObjectCreator<>();
                List<TransaccionLineasUsucampos> camposUsuario = objectCreator.getTransaccionLineaCamposUsuarios(feID, linea.getTransaccionLineasPK().getNroOrden(), rs.getFields(), usuariocampos);
                linea.setTransaccionLineasUsucampos(new HashSet<>(camposUsuario));
                linea.setTransaccionLineasBillrefList(extraerTransaccionLineasBillRefs(linea.getTransaccionLineasPK().getNroOrden(), feID, sociedad, feObjectType, docEntry + "", numeroOrden));
                descuentoTotal = descuentoTotal.add(valueOf(rs.getFields().item("DSCTO_Monto").getValueDouble()));
                lineas.add(linea);
                rs.moveNext();
            }
            return lineas;
        } catch (SBOCOMException | VenturaExcepcion ex) {
            throw new VenturaExcepcion("Lineas de Transaccion. ", ex);
        }
    }

    //Extraer transaccionesCuotas
    private Set<TransaccionCuotas> extraerTranssacionCuotas(String feID, ICompany sociedad, String feObjectType, Integer docEntry) throws VenturaExcepcion {
        Set<TransaccionCuotas> lineasCuotas = new HashSet<>();
        try {
            //se relaciona el 20 con el procedure que se ejecuta en sqlServer
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(sociedad, factory.getQuery(20, tipoServidor, feObjectType, docEntry + ""));

            while (!rs.isEoF()) {
                TransaccionCuotas linea = new TransaccionCuotas(new TransaccionCuotasPK(feID,rs.getFields().item("NroOrden").getValueInteger()));
                IFields fields = rs.getFields();

                linea.setCuota(fields.item("Cuota").getValueString());
                linea.setFechaCuota(fields.item("FechaCuota").getValueDate());
                linea.setMontoCuota(valueOf(fields.item("MontoCuota").getValueDouble()));
                linea.setFechaEmision(fields.item("FechaEmision").getValueDate());
                linea.setFormaPago(fields.item("FormaPago").getValueString());

                lineasCuotas.add(linea);  // se agrega las lineas de cada couta

                rs.moveNext();
            }
            return lineasCuotas; //retorna las lineas de cuotas de la transaccion a la cual pertenece

        } catch (SBOCOMException | VenturaExcepcion ex) {
            throw new VenturaExcepcion("Transaccion lineas cuotas ", ex);
        }

    }

    private Set<TransaccionLineaImpuestos> extraerTransaccionLineasImpuestos(Integer nroOrden, String feID, ICompany sociedad, String... params) throws VenturaExcepcion {
        Set<TransaccionLineaImpuestos> impuestos = new HashSet<>();
        try {
            boolean isUbl21 = versionUBLFactura.equals("21") || versionUBLBoleta.equals("21") || versionUBLNotaCredito.equals("21") || versionUBLNotaDebito.equals("21");
            boolean isUbl20 = versionUBLFactura.equals("20") || versionUBLBoleta.equals("20") || versionUBLNotaCredito.equals("20") || versionUBLNotaDebito.equals("20");
            if (isUbl21 || isUbl20) {
                IRecordset rs = SBOCOMUtil.runRecordsetQuery(sociedad, factory.getQuery(isUbl21 ? 4 : 19, tipoServidor, params));
                while (!rs.isEoF()) {
                    IFields iFields = rs.getFields();
                    TransaccionLineaImpuestos impuesto = new TransaccionLineaImpuestos(new TransaccionLineaImpuestosPK(feID, nroOrden, iFields.item("LineId").getValueInteger()));
                    impuesto.setMoneda(iFields.item("Moneda").getValueString());
                    impuesto.setMonto(valueOf(iFields.item("Monto").getValueDouble()));
                    impuesto.setPorcentaje(valueOf(iFields.item("Porcentaje").getValueDouble()));
                    impuesto.setTipoTributo(iFields.item("TipoTributo").getValueString());
                    impuesto.setTipoAfectacion(iFields.item("TipoAfectacion").getValueString());
                    impuesto.setTierRange(iFields.item("TierRange").getValueString());
                    if (isUbl21) {
                        impuesto.setAbreviatura(iFields.item("Abreviatura").getValueString());
                        impuesto.setCodigo(iFields.item("Codigo").getValueString());
                        impuesto.setValorVenta(valueOf(iFields.item("ValorVenta").getValueDouble()));
                        impuesto.setNombre(iFields.item("Nombre").getValueString());
                    }
                    impuestos.add(impuesto);
                    rs.moveNext();
                }
            }
            return impuestos;
        } catch (Exception ex) {
            throw new VenturaExcepcion("Impuesto de linea. ", ex);
        }
    }

    private Set<TransaccionLineasBillref> extraerTransaccionLineasBillRefs(int linea, String feID, ICompany Sociedad, String... parametros) throws VenturaExcepcion {
        Set<TransaccionLineasBillref> billrefs = new HashSet<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(5, tipoServidor, parametros));
            while (!rs.isEoF()) {
                TransaccionLineasBillref billref = new TransaccionLineasBillref(new TransaccionLineasBillrefPK(feID, linea, rs.getFields().item("LineId").getValueInteger()));
                billref.setAdtDocRefId(rs.getFields().item("AdtDocRef_Id").getValueString());
                billref.setAdtDocRefSchemaId(rs.getFields().item("AdtDocRef_SchemaId").getValueString());
                billref.setInvDocRefDocTypeCode(rs.getFields().item("InvDocRef_DocTypeCode").getValueString());
                billref.setInvDocRefId(rs.getFields().item("InvDocRef_Id").getValueString());
                billrefs.add(billref);
                rs.moveNext();
            }
            return billrefs;
        } catch (Exception ex) {
            throw new VenturaExcepcion("Impuesto de linea. ", ex);
        }
    }

    private Set<TransaccionContractdocref> extraerTransaccionContractDocRefs(String feID, ICompany Sociedad, String... params) throws VenturaExcepcion {
        Set<TransaccionContractdocref> docrefs = new HashSet<>();
        SBOObjectCreator<Transaccion> objectCreator = new SBOObjectCreator<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(7, tipoServidor, params));
            while (!rs.isEoF()) {
                List<Usuariocampos> usuariocampos = usuariocamposRepository.findAll();
                docrefs.addAll(objectCreator.getTransaccionContractdocref(rs, feID, usuariocamposRepository, usuariocampos));
                rs.moveNext();
            }
            return docrefs;
        } catch (Exception ex) {
            throw new VenturaExcepcion("ContractDocRefs. ", ex);
        }
    }


    private Set<TransaccionTotales> extraerTransaccionTotales(String feID, ICompany Sociedad, String... params) throws VenturaExcepcion {
        Set<TransaccionTotales> totales = new HashSet<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(8, tipoServidor, params));
            log.info("Se verifico que hay una extraccion de totales {}", rs.getRecordCount());
            while (!rs.isEoF()) {
                IFields campos = rs.getFields();
                TransaccionTotales tt = new TransaccionTotales(new TransaccionTotalesPK(feID, campos.item("Id").getValueString()));
                tt.setMonto(BigDecimal.valueOf(campos.item("Monto").getValueDouble()));
                tt.setPrcnt(BigDecimal.valueOf(campos.item("Prcnt").getValueDouble()));
                totales.add(tt);
                rs.moveNext();
            }
            return totales;
        } catch (Exception ex) {
            throw new VenturaExcepcion("TransccionTotales. " + ex.getMessage());
        }
    }

    private TransaccionGuiaRemision extraerTransaccionGuias(String feID, ICompany sociedad, String objectType, String docEntry,Connection connection) throws VenturaExcepcion {
        TransaccionGuiaRemision transaccionGuiaRemision = new TransaccionGuiaRemision(feID);
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(sociedad, factory.getQuery(16, tipoServidor, objectType, docEntry));
            while (!rs.isEoF()) {
                IFields iFields = rs.getFields();
                transaccionGuiaRemision.setCodigoMotivo(iFields.item("CodigoMotivo").getValueString());
                transaccionGuiaRemision.setCodigoPuerto(iFields.item("CodigoPuerto").getValueString());
                transaccionGuiaRemision.setDescripcionMotivo(iFields.item("DescripcionMotivo").getValueString());
                transaccionGuiaRemision.setDocumentoConductor(iFields.item("DocumentoConductor").getValueString());
                transaccionGuiaRemision.setLicenciaConducir(iFields.item("LicenciaConductor").getValueString());
                transaccionGuiaRemision.setFEId(feID);
                Date fechaInicioTraslado = addHoursToJavaUtilDate(iFields.item("FechaInicioTraslado").getValueDate(), 3);
                System.out.println("Fecha Inicio de Traslado:" + fechaInicioTraslado);
                transaccionGuiaRemision.setFechaInicioTraslado(fechaInicioTraslado);
                transaccionGuiaRemision.setIndicadorTransbordoProgramado(iFields.item("IndicadorTransbordoProgramado").getValueString());
                transaccionGuiaRemision.setModalidadTraslado(iFields.item("ModalidadTraslado").getValueString());
                transaccionGuiaRemision.setNombreRazonTransportista(iFields.item("NombreRazonTransportista").getValueString());
                transaccionGuiaRemision.setNumeroContenedor(iFields.item("NumeroContenedor").getValueString());
                transaccionGuiaRemision.setPeso(BigDecimal.valueOf(iFields.item("Peso").getValueDouble()));
                transaccionGuiaRemision.setPlacaVehiculo(iFields.item("PlacaVehiculo").getValueString());
                transaccionGuiaRemision.setRUCTransporista(iFields.item("RUCTransporista").getValueString());
                transaccionGuiaRemision.setTipoDOCTransportista(iFields.item("TipoDOCTransportista").getValueString());
                transaccionGuiaRemision.setTipoDocConductor(iFields.item("TipoDocConductor").getValueString());
                transaccionGuiaRemision.setUnidadMedida(iFields.item("UnidadMedida").getValueString());
                transaccionGuiaRemision.setDireccionPartida(iFields.item("DireccionPartida").getValueString());
                transaccionGuiaRemision.setUbigeoPartida(iFields.item("UbigeoPartida").getValueString());

                //GUIAS REMISION REST

                transaccionGuiaRemision.setTipoDocRelacionadoTrans(iFields.item("TipoDocRelacionadoTrans").getValueString());
                transaccionGuiaRemision.setTipoDocRelacionadoTransDesc(iFields.item("TipoDocRelacionadoTransDesc").getValueString());
                transaccionGuiaRemision.setDocumentoRelacionadoTrans(iFields.item("DocumentoRelacionadoTrans").getValueString());
                transaccionGuiaRemision.setIndicadorTransbordo(iFields.item("IndicadorTransbordo").getValueString());
                transaccionGuiaRemision.setIndicadorTraslado(iFields.item("IndicadorTraslado").getValueString());
                transaccionGuiaRemision.setIndicadorRetorno(iFields.item("IndicadorRetorno").getValueString());
                transaccionGuiaRemision.setIndicadorRetornoVehiculo(iFields.item("IndicadorRetornoVehiculo").getValueString());
                transaccionGuiaRemision.setIndicadorTrasladoTotal(iFields.item("IndicadorTrasladoTotal").getValueString());
                transaccionGuiaRemision.setIndicadorRegistro(iFields.item("IndicadorRegistro").getValueString());
                transaccionGuiaRemision.setNroRegistroMTC(iFields.item("NroRegistroMTC").getValueString());
                transaccionGuiaRemision.setNombreApellidosConductor(iFields.item("NombreApellidosConductor").getValueString());
                transaccionGuiaRemision.setUbigeoLlegada(iFields.item("UbigeoLlegada").getValueString());
                transaccionGuiaRemision.setDireccionLlegada(iFields.item("DireccionLlegada").getValueString());
                transaccionGuiaRemision.setTarjetaCirculacion(iFields.item("TarjetaCirculacion").getValueString());
                transaccionGuiaRemision.setNumeroPrecinto(iFields.item("numeroPrecinto").getValueString());
                transaccionGuiaRemision.setNumeroContenedor2(iFields.item("numeroContenedor2").getValueString());
                transaccionGuiaRemision.setNumeroPrecinto2(iFields.item("numeroPrecinto2").getValueString());
                transaccionGuiaRemision.setDescripcionPuerto(iFields.item("DescripcionPuerto").getValueString());
                transaccionGuiaRemision.setCodigoAereopuerto(iFields.item("CodigoAereopuerto").getValueString());
                transaccionGuiaRemision.setDescripcionAereopuerto(iFields.item("DescripcionAereopuerto").getValueString());
                /** Harol 29-03-2024 llenado de data*/
                transaccionGuiaRemision.setGRT_TipoDocRemitente(validarFieldItem(iFields,"GRT_TipoDocRemitente",""));
                transaccionGuiaRemision.setGRT_DocumentoRemitente(validarFieldItem(iFields,"GRT_DocumentoRemitente",""));
                transaccionGuiaRemision.setGRT_NombreRazonRemitente(validarFieldItem(iFields,"GRT_NombreRazonRemitente",""));
                transaccionGuiaRemision.setGRT_IndicadorPagadorFlete(validarFieldItem(iFields,"GRT_IndicadorPagadorFlete",""));
                //validarFieldItem(iFields,"IndTrasladoVehiculoM1L","")
                transaccionGuiaRemision.setGRT_TipoDocDestinatario(validarFieldItem(iFields,"GRT_TipoDocDestinatario",""));
                transaccionGuiaRemision.setGRT_DocumentoDestinatario(validarFieldItem(iFields,"GRT_DocumentoDestinatario",""));
                transaccionGuiaRemision.setGRT_NombreRazonDestinatario(validarFieldItem(iFields,"GRT_NombreRazonDestinatario",""));

                transaccionGuiaRemision.setGRT_NumeroTUCEPrincipal(validarFieldItem(iFields,"GRT_NumeroTUCEPrincipal",""));
                transaccionGuiaRemision.setGRT_EntidadEmisoraPrincipal(validarFieldItem(iFields,"GRT_EntidadEmisoraPrincipal",""));
                transaccionGuiaRemision.setGRT_PlacaVehiculoSecundario(validarFieldItem(iFields,"GRT_PlacaVehiculoSecundario",""));
                transaccionGuiaRemision.setGRT_NumeroTUCESecuendario(validarFieldItem(iFields,"GRT_NumeroTUCESecuendario",""));
                transaccionGuiaRemision.setGRT_EntidadEmisoraSecundario(validarFieldItem(iFields,"GRT_EntidadEmisoraSecundario",""));
                /** */

                /** Harol 29-06-2024 insetado NumeroBultos con JDBC*/
                if(connection != null){
                    transaccionGuiaRemision.setNumeroBultos(obtenerNumeroBultos(15,Integer.parseInt(docEntry),"NumeroBultos",connection));
                }else{
                    transaccionGuiaRemision.setNumeroBultos(BigDecimal.valueOf(iFields.item("NumeroBultos").getValueDouble()));
                }
                /** */

                transaccionGuiaRemision.setTicketRest(iFields.item("TicketRest").getValueString());
                rs.moveNext();
            }
            return transaccionGuiaRemision;
        } catch (Exception ex) {
            throw new VenturaExcepcion("TransccionTotales. " + ex.getMessage());
        }
    }

    private String validarFieldItem(IFields iFields,String item , String retorno){
        try {
            return (iFields != null && iFields.item(item).getValueString() != null) ? iFields.item(item).getValueString() : retorno;
        }catch (Exception ex){
            return retorno;
        }
    }

    private Set<TransaccionPropiedades> extraerTransaccionPropiedades(String feID, ICompany Sociedad, String objectType, Integer docEntry) throws VenturaExcepcion {
        Set<TransaccionPropiedades> propiedades = new HashSet<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(9, tipoServidor, objectType, docEntry + ""));
            while (!rs.isEoF()) {
                IFields campos = rs.getFields();
                TransaccionPropiedades tt = new TransaccionPropiedades(new TransaccionPropiedadesPK(feID, campos.item("Id").getValueString()));
                tt.setValor(campos.item("Valor").getValueString());
                tt.setDescription(campos.item("Description").getValueString());
                propiedades.add(tt);
                rs.moveNext();
            }
            return propiedades;
        } catch (Exception ex) {
            throw new VenturaExcepcion("TransccionPropiedades. " + ex.getMessage());
        }
    }

    private Set<TransaccionDocrefers> ExtraerTransaccionDocReferes(String feID, ICompany Sociedad, String... params) throws VenturaExcepcion {
        Set<TransaccionDocrefers> docrefs = new HashSet<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(10, tipoServidor, params));
            while (!rs.isEoF()) {
                IFields campos = rs.getFields();
                int lineid = campos.item("LineId").getValueInteger();
                TransaccionDocrefers docref = new TransaccionDocrefers(new TransaccionDocrefersPK(feID, lineid));
                docref.setId(campos.item("Id").getValueString());
                docref.setTipo(campos.item("Tipo").getValueString());
                docrefs.add(docref);
                rs.moveNext();
            }
            return docrefs;
        } catch (Exception ex) {
            throw new VenturaExcepcion("ExtraerTransaccionDocReferes. " + ex.getMessage());
        }
    }

    private Set<TransaccionComprobantePago> ExtraerTransaccionComprobantes(String feID, ICompany Sociedad, String... params) throws VenturaExcepcion {
        Set<TransaccionComprobantePago> comprobante = new HashSet<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(11, tipoServidor, params));
            crearCamposUsuario(rs);
            while (!rs.isEoF()) {
                IFields iFields = rs.getFields();
                TransaccionComprobantePago comprobantePago = new TransaccionComprobantePago(new TransaccionComprobantePagoPK(feID, iFields.item("NroOrden").getValueInteger()));
                try {
                    comprobantePago.setCPFecha(iFields.item("CP_Fecha").getValueDate());
                } catch (Exception e) {
                    comprobantePago.setCPFecha(new Date());
                }
                try {
                    comprobantePago.setDOCFechaEmision(iFields.item("DOC_FechaEmision").getValueDate());
                } catch (Exception e) {
                    comprobantePago.setDOCFechaEmision(new Date());
                }
                try {
                    comprobantePago.setPagoFecha(iFields.item("PagoFecha").getValueDate());
                } catch (Exception e) {
                    comprobantePago.setPagoFecha(new Date());
                }
                try {
                    comprobantePago.setTCFecha(iFields.item("TC_Fecha").getValueDate());
                } catch (Exception e) {
                    comprobantePago.setTCFecha(new Date());
                }
                comprobantePago.setCPImporte(BigDecimal.valueOf(iFields.item("CP_Importe").getValueDouble()));
                comprobantePago.setCPImporteTotal(BigDecimal.valueOf(iFields.item("CP_ImporteTotal").getValueDouble()));
                comprobantePago.setCPMoneda(iFields.item("CP_Moneda").getValueString());
                comprobantePago.setCPMonedaMontoNeto(iFields.item("CP_MonedaMontoNeto").getValueString());

                comprobantePago.setDOCImporte(BigDecimal.valueOf(iFields.item("DOC_Importe").getValueDouble()));
                comprobantePago.setDOCMoneda(iFields.item("DOC_Moneda").getValueString());
                comprobantePago.setDOCNumero(iFields.item("DOC_Numero").getValueString());
                comprobantePago.setDOCTipo(iFields.item("DOC_Tipo").getValueString());

                comprobantePago.setPagoImporteSR(BigDecimal.valueOf(iFields.item("PagoImporteSR").getValueDouble()));
                comprobantePago.setPagoMoneda(iFields.item("PagoMoneda").getValueString());
                comprobantePago.setPagoNumero(iFields.item("PagoNumero").getValueInteger().toString());
                comprobantePago.setTCFactor(BigDecimal.valueOf(iFields.item("TC_Factor").getValueDouble()));

                comprobantePago.setTCMonedaObj(iFields.item("TC_MonedaObj").getValueString());
                comprobantePago.setTCMonedaRef(iFields.item("TC_MonedaRef").getValueString());
                List<Usuariocampos> usuariocampos = usuariocamposRepository.findAll();
                comprobantePago.setTransaccionComprobantepagoUsuarioList(getTransaccionComprobanteCampoUsuario(feID, comprobantePago.getTransaccionComprobantePagoPK().getNroOrden(), iFields, usuariocampos));
                comprobante.add(comprobantePago);
                rs.moveNext();
            }
            return comprobante;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new VenturaExcepcion("Comprobante de Pago. " + ex.getMessage());
        }
    }

    private List<TransaccionComprobantepagoUsuario> getTransaccionComprobanteCampoUsuario(String FE_ID, int nroOrden, IFields campos, List<Usuariocampos> usuariocampos) {
        List<TransaccionComprobantepagoUsuario> lstComprobantePagoUsuario = new ArrayList<>();
        for (int i = 0; i < campos.getCount(); i++) {
            IField icampo = campos.item(i);
            String ncampo = icampo.getName();
            Integer tcampo = icampo.getType();
            Object objeto = null;
            if (ncampo.startsWith("U_")) {
                String campoElemento = ncampo.substring(2);
                Optional<Usuariocampos> optional = usuariocampos.parallelStream().filter(usuariocampo -> campoElemento.equalsIgnoreCase(usuariocampo.getNombre())).findAny();
                if (optional.isPresent()) {
                    try {
                        Usuariocampos campoUsuario = optional.get();
                        TransaccionComprobantepagoUsuario cu = new TransaccionComprobantepagoUsuario(new TransaccionComprobantepagoUsuarioPK(campoUsuario.getId(), FE_ID, nroOrden));
                        switch (tcampo) {
                            case 0:
                            case 1:  //Alpha,Memo
                                objeto = icampo.getValueString();
                                break;
                            case 2:  //Numeric
                                objeto = icampo.getValueInteger();
                                break;
                            case 3:  //Date
                                objeto = icampo.getValueDate();
                                break;
                            case 4: //Float
                                objeto = BigDecimal.valueOf(icampo.getValueDouble());
                                break;
                        }
                        cu.setValor(objeto == null ? "" : objeto.toString());
                        lstComprobantePagoUsuario.add(cu);
                    } catch (SBOCOMException ex) {
                        log.error("Error de SAP.", ex);
                    }
                }
            }
        }
        return lstComprobantePagoUsuario;
    }

    private Set<TransaccionAnticipo> extraerTransaccionAnticipos(String feID, ICompany Sociedad, String... params) throws VenturaExcepcion {
        Set<TransaccionAnticipo> lineas = new HashSet<>();
        IRecordset rs;
        try {
            rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(6, tipoServidor, params));
            crearCamposUsuario(rs);
            while (!rs.isEoF()) {
                TransaccionAnticipo linea = new TransaccionAnticipo(new TransaccionAnticipoPK(feID, rs.getFields().item("NroAnticipo").getValueInteger()));
                linea.setAnticipoMonto(BigDecimal.valueOf(rs.getFields().item("Anticipo_Monto").getValueDouble()));
                linea.setAntiDOCTipo(rs.getFields().item("AntiDOC_Tipo").getValueString());
                linea.setAntiDOCSerieCorrelativo(rs.getFields().item("AntiDOC_Serie_Correlativo").getValueString());
                linea.setDOCNumero(rs.getFields().item("DOC_Numero").getValueString());
                linea.setDOCTipo(rs.getFields().item("DOC_Tipo").getValueString());
                linea.setDOCMoneda(rs.getFields().item("DOC_Moneda").getValueString());
                lineas.add(linea);
                rs.moveNext();
            }
            return lineas;
        } catch (SBOCOMException ex) {
            throw new VenturaExcepcion(" Transaccion de Anticipo. ", ex);
        }
    }

    private void crearCamposUsuario(IRecordset rs) throws VenturaExcepcion {
        try {
            //Si existen campos que inician con U_
            IFields campos = rs.getFields();
            int cantidad = campos.getCount();
            for (int i = 0; i < cantidad; i++) {
                IField icampo = campos.item(i);
                String ncampo = icampo.getName();
                if (ncampo.startsWith("U_")) {
                    ncampo = ncampo.substring(2);
                    Optional<Usuariocampos> optional = usuariocamposRepository.findByNombre(ncampo);
                    if (!optional.isPresent()) {
                        Usuariocampos uc = new Usuariocampos();
                        uc.setNombre(ncampo);
                        try {
                            usuariocamposRepository.saveAndFlush(uc);
                        } catch (Exception ex) {
                            throw new VenturaExcepcion("No fue posible crear el campo: " + ncampo + ". " + ex.getMessage());
                        }
                    }
                }
            }
            //Propiedades de configuracion
            for (TransaccionUsucampos tuc : Transaccion.propiedades) {
                String ncampo = tuc.getUsuariocampos().getNombre();
                Optional<Usuariocampos> optional = usuariocamposRepository.findByNombre(ncampo);
                if (optional.isPresent()) {
                    try {
                        usuariocamposRepository.saveAndFlush(tuc.getUsuariocampos());
                    } catch (Exception ex) {
                        throw new VenturaExcepcion("No fue posible crear el campo: " + ncampo + ". " + ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new VenturaExcepcion(ex.getMessage());
        }
    }

    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }
}
