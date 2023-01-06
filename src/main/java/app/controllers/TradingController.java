package app.controllers;

import app.exceptions.DBErrorException;
import app.models.Card;
import app.models.MonsterCard;
import app.models.Trade;
import app.models.User;
import app.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
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
    private final UserService userService;

    public TradingController(TradingService tradingService, CardService cardService, UserService userService) {
        this.tradingService = tradingService;
        this.cardService = cardService;
        this.userService = userService;
    }

    public TradingController() {
        this(new TradingServiceImpl(), new CardServiceImpl(), new UserServiceImpl());
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
        try {
            JsonNode jsonNode = getObjectMapper().readTree(cardIdToTrade);
            cardIdToTrade = jsonNode.asText();
        } catch (JsonProcessingException e) {
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"malformed request\"}"
            );
        }
        Optional<Card> optionalCardFromTrade = cardService.findCardById(trade.getCardId());
        Optional<Card> optionalCardToTrade = cardService.findCardById(cardIdToTrade);

        if (optionalCardFromTrade.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"The card in the trade or the provided card could not be found\"}"
            );
        }

        Card cardFromTrade = optionalCardFromTrade.get();
        if (trade.getCoins() != null) {
            return executeCoinTrade(cardFromTrade, user, trade);
        }

        if (optionalCardToTrade.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"The card in the trade or the provided card could not be found\"}"
            );
        }
        Card cardToTrade = optionalCardToTrade.get();

        Optional<Trade> tradeWithCardToTrade = cardService.findTradeByCardId(cardToTrade.getId());
        if (tradeWithCardToTrade.isPresent()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"error\": \"The Provided Card is already locked in another Trade\" }"
            );
        }

        if (Objects.equals(cardFromTrade.getUserId(), cardToTrade.getUserId()) || !Objects.equals(cardToTrade.getUserId(), user.getId())
                || cardToTrade.getDeckId() != null) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"error\": \"The offered card is either not owned by the user, or card is locked in deck, or the user tries to trade with self\"}"
            );
        }

        if (cardToTrade.getDamage() < trade.getMinimumDamage() ||
                !cardToTrade.getClass().getSimpleName().toLowerCase(Locale.ROOT).contains(trade.getCardType().toLowerCase(Locale.ROOT))) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"error\": \"The offered card is not owned by the user, or the requirements are not met (Type, MinimumDamage), or the offered card is locked in the deck\"}"
            );
        }


        return executeTrade(cardFromTrade, cardToTrade, trade);
    }

    private Response executeCoinTrade(Card cardFromTrade, User user, Trade trade) {
        if (user.getCoins() < trade.getCoins()) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"error\": \"Not enough coins\"}"
            );
        }

        int deletedRows = tradingService.deleteTrade(trade);
        if (deletedRows == 0) {
            return new Response(
                    HttpStatus.GONE,
                    ContentType.JSON,
                    "{ \"error\": \"Oops it looks like this trade is not available anymore\"}"
            );
        }

        Optional<User> optionalCardOwner = userService.findUserById(cardFromTrade.getUserId());
        if (optionalCardOwner.isEmpty()) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Something went wrong\"}"
            );
        }
        User cardOwner = optionalCardOwner.get();
        cardOwner.setCoins(cardOwner.getCoins() + trade.getCoins());
        userService.updateUser(cardOwner.getUsername(), cardOwner);

        user.setCoins(user.getCoins() - trade.getCoins());
        userService.updateUser(user.getUsername(), user);

        Card cardFromTradeUpdated = new MonsterCard(cardFromTrade);
        cardFromTradeUpdated.setUserId(user.getId());

        cardService.updateCard(cardFromTrade.getId(), cardFromTradeUpdated);
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"Trading deal successfully executed\" }"
        );
    }


    private Response executeTrade(Card cardFromTrade, Card cardToTrade, Trade trade) {
        int deletedRows = tradingService.deleteTrade(trade);
        if (deletedRows == 0) {
            return new Response(
                    HttpStatus.GONE,
                    ContentType.JSON,
                    "{ \"error\": \"Oops it looks like this trade is not available anymore\"}"
            );
        }

        Card cardFromTradeUpdated = new MonsterCard(cardFromTrade);
        Card cardToTradeUpdated = new MonsterCard(cardToTrade);

        cardFromTradeUpdated.setUserId(cardToTrade.getUserId());
        cardToTradeUpdated.setUserId(cardFromTrade.getUserId());

        cardService.updateCard(cardFromTrade.getId(), cardFromTradeUpdated);
        cardService.updateCard(cardToTrade.getId(), cardToTradeUpdated);
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

        int deletedRows = tradingService.deleteTrade(trade);
        if (deletedRows == 0) {
            return new Response(
                    HttpStatus.GONE,
                    ContentType.JSON,
                    "{ \"error\": \"Trading deal already deleted\"}"
            );
        }
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
            if (trade.getId() == null || trade.getCardId() == null) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"Trade Id and/or Card Id must be provided\"}"
                );
            }
            if (trade.getCoins() != null) {
                if (trade.getCoins() > 0) {
                    trade.setCardType(null);
                    trade.setMinimumDamage(null);
                } else
                    return new Response(HttpStatus.BAD_REQUEST, ContentType.JSON, "{ \"error\": \"Coins must be greater than 0\"}");
            } else if (trade.getMinimumDamage() == null || trade.getMinimumDamage() < 0 || trade.getCardType() == null) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"MinimumDamage and/or CardType must be provided\"}"
                );
            }
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

        Optional<Trade> optionalCardInTrade = cardService.findTradeByCardId(card.getId());
        if (optionalCardInTrade.isPresent()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"error\": \"The provided card is already locked in a Trade\"}"
            );
        }

        Response notOwnerOrLockedResponse = new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"The deal contains a card that is not owned by the user or locked in this deck\"}"
        );

        if (!Objects.equals(card.getUserId(), user.getId()) || card.getDeckId() != null) {
            return notOwnerOrLockedResponse;
        }

        try {
            int createdRows = tradingService.createTrade(trade);
            if (createdRows == 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"Could not create Trade\"}"
                );
            }
        } catch (DBErrorException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Something went wrong: " + e.getMessage() + "\"}"
            );
        }

        return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"Trading deal successfully created\"}"
        );
    }
}
