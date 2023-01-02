package app.service;

import app.dao.StatsDao;
import app.dao.UserDao;
import app.dto.UserStatsDTO;
import app.models.Stats;
import app.models.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class GameServiceImpl implements GameService {
    private final StatsDao statsDao;
    private final UserDao userDao;

    public GameServiceImpl(StatsDao statsDao, UserDao userDao) {
        this.statsDao = statsDao;
        this.userDao = userDao;
    }

    public GameServiceImpl() {
        this(new StatsDao(), new UserDao());
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
}
