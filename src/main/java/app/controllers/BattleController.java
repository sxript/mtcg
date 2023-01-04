package app.controllers;

import app.dto.QueueUser;
import app.exceptions.InvalidDeckException;
import app.models.User;
import app.service.GameService;
import app.service.GameServiceImpl;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import server.Response;

import java.util.concurrent.*;

public class BattleController {
    private final BlockingQueue<QueueUser> userGameQueue;
    private final GameService gameService = new GameServiceImpl();

    public BattleController(BlockingQueue<QueueUser> userGameQueue) {
       this.userGameQueue = userGameQueue;
    }

    public Response battle(User user) {
        System.out.println("Currently: " + userGameQueue.size() + " Users in Queue, in Thread: " + Thread.currentThread().getId());

        try {
            gameService.battlePreCheck(user);
        } catch (InvalidDeckException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"" + e.getMessage() + "\"}"
            );
        }

        try {
            QueueUser queueUser = new QueueUser(user);
            userGameQueue.put(queueUser);

            // Could change this to poll with a timeout so if something happens in a game, or it takes to long the user can still get a response
            return queueUser.getResponseQueue().take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }
    }
}
