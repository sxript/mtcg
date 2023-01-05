package app.controllers;

import app.dto.UserStatsProfileDTO;
import app.models.User;
import app.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Response;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    UserService userService;
    UserController userController;

    @BeforeEach
    void init() {
        this.userService = mock(UserService.class);
        this.userController = new UserController(userService);
    }

    @Test
    void createUser_WithNoUsername_ShouldReturnBadRequest() {
        String userJson = "{ \"Username\": \"\", \"Password\": \"password\"}";
        Response response = userController.createUser(userJson);

        assertEquals(HttpStatus.BAD_REQUEST.getMessage(), response.getStatusMessage());

        userJson = "{\"Password\": \"password\"}";
        response = userController.createUser(userJson);

        assertEquals(HttpStatus.BAD_REQUEST.getMessage(), response.getStatusMessage());
    }

    @Test
    void createUser_WithNoPassword_ShouldReturnBadRequest() {
        String userJson = "{ \"Username\": \"Maxi\", \"Password\": \"\"}";
        Response response = userController.createUser(userJson);

        assertEquals(HttpStatus.BAD_REQUEST.getMessage(), response.getStatusMessage());

        userJson = "{\"Username\": \"Maxi\"}";
        response = userController.createUser(userJson);

        assertEquals(HttpStatus.BAD_REQUEST.getMessage(), response.getStatusMessage());
    }

    @Test
    void createUser_WithBlankJson_ShouldReturnBadRequest() {
        String userJson = "{ }";
        Response response = userController.createUser(userJson);

        assertEquals(HttpStatus.BAD_REQUEST.getMessage(), response.getStatusMessage());
    }

    @Test
    void createUser_WithExistingUser_ShouldReturnConflict() {
        Optional<User> optionalUser = Optional.of(new User("max", "passwort"));
        when(userService.getUserByUsername(any(String.class))).thenReturn(optionalUser);

        String userJson = "{ \"Username\": \"max\", \"Password\": \"passwort\"}";
        Response response = userController.createUser(userJson);
        assertEquals(HttpStatus.CONFLICT.getMessage(), response.getStatusMessage());
    }

    @Test
    void createUser_WithNewUser_ShouldReturnCreated() {
        Optional<User> optionalUser = Optional.empty();
        when(userService.getUserByUsername(any(String.class))).thenReturn(optionalUser);

        String userJson = "{ \"Username\": \"max\", \"Password\": \"passwort\"}";
        Response response = userController.createUser(userJson);
        assertEquals(HttpStatus.CREATED.getMessage(), response.getStatusMessage());
    }

    @Test
    void updateDifferentUser_WithNoAdmin_ShouldReturnUnauthorized() {
        User user = new User("username", "password");
        assertFalse(user.isAdmin());

        String usernameToUpdate = "herbert";
        String rawUserToBeUpdated = "";
        Response response = userController.updateUser(user, usernameToUpdate, rawUserToBeUpdated);
        assertEquals(HttpStatus.UNAUTHORIZED.getMessage(), response.getStatusMessage());
    }

    @Test
    void updateDifferentUser_WithAdmin_ShouldNotReturnUnauthorized() throws JsonProcessingException {
        User user = new User("username", "password");
        user.setAdmin(true);
        assertTrue(user.isAdmin());

        UserStatsProfileDTO updatedUser = new UserStatsProfileDTO();
        updatedUser.setUser(new User("max", "password"));

        ObjectMapper objectMapper = mock(ObjectMapper.class);
        userController.setObjectMapper(objectMapper);
        when(userController.getObjectMapper().readValue(anyString(), eq(UserStatsProfileDTO.class))).thenReturn(updatedUser);
        when(userService.getUserByUsername("username")).thenReturn(Optional.empty());

        String usernameToUpdate = "herbert";
        String rawUserToBeUpdated = "{ \"username\": \"max\"}";
        Response response = userController.updateUser(user, usernameToUpdate, rawUserToBeUpdated);
        assertNotEquals(HttpStatus.UNAUTHORIZED.getMessage(), response.getStatusMessage());
    }
}