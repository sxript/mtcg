package app.controllers;

import app.dao.StatsDao;
import app.models.Stats;
import app.models.User;
import app.service.GameService;
import app.service.GameServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.ArrayList;
import java.util.Optional;

public class GameController extends Controller {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    public GameController() {
        this(new GameServiceImpl());
    }

    // GET /stats
    public Response getStats(User user) {
        if(user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Stats> optionalStats = getStatsDao().get(user.getId());

        if(optionalStats.isEmpty()) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"No user with user_id: "+ user.getId() + " found\" }"
            );
        }
        try {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Stats found\", \"data\": " + getObjectMapper().writeValueAsString(optionalStats.get()) + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Something went wrong\" }"
            );
        }
    }

    // GET /scores
    // TODO: THIS CURRENTLY HAS NO USERNAME MAYBE ADD THAT?
    public Response getScoreboard() {
        ArrayList<Stats> stats = (ArrayList<Stats>) statsDao.getAll();
        try {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Stats found\", \"data\": " + getObjectMapper().writeValueAsString(stats) + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Something went wrong\" }"
            );
        }
    }
}
