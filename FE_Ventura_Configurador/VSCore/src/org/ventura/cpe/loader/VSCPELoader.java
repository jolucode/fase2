/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.loader;

import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.excepciones.VenturaExcepcion;
import org.ventura.cpe.loaderbl.LoaderBL;
import org.ventura.cpe.log.LoggerTrans;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 *
 * @author Percy
 */
public class VSCPELoader {

    private static ScheduledFuture sf1 = null;
    private static ScheduledFuture sf2 = null;

    public static void procesar() {

        final File f = new File("lastRQ.lck");
        if (f.exists()) {
            f.delete();
        }

        final int intervalo = TransaccionRespuesta.RQINTERVAL;
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(intervalo, (Runnable r) -> {
            return new Thread(r, "Main_Loader");
        });

        sf1 = scheduler.scheduleWithFixedDelay(() -> {
            try {
                LoaderBL.CargarSeteoParametro();
            } catch (VenturaExcepcion ex) {
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Request.Loader: {0}", new Object[]{ex.getMessage()});
            }
        }, 120, 30, TimeUnit.SECONDS);

        sf2 = scheduler.scheduleAtFixedRate(() -> {
            if (f.exists()) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "Finalizando Loader...");
                if (f.exists()) {
                    f.delete();
                }
                scheduler.shutdown();
                sf1.cancel(false);
                sf2.cancel(false);
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

}
