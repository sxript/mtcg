package app.controllers;

import app.models.Card;
import app.models.Deck;
import app.models.User;
import app.service.PlayerServiceImpl;
import game.Arena;
import http.ContentType;
import http.HttpStatus;
import lombok.Getter;
import server.Response;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.*;

public class BattleController {
    @Getter
    private final BlockingQueue<User> blockingQueue = new LinkedBlockingQueue<>(1);
    private final ConcurrentMap<String, BlockingQueue<Response>> userIdToResponse = new ConcurrentHashMap<>();
    private final Arena arena = new Arena();
    private PlayerServiceImpl playerService = new PlayerServiceImpl();

    private Response battlePreCheck(User user) {
        Optional<Deck> optionalDeck = playerService.findDeckByUserId(user.getId());
        if (optionalDeck.isEmpty()) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"No Deck configured\"}"
            );
        }
        ArrayList<Card> cards = (ArrayList<Card>) playerService.findCardsByDeckId(optionalDeck.get().getId());
        if (cards.size() != 4) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Deck must consist of 4 Cards\"}"
            );
        }

        return null;
    }

    public Response battle(User user) {
        System.out.println("Currently: " + blockingQueue.size() + " Users in Queue, in Thread: " + Thread.currentThread().getId());
        User opponent;
        Response response;

        Response preCheck = battlePreCheck(user);
        if(preCheck != null) return preCheck;

        try {
            boolean stopLoop = false;
            while (!stopLoop) {
                if (blockingQueue.remainingCapacity() > 0) {
                    stopLoop = blockingQueue.offer(user, 4, TimeUnit.SECONDS);
                    if (stopLoop) {
                        BlockingQueue<Response> result = new LinkedBlockingQueue<>(1);
                        userIdToResponse.put(user.getId(), result);
                        return result.take();
                    }
                } else {
                    opponent = blockingQueue.take();
                    response = arena.battle(user, opponent);
                    userIdToResponse.get(opponent.getId()).put(response);
                    return response;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Something went wrong\"}"
            );
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"error\": \"Something went wrong\"}"
        );
    }
}
