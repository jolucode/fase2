package org.ventura.cpe.resumenbl;

import org.springframework.context.ConfigurableApplicationContext;
import org.ventura.cpe.bl.TransaccionResumenBL;
import org.ventura.cpe.dao.controller.TransaccionResumenJC;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.dto.hb.TransaccionResumen;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.pfeconn.TransaccionPFE;
import org.ventura.wsclient.TransaccionWS;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 *
 */
public class ResumenBL {

    public static ConfigurableApplicationContext applicationContext;

    public static void EnviarResumenDiario() {
        try {
            List<TransaccionResumen> lstResumen = TransaccionResumenJC.findPendientes();
            for (TransaccionResumen tr : lstResumen) {
                TransaccionPFE.applicationContext = ResumenBL.applicationContext;
                TransaccionWS transaccionWS = new TransaccionWS();
                TransaccionRespuesta trp = transaccionWS.EnviarResumenDiario(tr);
                if (Optional.ofNullable(trp.getNroTique()).isPresent() && trp.getNroTique().equalsIgnoreCase("88888888")) {
                    tr.setNumeroTicket("88888888");
                    tr.setEstado("R");
                    TransaccionResumenJC.edit(tr);
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "Se culmin\u00f3 el envio de Resumenes");
                } else {
                    if (trp.getNroTique() == null) {
                        TransaccionResumenBL.MarcarEnviado(tr);
                        LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "EnviarConsultas: No se encontro el ticket {0} - {1} ", new Object[]{trp.getUuid(), trp.getNroTique()});
                    } else {
                        tr.setNumeroTicket(trp.getNroTique());
                        tr.setEstado("R");
                        TransaccionResumenJC.edit(tr);
                        LoggerTrans.getCDThreadLogger().log(Level.INFO, "Se culmin\u00f3 el envio de Resumenes");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getCause().getLocalizedMessage(), e);
        }

    }

}
