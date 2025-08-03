package org.ventura.cpe.bl;

import org.ventura.cpe.dao.controller.ReglasIdiomasDocJC;
import org.ventura.cpe.dao.exceptions.PreexistingEntityException;
import org.ventura.cpe.dto.hb.ReglasIdiomasDoc;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.log.LoggerTrans;

import java.util.List;
import java.util.logging.Level;

/**
 *
 * @author Jhony Monzalve
 */
public class ReglasIdiomaDocBL {

    public static int Crear(List<ReglasIdiomasDoc> listReglasIdiomasDoc) throws Exception {
        try {
            for (ReglasIdiomasDoc reglasIdiomasDoc : listReglasIdiomasDoc) {
                ReglasIdiomasDocJC.create(reglasIdiomasDoc);
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

    public static List<ReglasIdiomasDoc> listarReglas() {
        return ReglasIdiomasDocJC.findAllReglas();
    }

    public static boolean destroyAllReglas(List<ReglasIdiomasDoc> listKeyReglasBD) {
        try {
            for (ReglasIdiomasDoc keyReglaBD : listKeyReglasBD) {
                ReglasIdiomasDocJC.destroy(keyReglaBD.getFEKey());
            }
            return true;
        } catch (Exception ex) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, ex.getMessage());
            return false;
        }
    }

    public static List<ReglasIdiomasDoc> listarKeyReglasBD() {
        try {
            return ReglasIdiomasDocJC.findAllReglas();
        } catch (Exception e) {
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, e.getMessage());
            return null;
        }
    }

}
