package game;

import app.dto.QueueUser;
import lombok.AccessLevel;
import lombok.Getter;
import server.Response;

import java.util.concurrent.BlockingQueue;

@Getter(AccessLevel.PRIVATE)
public class GameQueueConsumer implements Runnable {
    private final BlockingQueue<QueueUser> queue;
    private final Arena arena;

    public GameQueueConsumer(BlockingQueue<QueueUser> queue, Arena arena) {
        this.queue = queue;
        this.arena = arena;
    }

    public GameQueueConsumer(BlockingQueue<QueueUser> queue) {
        this(queue, new Arena());
    }

    private void matchmaking() {
        while (true) {
            try {
                QueueUser user1 = getQueue().take();
                QueueUser user2 = getQueue().take();
                Response response = getArena().battle(user1.getUser(), user2.getUser());
                user1.getResponseQueue().put(response);
                user2.getResponseQueue().put(response);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        matchmaking();
    }
}
