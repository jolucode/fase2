package ventura.soluciones.ejecutable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author JMonzalve
 */
public class EjecutableBAT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.getRuntime();
            String rutaCore = System.getProperty("user.dir");
            String rutaConfigurador = rutaCore + "/FE_Ventura_Configurador/iniciarVSCPE.bat";
            Process proceso_Interface = null;
            String command = "cmd.exe /c ";
            if (args.length > 0 && args[0].equals("0")) {
                command = command + "start ";
            }
            proceso_Interface = rt.exec(command + rutaConfigurador);
            File file = new File(rutaCore + "/Config.xml");
            if (file.exists()) {
                String rutaRequest = rutaCore + "/FE_Ventura_Request/iniciarVSCPE.bat";
                String rutaResponse = rutaCore + "/FE_Ventura_Response/iniciarVSCPE.bat";
                String rutaPublicWS = rutaCore + "/FE_Ventura_Publicador/iniciarVSCPE.bat";
                String rutaResumen = rutaCore + "/FE_Ventura_Resumen/iniciarVSCPE.bat";
                String rutaExtractor = rutaCore + "/FE_Ventura_Extractor/iniciarVSCPE.bat";

                Process proceso_Request = rt.exec(command + rutaRequest);
                Process proceso_Response = rt.exec(command + rutaResponse);
                Process proceso_Resumen = rt.exec(command + rutaPublicWS);
                Process proceso_Publicador = rt.exec(command + rutaResumen);
                Process proceso_Extractor = rt.exec(command + rutaExtractor);

                proceso_Interface.waitFor();
                proceso_Request.waitFor();
                proceso_Response.waitFor();
                proceso_Publicador.waitFor();
                proceso_Resumen.waitFor();
                proceso_Extractor.waitFor();
            }

        } catch (InterruptedException | IOException ex) {
            Logger.getLogger(EjecutableBAT.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
