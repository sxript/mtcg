package app.service;

import app.dao.*;
import app.exceptions.DBErrorException;
import app.models.*;
import app.models.Package;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collection;
import java.util.Optional;

@Getter(AccessLevel.PRIVATE)
public class CardServiceImpl implements CardService {
    private final CardDao cardDao;
    private final PackageDao packageDao;
    private final DeckDao deckDao;
    private final TradeDao tradeDao;

    public CardServiceImpl(CardDao cardDao, PackageDao packageDao, DeckDao deckDao, TradeDao tradeDao) {
        this.cardDao = cardDao;
        this.packageDao = packageDao;
        this.deckDao = deckDao;
        this.tradeDao = tradeDao;
    }

    public CardServiceImpl() {
       this(new CardDao(), new PackageDao(), new DeckDao(), new TradeDao());
    }

    @Override
    public Optional<Card> findCardById(String cardId) {
        return getCardDao().get(cardId);
    }

    @Override
    public Collection<Card> findAllCardsByDeckId(String deckId) {
        return getCardDao().getAllByPackageUserDeckId(null, null, deckId);
    }

    @Override
    public Collection<Card> findAllCardsByUserId(String userId) {
        return getCardDao().getAllByPackageUserDeckId(null, userId, null);
    }

    @Override
    public Collection<Card> findAllCardsByPackageId(String packageId) {
        return getCardDao().getAllByPackageUserDeckId(packageId, null, null);
    }

    @Override
    public int saveCard(Card card) throws DBErrorException {
        return getCardDao().save(card);
    }

    @Override
    public int deleteCard(Card card) {
        try {
            return getCardDao().delete(card);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public int updateCard(String cardId, Card newCard) {
        try {
            return getCardDao().update(cardId, newCard);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public Optional<Deck> findDeckByUserId(String userId) {
        return getDeckDao().getByUserId(userId);
    }

    @Override
    public int saveDeck(Deck deck) throws DBErrorException {
        return getDeckDao().save(deck);
    }

    @Override
    public Optional<Package> findFirstPackage() {
        return getPackageDao().getFirst();
    }

    @Override
    public int createPackage(Package packageToCreate) throws DBErrorException {
        return getPackageDao().save(packageToCreate);
    }

    @Override
    public int deletePackage(Package packageToDelete) {
        try {
            return getPackageDao().delete(packageToDelete);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public Optional<Trade> findTradeByCardId(String cardId) {
        return getTradeDao().getByCardId(cardId);
    }
}
