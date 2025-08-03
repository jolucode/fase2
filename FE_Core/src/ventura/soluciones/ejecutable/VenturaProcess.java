package ventura.soluciones.ejecutable;

import ventura.soluciones.ejecutable.dto.MessageResult;
import ventura.soluciones.ejecutable.dto.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class VenturaProcess extends Observable implements Runnable, Observer {

    private String name;

    private String path;

    private Process process;

    private MessageThread messageThread;

    private MessageThread errorThread;

    public VenturaProcess(String name, String path) {
        this.name = name;
        this.path = path;
    }

    @Override
    public void run() {
        Path filePath = Paths.get(path);
        System.out.println("Ejecutando el proceso " + name);
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", filePath.toFile().getAbsolutePath());
        try {
            process = processBuilder.start();
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            messageThread = new MessageThread(input, MessageType.INFO);
            messageThread.addObserver(this);
            Thread threadMessage = new Thread(messageThread);
            threadMessage.start();
            BufferedReader errorBuffer = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String error;
            while ((error = errorBuffer.readLine()) != null) {
                notifyToObservers(new MessageResult(Optional.ofNullable(error).orElse(""), MessageType.WARNING, LocalDateTime.now()));
            }
            int status = process.waitFor();
            if (status == 0) {
                Thread.currentThread().interrupt();
                notifyToObservers(new MessageResult("Exited with status: " + status, MessageType.EXIT, LocalDateTime.now()));
                process.destroy();
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            process.destroy();
        }
    }

    private void notifyToObservers(MessageResult result) {
        setChanged();
        notifyObservers(result);
    }

    public void stopProcess() {
        if (Optional.ofNullable(process).isPresent()) {
            process.destroy();
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    @Override
    public void update(Observable o, Object mensaje) {
        notifyToObservers((MessageResult) mensaje);
    }

    private static class MessageThread extends Observable implements Runnable {

        private BufferedReader bufferedReader;

        private MessageType messageType;

        public MessageThread(BufferedReader bufferedReader, MessageType messageType) {
            this.bufferedReader = bufferedReader;
            this.messageType = messageType;
        }

        @Override
        public void run() {
            try {
                String messsage;
                while ((messsage = bufferedReader.readLine()) != null) {
                    setChanged();
                    notifyObservers(new MessageResult(Optional.ofNullable(messsage).orElse(""), messageType, LocalDateTime.now()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
