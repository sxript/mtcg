package app.service;

import app.models.Profile;
import app.models.Stats;
import app.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByUsername(String username);

    // TODO: Update to use String oldUserId Updated User
    void updateUser(User oldUser, User updatedUser);

    void saveUser(User user);

    Optional<Stats> findStatsByUserId(String userId);

    void updateStats(Stats oldStats, Stats updatedStats);

    Optional<Profile> findProfileByUserId(String userId);

    void createProfile(Profile profile);

    void updateProfile(Profile oldProfile, Profile updatedProfile);
}
