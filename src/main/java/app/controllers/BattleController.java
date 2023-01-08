package app.controllers;

import app.dto.QueueUser;
import app.exceptions.InvalidDeckException;
import app.models.BattleLog;
import app.models.User;
import app.service.BattleService;
import app.service.BattleServiceImpl;
import app.service.GameService;
import app.service.GameServiceImpl;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import server.Response;

import java.util.Optional;
import java.util.concurrent.*;

@Getter(AccessLevel.PRIVATE)
public class BattleController {
    private final BlockingQueue<QueueUser> userGameQueue;
    private final GameService gameService = new GameServiceImpl();
    private final BattleService battleService = new BattleServiceImpl();
    public BattleController(BlockingQueue<QueueUser> userGameQueue) {
       this.userGameQueue = userGameQueue;
    }

    // POST /battles
    public Response battle(User user) {
        System.out.println("Currently: " + getUserGameQueue().size() + " Users in Queue, in Thread: " + Thread.currentThread().getId());

        try {
            getGameService().battlePreCheck(user);
        } catch (InvalidDeckException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"" + e.getMessage() + "\"}"
            );
        }

        try {
            QueueUser queueUser = new QueueUser(user);
            getUserGameQueue().put(queueUser);

            // Could change this to poll with a timeout so if something happens in a game, or it takes to long the user can still get a response
            return queueUser.getResponseQueue().take();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }
    }

    // GET /battles/:battleLogId
    public Response getBattleLog(String battleLogId) {
        Optional<BattleLog> optionalBattleLog = battleService.findBattleLogById(battleLogId);
        if(optionalBattleLog.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"message\": \"No BattleLog found with the provided id\"}"
            );
        }
        BattleLog battleLog = optionalBattleLog.get();
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"game_id\": \""+ battleLog.getId() +"\", \"message\": \""+ battleLog.getMessage() +"\", \"log\": "+ battleLog.getJson() +"}"
        );
    }
}
