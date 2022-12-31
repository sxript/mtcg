package game.events;

import app.models.User;
import server.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// my own implementation
// the very special observer pattern
public class EventManager {
    Map<String, List<EventListener>> listeners = new HashMap<>();

    public void subscribe(String userId, EventListener listener) {
        List<EventListener> users = listeners.get(userId);
        if(users == null) this.listeners.put(userId, new ArrayList<>());
        users = listeners.get(userId);
        users.add(listener);
    }

    public void unsubscribe(String userId, EventListener listener) {
        List<EventListener> users = listeners.get(userId);
        users.remove(listener);
    }

    public void notify(String userId, User user, Response response) {
        List<EventListener> users = listeners.get(userId);
        for (EventListener listener : users) {
            listener.update(userId, user, response);
        }
    }
}
