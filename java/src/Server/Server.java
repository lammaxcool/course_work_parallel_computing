package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;

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
    private final Index index;

    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;

    private final Object pingObj = "";

    ClientHandler(Socket socket) {
        clientSocket = socket;
        index = Index.getInstance();
        try {
            outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // client send an array of words
    // and get Collection with results
    public void run() {
        System.out.println("Accepted client " + clientSocket);

        // commands
        //     * find
        //     * exit
        while (true) {
            // check client connection
            if (ping()) {
                // read command
                try {
                    String command = receive();
                    if (command.equals("/find")) {
                        String[] words = receive();
                        try {
                            Collection<String> result = (Collection<String>) index.getFilesByWords(words);
                            send(result);
                            System.out.println(result);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else if (command.equals("/exit")) {
                        stopConnection();
                        break;
                    }
                } catch (IOException e) {
                    stopConnection();
                    break;
                }
            } else {
                stopConnection();
                break;
            }
        }
    }

    void send(Object obj) {
        try {
            outStream.writeObject(obj);
            outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            stopConnection();
        }
    }

    <T> T receive() throws SocketTimeoutException {
        try {
            return (T) inStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SocketTimeoutException();
        }
    }

    boolean ping() {
        boolean res = false;
        try {
            outStream.writeObject(pingObj);
            try {
                res = inStream.readObject() != null;
            } catch (SocketTimeoutException ignored) {}
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
        indexer = new Indexer("/data/2000");
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