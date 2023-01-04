package app.service;

import app.dao.*;
import app.exceptions.DBErrorException;
import app.models.*;
import app.models.Package;

import java.util.Collection;
import java.util.Optional;

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
        return cardDao.get(cardId);
    }

    @Override
    public Collection<Card> findAllCardsByDeckId(String deckId) {
        return cardDao.getAllByPackageUserDeckId(null, null, deckId);
    }

    @Override
    public Collection<Card> findAllCardsByUserId(String userId) {
        return cardDao.getAllByPackageUserDeckId(null, userId, null);
    }

    @Override
    public Collection<Card> findAllCardsByPackageId(String packageId) {
        return cardDao.getAllByPackageUserDeckId(packageId, null, null);
    }

    @Override
    public int saveCard(Card card) throws DBErrorException {
        return cardDao.save(card);
    }

    @Override
    public int deleteCard(Card card) {
        try {
            return cardDao.delete(card);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public int updateCard(String cardId, Card newCard) {
        try {
            return cardDao.update(cardId, newCard);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public Optional<Deck> findDeckByUserId(String userId) {
        return deckDao.getByUserId(userId);
    }

    @Override
    public int saveDeck(Deck deck) throws DBErrorException {
        return deckDao.save(deck);
    }

    @Override
    public Optional<Package> findFirstPackage() {
        return packageDao.getFirst();
    }

    @Override
    public int createPackage(Package packageToCreate) throws DBErrorException {
        return packageDao.save(packageToCreate);
    }

    @Override
    public int deletePackage(Package packageToDelete) {
        try {
            return packageDao.delete(packageToDelete);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public Optional<Trade> findTradeByCardId(String cardId) {
        return tradeDao.getByCardId(cardId);
    }
}
