package app;

import app.controllers.*;
import app.dao.*;
import app.models.User;
import app.service.TokenServiceImpl;
import game.GameQueue;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Request;
import server.Response;
import server.ServerApp;

import java.util.Objects;
import java.util.concurrent.BlockingQueue;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class App implements ServerApp {
    private UserController userController;
    private CardController cardController;
    private PackageController packageController;
    private PackageCardUserController packageCardUserController;
    private StatsController statsController;
    private BattleController battleController;
    private TradeController tradeController;

    private TokenServiceImpl tokenService = new TokenServiceImpl();

    public App() {
        setUserController(new UserController(new UserDao()));
        setCardController(new CardController(new CardDao()));
        setPackageController(new PackageController(new PackageDao()));
        setPackageCardUserController(new PackageCardUserController(new UserDao(), new PackageDao(), new CardDao(), new DeckDao()));
        setStatsController(new StatsController(new StatsDao()));
        setBattleController(new BattleController());
        setTradeController(new TradeController());
    }

    @Override
    public Response handleRequest(Request request) {
        User user = tokenService.authenticateToken(request.getAuthorization());
        switch (request.getMethod()) {
            case GET: {
                if (request.getBasePath().equals("/users") && request.getPathParams().size() == 1) {
                    return getUserController().getUser(user, request.getPathParams().get(0));
                } else if (request.getPathName().equals("/cards")) {
                    return getPackageCardUserController().getUserCards(user);
                } else if (request.getPathName().equals("/decks")) {
                    return getPackageCardUserController().getDeck(user);
                } else if (request.getPathName().equals("/stats")) {
                    return getStatsController().getStats(user);
                } else if (request.getPathName().equals("/scores")) {
                    return getStatsController().getScoreboard();
                } else if (request.getPathName().equals("/tradings")) {
                    return getTradeController().getAllTrades(user);
                }
                break;
            }
            case POST: {
                if (request.getPathName().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                } else if (request.getPathName().equals("/sessions")) {
                    return getUserController().loginUser(request.getBody());
                } else if (request.getPathName().equals("/packages")) {
                    // TODO: REFACTOR no logic in here
                    String packageId = getPackageController().createPackage();
                    return getCardController().createCard(user, request.getBody(), packageId);
                } else if (request.getPathName().equals("/transactions/packages")) {
                    return getPackageCardUserController().acquirePackage(user);
                } else if (request.getPathName().equals("/battles")) {
                   return getBattleController().battle(user);
                } else if (request.getPathName().equals("/tradings")) {
                    return getTradeController().createTrade(user, request.getBody());
                } else if (request.getBasePath().equals("/tradings") && request.getPathParams().size() == 1) {
                    return getTradeController().completeTrade(user, request.getPathParams().get(0), request.getBody());
                }
                break;
            }
            case PUT:
                if (request.getBasePath().equals("/users") && request.getPathParams().size() == 1) {
                    return getUserController().updateUser(user, request.getPathParams().get(0), request.getBody());
                } else if (request.getPathName().equals("/decks")) {
                    return getPackageCardUserController().setUserDeck(user, request.getBody());
                }
                break;
            case DELETE: {
                if (request.getBasePath().equals("/tradings") && request.getPathParams().size() == 1) {
                    return getTradeController().deleteTrade(user, request.getPathParams().get(0));
                }
                break;
            }
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Not Found\", \"data\": null }");
    }
}
