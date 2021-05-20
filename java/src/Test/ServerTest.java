package Test;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ServerTest {

    public static void main(String[] args) throws IOException {
        String ip = "localhost";
        int port = 2021;
        Client client = new Client(ip, port);
//        client.testConnection(ip, port);
//        client.pingTest();
        client.findTest();
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
    // passed
    public void testConnection(String ip, int port) {
        startConnection(ip, port);
        List<String> listToSend = new LinkedList<>();
        listToSend.add("Hello from client");
        send(listToSend);
        List<String> listToReceive;
        listToReceive = receive();
        System.out.println(listToReceive);
        stopConnection();
    }
    // passed
    void pingTest() {
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
    // passed
    void findTest() throws IOException {
        startConnection(ip, port);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            if (ping()) {
                String line = reader.readLine();
                String[] lineValue = line.split(" ", 2);
                if (lineValue.length < 2) {
                    System.out.println("Unknown command");
                    continue;
                }
                if  (lineValue[0].equals("/find")) {
                    send("/find");
                    send(lineValue[1].split(" "));
                    Collection<String> listToReceive = receive();
                    System.out.println(listToReceive);
                } else {
                    System.out.println("Unknown command");
                }
            } else {
                stopConnection();
                break;
            }
        }
    }

    private void startConnection(String ip, int port) {
        System.out.println("Trying to connect " + ip + ":" + port + "...");
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
            outStream.flush();
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
