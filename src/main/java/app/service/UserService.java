package app.service;

import app.models.User;

import java.util.Optional;

public interface UserService {
    Optional<User> getUserByUsername(String username);

    // TODO: Update to use String oldUserId Updated User
    void updateUser(User oldUser, User updatedUser);

    void saveUser(User user);
}
