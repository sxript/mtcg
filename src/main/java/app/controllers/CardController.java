package app.controllers;

import app.exceptions.CustomJsonProcessingException;
import app.exceptions.DBErrorException;
import app.models.*;
import app.models.Package;
import app.service.CardService;
import app.service.CardServiceImpl;
import app.service.UserService;
import app.service.UserServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import server.Response;

import java.util.*;

@Getter(AccessLevel.PRIVATE)
public class CardController extends Controller {
    private final CardService cardService;
    private final UserService userService;

    public CardController(CardService cardService, UserService userService) {
        this.cardService = cardService;
        this.userService = userService;
    }

    public CardController() {
        this(new CardServiceImpl(), new UserServiceImpl());
    }

    // POST /packages
    public Response createPackage(User user, String rawCard) {
        if (user == null || !user.isAdmin()) {
            return CommonErrors.TOKEN_ERROR;
        }

        Package newPackage = new Package();
        String packageId = newPackage.getId();

        Response res = handleCreatePackage(newPackage);
        if (res != null) return res;

        Card card;

        try {
            JsonNode obj = getObjectMapper().readTree(rawCard);
            if (obj.isArray()) {
                ArrayList<String> createdCards = new ArrayList<>();
                for (JsonNode node : obj) {
                    card = parseCard(getObjectMapper().writeValueAsString(node));
                    Response response = createPackage(card, packageId);
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

        return createPackage(card, packageId);
    }

    private Response createPackage(Card card, String packageId) {
        Optional<Card> optionalCard = getCardService().findCardById(card.getId());
        if (optionalCard.isPresent()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"error\": \"Card with id \"" + card.getId() + "  already exists\"}"
            );
        }

        card.setPackageId(packageId);
        try {
            int createdRows = getCardService().saveCard(card);
            if (createdRows == 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"Could not create card\"}"
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
                "{ \"message\": \"Card created successfully\" }"
        );
    }

    // POST /transactions/packages
    public Response acquirePackage(User user) {
        if (user == null) {
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Package> optionalPackage = getCardService().findFirstPackage();
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

        Collection<Card> cards = getCardService().findAllCardsByPackageId(packageId);
        int deletedRows = getCardService().deletePackage(aPackage);
        if (deletedRows == 0) {
            return new Response(
                    HttpStatus.GONE,
                    ContentType.JSON,
                    "{ \"error\": \"Oops it looks this package is not available anymore\"}"
            );
        }
        user.setCoins(user.getCoins() - aPackage.getPrice());
        getUserService().updateUser(user.getUsername(), user);

        ArrayList<Card> cardsChangedInTx = new ArrayList<>();
        for (Card card : cards) {
            cardsChangedInTx.add(new MonsterCard(card));
            card.setPackageId(null);
            card.setUserId(user.getId());
            getCardService().updateCard(card.getId(), card);
        }

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

        Collection<Card> cards = getCardService().findAllCardsByUserId(user.getId());
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

    // PATCH /cards/:cardId
    public Response updateCard(User user, String cardId, String rawCard) {
        if (user == null || !user.isAdmin()) {
            return CommonErrors.TOKEN_ERROR;
        }

        Card card;

        try {
            card = parseSimpleCard(rawCard);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"error\": \"Could not parse\", \"data\": " + rawCard + " }"
            );
        }

        return updateCard(cardId, card);
    }

    private Response updateCard(String cardId, Card card) {
        Optional<Card> optionalCard = getCardService().findCardById(cardId);

        if (optionalCard.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"Could not find any card with the provided id\"}"
            );
        }

        Card toUpdate = optionalCard.get();

        toUpdate.setDescription(card.getDescription());

        getCardService().updateCard(toUpdate.getId(), toUpdate);
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"Successfully updated Card description\"}"
        );
    }

    // GET /decks
    public Response getDeck(User user, String params) {
        boolean isPlain = false;
        if (params != null) {
            String[] split = params.split("=");
            isPlain = split.length == 2 && split[1].toLowerCase(Locale.ROOT).equals("plain");
        }

        if (user == null) {
            if (isPlain) {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        ContentType.TEXT,
                        "Access token is missing or invalid"
                );
            }
            return CommonErrors.TOKEN_ERROR;
        }

        Optional<Deck> optionalDeck = getCardService().findDeckByUserId(user.getId());

        Deck deck;

        Response noContentResponse;
        if (isPlain) {
            noContentResponse = new Response(
                    HttpStatus.NO_CONTENT,
                    ContentType.TEXT,
                    "Deck is empty"
            );
        } else {
           noContentResponse = new Response(
                    HttpStatus.NO_CONTENT,
                    ContentType.JSON,
                    "{ \"message\": \"Deck is empty\", \"data\": []}"
            );
        }

        if (optionalDeck.isPresent()) {
            deck = optionalDeck.get();
        } else return noContentResponse;


        Collection<Card> cardDeck = getCardService().findAllCardsByDeckId(deck.getId());
        try {
            if (cardDeck.isEmpty()) {
                return noContentResponse;
            }
            if (isPlain) {
                List<String> responseCards = cardDeck.stream().map(Card::toString).toList();
                return new Response(
                        HttpStatus.OK,
                        ContentType.TEXT,
                        getObjectMapper().writeValueAsString(responseCards)
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
            Optional<Deck> optionalDeck = getCardService().findDeckByUserId(user.getId());
            Deck deck = null;
            Collection<Card> cardsInDeckBeforeTx = Collections.emptyList();
            if (optionalDeck.isPresent()) {
                deck = optionalDeck.get();
                cardsInDeckBeforeTx = getCardService().findAllCardsByDeckId(deck.getId());
                for (Card c : cardsInDeckBeforeTx) {
                    c.setDeckId(null);
                    getCardService().updateCard(c.getId(), c);
                }
            }

            Deck tempDeck = new Deck(user.getId());
            if (deck == null) {
                Response res = handleCreateDeck(tempDeck);
                if (res != null) return res;
            }

            deck = deck == null ? tempDeck : deck;
            Set<String> cardsAddedToDeck = new HashSet<>();
            for (JsonNode node : jsonNode) {
                String cardId = node.asText();
                if (getCardService().findTradeByCardId(cardId).isPresent()) {
                    rollbackUserDeck(deck, cardsAddedToDeck, cardsInDeckBeforeTx);
                    return new Response(
                            HttpStatus.CONFLICT,
                            ContentType.JSON,
                            "{ \"error\": \"Card with the Id " + cardId + " is locked\"}"
                    );
                }
                if (cardsAddedToDeck.contains(cardId)) {
                    rollbackUserDeck(deck, cardsAddedToDeck, cardsInDeckBeforeTx);
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.JSON,
                            "{ \"error\": \"No duplicate Card IDs\"}"
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

    private Response handleCreatePackage(Package packageToCreate) {
        try {
            int createdRows = getCardService().createPackage(packageToCreate);
            if (createdRows == 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"Could not create Deck\"}"
                );
            }
        } catch (DBErrorException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Something went wrong: " + e.getMessage() + "\"}"
            );
        }
        return null;
    }

    private Response handleCreateDeck(Deck tempDeck) {
        try {
            int createdRows = getCardService().saveDeck(tempDeck);
            if (createdRows == 0) {
                return new Response(
                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"error\": \"Could not create Deck\"}"
                );
            }
        } catch (DBErrorException e) {
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"error\": \"Something went wrong: " + e.getMessage() + "\"}"
            );
        }
        return null;
    }

    private void rollbackUserDeck(Deck deck, Collection<String> cardIdsAddedToDeck, Collection<Card> cardsInDeckBeforeTx) {
        for (String cId : cardIdsAddedToDeck) {
            Optional<Card> optionalCard = getCardService().findCardById(cId);
            if (optionalCard.isEmpty()) continue;

            Card c = optionalCard.get();
            c.setDeckId(null);
            getCardService().updateCard(c.getId(), c);
        }

        for (Card c : cardsInDeckBeforeTx) {
            c.setDeckId(deck.getId());
            getCardService().updateCard(c.getId(), c);
        }
    }

    private Response setUserCard(User user, Deck deck, String cardId) {
        Optional<Card> optionalCard = getCardService().findCardById(cardId);
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
        getCardService().updateCard(card.getId(), card);

        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\": \"Successfully added to deck\"}"
        );
    }

    private void rollBackCardInsert(ArrayList<String> cards, Package newPackage) {
        for (String cardId : cards) {
            Optional<Card> optionalCard = getCardService().findCardById(cardId);
            optionalCard.ifPresent(getCardService()::deleteCard);
        }

        getCardService().deletePackage(newPackage);
    }

    private Card parseSimpleCard(String rawCard) throws JsonProcessingException {
        JsonNode jsonNode = getObjectMapper().readTree(rawCard);
        if (!jsonNode.has("type")) {
            String name;
            if (jsonNode.has("Name")) {
                name = jsonNode.get("Name").asText();
            } else if (jsonNode.has("name")) {
                name = jsonNode.get("name").asText();
            } else name = "";

            ((ObjectNode) jsonNode).put("type", name.toLowerCase(Locale.ROOT).contains("spell") ? "spell" : "monster");
            rawCard = getObjectMapper().writeValueAsString(jsonNode);
        }
        return getObjectMapper().readValue(rawCard, Card.class);
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
