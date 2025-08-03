package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.BdsMaestrasJC;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.BdsMaestras;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;

import java.util.List;
import java.util.logging.Level;

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
        return BdsMaestrasJC.findAllBDAnadidas();
    }

    public static boolean destroyAllBDAnadidas(List<BdsMaestras> listKeyBDAnadidas) {
        try {
            for (BdsMaestras keyBDAnadida : listKeyBDAnadidas) {
                BdsMaestrasJC.destroy(keyBDAnadida.getBDId());
            }
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static List<BdsMaestras> listarKeyBDAnadidas() {
        try {
            return BdsMaestrasJC.findAllBDAnadidas();
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return null;
        }
    }
    
}
