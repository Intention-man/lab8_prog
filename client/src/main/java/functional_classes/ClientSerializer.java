package functional_classes;


import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;
import gui.FXApplication;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Objects;


public class ClientSerializer {
    static DatagramSocket socket;
    InetAddress host;
    int clientPort;
    static DatagramChannel dc;
    static ByteBuffer buffer;
    static SocketAddress serverAddress;
    byte[] byteBAOS;
    public ResponseMessage newResponse;
    boolean readyToReturnMessage;
    FXApplication app;


    public ClientSerializer(int clientPort) throws SocketException, UnknownHostException {
        this.clientPort = clientPort;
        socket = new DatagramSocket();
        host = InetAddress.getByName("localhost");
        serverAddress = new InetSocketAddress(host, 7000);
        socket = new DatagramSocket(clientPort);
        readyToReturnMessage = false;
    }


    public ResponseMessage send(CommandMessage<Object> commandMessage) {
        // creation channel and open it
        try {
            dc = DatagramChannel.open();
            dc.configureBlocking(false);
            // byte object formation
            ArrayList<Object> sendingData = new ArrayList<>();
            sendingData.add(commandMessage);
            sendingData.add(clientPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(sendingData);

            byteBAOS = byteArrayOutputStream.toByteArray();
            buffer = ByteBuffer.wrap(byteBAOS);
            dc.send(buffer, serverAddress);
            //            dc.close();

//            ResponseMessage message;
//            do {
//                message = getAndReturnMessageLoop();
//            }
//            while (Objects.equals(message.getTypeName(), "null"));
//            return message;

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            System.out.println("Канал перегружен. Увеличьте объем буфера либо оптимизируйте скрипт. Некоторые команды могли не выполниться");
        } catch (IOException e) {
            System.out.println(e);
        }
        return new ResponseMessage<>("null", null);
    }

    public String getAndReturnMessageLoop() {
        try {
            // space between sending and getting
            byteBAOS = new byte[1024 * 16];
            DatagramPacket packet = new DatagramPacket(byteBAOS, byteBAOS.length);
//            socket.setSoTimeout(15000);

            // getting
            socket.receive(packet);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());

            byte[] a = byteArrayInputStream.readAllBytes();
            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(a));
            ResponseMessage deserializedResponse = (ResponseMessage) objectInputStream.readObject();
            if (Objects.equals(deserializedResponse.getTypeName(), "NOTIFY") || Objects.equals(deserializedResponse.getTypeName(), "java.lang.Boolean")){
//                app.customizedAlert("Ререндер сцены из-за того, что один из клиентов обновил коллекцию").showAndWait();
//                app.render();
                return "U";
            }
            else {
                System.out.println(1);
                System.out.println("deserializedResponse.getResponseData: " + deserializedResponse.getResponseData());
                newResponse = deserializedResponse;
                setReadyToReturnMessage(true);
            }
        } catch (IOException | ClassNotFoundException err) {
            err.printStackTrace();
        }
//        return new ResponseMessage<>("null", null);
        return "null";
    }

    public void setApp(FXApplication app) {
        this.app = app;
    }

    public void setReadyToReturnMessage(boolean readyToReturnMessage) {
        this.readyToReturnMessage = readyToReturnMessage;
    }

//    public String getNewMessage() {
//        return newMessage;
//    }

    public boolean isReadyToReturnMessage() {
        return readyToReturnMessage;
    }

//    public void changed(ObservableValue<? extends String> prop,
//                               String oldValue,
//                               String newValue) {
//        app.customizedAlert(newValue).showAndWait();
//    }
}