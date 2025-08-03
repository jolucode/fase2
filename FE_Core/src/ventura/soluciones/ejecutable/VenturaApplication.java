package ventura.soluciones.ejecutable;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.jutils.jprocesses.JProcesses;
import org.jutils.jprocesses.model.ProcessInfo;
import org.kordamp.ikonli.material.Material;
import ventura.soluciones.ejecutable.controller.InicioController;
import ventura.soluciones.ejecutable.dto.Componente;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VenturaApplication extends Application {

    public static HashMap<String, Componente> projectsDirs = new HashMap<>();

    public static void main(String[] args) throws JAXBException {
        String rutaCore = System.getProperty("user.dir");
        File directory = new File(rutaCore);
        directory = directory.getParentFile();
        rutaCore = directory.getAbsolutePath();
        File file = new File(rutaCore + "/Config.xml");
//        JAXBContext jaxbContext = JAXBContext.newInstance(Configuracion.class);
//        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
//        Configuracion configuracion = (Configuracion) jaxbUnmarshaller.unmarshal(file);
//        List<Configuracion.Modulos.Modulo> modulos = configuracion.getModulos().getModulos();
//        modulos.forEach(modulo -> System.out.println(modulo));
        String sep = File.separator;
        String rutaProduccion = sep + "VSCPEMain.jar", rutaTest = sep + "VSCPEMain/dist/VSCPEMain.jar";
        File extractorFile = new File(rutaCore + sep + "FE_Ventura_Extractor" + rutaProduccion);
        String componentPath = extractorFile.exists() ? rutaProduccion : rutaTest;
        Componente extractor = new Componente("FE_Ventura_Extractor", "Extractor", Material.SYSTEM_UPDATE_ALT);
        Componente request = new Componente("FE_Ventura_Request", "Request", Material.COMPARE_ARROWS);
        Componente response = new Componente("FE_Ventura_Response", "Response", Material.ARROW_BACK);
        Componente publicador = new Componente("FE_Ventura_Publicador", "Publicador", Material.PUBLISH);
        Componente resumen = new Componente("FE_Ventura_Resumen", "Resumen", Material.DATE_RANGE);
        List<Componente> projects = new ArrayList<>(Arrays.asList(extractor, request, response, publicador, resumen));
        if (file.exists()) {
            for (Componente componente : projects) {
                String key = componente.getKey();
                componente.setPath(rutaCore + sep + key + componentPath);
                projectsDirs.put(key, componente);
            }
        }
//        JProcesses.
//        List<ProcessInfo> processesList = JProcesses.getProcessList();
//
//        for (final ProcessInfo processInfo : processesList) {
//            System.out.println("Process PID: " + processInfo.getPid());
//            System.out.println("Process Name: " + processInfo.getName());
//            System.out.println("Process Time: " + processInfo.getTime());
//            System.out.println("User: " + processInfo.getUser());
//            System.out.println("Virtual Memory: " + processInfo.getVirtualMemory());
//            System.out.println("Physical Memory: " + processInfo.getPhysicalMemory());
//            System.out.println("CPU usage: " + processInfo.getCpuUsage());
//            System.out.println("Start Time: " + processInfo.getStartTime());
//            System.out.println("Priority: " + processInfo.getPriority());
//            System.out.println("Full command: " + processInfo.getCommand());
//            System.out.println("------------------");
//        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventura/soluciones/ejecutable/fxml/inicio.fxml"));
        Parent parent = loader.load();
        InicioController controller = loader.getController();
        controller.setPathsMap(projectsDirs);
        Scene scene = new Scene(parent, 1000, 700);
        Image iconWithNotifications = new Image(getClass().getResourceAsStream("/icon/icono16.png"));
        scene.getStylesheets().addAll("css/jfoenix-fonts.css", "css/jfoenix-design.css", "css/styles.css", "css/jfoenix-main-demo.css");
        primaryStage.getIcons().add(iconWithNotifications);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Componente de Facturación Electrónica");
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }
}
