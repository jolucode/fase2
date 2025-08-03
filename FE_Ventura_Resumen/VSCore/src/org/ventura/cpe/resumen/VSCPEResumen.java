package org.ventura.cpe.resumen;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.springframework.context.ConfigurableApplicationContext;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.resumenbl.ResumenBL;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 */
public class VSCPEResumen {

    private static ScheduledFuture sf1 = null;
    private static ScheduledFuture sf2 = null;
    public static ConfigurableApplicationContext applicationContext;

    public static void procesar() {

        final File f = new File("lastRSM.lck");
        if (f.exists()) {
            f.delete();
        }

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, (Runnable r) -> new Thread(r, "Resumen_Resumen"));

        sf1 = scheduler.scheduleAtFixedRate(() -> {
            try {
                ResumenBL.applicationContext = VSCPEResumen.applicationContext;
                ResumenBL.EnviarResumenDiario();
                
            } catch (Exception ex) {
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Resumen.Procesar: {0}", new Object[]{ex.getMessage()});
            }
        }, 1, 1, TimeUnit.MINUTES);

        sf2 = scheduler.scheduleAtFixedRate(() -> {
            if (f.exists()) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "Finalizando Resumen...");
                if (f.exists()) {
                    f.delete();
                }
                scheduler.shutdown();
                sf1.cancel(false);
                sf2.cancel(false);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

}
