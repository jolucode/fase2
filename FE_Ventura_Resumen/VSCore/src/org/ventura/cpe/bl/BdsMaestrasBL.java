package org.ventura.cpe.bl;

import java.util.List;
import java.util.logging.Level;
import org.ventura.cpe.dao.controller.BdsMaestrasJC;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.BdsMaestras;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;

/**
 *
 * @author Jhony Monzalve
 */
public class BdsMaestrasBL {
    
    public static int Crear(List<BdsMaestras> listBdsMaestras) throws Exception {
        try {
            for (BdsMaestras regBDMaestra : listBdsMaestras) {
                BdsMaestrasJC.create(regBDMaestra);
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

    public static List<BdsMaestras> listarBDAnadidas() {
        return BdsMaestrasJC.findAllBDAñadidas();
    }

    public static boolean destroyAllBDAñadidas(List<BdsMaestras> listKeyBDAñadidas) {
        try {
            for (BdsMaestras keyBDAñadida : listKeyBDAñadidas) {
                BdsMaestrasJC.destroy(keyBDAñadida.getBDId());
            }
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static List<BdsMaestras> listarKeyBDAnadidas() {
        try {
            return BdsMaestrasJC.findAllBDAñadidas();
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
    
}
