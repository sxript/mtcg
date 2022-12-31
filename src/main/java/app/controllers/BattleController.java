package app.controllers;

import app.models.User;
import game.GameQueue;
import game.events.EventManager;
import game.events.GameListener;
import http.ContentType;
import http.HttpStatus;
import lombok.Getter;
import server.Response;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BattleController {
    @Getter
    private GameQueue gameQueue = new GameQueue();
    private ConcurrentMap<String, Response> userIdToResponse = new ConcurrentHashMap<>();

    public Response battle(User user) {
        System.out.println("------------------------------------------------------------------------");
        BlockingQueue<User> blockingQueue = getGameQueue().getBlockingQueue();
        System.out.println("GameQUEUE current instance: " + gameQueue.hashCode());
        System.out.println("JOIN LOBBY, in Thread: " + Thread.currentThread().getId());
        System.out.println("Currently: " + blockingQueue.size() + " Users in Queue, in Thread: " + Thread.currentThread().getId());
        User opponent = null;
        if (blockingQueue.isEmpty()) {
            try {
                blockingQueue.put(user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            try {
                opponent = blockingQueue.take();
                System.out.println("starting game");
                Thread.sleep(10000);
                userIdToResponse.put(opponent.getId(), new Response(HttpStatus.OK, ContentType.JSON, "OK"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (opponent != null)
            System.out.println("Thread: " + Thread.currentThread().getId() + " Opponent: " + ((opponent == null) ? null : Objects.requireNonNull(opponent).getUsername()));
        System.out.println("Thread:" + Thread.currentThread().getId() + " the current GAME QUEUE IS: " + getGameQueue().hashCode());

        if (opponent == null) {
            while (!userIdToResponse.containsKey(user.getId())) {
            }
            return userIdToResponse.get(user.getId());
        }

        System.out.println("---- GAME COMPLETED ----");
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"message\": \"Game completed\"}"
        );
    }

}
