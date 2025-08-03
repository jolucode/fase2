package org.ventura.cpe.extractor.sbo;

import com.sap.smb.sbo.api.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.ventura.cpe.core.domain.*;
import org.ventura.cpe.core.erp.sapbo.SAPBOService;
import org.ventura.cpe.core.exception.VenturaExcepcion;
import org.ventura.cpe.core.repository.ResumenDiarioConfigRepository;
import org.ventura.cpe.core.repository.TransaccionResumenRepository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SapBOResumenExtractor {

    private final VSFactory factory;

    private final ResumenDiarioConfigRepository resumenDiarioConfigRepository;

    private final TransaccionResumenRepository resumenRepository;

    private final SAPBOService sapboService;

    @Value("${configuracion.erp.tipoServidor}")
    private String tipoServidor;

    public void extraerResumenDiario(ICompany sociedad, String fechaEmision, @NonNull int posicion) {
        int correlativo;
        try {
            boolean isHana = tipoServidor.equalsIgnoreCase("9");
            String pattern = isHana ? "yyyy-MM-dd" : "yyyyMMdd";
            SimpleDateFormat dateFormatSend = new SimpleDateFormat(pattern);
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(sociedad, factory.getQuery(17, tipoServidor, fechaEmision));
            boolean existeResumen = false;
            while (!rs.isEoF()) {
                String sFecFormateada = dateFormatSend.format(rs.getFields().item("FEC_DOC").getValueDate());
                Optional<ResumendiarioConfig> optional = resumenDiarioConfigRepository.findById(sFecFormateada);
                if (optional.isPresent()) {
                    ResumendiarioConfig resumendiarioConfig = optional.get();
                    correlativo = Integer.parseInt(resumendiarioConfig.getId()) + 1;
                } else {
                    correlativo = 1;
                }
                List<TransaccionResumen> transaccionResumenes = extraerResumenes(sFecFormateada, correlativo, posicion, sociedad);
                for (TransaccionResumen resumen : transaccionResumenes) {
                    if (!resumen.getTransaccionResumenLineas().isEmpty()) {
                        resumenRepository.saveAndFlush(resumen);
                        ResumendiarioConfig rc = new ResumendiarioConfig();
                        rc.setFecha(sFecFormateada);
                        LocalTime localTime = LocalTime.now();
                        rc.setHora(localTime.format(DateTimeFormatter.ofPattern("HH:mm")));
                        rc.setId(String.valueOf(correlativo));
                        resumenDiarioConfigRepository.saveAndFlush(rc);
                        List<TransaccionResumenLineaAnexo> transaccionResumenLineaAnexos = resumen.getTransaccionResumenLineaAnexos();
                        for (TransaccionResumenLineaAnexo lineaAnexo : transaccionResumenLineaAnexos) {
                            boolean seguimientoCorrecto = sapboService.ponerEnSeguimientoResumen(lineaAnexo, sociedad.getCompanyDB());
                            if (seguimientoCorrecto) {
                                log.info("Documento [{}-{}{}] actualizado correctamente a seguimiento", lineaAnexo.getTipoDocumento(), lineaAnexo.getSerie(), lineaAnexo.getCorrelativo());
                            }
                        }
                        log.info("Se registro correctamente el resumen diario de {}", fechaEmision);
                    } else {
                        log.info("No se encontro registro del dia {}", fechaEmision);
                    }
                }
                rs.moveNext();
                existeResumen = true;
            }
            if (!existeResumen) {
                log.info("No se encontr\u00f3 registro del d\u00eda {}", fechaEmision);
            }
        } catch (SBOCOMException | VenturaExcepcion e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    private List<TransaccionResumen> extraerResumenes(String fechaEmision, int correlativo, int posicion, ICompany sociedad) {
        List<TransaccionResumen> trans = new ArrayList<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(sociedad, factory.getQuery(12, tipoServidor, fechaEmision, String.valueOf(correlativo)));
            while (!rs.isEoF()) {
                IFields fields = rs.getFields();
                TransaccionResumen transaccionResumen = new TransaccionResumen();
                transaccionResumen.setDIRDepartamento(fields.item("DIR_Departamento").getValue().toString());
                transaccionResumen.setDIRDireccion(fields.item("DIR_Direccion").getValue().toString());
                transaccionResumen.setDIRDistrito(fields.item("DIR_Distrito").getValue().toString());
                transaccionResumen.setDIRNomCalle(fields.item("DIR_NomCalle").getValue().toString());
                transaccionResumen.setDIRNroCasa(fields.item("DIR_NroCasa").getValue().toString());
                transaccionResumen.setDIRPais(fields.item("DIR_Pais").getValue().toString());
                transaccionResumen.setDIRProvincia(fields.item("DIR_Provincia").getValue().toString());
                transaccionResumen.setDIRUbigeo(fields.item("DIR_Ubigeo").getValue().toString());
                transaccionResumen.setDocIdentidadTipo(fields.item("DocIdentidad_Tipo").getValue().toString());
                transaccionResumen.setEMail(fields.item("EMail").getValue().toString());
                transaccionResumen.setEstado(fields.item("Estado").getValue().toString());
                transaccionResumen.setFechaEmision(fields.item("Fecha_Emision").getValue().toString());
                transaccionResumen.setFechaGeneracion(fields.item("Fecha_Generacion").getValue().toString());
                transaccionResumen.setNombreComercial(fields.item("NombreComercial").getValue().toString());
                transaccionResumen.setNumeroRuc(fields.item("Numero_Ruc").getValue().toString());
                transaccionResumen.setNumeroTicket(fields.item("Numero_Ticket").getValue().toString());
                transaccionResumen.setPersonContacto(fields.item("PersonContacto").getValue().toString());
                transaccionResumen.setRazonSocial(fields.item("RazonSocial").getValue().toString());
                transaccionResumen.setIdTransaccion(fields.item("Id_Transaccion").getValueString());
                transaccionResumen.setKeySociedad(sociedad.getCompanyDB());
                List<TransaccionResumenLinea> transaccionResumenLineas = extransaccionResumenLineas(transaccionResumen.getIdTransaccion(), sociedad, fechaEmision);
                List<TransaccionResumenLineaAnexo> transaccionResumenLineaAnexos = extransaccionResumenLineasAnexo(transaccionResumen.getIdTransaccion(), sociedad, posicion, fechaEmision);
                if (!transaccionResumenLineas.isEmpty()) {
                    transaccionResumen.setTransaccionResumenLineas(transaccionResumenLineas);
                }
                if (!transaccionResumenLineaAnexos.isEmpty()) {
                    transaccionResumen.setTransaccionResumenLineaAnexos(transaccionResumenLineaAnexos);
                }
                trans.add(transaccionResumen);
                rs.moveNext();
            }
            log.info("Finaliz\u00f3 la extracci\u00f3n del resumen");
            return trans;
        } catch (VenturaExcepcion ex) {
            log.error("{}: Se encontro un incidencia en el metodo {} con el siguiente mensaje {}", new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage());
        } catch (SBOCOMException ex) {
            log.error("{}: Se encontro un incidencia en el metodo {}  con el siguiente mensaje {}", new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage());
        }
        return new ArrayList<>();
    }

    private List<TransaccionResumenLinea> extransaccionResumenLineas(String feId, ICompany Sociedad, String... params) throws VenturaExcepcion {
        List<TransaccionResumenLinea> lineas = new LinkedList<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(13, tipoServidor, params));
            while (!rs.isEoF()) {
                IFields fields = rs.getFields();
                TransaccionResumenLinea transaccionResumenLinea = new TransaccionResumenLinea();
                TransaccionResumenLineaPK resumenLineaPK = new TransaccionResumenLineaPK();
                resumenLineaPK.setIdLinea(fields.item("ID").getValueInteger());
                resumenLineaPK.setIdTransaccion(feId);
                transaccionResumenLinea.setTransaccionResumenLineaPK(resumenLineaPK);
                transaccionResumenLinea.setTipoDocumento(fields.item("Tipo_Documento").getValue().toString());
                transaccionResumenLinea.setNumeroSerie(fields.item("Numero_Serie").getValueString());
                transaccionResumenLinea.setNumeroCorrelativo(fields.item("Numero_Correlativo").getValueString());
                transaccionResumenLinea.setImporteOtrosCargos(BigDecimal.valueOf(fields.item("Importe_Otros_Cargos").getValueDouble()));
                transaccionResumenLinea.setTotalISC(BigDecimal.valueOf(fields.item("Total_ISC").getValueDouble()));
                transaccionResumenLinea.setTotaIGV(BigDecimal.valueOf(fields.item("Tota_IGV").getValueDouble()));
                transaccionResumenLinea.setTotalOtrosTributos(BigDecimal.valueOf(fields.item("Total_Otros_Tributos").getValueDouble()));
                transaccionResumenLinea.setImporteTotal(BigDecimal.valueOf(fields.item("Importe_Total").getValueDouble()));
                transaccionResumenLinea.setCodMoneda(fields.item("CodMoneda").getValueString());
                transaccionResumenLinea.setDocIdentidad(fields.item("DocIdentidad").getValueString());
                transaccionResumenLinea.setTipoDocIdentidad(fields.item("TipoDocIdentidad").getValueString());
                transaccionResumenLinea.setDocIdModificado(fields.item("DocId_Modificado").getValueString());
                transaccionResumenLinea.setTipoDocIdentidadModificado(fields.item("TipoDocIdentidad_Modificado").getValueString());
                transaccionResumenLinea.setRegimenPercepcion(fields.item("Regimen_Percepcion").getValueString());
                transaccionResumenLinea.setTasaPercepcion(BigDecimal.valueOf(fields.item("Tasa_Percepcion").getValueDouble()));
                transaccionResumenLinea.setMontoTotalCobrar(BigDecimal.valueOf(fields.item("Monto_Total_Cobrar").getValueDouble()));
                transaccionResumenLinea.setBaseImponible(BigDecimal.valueOf(fields.item("Base_Imponible").getValueDouble()));
                transaccionResumenLinea.setEstado(fields.item("Estado").getValueString());
                transaccionResumenLinea.setNumeroCorrelativoFin(fields.item("Numero_Correlativo_Fin").getValueString());
                transaccionResumenLinea.setNumeroCorrelativoInicio(fields.item("Numero_Correlativo_Inicio").getValueString());
                transaccionResumenLinea.setMontoPercepcion(BigDecimal.valueOf(fields.item("Monto_Percepcion").getValueDouble()));
                transaccionResumenLinea.setTotalOPGravadas(BigDecimal.valueOf(fields.item("Total_OP_Gravadas").getValueDouble()));
                transaccionResumenLinea.setTotalOPExoneradas(BigDecimal.valueOf(fields.item("Total_OP_Exoneradas").getValueDouble()));
                transaccionResumenLinea.setTotalOPInafectas(BigDecimal.valueOf(fields.item("Total_OP_Inafectas").getValueDouble()));
                transaccionResumenLinea.setTotalOPGratuitas(BigDecimal.valueOf(fields.item("Total_OP_Gratuitas").getValueDouble()));
                lineas.add(transaccionResumenLinea);
                rs.moveNext();
            }
            log.info("Resumen Diario: Se extrajo correctamente {}  linea ", lineas.size());
            return lineas;
        } catch (SBOCOMException ex) {
            log.error("{}: Se encontro un incidencia en el metodo {}  con el siguiente mensaje {}", new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage());
            throw new VenturaExcepcion("Extransaccion Resumen Lineas. ", ex);
        }
    }

    private List<TransaccionResumenLineaAnexo> extransaccionResumenLineasAnexo(String feId, ICompany Sociedad, int posicion, String... params) throws VenturaExcepcion {
        List<TransaccionResumenLineaAnexo> lineas = new ArrayList<>();
        try {
            IRecordset rs = SBOCOMUtil.runRecordsetQuery(Sociedad, factory.getQuery(14, tipoServidor, params));
            int contador = 1;
            while (!rs.isEoF()) {
                IFields fields = rs.getFields();
                TransaccionResumenLineaAnexo transaccionResumenLineaAnexo = new TransaccionResumenLineaAnexo();
                TransaccionResumenLineaAnexoPK resumenLineaAnexoPK = new TransaccionResumenLineaAnexoPK();
                resumenLineaAnexoPK.setIdLinea(contador);
                resumenLineaAnexoPK.setIdTransaccion(feId);
                transaccionResumenLineaAnexo.setTransaccionResumenLineaAnexoPK(resumenLineaAnexoPK);
                transaccionResumenLineaAnexo.setDocEntry(fields.item("Docentry").getValue().toString());
                transaccionResumenLineaAnexo.setObjcType(fields.item("Objtype").getValue().toString());
                transaccionResumenLineaAnexo.setSn(fields.item("SN").getValue().toString());
                transaccionResumenLineaAnexo.setTipoDocumento(fields.item("TipoDocumento").getValue().toString());
                transaccionResumenLineaAnexo.setTipoTransaccion(fields.item("tipoTransaccion").getValue().toString());
                transaccionResumenLineaAnexo.setSerie(fields.item("Serie").getValue().toString());
                transaccionResumenLineaAnexo.setCorrelativo(fields.item("Correlativo").getValue().toString());
                transaccionResumenLineaAnexo.setTipoEstado(fields.item("TipoEstado").getValue().toString());
                transaccionResumenLineaAnexo.setPosicion(posicion);
                lineas.add(transaccionResumenLineaAnexo);
                contador++;
                rs.moveNext();
            }
            log.info("Resumen Diario: Se extrajo correctamente {}  linea ", lineas.size());
            return lineas;
        } catch (SBOCOMException ex) {
            log.error("{}: Se encontro un incidencia en el metodo {}  con el siguiente mensaje {}", new Object() {
            }.getClass().getName(), new Object() {
            }.getClass().getEnclosingMethod().getName(), ex.getMessage());
            throw new VenturaExcepcion("Lineas de Transaccion Resumen Anexo. ", ex);
        }
    }

}
