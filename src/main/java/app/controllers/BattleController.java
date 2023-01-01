package app.controllers;

import app.models.User;
import http.ContentType;
import http.HttpStatus;
import lombok.Getter;
import server.Response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class BattleController {
    @Getter
    private final BlockingQueue<User> blockingQueue = new LinkedBlockingQueue<>(1);
    private final ConcurrentMap<String, Response> userIdToResponse = new ConcurrentHashMap<>();

    public Response battle(User user) {
        System.out.println("Currently: " + blockingQueue.size() + " Users in Queue, in Thread: " + Thread.currentThread().getId());
        User opponent = null;
        try {
            if(blockingQueue.remainingCapacity() > 0) {
                blockingQueue.put(user);
            } else {
                opponent = blockingQueue.take();
                System.out.println("starting game");
                Thread.sleep(10000);
                userIdToResponse.put(opponent.getId(), new Response(HttpStatus.OK, ContentType.JSON, "OK"));
            }
        } catch (InterruptedException e) {
                e.printStackTrace();
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"error\": \"Something went wrong\"}"
                );
        }

        if (opponent == null) {
            while (!userIdToResponse.containsKey(user.getId())) {
            }
            return userIdToResponse.get(user.getId());
        }

        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"message\": \"Game completed\"}"
        );
    }
}
