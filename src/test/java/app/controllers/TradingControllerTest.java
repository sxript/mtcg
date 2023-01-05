package app.controllers;

import app.models.Card;
import app.models.MonsterCard;
import app.models.Trade;
import app.models.User;
import app.service.CardService;
import app.service.TradingService;
import enums.Element;
import http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TradingControllerTest {
    private TradingController tradingController;
    private TradingService tradingService;
    private CardService cardService;
    private User user;

    @BeforeEach
    void setUp() {
        this.tradingService = mock(TradingService.class);
        this.cardService = mock(CardService.class);
        this.tradingController = new TradingController(tradingService, cardService);
        this.user = new User("username", "password");
    }

    @Test
    void getAllTrades_WithNoTrades_ShouldReturnNoContent() {
        when(tradingService.findAllTrades()).thenReturn(Collections.emptyList());

        Response response = tradingController.getAllTrades(user);
        assertEquals(HttpStatus.NO_CONTENT.getMessage(), response.getStatusMessage());
    }

    @Test
    void getAllTrades_WithTradesAvailable_ShouldReturnOk() {
        ArrayList<Trade> trades = new ArrayList<>();
        trades.add(new Trade("id1", "c1", "monster", 15));
        trades.add(new Trade("id2", "c2", "monster", 15));
        trades.add(new Trade("id3", "c3", "monster", 15));
        trades.add(new Trade("id4", "c4", "monster", 15));

        when(tradingService.findAllTrades()).thenReturn(trades);

        Response response = tradingController.getAllTrades(user);
        assertEquals(HttpStatus.OK.getMessage(), response.getStatusMessage());
    }

    @Test
    void completeTrade_WithCardLockedInTrade_ShouldReturnConflict() {
        Trade trade = new Trade("id1", "cardId1", "monster", 15);
        Card cardFromTrade = new MonsterCard("cardId1", "goblin", 10, Element.FIRE, "", user.getId(), "");
        when(tradingService.findTradeById(trade.getId())).thenReturn(Optional.of(trade));

        String cardIdToTrade = "\"cardId2\"";
        Card cardToTrade = new MonsterCard("cardId2", "dragon", 35, Element.FIRE, "", "userId2", "");

        when(cardService.findCardById(trade.getCardId())).thenReturn(Optional.of(cardFromTrade));
        when(cardService.findCardById(cardToTrade.getId())).thenReturn(Optional.of(cardToTrade));

        Trade tradeLockingCardToTrade = new Trade("id2", cardToTrade.getId(), "spell", 10);
        when(cardService.findTradeByCardId(cardToTrade.getId())).thenReturn(Optional.of(tradeLockingCardToTrade));

        Response response = tradingController.completeTrade(user,trade.getId(), cardIdToTrade);
        System.out.println(response.getContent());
        assertEquals(HttpStatus.CONFLICT.getMessage(), response.getStatusMessage());
    }

    @Test
    void completeTrade_WithOwnCard_ShouldReturnForbidden() {
        Trade trade = new Trade("id1", "cardId1", "monster", 15);
        Card cardFromTrade = new MonsterCard("cardId1", "goblin", 10, Element.FIRE, "", user.getId(), "");
        when(tradingService.findTradeById(trade.getId())).thenReturn(Optional.of(trade));

        String cardIdToTrade = "\"cardId2\"";
        Card cardToTrade = new MonsterCard("cardId2", "dragon", 35, Element.FIRE, "", user.getId(), "");

        when(cardService.findCardById(trade.getCardId())).thenReturn(Optional.of(cardFromTrade));
        when(cardService.findCardById(cardToTrade.getId())).thenReturn(Optional.of(cardToTrade));

        when(cardService.findTradeByCardId(cardToTrade.getId())).thenReturn(Optional.empty());

        Response response = tradingController.completeTrade(user,trade.getId(), cardIdToTrade);
        assertEquals(HttpStatus.FORBIDDEN.getMessage(), response.getStatusMessage());
    }

    @Test
    void completeTrade_WithNotOwningCard_ShouldReturnForbidden() {
        Trade trade = new Trade("id1", "cardId1", "monster", 15);
        Card cardFromTrade = new MonsterCard("cardId1", "goblin", 10, Element.FIRE, "", user.getId(), "");
        when(tradingService.findTradeById(trade.getId())).thenReturn(Optional.of(trade));

        String cardIdToTrade = "\"cardId2\"";
        Card cardToTrade = new MonsterCard("cardId2", "dragon", 35, Element.FIRE, "", "ID_FROM_OTHER_USER", "");

        when(cardService.findCardById(trade.getCardId())).thenReturn(Optional.of(cardFromTrade));
        when(cardService.findCardById(cardToTrade.getId())).thenReturn(Optional.of(cardToTrade));

        when(cardService.findTradeByCardId(cardToTrade.getId())).thenReturn(Optional.empty());

        Response response = tradingController.completeTrade(user,trade.getId(), cardIdToTrade);
        assertEquals(HttpStatus.FORBIDDEN.getMessage(), response.getStatusMessage());
    }

    @Test
    void completeTrade_WithCardLockedInDeck_ShouldReturnForbidden() {
        Trade trade = new Trade("id1", "cardId1", "monster", 15);
        Card cardFromTrade = new MonsterCard("cardId1", "goblin", 10, Element.FIRE, "", "USER_ID_FROM_TRADER", "");
        when(tradingService.findTradeById(trade.getId())).thenReturn(Optional.of(trade));

        String cardIdToTrade = "\"cardId2\"";
        Card cardToTrade = new MonsterCard("cardId2", "dragon", 35, Element.FIRE, "", user.getId(), "DECK_ID");

        when(cardService.findCardById(trade.getCardId())).thenReturn(Optional.of(cardFromTrade));
        when(cardService.findCardById(cardToTrade.getId())).thenReturn(Optional.of(cardToTrade));

        when(cardService.findTradeByCardId(cardToTrade.getId())).thenReturn(Optional.empty());

        Response response = tradingController.completeTrade(user,trade.getId(), cardIdToTrade);
        assertEquals(HttpStatus.FORBIDDEN.getMessage(), response.getStatusMessage());
    }

    @Test
    void completeTrade_WithValidCard_ShouldReturnOk() {
        Trade trade = new Trade("id1", "cardId1", "monster", 15);
        Card cardFromTrade = new MonsterCard("cardId1", "goblin", 10, Element.FIRE, "", "USER_ID_FROM_TRADER", "");
        when(tradingService.findTradeById(trade.getId())).thenReturn(Optional.of(trade));

        String cardIdToTrade = "\"cardId2\"";
        Card cardToTrade = new MonsterCard("cardId2", "dragon", 35, Element.FIRE, "", user.getId(), null);

        when(cardService.findCardById(trade.getCardId())).thenReturn(Optional.of(cardFromTrade));
        when(cardService.findCardById(cardToTrade.getId())).thenReturn(Optional.of(cardToTrade));

        when(cardService.findTradeByCardId(cardToTrade.getId())).thenReturn(Optional.empty());
        when(tradingService.deleteTrade(trade)).thenReturn(1);

        Response response = tradingController.completeTrade(user,trade.getId(), cardIdToTrade);
        System.out.println(response.getContent());
        assertEquals(HttpStatus.OK.getMessage(), response.getStatusMessage());
    }

    @Test
    void deleteTrade_WithDifferentUser_ShouldReturnForbidden() {
        Trade trade = new Trade("id1", "cardId1", "monster", 15);
        Card cardFromTrade = new MonsterCard(trade.getCardId(), "goblin", 10, Element.FIRE, "", "USER_ID_FROM_TRADER", null);

        when(tradingService.findTradeById(trade.getId())).thenReturn(Optional.of(trade));
        when(cardService.findCardById(cardFromTrade.getId())).thenReturn(Optional.of(cardFromTrade));

        Response response = tradingController.deleteTrade(user, trade.getId());
        System.out.println(response.getContent());
        assertEquals(HttpStatus.FORBIDDEN.getMessage(), response.getStatusMessage());
    }
}