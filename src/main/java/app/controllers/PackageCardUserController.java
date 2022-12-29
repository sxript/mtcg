package app.controllers;

import app.dao.CardDao;
import app.dao.PackageDao;
import app.dao.UserDao;
import app.models.Card;
import app.models.Package;
import app.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Response;

import java.util.Collection;
import java.util.Optional;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class PackageCardUserController extends Controller {
    private UserDao userDao;
    private PackageDao packageDao;
    private CardDao cardDao;

    public PackageCardUserController(UserDao userDao, PackageDao packageDao, CardDao cardDao) {
        setUserDao(userDao);
        setPackageDao(packageDao);
        setCardDao(cardDao);
    }

    // TODO: IMPLEMENT PROPER AUTH HANDLING;
    // GET /cards
    public Response getUserCards(String authToken) {
        Optional<User> optionalUser = userDao.get(authToken);
        if(optionalUser.isEmpty()) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"error\": \"Access token is missing or invalid\"}"
            );
        }
        User user = optionalUser.get();
        Collection<Card> cards = cardDao.getAllByPackageIdOrUserId(null, user.getId());
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
                    "{ \"message\": \"Successfully fetched User cards\", \"data\": " + getObjectMapper().writeValueAsString(cards) +"}"
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
    // TODO: REFACTOR TO REALLY USE TOKEN THIS WILL NOW ONLY USE PREDEFINED VALUE
    public Response acquirePackage(String token) {
        Optional<User> optionalUser = userDao.get(token);
        if (optionalUser.isEmpty()) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"error\": \" User not found \"}"
            );
        }

        Optional<Package> optionalPackage = packageDao.getFirst();
        if (optionalPackage.isEmpty()) {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    ContentType.JSON,
                    "{ \"error\": \"No Package to buy\"}"
            );
        }

        User user = optionalUser.get();
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
        Collection<Card> cards = cardDao.getAllByPackageIdOrUserId(packageId, null);
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
                    "{ \"message\": \"Successfully acquired package\", \"data\": " + getObjectMapper().writeValueAsString(cards) +"}"
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
