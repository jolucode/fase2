/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.main;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.main.config.Loader;
import org.ventura.cpe.resumen.VSCPEResumen;
import org.ventura.utilidades.entidades.PuertoVSCPE;

/**
 * @author Percy
 */
@SpringBootApplication
@ComponentScan(value = {"org.ventura", "ventura"})
public class Programa {

    public static ConfigurableApplicationContext applicationContext;

    public static final short ESTADO_DETENIDO = 0x00;

    public static final short ESTADO_INICIANDO = 0x01;

    public static final short ESTADO_INICIADO = 0x02;

    public static final short ESTADO_DETENIENDO = 0x03;

    public static final short ESTADO_ERRORINTERNET = 0x04;

    public static String SAPSociedad = "";

    public static String MENSAJE = "";

    public static short ESTADO = ESTADO_DETENIDO;

    public static boolean flag = false;

    private static ScheduledFuture sf = null;

    public static ServerSocket socket = null;

    private static void cerrarInstanciaAbierta() {

        if (socket != null) {
            try {
                if (!socket.isClosed()) {
                    //socket.close();
                    System.out.println("Socket abierto");
                }
            } catch (Exception ex) {
                //ex.printStackTrace();
            }

        }

    }

    private static boolean InstanciaUnica(int puerto) {
        try {
            if (socket == null) {
                socket = new ServerSocket(puerto);
            }
            Thread N = new Thread(() -> {
                try {
                    socket.accept();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                        }
                    }
                }
            });
            N.start();
            return true;
        } catch (IOException ex) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
            return false;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Programa.class);
        application.setBanner(new ResourceBanner(new ClassPathResource("org/ventura/cpe/iconos/banner.txt")));
        applicationContext = application.run(args);
        if (args.length > 0 && args[0].compareTo("shutdown") == 0) { //teminar programa
            stop();
        } else { //iniciar
            start();
        }
    }

    public static void start() {
        Thread T = new Thread(() -> {
            ESTADO = ESTADO_INICIANDO;
            MENSAJE = "Inicializando servicios...";
            LoggerTrans.createCDLogger("Resumen_Resumen");
            LoggerTrans.createCDLogger("Resumen_Loader");
            LoggerTrans.createCDLogger("Resumen_PublicWS");

            if (!Loader.CargarParametros()) {
                MENSAJE = "No se pudieron cargar los parametros de configuracion. Revisar log";
                ESTADO = ESTADO_DETENIDO;
                return;
            }
            //SAPSociedad = "";

            if (!InstanciaUnica(PuertoVSCPE.PuertoAbierto_Resumen)) {
                JOptionPane.showMessageDialog(null, "Existe otra instancia de la aplicación ejectandose.");
                System.exit(0);
                return;
            } else {
                LoggerTrans.getCDMainLogger().log(Level.CONFIG, "Puerto encontrado {0}", PuertoVSCPE.PuertoAbierto_Resumen);
            }

//                String mensaje = DocumentoINF.Conector.Conectar();
//                SAPSociedad = mensaje;
//                if (mensaje.startsWith("ERR:")) {
//
//                    LoggerTrans.getCDMainLogger().log(Level.CONFIG, "Error en la conexión. {0}", mensaje);
//                    MENSAJE = mensaje;
//                    ESTADO = ESTADO_DETENIDO;
//                } else {
//                    if(DocumentoBL.listSociedades != null && DocumentoBL.listSociedades.size() > 0){
//                        mensaje = "Conectado a " + ( 1 + DocumentoBL.listSociedades.size() ) + " Sociedad(s)";
//                    }
//                    LoggerTrans.getCDMainLogger().log(Level.INFO, "Conectado a {0} ", mensaje);
//                    VSCPELoader.procesar();
//                    VSCPEResumen.procesar();
//                    ESTADO = ESTADO_INICIADO;
//                    MENSAJE = mensaje;
//                }
            LoggerTrans.getCDMainLogger().log(Level.INFO, "Conectado a VSCPEResumen ");
            VSCPEResumen.applicationContext = Programa.applicationContext;
            VSCPEResumen.procesar();
            try {
                Thread.currentThread().join();
            } catch (InterruptedException ex) {
                LoggerTrans.getCDMainLogger().log(Level.INFO, "Start.JoinThread: {0} ", ex.getMessage());
            }
        });
        T.start();
    }

    public static void stop() {
        Thread T = new Thread(() -> {
            ESTADO = ESTADO_DETENIENDO;
            MENSAJE = "Deteniendo servicios...";
            try {
                final File f3 = new File("lastRSM.lck");
                f3.createNewFile();
                final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, (Runnable r) -> new Thread(r));
                sf = scheduler.scheduleWithFixedDelay(() -> {
                    if (!f3.exists()) {
                        //ConexionSAPB1.desconectar();
                        ESTADO = ESTADO_DETENIDO;
                        MENSAJE = "Servicio detenido";
                        scheduler.shutdown();
                        sf.cancel(false);

                    }
                }, 0, 3, TimeUnit.SECONDS);
            } catch (IOException ex) {
                MENSAJE = ex.getMessage();
                LoggerTrans.getCDMainLogger().log(Level.SEVERE, ex.getMessage());
            } finally {
                cerrarInstanciaAbierta();
                LoggerTrans.cerrarLogger("Resumen_Resumen");
                LoggerTrans.cerrarLogger("Resumen_Loader");
                LoggerTrans.cerrarLogger("Resumen_Main");
                LoggerTrans.cerrarLogger("Resumen_PublicWS");
            }
        });
        T.start();
    }
}
