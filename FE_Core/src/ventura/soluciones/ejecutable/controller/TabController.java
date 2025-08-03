package ventura.soluciones.ejecutable.controller;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.fxmisc.richtext.StyleClassedTextArea;
import ventura.soluciones.ejecutable.VenturaProcess;
import ventura.soluciones.ejecutable.dto.Componente;
import ventura.soluciones.ejecutable.dto.MessageResult;
import ventura.soluciones.ejecutable.dto.MessageType;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import static javafx.beans.binding.Bindings.when;

public class TabController implements Observer {

    @FXML
    private StyleClassedTextArea console;

    @FXML
    private JFXToggleButton startButon;

    @FXML
    private Label headerLabel;

    @FXML
    private JFXBadge notificacion;

    private Thread thread;

    private Componente componente;

    private SimpleStringProperty property = new SimpleStringProperty("0");

    private SimpleBooleanProperty playPauseProperty = new SimpleBooleanProperty(false);

    private int contador = 0;

    private VenturaProcess process;

    private boolean started = false, lastWasError = false;

    private int lastPosition = 0, wordCount = 0;

    @FXML
    public void initialize() {
        startButon.selectedProperty().bindBidirectional(playPauseProperty);
        notificacion.textProperty().bind(property);
        console.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 2;-fx-border-radius: 2 2 2 2; -fx-background-radius: 2 2 2 2");
    }

    @FXML
    public void startStopProcess() {
        Optional<Thread> optional = Optional.ofNullable(this.thread);
        if (!optional.isPresent()) {
            this.thread = createThread();
        }
        if (this.thread.getState().equals(Thread.State.TERMINATED)) {
            this.thread = createThread();
        }
        thread.setPriority(Thread.MAX_PRIORITY);
        System.out.println(this.thread.getState());
        System.out.println(this.thread.getName());
        boolean notRunning = this.thread.getState().equals(Thread.State.NEW);
        if (notRunning) {
            console.clear();
            this.thread.start();
        } else {
            this.thread.interrupt();
            process.stopProcess();
        }
    }

    @Override
    public void update(Observable o, final Object arg) {
        Platform.runLater(() -> {
            MessageResult result = (MessageResult) arg;
            if (result.getType().equals(MessageType.EXIT)) {
                playPauseProperty.setValue(false);
            }
            String text = result.getMessage() + "\n";
            if (result.getType().equals(MessageType.WARNING)) {
                startButon.selectedProperty().setValue(false);
                PseudoClass ERROR = PseudoClass.getPseudoClass("texto");
                console.pseudoClassStateChanged(ERROR, true);
                property.setValue(++contador + "");
                if (!lastWasError) {
                    lastPosition = console.getText().length();
                    wordCount = text.length();
                } else {
                    wordCount += text.length();
                }
                lastWasError = true;
            } else {
                lastWasError = false;
            }
            console.appendText(text);
            if (result.getType().equals(MessageType.WARNING))
                console.setStyleClass(lastPosition, lastPosition + wordCount, "warning");
        });
    }

    public void updateComponent(Componente componente) {
        this.componente = componente;
        headerLabel.setText(componente.getName() + " Ventura");
        startButon.textProperty().bind(when(startButon.selectedProperty()).then("Detener " + componente.getName()).otherwise("Iniciar " + componente.getName()));
        process = new VenturaProcess(componente.getName(), componente.getPath());
        process.addObserver(this);
    }

    private Thread createThread() {
        process = new VenturaProcess(componente.getName(), componente.getPath());
        process.addObserver(this);
        return new Thread(process);
    }

    @Override
    public String toString() {
        return "Soy el controlador " + hashCode();
    }
}
