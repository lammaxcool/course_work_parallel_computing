package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {

    }
}

class ClientService {
    private Socket clientSocket;
    private final String ip;
    private final int port;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private final Object pingObj = "";

    public ClientService(String ip, int port) {
        this.ip = ip;
        this.port = port;
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

    private boolean ping() {
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