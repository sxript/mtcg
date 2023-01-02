package server;

import app.App;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.*;

@Getter(AccessLevel.PRIVATE)
@Setter(AccessLevel.PRIVATE)
public class Server implements Runnable {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private App app;
    private int port;
    private int clients = 5;

    public Server(App app, int port) {
        setApp(app);
        setPort(port);
    }

    public Server(App app, int port, int clients) {
        setApp(app);
        setPort(port);
        setClients(clients);
    }

    private void listen() {

        try {
            setServerSocket(new ServerSocket(port, clients));
            System.out.println("Server started on " + getServerSocket().getLocalSocketAddress());

            while (true) {
                setClientSocket(getServerSocket().accept());
                new Thread(new ClientHandler(getClientSocket(), app)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        System.out.println("Closing Server at PORT " + port);
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
        listen();
    }
}
