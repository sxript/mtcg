package app.controllers;

import app.models.Package;
import app.models.User;
import app.service.CardService;
import app.service.UserService;
import http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Response;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CardControllerTest {
    CardService cardService;
    UserService userService;
    CardController cardController;
    User user;

    @BeforeEach
    void init() {
        this.cardService = mock(CardService.class);
        this.userService = mock(UserService.class);
        this.cardController = new CardController(cardService, userService);
        this.user = new User("username", "password");
    }

    @Test
    void acquirePackage_WithNoPackage_ShouldReturnNotFound() {
        when(cardService.findFirstPackage()).thenReturn(Optional.empty());
        Response response = cardController.acquirePackage(user);
        assertEquals(HttpStatus.NOT_FOUND.getMessage(), response.getStatusMessage());
    }

    @Test
    void acquirePackage_WithNotEnoughCoins_ShouldReturnForbidden() {
        Package aPackage = new Package("id1", 5);

        user.setCoins(3);
        assertTrue(user.getCoins() < aPackage.getPrice());

        when(cardService.findFirstPackage()).thenReturn(Optional.of(aPackage));
        Response response = cardController.acquirePackage(user);
        assertEquals(HttpStatus.FORBIDDEN.getMessage(), response.getStatusMessage());
    }

    @Test
    void acquirePackage_WithPackageAlreadyBought_ShouldReturnGone() {
        Package aPackage = new Package("id1", 5);

        when(cardService.findFirstPackage()).thenReturn(Optional.of(aPackage));
        when(cardService.findAllCardsByPackageId(aPackage.getId())).thenReturn(Collections.emptyList());

        // Package already deleted
        when(cardService.deletePackage(aPackage)).thenReturn(0);

        Response response = cardController.acquirePackage(user);
        assertEquals(HttpStatus.GONE.getMessage(), response.getStatusMessage());
    }

    @Test
    void getUserCards_WithNoCards_ShouldReturnNoContent() {
       when(cardService.findAllCardsByUserId(user.getId())).thenReturn(Collections.emptyList());
       Response response = cardController.getUserCards(user);
       assertEquals(HttpStatus.NO_CONTENT.getMessage(), response.getStatusMessage());
    }

    @Test
    void getDeck_WithNoDeck_ShouldReturnNoContent() {
        when(cardService.findDeckByUserId(user.getId())).thenReturn(Optional.empty());

        Response response = cardController.getDeck(user, null);
        assertEquals(HttpStatus.NO_CONTENT.getMessage(), response.getStatusMessage());
    }
}