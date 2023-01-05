package game;

import app.dto.QueueUser;
import app.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class GameQueueConsumerTest {
    private BlockingQueue<QueueUser> gameQueue;

    @BeforeEach
    void init() {
        gameQueue = new LinkedBlockingQueue<>();
        GameQueueConsumer gameQueueConsumer = new GameQueueConsumer(gameQueue);

        Thread t = new Thread(gameQueueConsumer);
        t.start();
    }

//    @Test
//    void user_can_join_wait_queue() throws InterruptedException {
//        int threadCount = 1;
//        ExecutorService service = Executors.newFixedThreadPool(threadCount);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        QueueUser queueUser = new QueueUser(new User("user1", ""));
//        for (int i = 0; i < threadCount; i++) {
//            service.execute(() -> {
//                try {
//                    gameQueue.put(queueUser);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                latch.countDown();
//            });
//        }
//        latch.await();
//        assertEquals(1, gameQueue.size());
//    }

    @Test
    void joining_creates_battle() throws InterruptedException {
        int threadCount = 4;
        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        List<Response> responseList = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            QueueUser queueUser = new QueueUser(new User("user" + i, ""));
            service.execute(() -> {
                try {
                    gameQueue.put(queueUser);
                    Response response = queueUser.getResponseQueue().poll(3, TimeUnit.SECONDS);
                    if (response != null) {
                        responseList.add(response);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                latch.countDown();
            });
        }
        latch.await();
        assertEquals(threadCount, responseList.size());
    }

}