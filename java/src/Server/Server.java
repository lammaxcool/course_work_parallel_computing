package Server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Server {

    private static ServerSocket serverSocket;
    private static int port = 2021;

    public static void main(String[] args) throws IOException {
        // choose port or default 2021
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        System.out.println("Server is running on port " + port);
        serverSocket = new ServerSocket(port);

        // client handling
        try {
            while (true) {
                // wait for connection
                Socket clientSocket = serverSocket.accept();
                // start handler
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } finally {
            serverSocket.close();
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

    private final Socket clientSocket;
    private Index index;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private final Object pingObj = "";

    ClientHandler(Socket socket) {
        clientSocket = socket;
        // set max wait timeout for reading
        try {
            clientSocket.setSoTimeout(500);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        try {
            outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // client send an array of words
    // and get List with results
    public void run() {
        System.out.println("Accepted client " + clientSocket);
        System.out.println();

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
//        List<String> listToReceive;
//        listToReceive = receive();
//        if (listToReceive == null) {
//            System.out.println("Disconnected");
//            return;
//        }
//        System.out.println(listToReceive);
//        List<String> listToSend = new LinkedList<>();
//        listToSend.add("Hello from server");
//        send(listToSend);
    }

    void send(Object obj) {
        try {
            outStream.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
            stopConnection();
        }
    }

    <T> T receive() {
        try {
            return (T) inStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            stopConnection();
        }
        return null;
    }

    boolean ping() {
        boolean res = false;
        try {
            outStream.writeObject(pingObj);
            try {
                res = inStream.readObject() != null;
            } catch (SocketException e) {
                res = false;
            }
        } catch (IOException | ClassNotFoundException ignored) {}

        return res;
    }

    private void stopConnection() {
        try {
            if(!clientSocket.isClosed()) {
                clientSocket.close();
                inStream.close();
                outStream.close();
            }
        } catch (IOException ignored) {}

        System.out.println("Disconnected " + clientSocket);
    }
}

class Index {
    private static Index instance;
    private final Indexer indexer;

    private Index(int threadAmount) {
        indexer = new Indexer("/data");
        indexer.initIndex(threadAmount);
    }

    // use to create index and recreate index
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