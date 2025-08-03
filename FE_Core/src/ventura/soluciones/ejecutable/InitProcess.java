package ventura.soluciones.ejecutable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitProcess {

    public static String LAST_PROCESS = "";

    public static HashMap<String, String> pathsMap = new HashMap<>();

    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        try {
            Runtime rt = Runtime.getRuntime();
            String rutaCore = System.getProperty("user.dir");
            File directory = new File(rutaCore);
            Path parentPath = directory.getParentFile().toPath();
            rutaCore = parentPath.toFile().getAbsolutePath();
            String rutaConfigurador = rutaCore + "/FE_Ventura_Configurador/iniciarVSCPE.bat";
            Process proceso_Interface = null;
            String command = "cmd.exe /c ";
            if (args.length > 0 && args[0].equals("0")) {
                command = command + "start ";
            }
            proceso_Interface = rt.exec(command + rutaConfigurador);
            File file = new File(rutaCore + "/Config.xml");
            List<String> projects = new ArrayList<>(Arrays.asList("FE_Ventura_Extractor", "FE_Ventura_Publicador", "FE_Ventura_Request", "FE_Ventura_Response", "FE_Ventura_Resumen"));
            HashMap<String, String> projectsDirs = new HashMap<>();
            for (String project : projects) {
                projectsDirs.put(project, rutaCore + "/" + project + "/VSCPEMain/dist/VSCPEMain.jar");
//                projectsDirs.add(rutaCore + "/" + project + "/VSCPEMain/dist/VSCPEMain.jar");
            }

            if (file.exists()) {
                for (String key : projectsDirs.keySet()) {
                    System.out.println(key);
//                    Thread thread = new Thread(new VenturaProcess(projectsDirs.get(key), key));
//                    thread.start();
//                    threads.add(thread);
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(InitProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
