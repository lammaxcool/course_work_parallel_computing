package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;

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

class Index {
    private static Index instance;
    private final Indexer indexer;

    private Index(int threadAmount) {
        indexer = new Indexer("/data");
        indexer.initIndex(threadAmount);
    }

    // use to create index and recreate
    // or get instance
    public static Index getInstance(int threadAmount) {
        if (instance == null) {
            synchronized (Index.class) {
                instance = new Index(threadAmount);
            }
        }
        return instance;
    }

    // 4 threads by default
    // or get instance
    public static Index getInstance() {
        if (instance == null) {
            synchronized (Index.class) {
                instance = new Index(4);
            }
        }
        return instance;
    }

    public Collection<String> getFilesByWords(String... words) {
        return indexer.getFilesByWords(words);
    }
}