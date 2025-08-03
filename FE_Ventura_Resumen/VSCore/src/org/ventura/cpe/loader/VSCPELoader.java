/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.loader;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.loaderbl.LoaderBL;
import org.ventura.cpe.log.LoggerTrans;

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
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "Resumen_Loader");
            }
        });

        sf1 = scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    LoaderBL.CargarSeteoParametro();
                } catch (Exception ex) {
                    LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Request.Loader: {0}", new Object[]{ex.getMessage()});
                }
            }
        }, 120, 30, TimeUnit.SECONDS);

        sf2 = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (f.exists()) {
                    LoggerTrans.getCDThreadLogger().log(Level.INFO, "Finalizando Loader...");
                    if (f.exists()) {
                        f.delete();
                    }
                    scheduler.shutdown();
                    sf1.cancel(false);
                    sf2.cancel(false);
                }
            }
        }, 0, 5, TimeUnit.SECONDS);

    }

}
