package org.ventura.cpe.bl;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.ventura.cpe.dao.controller.PublicardocWsJC;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.PublicardocWs;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;

/**
 *
 * @author JMonzalve
 */
public class PublicardocWsBL {

    public static int Crear(PublicardocWs docPublicarWS) throws Exception {
        try {
//            LoggerTrans.getCDThreadLogger().log(Level.INFO, "docPublicarWS. {0}", new Object[]{docPublicarWS.getFEId()});
//            LoggerTrans.getCDThreadLogger().log(Level.INFO, "docPublicarWS. {0}", new Object[]{docPublicarWS.getDOCFechaEmision()});
//            LoggerTrans.getCDThreadLogger().log(Level.INFO, "docPublicarWS. {0}", new Object[]{docPublicarWS.getDOCMontoTotal()});
//            LoggerTrans.getCDThreadLogger().log(Level.INFO, "docPublicarWS. {0}", new Object[]{docPublicarWS.getDOCCodigo()});
//            LoggerTrans.getCDThreadLogger().log(Level.INFO, "docPublicarWS. {0}", new Object[]{docPublicarWS.getSNDocIdentidadNro()});
//            LoggerTrans.getCDThreadLogger().log(Level.INFO, "docPublicarWS. {0}", new Object[]{docPublicarWS.getSNRazonSocial()});
//            LoggerTrans.getCDThreadLogger().log(Level.INFO, "docPublicarWS. {0}", new Object[]{docPublicarWS.getEstadoSUNAT()});
            if (PublicardocWsJC.findPublicardocWs(docPublicarWS.getFEId()) == null) {
                PublicardocWsJC.create(docPublicarWS);
            }else{
                PublicardocWsJC.edit(docPublicarWS);
            }
            
        } catch (PreexistingEntityException ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return 1;
        } catch (VenturaExcepcion ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return 2;
        }
        return 0;
    }

    public static List<PublicardocWs> listarHabilitadas() {
        Date fecha = new Date();
        return PublicardocWsJC.findHabilitadas(fecha);
    }

    public static boolean setEstadoDocPublicado(PublicardocWs docEditPublicado) {
        try {
            docEditPublicado.setEstadoPublicacion('B');
            PublicardocWsJC.edit(docEditPublicado);
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static boolean ActualizarPublicado(PublicardocWs docEditPublicado) {
        try {
            PublicardocWsJC.edit(docEditPublicado);
            return true;
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return false;
        }
    }

    public static PublicardocWs publicador(String feId) {
        PublicardocWs pbWS = null;
        try {
            pbWS = PublicardocWsJC.findPublicardocWs(feId);
            return pbWS;
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return null;
        }

    }

}
