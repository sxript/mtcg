# Setup
```
# Start the container
docker compose up -d
	
# Run the migration.sql to create the tables
docker exec -i postgres_mtcg psql -U postgres -d mtcg < migration.sql
```

# Technical Implementation
## Designs

### Database Schema
![Database Schema](./docs/db_schema.png)

[Migration Script](migration.sql)

The code is mainly structured into:
- Controller
- Service
- DAO
- Models

The Controllers handle incoming requests and communicate with the Service layer to retrieve or manipulate data. The Service layer uses the DAO (data access object) layer to access data from the database.

This type of organization allows for separation of concerns, such as keeping the user interface and data access logic separate, which can make the code easier to maintain and extend. The use of a service layer also allows for reuse of common business logic across multiple controllers, improving the efficiency of the code. The Dao layer provides a consistent interface for data access, making it easier to change the underlying database without affecting the rest of the code.

The Game Queueing system is implemented with the Consumer/Producer pattern where the clients are producers and the server is the consumer of the clients and with them creates games. For the Queue, I used a   `BlockingQueue` that takes `QueueUser`'s a `QueueUser` consists of `User` and another `BlockingQueue<Response>` that has a capacity of 1. The Queue from the Queue User is used to offer two-way communication and to synchronize the threads. After joining a game every `QueueUser` calls `take` on their Response Queue and waits for the game to finish.

## Features
- **Mandatory feature**
    - After every Battle the battle log is stored in the database and all users can access existing battle logs by the game id on the route `GET /battles/:game_id`
- **Optional features**
    - Trading system: trade cards vs coins
        - A user can create a coin trade on the same route as the card trade `POST /tradings` only that in this case a coins property must be provided in the Request Body along with the card id to trade
    - Card Description
        - A Card can have a description that can be set by an admin on the route `PATCH /cards/:card_id`
    - Implemented a more sophisticated Elo System [Chess Elo System](https://de.wikipedia.org/wiki/Elo-Zahl)
    - Added Win/Lose Ratio to user stats

## Failures and Solutions

One of the main challenges encountered during the development of the project was the Game Queue system, which initially had poor performance and resource utilization due to busy looping and checking keys in a `HashMap`. Additionally, the code was not thread safe, leading to the possibility of clients getting stuck in the queue indefinitely.

The solution to address these problems was implementing the consumer/producer pattern. This design pattern helps to improve resource utilization by allowing the producer (client) and consumer (server) to work asynchronously, so that the producers (clients) can continue joining the queue while the consumer (`GameQueueConsumer`) can process the clients in the queue and match them.

# Testing

I began writing unit tests for the `CardController`, `UserController`, `GameController`, and `TradingController` to verify that the correct errors are returned for edge cases, such as attempting to purchase a package that has already been bought. In addition, I wrote tests for the Game Queue to ensure that a predetermined number of players can join the queue and be matched against each other. The elo system, which is responsible for calculating and updating user stats, was also tested to ensure that it functions correctly.

It is essential to thoroughly test the code, particularly edge cases, to prevent bugs that could potentially be exploited by users. The game queue is a central component of the project, and it is vital that it functions smoothly so that players can join and be matched against each other. Similarly, the elo system plays a critical role in determining the skill level of players, so it is important to ensure that it is accurately calculating and updating user stats.

# Time Spent

Around 70h (Git history more detailed with commits)

[Git Repository](https://github.com/sxript/mtcg)
