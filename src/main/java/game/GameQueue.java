package game;

import app.models.User;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
@Setter
@ToString
public class GameQueue {
    private BlockingQueue<User> blockingQueue = new LinkedBlockingQueue<>(1);
    private volatile boolean inGame = false;

}