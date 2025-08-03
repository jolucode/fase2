/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.bl;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ventura.cpe.dto.Directorio;
import org.ventura.cpe.dto.hb.Transaccion;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.log.LoggerTrans;

/**
 *
 * @author VSUser
 */
public class PluginBL {
    
    private static ScheduledFuture sf1 = null;
    private static ScheduledFuture sf2 = null;
    
    public static void PublicarTransaccionWS(Transaccion tc, Map<String, String> PathFile, Boolean aceptado) {
        try {

            Map<String, String> dbTransaccion = new HashMap();
            dbTransaccion.put("numeroSerie", tc.getDOCId());
            String estado = "";
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            dbTransaccion.put("fechaEmision", df.format(tc.getDOCFechaEmision()));
            dbTransaccion.put("total", tc.getDOCMontoTotal().toString());
            dbTransaccion.put("tipoDoc", tc.getDOCCodigo());
            dbTransaccion.put("rucConsumidor", tc.getSNDocIdentidadNro());
            dbTransaccion.put("nombreConsumidor", tc.getSNRazonSocial());
            dbTransaccion.put("emailConsumidor", tc.getSNEMail());
            SimpleDateFormat mdyformat = new SimpleDateFormat("dd-MM-yyyy");
            String mdy = mdyformat.format(tc.getDOCFechaEmision());
            dbTransaccion.put("rutaPDF", PathFile.get("rutaPDF"));
            dbTransaccion.put("rutaXML", PathFile.get("rutaXML"));
            dbTransaccion.put("rutaZIP", PathFile.get("rutaCDR"));
            if (aceptado) {
                estado = "V";
                dbTransaccion.put("estadoSunat", estado);
            } else {

                if ("03".equals(tc.getDOCCodigo())) {
                    estado = "D";
                    dbTransaccion.put("estadoSunat", estado);
                } else {
                    estado = "R";
                    dbTransaccion.put("estadoSunat", estado);
                }
            }
            dbTransaccion.put("estadoSunat", estado);
            dbTransaccion.put("moneda", tc.getDOCMONCodigo());
            dbTransaccion.put("emailEmisor", tc.getEMail());
            //interfaz.publicar(dbTransaccion, tc.getFEId(), tc.getDOCId(), tc.getFETipoTrans());
        } catch (SecurityException | IllegalArgumentException e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]: {1}", new Object[]{tc.getDOCSerie() + "-" + tc.getDOCNumero(), e.getMessage()});
        } /*catch (IOException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "ERROR: {0}", new  Object[]{ex});
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "ERROR: {0}", new  Object[]{ex});
        }*/
    }

    public static void PublicarTransaccionResumenWS(TransaccionResumen tc, Boolean aceptado) {
        try {
            String rutaCDR = Directorio.ADJUNTOS + File.separator + File.separator + "resumen" + File.separator + "CDR" + File.separator + "R-" + tc.getNumeroRuc() + "-" + tc.getIdTransaccion() + ".zip";
            String rutaXML = Directorio.ADJUNTOS + File.separator + File.separator + "resumen" + File.separator + "XML" + File.separator + tc.getNumeroRuc() + "-" + tc.getIdTransaccion() + ".xml";
            Map<String, String> dbTransaccion = new HashMap();
            dbTransaccion.put("numeroSerie", tc.getIdTransaccion());
            String estado = "";
            dbTransaccion.put("fechaEmision", tc.getFechaEmision());
            dbTransaccion.put("total", "0.00");
            dbTransaccion.put("tipoDoc", tc.getIdTransaccion().substring(0, 2));
            dbTransaccion.put("rucConsumidor", tc.getNumeroRuc());
            dbTransaccion.put("nombreConsumidor", tc.getRazonSocial());
            dbTransaccion.put("emailConsumidor", tc.getEMail());
            dbTransaccion.put("rutaZIP", rutaCDR);
            dbTransaccion.put("rutaPDF", rutaXML);
            dbTransaccion.put("rutaXML", "");
            if (aceptado) {
                estado = "V";
                dbTransaccion.put("estadoSunat", estado);
            }
            dbTransaccion.put("estadoSunat", estado);
            dbTransaccion.put("moneda", "PEN");
            dbTransaccion.put("emailEmisor", tc.getEMail());

            //interfaz.publicar(dbTransaccion, tc.getIdTransaccion(), tc.getIdTransaccion(), "E");
        } catch (SecurityException | IllegalArgumentException e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "[{0}]", new Object[]{tc.getIdTransaccion(), e.getMessage()});
        } catch (Exception ex) {
            Logger.getLogger(PluginBL.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
