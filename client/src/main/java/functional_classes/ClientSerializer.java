package functional_classes;


import auxiliary_classes.CommandMessage;
import auxiliary_classes.ResponseMessage;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;


public class ClientSerializer {
    static DatagramSocket socket;
    InetAddress host;
    int clientPort;
    static DatagramChannel dc;
    static ByteBuffer buffer;
    static SocketAddress serverAddress;


    public ClientSerializer(int clientPort) throws SocketException, UnknownHostException {
        this.clientPort = clientPort;
        socket = new DatagramSocket();
        host = InetAddress.getByName("localhost");
        serverAddress = new InetSocketAddress(host, 7000);
        socket = new DatagramSocket(clientPort);
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

            byte[] byteBAOS = byteArrayOutputStream.toByteArray();
            buffer = ByteBuffer.wrap(byteBAOS);
            dc.send(buffer, serverAddress);
//            dc.close();

            // space between sending and getting
            byteBAOS = new byte[1024 * 16];
            DatagramPacket packet = new DatagramPacket(byteBAOS, byteBAOS.length);
            socket.setSoTimeout(15000);

            // getting
            socket.receive(packet);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(packet.getData());

            byte[] a = byteArrayInputStream.readAllBytes();

            ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(a));
            ResponseMessage deserializedResponse = (ResponseMessage) objectInputStream.readObject();
            return deserializedResponse;
        } catch (SocketTimeoutException e){
            e.printStackTrace();
        } catch (EOFException e) {
            System.out.println("Канал перегружен. Увеличьте объем буфера либо оптимизируйте скрипт. Некоторые команды могли не выполниться");
        } catch (IOException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        finally {
//            socket.close();
//        }
        return new ResponseMessage<>("null", null);
    }

}