package app.service;

import app.dao.ProfileDao;
import app.dao.StatsDao;
import app.dao.UserDao;
import app.exceptions.DBErrorException;
import app.models.Profile;
import app.models.Stats;
import app.models.User;

import java.util.Optional;

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
        return userDao.get(username);
    }

    @Override
    public Optional<User> findUserById(String id) {
        return userDao.getById(id);
    }

    @Override
    public int updateUser(String username, User updatedUser) {
        try {
            return userDao.update(username, updatedUser);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public void saveUser(User user) throws DBErrorException {
        try {
            userDao.save(user);
            statsDao.save(new Stats(user.getId()));
            profileDao.save(new Profile(user.getId()));
        } catch (DBErrorException e) {
            userDao.delete(user);
            throw e;
        }
    }

    @Override
    public Optional<Stats> findStatsByUserId(String userId) {
        return statsDao.get(userId);
    }

    @Override
    public int updateStats(String userId, Stats updatedStats) {
        try {
            return statsDao.update(userId, updatedStats);
        } catch (DBErrorException e) {
            return 0;
        }
    }

    @Override
    public Optional<Profile> findProfileByUserId(String userId) {
        return profileDao.get(userId);
    }

    @Override
    public int createProfile(Profile profile) throws DBErrorException {
        return profileDao.save(profile);
    }

    @Override
    public int updateProfile(String profileId, Profile updatedProfile) {
        try {
            return profileDao.update(profileId, updatedProfile);
        } catch (DBErrorException e) {
            return 0;
        }
    }
}
