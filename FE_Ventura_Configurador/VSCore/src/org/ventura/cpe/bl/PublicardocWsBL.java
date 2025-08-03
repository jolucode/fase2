package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.PublicardocWsJC;
import org.ventura.cpe.dto.hb.PublicardocWs;
import org.ventura.cpe.log.LoggerTrans;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author JMonzalve
 */
public class PublicardocWsBL {

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

}
