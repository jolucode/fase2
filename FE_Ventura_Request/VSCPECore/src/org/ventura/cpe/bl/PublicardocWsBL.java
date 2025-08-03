package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.PublicardocWsJC;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.PublicardocWs;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;

import java.util.logging.Level;

/**
 * @author JMonzalve
 */
public class PublicardocWsBL {

    public static int Crear(PublicardocWs docPublicarWS) throws Exception {
        try {
            if (PublicardocWsJC.findPublicardocWs(docPublicarWS.getFEId()) == null) {
                PublicardocWsJC.create(docPublicarWS);
            } else {
                docPublicarWS.setEstadoPublicacion('A');
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

    public static PublicardocWs publicador(String feId) {
        PublicardocWs pbWS;
        try {
            pbWS = PublicardocWsJC.findPublicardocWs(feId);
            return pbWS;
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
}
