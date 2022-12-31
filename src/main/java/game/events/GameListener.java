package game.events;

import app.models.User;
import server.Response;

public class GameListener implements EventListener {
    @Override
    public Response update(String eventType, User user, Response response) {
        return response;
    }
}
