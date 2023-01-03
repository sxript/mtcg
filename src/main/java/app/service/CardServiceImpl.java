package app.service;

import app.dao.CardDao;
import app.dao.DeckDao;
import app.dao.PackageDao;
import app.dao.UserDao;
import app.models.Card;
import app.models.Deck;
import app.models.Package;
import app.models.User;

import java.util.Collection;
import java.util.Optional;

public class CardServiceImpl implements CardService {
    private final CardDao cardDao;
    private final PackageDao packageDao;
    private final DeckDao deckDao;
    private final UserDao userDao;

    public CardServiceImpl(CardDao cardDao, PackageDao packageDao, DeckDao deckDao, UserDao userDao) {
        this.cardDao = cardDao;
        this.packageDao = packageDao;
        this.deckDao = deckDao;
        this.userDao = userDao;
    }

    public CardServiceImpl() {
       this(new CardDao(), new PackageDao(), new DeckDao(), new UserDao());
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
    public void saveCard(Card card) {
        cardDao.save(card);
    }

    @Override
    public void deleteCard(Card card) {
        cardDao.delete(card);
    }

    @Override
    public void updateCard(Card oldCard, Card newCard) {
        cardDao.update(oldCard, newCard);
    }

    @Override
    public Optional<Deck> findDeckByUserId(String userId) {
        return deckDao.getByUserId(userId);
    }

    @Override
    public void saveDeck(Deck deck) {
        deckDao.save(deck);
    }

    @Override
    public Optional<Package> findFirstPackage() {
        return packageDao.getFirst();
    }

    @Override
    public void createPackage(Package packageToCreate) {
        packageDao.save(packageToCreate);
    }

    @Override
    public void deletePackage(Package packageToDelete) {
        packageDao.delete(packageToDelete);
    }

    @Override
    public void updateUser(User oldUser, User updatedUser) {
        userDao.update(oldUser, updatedUser);
    }
}
