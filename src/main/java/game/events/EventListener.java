package game.events;

import app.models.User;
import server.Response;

public interface EventListener {
    Response update(String eventType, User user, Response response);
}
