package app.service;

import app.dao.CardDao;
import app.dao.DeckDao;
import app.dao.StatsDao;
import app.dao.UserDao;
import app.dto.UserStatsDTO;
import app.exceptions.InvalidDeckException;
import app.models.Card;
import app.models.Deck;
import app.models.Stats;
import app.models.User;
import http.ContentType;
import http.HttpStatus;
import server.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class GameServiceImpl implements GameService {
    private final StatsDao statsDao;
    private final UserDao userDao;
    private final DeckDao deckDao;
    private final CardDao cardDao;

    public GameServiceImpl(StatsDao statsDao, UserDao userDao, DeckDao deckDao, CardDao cardDao) {
        this.statsDao = statsDao;
        this.userDao = userDao;
        this.deckDao = deckDao;
        this.cardDao = cardDao;
    }

    public GameServiceImpl() {
        this(new StatsDao(), new UserDao(), new DeckDao(), new CardDao());
    }

    @Override
    public Collection<UserStatsDTO> getAllStatsSorted() {
        ArrayList<Stats> allStats = (ArrayList<Stats>) statsDao.getAll();
        ArrayList<UserStatsDTO> scoreboard = new ArrayList<>();
        allStats.forEach(stats -> {
            Optional<User> optionalUser = userDao.getById(stats.getUserId());
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();

                UserStatsDTO userStatsDTO = new UserStatsDTO();
                userStatsDTO.setStats(stats);
                userStatsDTO.setUsername(user.getUsername());
                userStatsDTO.setName(user.getName());
                scoreboard.add(userStatsDTO);
            }
        });
        return scoreboard;
    }

    @Override
    public Optional<UserStatsDTO> getStatsByUserId(String userId) {
        Optional<Stats> optionalStats = statsDao.get(userId);
        if (optionalStats.isEmpty()) return Optional.empty();

        UserStatsDTO userStatsDTO = new UserStatsDTO();

        Stats stats = optionalStats.get();
        userStatsDTO.setStats(stats);

        Optional<User> optionalUser = userDao.getById(stats.getUserId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userStatsDTO.setName(user.getName());
            userStatsDTO.setUsername(user.getUsername());
        }

        return Optional.of(userStatsDTO);
    }

    @Override
    public void battlePreCheck(User user) throws InvalidDeckException {
        Optional<Deck> optionalDeck = deckDao.getByUserId(user.getId());
        if (optionalDeck.isEmpty()) {
            throw new InvalidDeckException("No Deck configured");
        }
        ArrayList<Card> cards = (ArrayList<Card>) cardDao.getAllByPackageUserDeckId(null, null, optionalDeck.get().getId());
        if (cards.size() != 4) {
            throw new InvalidDeckException("Deck must consist of 4 Cards");
        }
    }
}
