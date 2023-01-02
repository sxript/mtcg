package app.service;

import app.dao.*;
import app.models.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.Optional;

@Getter(AccessLevel.PRIVATE)
public class PlayerServiceImpl implements  PlayerService {
    private final UserDao userDao;
    private final CardDao cardDao;
    private final PackageDao packageDao;
    private final StatsDao statsDao;
    private final DeckDao deckDao;
    private final TradeDao tradeDao;

    public PlayerServiceImpl(UserDao userDao, CardDao cardDao, PackageDao packageDao, StatsDao statsDao, DeckDao deckDao, TradeDao tradeDao) {
        this.userDao = userDao;
        this.cardDao = cardDao;
        this.packageDao = packageDao;
        this.statsDao = statsDao;
        this.deckDao = deckDao;
        this.tradeDao = tradeDao;
    }

    public PlayerServiceImpl() {
        this(new UserDao(), new CardDao(), new PackageDao(), new StatsDao(), new DeckDao(), new TradeDao());
    }

    @Override
    public Collection<User> findAllUsers() {
        return getUserDao().getAll();
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return getUserDao().get(username);
    }

    @Override
    public Collection<Stats> findAllStatsSorted() {
        // TODO: REWRITE TO RETURN WITH NAME?
        // MAYBE CREATE A SCOREBOARD STATS MODEL
        return statsDao.getAll();
    }


    @Override
    public Collection<Card> findCardsByDeckId(String deckId) {
        return cardDao.getAllByPackageUserDeckId(null, null, deckId);
    }

    @Override
    public Collection<Card> findPackageByPackageId(String packageId) {
        return cardDao.getAllByPackageUserDeckId(packageId, null, null);
    }

    @Override
    public Collection<Card> findCardsByUserId(String userId) {
        return cardDao.getAllByPackageUserDeckId(null, userId, null);
    }

    @Override
    public Optional<Card> findCardById(String id) {
       return cardDao.get(id);
    }

    @Override
    public Optional<Deck> findDeckByUserId(String userId) {
        return getDeckDao().getByUserId(userId);
    }

    @Override
    public void updateUser(User oldUser, User updatedUser) {
        getUserDao().update(oldUser, updatedUser);
    }

    @Override
    public void updateCard(Card oldCard, Card newCard) {
       cardDao.update(oldCard, newCard);
    }

}
