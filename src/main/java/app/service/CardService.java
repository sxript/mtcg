package app.service;

import app.models.Card;
import app.models.Deck;
import app.models.Package;
import app.models.User;

import java.util.Collection;
import java.util.Optional;

public interface CardService {
    Optional<Card> findCardById(String cardId);

    Collection<Card> findAllCardsByDeckId(String deckId);

    Collection<Card> findAllCardsByUserId(String userId);

    Collection<Card> findAllCardsByPackageId(String packageId);

    void saveCard(Card card);

    void deleteCard(Card card);

    void updateCard(String cardId, Card newCard);

    Optional<Deck> findDeckByUserId(String userId);

    void saveDeck(Deck deck);

    Optional<Package> findFirstPackage();

    void createPackage(Package packageToCreate);

    void deletePackage(Package packageToDelete);

    void updateUser(String userId, User updatedUser);

}
