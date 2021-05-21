package Client;

import java.io.*;
import java.net.Socket;
import java.util.Collection;

public class Client {

    static String ip = "localhost";
    static int port = 2021;

    public static void main(String[] args) {
        // choose port and ip or default 2021 localhost
        if (args.length == 2) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
        }

        ClientService client = new ClientService(ip, port);

        try {
            client.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientService {

    private final String instruction = """
            Usage:
            '/find word1 word2 ...' to find files contains this words
            '/exit' to exit  
            """;
    private final String intro = "index: ";
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

    public void start() throws IOException {
        startConnection(ip, port);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print(intro);
            String userLine = reader.readLine();
            String[] dividedUserLine = userLine.split(" ", 2);
            String command = dividedUserLine[0];
            if (command.equals("")) {
                continue;
            }
            if (ping()) {
                if  (command.equals("/find")) {
                    send("/find");
                    String[] commandArgs = {""};
                    // if user doesn't provide words to find
                    // send empty string
                    if (dividedUserLine.length > 1) {
                        commandArgs = dividedUserLine[1].split(" ");
                    }
                    send(commandArgs);
                    Collection<String> listToReceive = receive();
                    System.out.println(listToReceive);
                } else if (command.equals("/exit")) {
                    send("/exit");
                    stopConnection();
                    break;
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