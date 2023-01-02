package app.controllers;

import app.dao.CardDao;
import app.dao.DeckDao;
import app.dao.PackageDao;
import app.dao.UserDao;
import app.exceptions.CustomJsonProcessingException;
import app.models.*;
import app.models.Package;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.*;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class PackageCardUserController extends Controller {
    private UserDao userDao;
    private PackageDao packageDao;
    private CardDao cardDao;
    private DeckDao deckDao;

    public PackageCardUserController(UserDao userDao, PackageDao packageDao, CardDao cardDao, DeckDao deckDao) {
        setUserDao(userDao);
        setPackageDao(packageDao);
        setCardDao(cardDao);
        setDeckDao(deckDao);
    }

    // GET /decks
    // TODO: REFACTOR ALL CONTROLLERS TO USE GET METHOD FOR DAO
    public Response getDeck(User user) {
        if(user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Deck> optionalDeck = getDeckDao().getByUserId(user.getId());

        Deck deck = new Deck(user.getId());
        if (optionalDeck.isPresent()) {
            deck = optionalDeck.get();
        }

        Collection<Card> cardDeck = getCardDao().getAllByPackageUserDeckId(null, null, deck.getId());
        try {
            if (cardDeck.isEmpty()) {
                return new Response(
                        HttpStatus.NO_CONTENT,
                        ContentType.JSON,
                        "{ \"message\": \"Deck is empty\", \"data\": " + getObjectMapper().writeValueAsString(cardDeck) + "}"
                );
            }
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Deck found\", \"data\": " + getObjectMapper().writeValueAsString(cardDeck) + "}"
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

    // PUT /decks
    public Response setUserDeck(User user, String deckJSON) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        try {
            JsonNode jsonNode = getObjectMapper().readTree(deckJSON);
            if (!jsonNode.isArray()) throw new CustomJsonProcessingException("No array Provided");
            if (jsonNode.size() != Deck.DECK_SIZE) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"Deck must consist of " + Deck.DECK_SIZE + " Cards\", \"data\": " + deckJSON + " }"
                );
            }

            // The deck and cards in the current deck are needed to rollback in case of an error
            Optional<Deck> optionalDeck = deckDao.getByUserId(user.getId());
            Deck deck = null;
            Collection<Card> cardsInDeckBeforeTx = Collections.emptyList();
            if (optionalDeck.isPresent()) {
                deck = optionalDeck.get();
                cardsInDeckBeforeTx = cardDao.getAllByPackageUserDeckId(null, null, deck.getId());
                for (Card c : cardsInDeckBeforeTx) {
                    c.setDeckId(null);
                    cardDao.update(c, c);
                }
            }

            Deck tempDeck = new Deck(user.getId());
            if (deck == null) {
                deckDao.save(tempDeck);
            }

            deck = deck == null ? tempDeck : deck;
            Set<String> cardsAddedToDeck = new HashSet<>();
            for (JsonNode node : jsonNode) {
                String cardId = node.asText();
                if (cardsAddedToDeck.contains(cardId)) {
                    rollbackUserDeck(deck, cardsAddedToDeck, cardsInDeckBeforeTx);
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.JSON,
                            "{ \"message\": \"No duplicate Card IDs\"}"
                    );
                }
                Response response = setUserCard(user, deck, cardId);
                if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                    rollbackUserDeck(deck, cardsAddedToDeck, cardsInDeckBeforeTx);
                    return response;
                }
                cardsAddedToDeck.add(cardId);
            }

            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\": \"The Deck has been successfully configured\", \"data\": " + getObjectMapper().writeValueAsString(cardsAddedToDeck) + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Could not parse " + e.getMessage() + "\", \"data\": " + deckJSON + " }"
            );
        }
    }

    private void rollbackUserDeck(Deck deck, Collection<String> cardIdsAddedToDeck, Collection<Card> cardsInDeckBeforeTx) {
        for (String cId : cardIdsAddedToDeck) {
            Optional<Card> optionalCard = cardDao.get(cId);
            if (optionalCard.isEmpty()) continue;

            Card c = optionalCard.get();
            c.setDeckId(null);
            cardDao.update(c, c);
        }

        for (Card c : cardsInDeckBeforeTx) {
            c.setDeckId(deck.getId());
            cardDao.update(c, c);
        }
    }

    private Response setUserCard(User user, Deck deck, String cardId) {
        Optional<Card> optionalCard = cardDao.get(cardId);
        Response forbidden = new Response(
                HttpStatus.FORBIDDEN,
                ContentType.JSON,
                "{ \"error\": \"At least one of the provided cards does not belong to the user or is not available\" }"
        );
        if (optionalCard.isEmpty()) {
            return forbidden;
        }

        Card card = optionalCard.get();
        if (!Objects.equals(user.getId(), card.getUserId())) {
            return forbidden;
        }

        Card oldCard = new MonsterCard(card);
        card.setDeckId(deck.getId());
        cardDao.update(oldCard, card);

        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"Successfully added to deck\"}"
        );
    }

    // GET /cards
    public Response getUserCards(User user) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Collection<Card> cards = cardDao.getAllByPackageUserDeckId(null, user.getId(), null);
        if (cards.isEmpty()) {
            return new Response(
                    HttpStatus.NO_CONTENT,
                    ContentType.JSON,
                    "{ \"error\": \"The request was fine, but the user doesn't have any cards\" }"
            );
        }
        try {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Successfully fetched User cards\", \"data\": " + getObjectMapper().writeValueAsString(cards) + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Something went wrong\"}"
            );
        }
    }

    public Response acquirePackage(User user) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Package> optionalPackage = packageDao.getFirst();
        if (optionalPackage.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No Package to buy\"}"
            );
        }

        Package aPackage = optionalPackage.get();
        String packageId = aPackage.getId();

        if (user.getCoins() < aPackage.getPrice()) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"error\": \"Not enough money for buying a card package\"}"
            );
        }

        // TODO: HANDLE DELETE ERRORS AND OVERALL MAKE IT POSSIBLE TO HANDLE IN CONTROLLER
        // TODO: IF DELETE FAILED UNDO THE DELETE
        // TODO: ON ROLLBACK GIVE BACK COINS
        Collection<Card> cards = cardDao.getAllByPackageUserDeckId(packageId, null, null);
        packageDao.delete(aPackage);
        user.setCoins(user.getCoins() - aPackage.getPrice());
        userDao.update(user, user);

        for (Card card : cards) {
            card.setPackageId(null);
            card.setUserId(user.getId());
            cardDao.update(card, card);
        }

        // TODO: ROLLBACK IF NOT ALL CARDS UPDATED
        try {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Successfully acquired package\", \"data\": " + getObjectMapper().writeValueAsString(cards) + "}"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"error\": \"Something went wrong\"}"
        );
    }
}
