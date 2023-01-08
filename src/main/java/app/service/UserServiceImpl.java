package app.service;

import app.dao.ProfileDao;
import app.dao.StatsDao;
import app.dao.UserDao;
import app.exceptions.DBErrorException;
import app.models.Profile;
import app.models.Stats;
import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;

@Getter(AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final StatsDao statsDao;
    private final ProfileDao profileDao;

    public UserServiceImpl(UserDao userDao, StatsDao statsDao, ProfileDao profileDao) {
        this.userDao = userDao;
        this.statsDao = statsDao;
        this.profileDao = profileDao;
    }

    public UserServiceImpl() {
        this(new UserDao(), new StatsDao(), new ProfileDao());
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return getUserDao().get(username);
    }

    @Override
    public Optional<User> findUserById(String id) {
        return getUserDao().getById(id);
    }

    @Override
    public int updateUser(String username, User updatedUser) {
        try {
            return getUserDao().update(username, updatedUser);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public void saveUser(User user) throws DBErrorException {
        try {
            getUserDao().save(user);
            getStatsDao().save(new Stats(user.getId()));
            getProfileDao().save(new Profile(user.getId()));
        } catch (DBErrorException e) {
            getUserDao().delete(user);
            throw e;
        }
    }

    @Override
    public Optional<Stats> findStatsByUserId(String userId) {
        return getStatsDao().get(userId);
    }

    @Override
    public int updateStats(String userId, Stats updatedStats) {
        try {
            return getStatsDao().update(userId, updatedStats);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public Optional<Profile> findProfileByUserId(String userId) {
        return getProfileDao().get(userId);
    }

    @Override
    public int createProfile(Profile profile) throws DBErrorException {
        return getProfileDao().save(profile);
    }

    @Override
    public int updateProfile(String profileId, Profile updatedProfile) {
        try {
            return getProfileDao().update(profileId, updatedProfile);
        } catch (DBErrorException e) {
            return 0;
        }
    }
}
