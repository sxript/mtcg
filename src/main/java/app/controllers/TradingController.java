package app.controllers;

import app.models.Card;
import app.models.MonsterCard;
import app.models.Trade;
import app.models.User;
import app.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import server.Response;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;


public class TradingController extends Controller {
    @Getter(AccessLevel.PRIVATE)
    private final CardService cardService;
    private final TradingService tradingService;

    public TradingController(TradingService tradingService, CardService cardService) {
        this.tradingService = tradingService;
        this.cardService = cardService;
    }

    public TradingController() {
        this(new TradingServiceImpl(), new CardServiceImpl());
    }

    // GET /tradings
    public Response getAllTrades(User user) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Collection<Trade> trades = tradingService.findAllTrades();
        if (trades.isEmpty()) {
            return new Response(
                    HttpStatus.NO_CONTENT,
                    ContentType.JSON,
                    "{\"message\": \"The request was fine, but there was no trading deals available\"}"
            );
        }

        try {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Showing available trading deals\", \"data\": " + getObjectMapper().writeValueAsString(trades) + " }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }
    }

    // POST /tradings/:tradingDealId
    public Response completeTrade(User user, String tradeId, String cardIdToTrade) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Trade> optionalTrade = tradingService.findTradeById(tradeId);

        if (optionalTrade.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"The provided deal ID was not found\"}"
            );
        }

        Trade trade = optionalTrade.get();
        Optional<Card> optionalCardFromTrade = cardService.findCardById(trade.getCardId());
        Optional<Card> optionalCardToTrade = cardService.findCardById(cardIdToTrade);

        if (optionalCardFromTrade.isEmpty() || optionalCardToTrade.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"The card in the trade or the provided card could not be found\"}"
            );
        }

        Card cardFromTrade = optionalCardFromTrade.get();
        Card cardToTrade = optionalCardToTrade.get();

        if (Objects.equals(cardFromTrade.getUserId(), cardToTrade.getUserId()) ||
                !Objects.equals(cardToTrade.getUserId(), user.getId()) ||
                cardToTrade.getDeckId() != null ||
                cardToTrade.getDamage() < trade.getMinimumDamage() ||
                !cardToTrade.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains(trade.getCardType().toLowerCase(Locale.ROOT))) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"error\": \"The offered card is not owned by the user, or the requirements are not met (Type, MinimumDamage), or the offered card is locked in the deck, or the user tries to trade with self\"}"
            );
        }

        // TODO: WHAT IF TRADE NOT HERE ANYMORE?
        tradingService.deleteTrade(trade);

        Card cardFromTradeUpdated = new MonsterCard(cardFromTrade);
        Card cardToTradeUpdated = new MonsterCard(cardToTrade);

        cardFromTradeUpdated.setUserId(cardToTrade.getUserId());
        cardToTradeUpdated.setUserId(cardFromTrade.getUserId());

        cardService.updateCard(cardFromTrade, cardFromTradeUpdated);
        cardService.updateCard(cardToTrade, cardToTradeUpdated);
        return new Response(
               HttpStatus.OK,
               ContentType.JSON,
               "{ \"message\": \"Trading deal successfully executed\" }"
        );
    }

    // DELETE /tradings/:tradingDealId
    public Response deleteTrade(User user, String tradeId) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Trade> optionalTrade = tradingService.findTradeById(tradeId);

        if (optionalTrade.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"The provided deal ID was not found\"}"
            );
        }
        Trade trade = optionalTrade.get();
        Optional<Card> optionalCard = cardService.findCardById(trade.getCardId());

        Response notOwnedResponse = new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"The deal contains a card that is not owned by the user\"}"
        );

        if (optionalCard.isPresent()) {
            Card card = optionalCard.get();
            if (!Objects.equals(card.getUserId(), user.getId())) {
                return notOwnedResponse;
            }
        }

        tradingService.deleteTrade(trade);
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{\"message\": \"Trading deal successfully deleted\"}"
        );
    }

    public Response createTrade(User user, String rawTrade) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Trade trade;

        try {
            trade = getObjectMapper().readValue(rawTrade, Trade.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Could not parse\", \"data\": " + rawTrade + " }"
            );
        }
        return createTrade(user, trade);
    }

    // TODO: CHECK WHEN CREATING DECK THAT CARD IS NOT IN TRADE 409 response?
    // POST /tradings
    private Response createTrade(User user, Trade trade) {
        Optional<Trade> optionalTrade = tradingService.findTradeById(trade.getId());
        if (optionalTrade.isPresent()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"error\": \"A deal with this deal ID already exists\"}"
            );
        }

        Optional<Card> optionalCard = cardService.findCardById(trade.getCardId());
        if (optionalCard.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No card with provided ID found\"}"
            );
        }

        Card card = optionalCard.get();

        Response notOwnerOrLockedResponse = new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"The deal contains a card that is not owned by the user or locked in this deck\"}"
        );

        if (!Objects.equals(card.getUserId(), user.getId()) || card.getDeckId() != null) {
            return notOwnerOrLockedResponse;
        }

        tradingService.createTrade(trade);
        return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"Trading deal successfully created\"}"
        );
    }
}