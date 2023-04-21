package functional_classes.threads;

import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;
import functional_classes.commands_executors.CommandDistributor;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ServerSerializer {
    private byte[] byteCommandMessage = new byte[1024 * 16];
    InetAddress host;
    int serverPortToSend = 7777;
    int clientPort;
    private DatagramSocket socketToSend;
    SocketAddress socketAddressToGet;
    DatagramChannel datagramChannel;
    CommandDistributor commandDistributor;
    CommandMessage deserializedCommandMessage;
    ResponseMessage<Object> response;
    private String stage = "get";
    List<Integer> clientPortsList = new ArrayList<>();


    public ServerSerializer(CommandDistributor commandDistributor) throws IOException {
        socketToSend = new DatagramSocket(serverPortToSend);
        socketAddressToGet = new InetSocketAddress(7000);
        this.commandDistributor = commandDistributor;
        datagramChannel = DatagramChannel.open();
        datagramChannel.bind(socketAddressToGet);
    }

    public void waitForRequest() {
        try {
            while (Objects.equals(stage, "get")) {
                socketAddressToGet = datagramChannel.receive(ByteBuffer.wrap(byteCommandMessage));
                if (socketAddressToGet != null) {
                    ByteArrayInputStream bis = new ByteArrayInputStream(byteCommandMessage);
                    ObjectInputStream ois = new ObjectInputStream(bis);
                    ArrayList<Object> deserializedData = (ArrayList<Object>) ois.readObject();
                    deserializedCommandMessage = (CommandMessage) deserializedData.get(0);
                    clientPort = (Integer) deserializedData.get(1);
                    if (!clientPortsList.contains(clientPort)) {clientPortsList.add(clientPort);}
                    stage = "execute";
                }
            }
        } catch (ClassCastException e) {
            System.out.println("Неверный тип полученных данных. Убедитесь, что сообщение с клиента приводится к классу CommandMessage");
        } catch (IOException e) {
            System.out.println("Прерван поток ожидания/получения данных от клиента");
        } catch (ClassNotFoundException e) {
            System.out.println("Команда, передаваемая пользователем не дошла(");
        }
    }

    public void executeCommand() {
        // command execution
        Object result = commandDistributor.execution(deserializedCommandMessage);
        System.out.println("result: " + result);

        response = result != null ? new ResponseMessage<>(result.getClass().getName(), result) : new ResponseMessage<>("String", "Что-то пошло не так...");
        stage = "send";
    }

    public void sendResponse() throws IOException {
        // sending
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(response);
        byte[] byteBAOS = byteArrayOutputStream.toByteArray();
        host = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(byteBAOS, byteBAOS.length, host, clientPort);
        stage = "get";
        System.out.println("notifyAboutCollectionUpdate " + clientPort);
        socketToSend.send(packet);
//        socketToSend.close();
    }

    public void notifyAboutCollectionUpdate(){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream;
            try {
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                ResponseMessage<? extends String> notifyResponse = new ResponseMessage<>("NOTIFY", "MoviesListUpdated");
                objectOutputStream.writeObject(notifyResponse);
                byte[] byteBAOS = byteArrayOutputStream.toByteArray();
                host = InetAddress.getByName("localhost");
                clientPortsList.forEach(port -> {
                    DatagramPacket packet = new DatagramPacket(byteBAOS, byteBAOS.length, host, port);
                    try {
                        System.out.println("notifyAboutCollectionUpdate " + port);
                        socketToSend.send(packet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public synchronized void close() throws IOException {
        datagramChannel.close();
    }
}

