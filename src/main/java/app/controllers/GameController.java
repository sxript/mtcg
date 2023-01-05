package app.controllers;

import app.dto.UserStatsDTO;
import app.models.User;
import app.service.GameService;
import app.service.GameServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import server.Response;

import java.util.ArrayList;
import java.util.Optional;

public class GameController extends Controller {
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

        Optional<UserStatsDTO> optionalUserStatsDTO = getGameService().getStatsByUserId(user.getId());

        if(optionalUserStatsDTO.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No user with Id: "+ user.getId() + " found\" }"
            );
        }
        try {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Stats found\", \"data\": " + getObjectMapper().writeValueAsString(optionalUserStatsDTO.get()) + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }
    }

    // GET /scores
    public Response getScoreboard() {
        ArrayList<UserStatsDTO> stats = (ArrayList<UserStatsDTO>) getGameService().getAllStatsSorted();
        try {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Stats found\", \"data\": " + getObjectMapper().writeValueAsString(stats) + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }
    }
}
