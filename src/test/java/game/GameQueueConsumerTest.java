package game;

import app.dto.QueueUser;
import app.models.User;
import app.service.BattleService;
import app.service.CardService;
import app.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameQueueConsumerTest {
    private BlockingQueue<QueueUser> gameQueue;

    @BeforeEach
    void init() {
        this.gameQueue = new LinkedBlockingQueue<>();
        CardService cardService = mock(CardService.class);
        UserService userService = mock(UserService.class);
        BattleService battleService = mock(BattleService.class);
        Arena arena = new Arena(cardService, userService, battleService);
        GameQueueConsumer gameQueueConsumer = new GameQueueConsumer(gameQueue, arena);

        Thread t = new Thread(gameQueueConsumer);
        t.start();
    }

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