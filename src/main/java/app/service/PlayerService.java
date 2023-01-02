package app.service;

import app.models.*;

import java.util.Collection;
import java.util.Optional;

public interface PlayerService {
    Collection<User> findAllUsers();

    Optional<User> findUserByUsername(String username);

    Collection<Stats> findAllStatsSorted();

    Collection<Trade> findAllTrades();

    Collection<Card> findCardsByDeckId(String deckId);

    Collection<Card> findPackageByPackageId(String packageId);

    Collection<Card> findCardsByUserId(String userId);

    Optional<Card> findCardById(String id);

    Optional<Trade> findTradeById(String id);

    Optional<Deck> findDeckByUserId(String userId);

    void updateUser(User oldUser, User updatedUser);

    void updateCard(Card oldCard, Card newCard);

    void createTrade(Trade trade);

    void deleteTrade(Trade trade);
}
