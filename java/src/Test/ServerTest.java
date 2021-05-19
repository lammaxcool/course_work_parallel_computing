package Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class ServerTest {

    public static void main(String[] args) {
        String ip = "localhost";
        int port = 2021;
        Client client = new Client(ip, port);
//        client.testConnection(ip, port);
        client.pingTest();
    }
}

class Client extends Thread {

    private Socket clientSocket;
    private final String ip;
    private final int port;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private final Object pingObj = "";

    public Client(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void testConnection(String ip, int port) {
        System.out.println("Client is running");
        System.out.println("Trying to connect " + ip + ":" + port + "...");
        startConnection(ip, port);
//        List<String> listToSend = new LinkedList<>();
//        listToSend.add("Hello from client");
//        send(listToSend);
//        List<String> listToReceive;
//        listToReceive = receive();
//        System.out.println(listToReceive);
        stopConnection();
    }

    void pingTest() {
        System.out.println("Client is running");
        System.out.println("Trying to connect " + ip + ":" + port + "...");
        startConnection(ip, port);

        while (true) {
            if (ping()) {
                System.out.println("ping");
            } else {
                stopConnection();
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void startConnection(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Connected to " + ip + ":" + port);
        } catch (IOException ignored) {
            stopConnection();
        }
    }

    private void send(Object obj) {
        try {
            outStream.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T> T receive() {
        try {
            return (T) inStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    boolean ping() {
        try {
            inStream.readObject();
            outStream.writeObject(pingObj);
            return true;
        } catch (IOException | ClassNotFoundException ignored) {
            return false;
        }
    }

    private void stopConnection() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
                inStream.close();
                outStream.close();
            }
        } catch (IOException ignored) {}

        System.out.println("Disconnected");
    }
}
