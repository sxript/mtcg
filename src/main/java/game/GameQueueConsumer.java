package game;

import app.dto.QueueUser;
import server.Response;

import java.util.concurrent.BlockingQueue;

public class GameQueueConsumer implements Runnable {
    private final BlockingQueue<QueueUser> queue;
    private final Arena arena = new Arena();

    public GameQueueConsumer(BlockingQueue<QueueUser> queue) {
        this.queue = queue;
    }

    private void matchmaking() {
        while (true) {
            try {
                QueueUser user1 = queue.take();
                QueueUser user2 = queue.take();
                Response response = arena.battle(user1.getUser(), user2.getUser());
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
