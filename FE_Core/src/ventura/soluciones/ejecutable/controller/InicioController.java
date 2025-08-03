package ventura.soluciones.ejecutable.controller;

import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;
import ventura.soluciones.ejecutable.dto.Componente;

import java.io.IOException;
import java.util.HashMap;

public class InicioController {

    private HashMap<String, Componente> pathsMap = new HashMap<>();

    @FXML
    private JFXTabPane tabs;

    @FXML
    public void initialize() {
        tabs.getStyleClass().add("tabclass");
    }

    public void setPathsMap(HashMap<String, Componente> pathsMap) {
        this.pathsMap = pathsMap;
        this.pathsMap.forEach((key, componente) -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ventura/soluciones/ejecutable/fxml/tab.fxml"));
            try {
                int index = key.lastIndexOf('_') + 1;
                Parent parent = loader.load();
                TabController controller = loader.getController();
                controller.updateComponent(componente);
                Tab tab = new Tab(key.substring(index), parent);
                tab.getStyleClass().add("tabclass");
                FontIcon fontIcon = FontIcon.of(componente.getIcon());
                fontIcon.setIconColor(Paint.valueOf("white"));
                fontIcon.setIconSize(22);
                tab.setGraphic(fontIcon);
                tabs.getTabs().add(tab);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
