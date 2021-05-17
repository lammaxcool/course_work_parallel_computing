package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static ServerSocket serverSocket;
    private static int port = 2021;

    public static void main(String[] args) {
        // choose port or default 2021
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        // client handling
        try {
            serverSocket = new ServerSocket(port);
            while (true)
                // run new thread with client handler
                new ClientHandler(serverSocket.accept()).start();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }
    // shutdown server
    public static void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {

    private Socket clientSocket;
    private Indexer indexer;

    ClientHandler(Socket socket) {
        clientSocket = socket;
    }

    public void run() {
        System.out.println("hello");
    }

}
