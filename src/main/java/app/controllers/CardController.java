package app.controllers;

import app.dao.CardDao;
import app.dao.PackageDao;
import app.exceptions.CustomJsonProcessingException;
import app.models.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.Element;
import helper.CommonErrors;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

public class CardController extends Controller {
    @Setter(AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    private CardDao cardDao;

    public CardController(CardDao cardDao) {
        setCardDao(cardDao);
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

    // TODO: DOES THIS MATCH THE RIGHT STATUS CODES
    public Response createCard(User user, String rawCard, String packageId) {
        if(user == null || !user.isAdmin()) {
            return CommonErrors.TOKEN_ERROR;
        }

        Card card;

        try {
            JsonNode obj = getObjectMapper().readTree(rawCard);
            if (obj.isArray()) {
                ArrayList<String> createdCards = new ArrayList<>();
                for (JsonNode node : obj) {
                    card = parseCard(getObjectMapper().writeValueAsString(node));
                    Response response = createCard(card, packageId);
                    if (response.getStatusCode() < 200 || response.getStatusCode() > 299) {
                        rollbackUserInsert(createdCards, packageId);
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
        Optional<Card> optionalCard = cardDao.get(card.getId());
        if (optionalCard.isPresent()) {
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"error\": \"Card with id \"" + card.getId() + "  already exists\"}"
            );
        }

        card.setPackageId(packageId);
        System.out.println(card);
        cardDao.save(card);

        return new Response(
                HttpStatus.CREATED,
                ContentType.JSON,
                "{ \"message\": \"Card created successfully\" }"
        );
    }

    private void rollbackUserInsert(ArrayList<String> userIds, String packageId) {
        for (String userId : userIds) {
            Optional<Card> optionalCard = cardDao.get(userId);
            optionalCard.ifPresent(card -> cardDao.delete(card));
        }
        // TODO: SHOULD ALSO DELETE EMPTY PACKAGE
    }
}
