import functional_classes.ClientManager;
import functional_classes.ClientReader;
import functional_classes.ClientSerializer;
import functional_classes.Writer;

import java.net.BindException;
import java.util.Objects;
import java.util.Scanner;

import gui.FXApplication;


public class ClientMain {
    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            FXApplication.main(args);
        });
//        Thread t2 = new Thread(() -> {
//            while (true) {
//                consoleApp();
//            }
//        });

//        t2.start();
        t1.start();
    }

    public static void consoleApp() {
        String nextLine;
        String executedCommand;
        try {
            // initialization
            ClientReader reader = new ClientReader();
            Writer writer = new Writer();
            int port;
            ClientSerializer clientSerializer;
            System.out.println("Введите номер порта для этого клиентского приложения");
            port = Integer.parseInt(new Scanner(System.in).nextLine().trim());
            try {
                clientSerializer = new ClientSerializer(port);
                ClientManager clientManager = new ClientManager(clientSerializer, reader, writer);
                reader.setClientManager(clientManager);

                // execution

                writer.suggestNewAction();
                nextLine = reader.readNextLine();
                executedCommand = (nextLine != null) ? nextLine.trim() : null;
                while (!Objects.equals(executedCommand, "exit")) {
                    clientManager.startNewAction(executedCommand);
                    writer.suggestNewAction();
                    executedCommand = reader.readNextLine().trim();
                }
            } catch (BindException e) {
                System.out.println("Порт уже используется. Создайте другой");
            }
        } catch (NumberFormatException e) {
            System.out.println("Номер порта должен быть числом");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}