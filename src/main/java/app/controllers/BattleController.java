package app.controllers;

import app.exceptions.InvalidDeckException;
import app.models.User;
import app.service.GameService;
import app.service.GameServiceImpl;
import game.Arena;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import server.Response;

import java.util.concurrent.*;

public class BattleController {
    private final BlockingQueue<User> blockingQueue = new LinkedBlockingQueue<>(1);
    private final ConcurrentMap<String, BlockingQueue<Response>> userIdToResponse = new ConcurrentHashMap<>();
    private final Arena arena = new Arena();

    private final GameService gameService = new GameServiceImpl();

    public Response battle(User user) {
        System.out.println("Currently: " + blockingQueue.size() + " Users in Queue, in Thread: " + Thread.currentThread().getId());
        User opponent;
        Response response;

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
            boolean inGameQueue = false;
            while (!inGameQueue) {
                if (blockingQueue.remainingCapacity() > 0) {
                    inGameQueue = blockingQueue.offer(user, 2, TimeUnit.SECONDS);
                    if (inGameQueue) {
                        BlockingQueue<Response> result = new LinkedBlockingQueue<>(1);
                        userIdToResponse.put(user.getId(), result);
                        Response res = result.take();
                        userIdToResponse.remove(user.getId());
                        return res;
                    }
                } else {
                    opponent = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
                    if (opponent != null) {
                        response = arena.battle(user, opponent);
                        userIdToResponse.get(opponent.getId()).put(response);
                        return response;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }

        return CommonErrors.INTERNAL_SERVER_ERROR;
    }
}
