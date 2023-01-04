package app.dto;

import app.models.User;
import lombok.Getter;
import server.Response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class QueueUser {
    private final User user;
    private final BlockingQueue<Response> responseQueue = new LinkedBlockingQueue<>(1);

    public QueueUser(User user) {
        this.user = user;
    }

}
