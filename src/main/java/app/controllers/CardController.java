package app.controllers;

import app.exceptions.CustomJsonProcessingException;
import app.models.*;
import app.models.Package;
import app.service.CardService;
import app.service.CardServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import server.Response;

import java.util.*;

public class CardController extends Controller {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    public CardController() {
        this(new CardServiceImpl());
    }

    // POST /packages
    public Response createCard(User user, String rawCard) {
        if(user == null || !user.isAdmin()) {
            return CommonErrors.TOKEN_ERROR;
        }

        Package newPackage = new Package();
        String packageId = newPackage.getId();
        cardService.createPackage(newPackage);

        Card card;

        try {
            JsonNode obj = getObjectMapper().readTree(rawCard);
            if (obj.isArray()) {
                ArrayList<String> createdCards = new ArrayList<>();
                for (JsonNode node : obj) {
                    card = parseCard(getObjectMapper().writeValueAsString(node));
                    Response response = createCard(card, packageId);
                    if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                        rollBackCardInsert(createdCards, newPackage);
                        throw new CustomJsonProcessingException("Card parsing failed");
                    }
                    createdCards.add(card.getId());
                }
                return new Response(
                        HttpStatus.CREATED,
                        ContentType.JSON,
                        "{ \"message\": \"Created successfully\", \"data\": " + getObjectMapper().writeValueAsString(createdCards) + "}"
                );
            }

            card = parseCard(rawCard);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Could not parse\", \"data\": " + rawCard + " }"
            );
        }

        return createCard(card, packageId);
    }

    private Response createCard(Card card, String packageId) {
        Optional<Card> optionalCard = cardService.findCardById(card.getId());
        if (optionalCard.isPresent()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"error\": \"Card with id \"" + card.getId() + "  already exists\"}"
            );
        }

        card.setPackageId(packageId);
        cardService.saveCard(card);

        return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"Card created successfully\" }"
        );
    }

    // POST /transactions/packages
    public Response acquirePackage(User user) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Package> optionalPackage = cardService.findFirstPackage();
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
        Collection<Card> cards = cardService.findAllCardsByPackageId(packageId);
        cardService.deletePackage(aPackage);
        user.setCoins(user.getCoins() - aPackage.getPrice());
        // TODO: USER IN CARD SERVICE ?
        cardService.updateUser(user.getUsername(), user);

        for (Card card : cards) {
            card.setPackageId(null);
            card.setUserId(user.getId());
            cardService.updateCard(card.getId(), card);
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
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }
    }

    // GET /cards
    public Response getUserCards(User user) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Collection<Card> cards = cardService.findAllCardsByUserId(user.getId());
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
            return CommonErrors.INTERNAL_SERVER_ERROR;
        }
    }

    // GET /decks
    // TODO: REFACTOR ALL CONTROLLERS TO USE GET METHOD FOR DAO
    public Response getDeck(User user) {
        if(user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Deck> optionalDeck = cardService.findDeckByUserId(user.getId());

        Deck deck = new Deck(user.getId());
        if (optionalDeck.isPresent()) {
            deck = optionalDeck.get();
        }

        Collection<Card> cardDeck = cardService.findAllCardsByDeckId(deck.getId());
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
            return CommonErrors.INTERNAL_SERVER_ERROR;
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
            Optional<Deck> optionalDeck = cardService.findDeckByUserId(user.getId());
            Deck deck = null;
            Collection<Card> cardsInDeckBeforeTx = Collections.emptyList();
            if (optionalDeck.isPresent()) {
                deck = optionalDeck.get();
                cardsInDeckBeforeTx = cardService.findAllCardsByDeckId(deck.getId());
                for (Card c : cardsInDeckBeforeTx) {
                    c.setDeckId(null);
                    cardService.updateCard(c.getId(), c);
                }
            }

            Deck tempDeck = new Deck(user.getId());
            if (deck == null) {
                cardService.saveDeck(tempDeck);
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
            Optional<Card> optionalCard = cardService.findCardById(cId);
            if (optionalCard.isEmpty()) continue;

            Card c = optionalCard.get();
            c.setDeckId(null);
            cardService.updateCard(c.getId(), c);
        }

        for (Card c : cardsInDeckBeforeTx) {
            c.setDeckId(deck.getId());
            cardService.updateCard(c.getId(), c);
        }
    }

    private Response setUserCard(User user, Deck deck, String cardId) {
        Optional<Card> optionalCard = cardService.findCardById(cardId);
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

        card.setDeckId(deck.getId());
        cardService.updateCard(card.getId(), card);

        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"Successfully added to deck\"}"
        );
    }

    private void rollBackCardInsert(ArrayList<String> cards, Package newPackage) {
        for (String cardId : cards) {
            Optional<Card> optionalCard = cardService.findCardById(cardId);
            optionalCard.ifPresent(cardService::deleteCard);
        }

        cardService.deletePackage(newPackage);
    }

    private Card parseCard(String rawCard) throws JsonProcessingException {
        JsonNode jsonNode = getObjectMapper().readTree(rawCard);
        if (!jsonNode.has("type")) {
            String name;
            if (jsonNode.has("Name")) {
                name = jsonNode.get("Name").asText();
            } else if (jsonNode.has("name")) {
                name = jsonNode.get("name").asText();
            } else throw new CustomJsonProcessingException("No name property provided");

            ((ObjectNode) jsonNode).put("type", name.toLowerCase(Locale.ROOT).contains("spell") ? "spell" : "monster");
            rawCard = getObjectMapper().writeValueAsString(jsonNode);
        }
        return getObjectMapper().readValue(rawCard, Card.class);
    }
}
