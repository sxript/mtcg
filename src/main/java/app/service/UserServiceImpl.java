package app.service;

import app.dao.UserDao;
import app.models.User;

import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserServiceImpl() {
        this(new UserDao());
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userDao.get(username);
    }

    @Override
    public void updateUser(User oldUser, User updatedUser) {
        userDao.update(oldUser, updatedUser);
    }

    @Override
    public void saveUser(User user) {
        userDao.save(user);
    }
}
