/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ventura.cpe.request;

import org.ventura.cpe.dto.TransaccionRespuesta;
import org.ventura.cpe.loaderbl.Criptor;
import org.ventura.cpe.log.LoggerTrans;
import org.ventura.cpe.requestbl.RequestBL;
import org.ventura.wsclient.config.ApplicationConfiguration;
import org.ventura.wsclient.config.configuration.Configuracion;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * @author Percy
 */
public class VSCPERequest {

    private static ScheduledFuture sf1 = null;

    private static ScheduledFuture sf2 = null;

    public static void procesar() {

        final File f = new File("lastRQ.lck");
        if (f.exists()) {
            f.delete();
        }

        final int intervalo = TransaccionRespuesta.RQINTERVAL;

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, r -> new Thread(r, "Request_Request"));
        Configuracion configuration = null;
        Connection connection = null;
        try{
            /* Extrayendo la informacion del archivo de configuracion 'config.xml' */
            configuration = ApplicationConfiguration.getInstance().getConfiguration();
            if(configuration.getJdbc().getEstadoJdbc()){
                connection = getConecction(connection,configuration);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Request.Procesar: {0}", new Object[]{ex.getMessage()});
        }
        Configuracion finalConfiguration = configuration;
        Connection finalConnection = connection;
        sf1 = scheduler.scheduleWithFixedDelay(() -> {
            try {
                RequestBL.EnviarTransacciones(finalConnection,finalConfiguration);
            } catch (Exception ex) {

                ex.printStackTrace();
                LoggerTrans.getCDThreadLogger().log(Level.SEVERE, "Request.Procesar: {0}", new Object[]{ex.getMessage()});
            }
        }, 0, intervalo, TimeUnit.SECONDS);

        sf2 = scheduler.scheduleAtFixedRate(() -> {
            if (f.exists()) {
                LoggerTrans.getCDThreadLogger().log(Level.INFO, "Finalizando Request...");
                if (f.exists()) {
                    f.delete();
                }
                scheduler.shutdown();
                sf1.cancel(false);
                sf2.cancel(false);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private static Connection getConecction (Connection connection,Configuracion configuracion){
        String servidorBDJdbc = configuracion.getJdbc().getServidorBDJdbc();
        String database = configuracion.getErp().getBaseDeDatos();
        String databaseUsername = configuracion.getErp().getUser();
        //String databasePassword = configuracion.getErp().getPassword();
        String databasePassword = Criptor.Desencriptar(configuracion.getErp().getPassword());
        Boolean estadoSqlServer = configuracion.getJdbc().getSqlServer();
        String url = "jdbc:sap://" + servidorBDJdbc + "/?currentschema=" + database + "&encrypt=true&validateCertificate=false";
        if (estadoSqlServer) {
            url = "jdbc:sqlserver://" + servidorBDJdbc + ";databaseName=" + database;
        }
        Properties props = new Properties();
        props.setProperty("user", databaseUsername);
        props.setProperty("password", databasePassword);
        try {
            connection = DriverManager.getConnection(url, props);
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "¡Conexión exitosa por JDBC a {0}!", database);
        } catch (SQLException e) {
            LoggerTrans.getCDThreadLogger().log(Level.INFO, "¡Conexion Fallida por JDBC! - Error: [{0}]", e.getMessage());
            connection = null;
            //e.printStackTrace();
        }
        return connection;
    }
}
