package app;

import app.controllers.*;
import app.dao.CardDao;
import app.dao.PackageDao;
import app.dao.UserDao;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import server.Request;
import server.Response;
import server.ServerApp;

@Setter(AccessLevel.PRIVATE)
@Getter(AccessLevel.PRIVATE)
public class App implements ServerApp {
    private UserController userController;
    private CardController cardController;
    private PackageController packageController;
    private PackageCardUserController packageCardUserController;

    public App() {
        setUserController(new UserController(new UserDao()));
        setCardController(new CardController(new CardDao()));
        setPackageController(new PackageController(new PackageDao()));
        setPackageCardUserController(new PackageCardUserController(new UserDao(), new PackageDao(), new CardDao()));
    }

    @Override
    public Response handleRequest(Request request) {
        switch (request.getMethod()) {
            case GET: {
                if (request.getBasePath().equals("/users") && request.getPathParams().size() == 1) {
                    return getUserController().getUser(request.getPathParams().get(0));
                } else if (request.getPathName().equals("/cards")) {
                    return getPackageCardUserController().getUserCards(request.getAuthorization());
                } else if (request.getPathName().equals("/decks")) {
                    System.out.println("GET USER DECKS");
                } else if (request.getPathName().equals("/stats")) {
                    System.out.println("GET USER STATS");
                } else if (request.getPathName().equals("/scores")) {
                    System.out.println("GET SCORES");
                } else if (request.getPathName().equals("/tradings")) {
                    System.out.println("GET TRADING DEALS");
                }
                break;
            }
            case POST: {
                if (request.getPathName().equals("/users")) {
                    return getUserController().createUser(request.getBody());
                } else if (request.getPathName().equals("/sessions")) {
                    return getUserController().loginUser(request.getBody());
                } else if (request.getPathName().equals("/packages")) {
                    String packageId = getPackageController().createPackage();
                    return getCardController().createCard(request.getBody(), packageId);
                } else if (request.getPathName().equals("/transactions/packages")) {
                    return getPackageCardUserController().acquirePackage(request.getAuthorization());
                } else if (request.getPathName().equals("/battles")) {
                    System.out.println("JOIN LOBBY");
                } else if (request.getPathName().equals("/tradings")) {
                    System.out.println("CREATE TRADING DEAL");
                } else if (request.getBasePath().equals("/tradings") && request.getPathParams().size() == 1) {
                    System.out.println("CREATE A TRADE WITH EXISTING");
                }
                break;
            }
            case PUT:
                if (request.getBasePath().equals("/users") && request.getPathParams().size() == 1) {
                    return getUserController().updateUser(request.getPathParams().get(0), request.getBody());
                }  else if (request.getPathName().equals("/decks")) {
                    System.out.println("CONFIGURE THE DECK");
                }
                break;
            case DELETE: {
                if (request.getBasePath().equals("/tradings") && request.getPathParams().size() == 1) {
                    System.out.println("DELETE A TRADING DEAL");
                }
                break;
            }
        }

        return new Response(HttpStatus.NOT_FOUND, ContentType.JSON, "{ \"error\": \"Not Found\", \"data\": null }");
    }
}
