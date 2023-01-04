package app.service;

import app.exceptions.DBErrorException;
import app.models.*;
import app.models.Package;

import java.util.Collection;
import java.util.Optional;

public interface CardService {
    Optional<Card> findCardById(String cardId);

    Collection<Card> findAllCardsByDeckId(String deckId);

    Collection<Card> findAllCardsByUserId(String userId);

    Collection<Card> findAllCardsByPackageId(String packageId);

    int saveCard(Card card) throws DBErrorException;

    int deleteCard(Card card);

    int updateCard(String cardId, Card newCard);

    Optional<Deck> findDeckByUserId(String userId);

    int saveDeck(Deck deck) throws DBErrorException;

    Optional<Package> findFirstPackage();

    int createPackage(Package packageToCreate) throws DBErrorException;

    int deletePackage(Package packageToDelete);

    Optional<Trade> findTradeByCardId(String cardId);
}
