package app.service;

import app.models.Card;
import app.models.Deck;
import app.models.Stats;
import app.models.User;

import java.util.Collection;
import java.util.Optional;

public interface PlayerService {
    Collection<User> findAllUsers();

    Optional<User> findUserByUsername(String username);

    Collection<Stats> findAllStatsSorted();

    Collection<Card> findCardsByDeckId(String deckId);

    Collection<Card> findPackageByPackageId(String packageId);

    Collection<Card> findCardsByUserId(String userId);

    Optional<Card> findCardById(String id);

    Optional<Deck> findDeckByUserId(String userId);

    void updateCard(Card oldCard, Card newCard);
}
