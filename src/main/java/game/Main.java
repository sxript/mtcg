package game;

import app.App;
import app.dto.QueueUser;
import app.models.*;
import db.DBConnection;
import enums.Element;
import server.Server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {
        DBConnection db = DBConnection.getInstance();
        db.getConnection();

        BlockingQueue<QueueUser> gameQueue = new LinkedBlockingQueue<>();

        // Start Game Queue Thread
        GameQueueConsumer gameQueueConsumer = new GameQueueConsumer(gameQueue);
        Thread consumerThread = new Thread(gameQueueConsumer);
        consumerThread.start();

        App app = new App(gameQueue);
        Thread service = new Thread(new Server(app, 7777));
        service.start();
    }
}