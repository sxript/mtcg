package game;

import app.App;
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

        App app = new App();
        Thread service = new Thread(new Server(app, 7777));
        service.start();
    }
}