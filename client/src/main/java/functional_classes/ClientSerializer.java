package functional_classes;


import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;
import gui.FXApplication;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


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
    private PropertyChangeSupport support;
    ResourceBundle bundle;


    public ClientSerializer(int clientPort) throws SocketException, UnknownHostException {
        this.clientPort = clientPort;
        socket = new DatagramSocket();
        host = InetAddress.getByName("localhost");
        serverAddress = new InetSocketAddress(host, 7000);
        socket = new DatagramSocket(clientPort);
        support = new PropertyChangeSupport(this);
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
            if (Objects.equals(deserializedResponse.getTypeName(), "NOTIFY")) {
//                System.out.println("NOTIFY to update");
                support.firePropertyChange("notify to update", this.newResponse, deserializedResponse);
                return "U";
            } else if (Objects.equals(deserializedResponse.getTypeName(), "java.lang.String")) {
                if (!Objects.equals(deserializedResponse.getResponseData().toString(), "")) {
//                    System.out.println(app.getLangShortTag());
//                    System.out.println(deserializedResponse.getResponseData().toString());
                    List<String> stringList = List.of(deserializedResponse.getResponseData().toString().split("\n"));
                    StringBuilder translatedText = new StringBuilder();
                    stringList.forEach(string -> {
                        if (bundle.containsKey(string)){
                            translatedText.append(bundle.getString(string));
                        } else {
                            translatedText.append(string);
                        }
                        translatedText.append("\n");
                    });
                    deserializedResponse.setResponseData(translatedText);
                    setNewResponse(deserializedResponse);
                    setReadyToReturnMessage(true);
                }
            } else {
                setNewResponse(deserializedResponse);
                setReadyToReturnMessage(true);
            }
        } catch (IOException |
                 ClassNotFoundException err) {
            err.printStackTrace();
        }
        return "null";
    }

    public void setApp(FXApplication app) {
        this.app = app;
    }

    public void setReadyToReturnMessage(boolean readyToReturnMessage) {
        this.readyToReturnMessage = readyToReturnMessage;
    }

    public void setNewResponse(ResponseMessage value) {
        // support.firePropertyChange("newResponse", this.newResponse, value);
        this.newResponse = value;
    }

    public void setBundle(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    public boolean isReadyToReturnMessage() {
        return readyToReturnMessage;
    }

    public ResponseMessage getNewResponse() {
        return newResponse;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        support.addPropertyChangeListener(pcl);
    }
}