package app.service;

import app.dao.ProfileDao;
import app.dao.StatsDao;
import app.dao.UserDao;
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
    public void updateUser(String username, User updatedUser) {
        userDao.update(username, updatedUser);
    }

    @Override
    public void saveUser(User user) {
        //TODO: Rollback on error?
        userDao.save(user);
        statsDao.save(new Stats(user.getId()));
        profileDao.save(new Profile(user.getId()));
    }

    @Override
    public Optional<Stats> findStatsByUserId(String userId) {
        return statsDao.get(userId);
    }

    @Override
    public void updateStats(String userId, Stats updatedStats) {
       statsDao.update(userId, updatedStats);
    }

    @Override
    public Optional<Profile> findProfileByUserId(String userId) {
        return profileDao.get(userId);
    }

    @Override
    public void createProfile(Profile profile) {
       profileDao.save(profile);
    }

    @Override
    public void updateProfile(String profileId, Profile updatedProfile) {
        profileDao.update(profileId, updatedProfile);
    }
}
