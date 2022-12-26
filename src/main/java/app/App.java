package app;

import app.controllers.Controller;
import http.ContentType;
import http.HttpStatus;
import lombok.AccessLevel;
import lombok.Setter;
import server.Request;
import server.Response;
import server.ServerApp;

@Setter(AccessLevel.PRIVATE)
public class App implements ServerApp {
    private Controller controller;

    @Override
    public Response handleRequest(Request request) {
        switch (request.getMethod()) {
            case GET: {
                if (request.getBasePath().equals("/users") && request.getPathParams().size() == 1) {
                    System.out.println("GET USER BY NAME");
                } else if (request.getPathName().equals("/cards")) {
                    System.out.println("GET USER CARDS");
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
                    System.out.println("CREATE USER");
                } else if (request.getPathName().equals("/sessions")) {
                    System.out.println("LOGIN WITH USER");
                } else if (request.getPathName().equals("/packages")) {
                    System.out.println("CREATES PACKAGE");
                } else if (request.getPathName().equals("/transactions/packages")) {
                    System.out.println("BUY A PACKAGE");
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
                    System.out.println("UPDATE USER");
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
