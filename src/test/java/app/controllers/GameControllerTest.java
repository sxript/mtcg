package app.controllers;

import app.dto.UserStatsDTO;
import app.models.Stats;
import app.models.User;
import app.service.GameService;
import http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Response;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {

    private GameService gameService;
    private GameController gameController;
    private User user;

    @BeforeEach
    void setUp() {
        this.gameService = mock(GameService.class);
        this.gameController = new GameController(gameService);
        this.user = new User("username", "password");
    }

    @Test
    void getStats_WithNoStats_ShouldReturnNotFound() {
        when(gameService.getStatsByUserId(user.getId())).thenReturn(Optional.empty());

        Response response = gameController.getStats(user);
        assertEquals(HttpStatus.NOT_FOUND.getMessage(), response.getStatusMessage());
    }

    @Test
    void getStats_WithStatsAvailable_ShouldReturnOk() {
        UserStatsDTO userStatsDTO = new UserStatsDTO();
        userStatsDTO.setStats(new Stats());
        userStatsDTO.setUsername(user.getUsername());

        when(gameService.getStatsByUserId(user.getId())).thenReturn(Optional.of(userStatsDTO));

        Response response = gameController.getStats(user);
        assertEquals(HttpStatus.OK.getMessage(), response.getStatusMessage());
    }
}