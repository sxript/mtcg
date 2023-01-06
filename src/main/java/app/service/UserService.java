package app.service;

import app.exceptions.DBErrorException;
import app.models.Profile;
import app.models.Stats;
import app.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByUsername(String username);
    Optional<User> findUserById(String id);

    int updateUser(String username, User updatedUser);

    void saveUser(User user) throws DBErrorException;

    Optional<Stats> findStatsByUserId(String userId);

    int updateStats(String statsId, Stats updatedStats);

    Optional<Profile> findProfileByUserId(String userId);

    int createProfile(Profile profile) throws DBErrorException;

    int updateProfile(String profileId, Profile updatedProfile);
}
