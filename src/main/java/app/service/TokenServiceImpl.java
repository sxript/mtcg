package app.service;

import app.dao.*;
import app.models.User;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;

@Getter(AccessLevel.PRIVATE)
// THIS WOULD BE BETTER WITH JWT AUTH
public class TokenServiceImpl implements TokenService {
    private final UserDao userDao;
    private static final String TOKEN_SUFFIX = "-mtcgToken";

    public TokenServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    public TokenServiceImpl() {
        this(new UserDao());
    }

    @Override
    public User authenticateToken(String token) {
        String username = parseAccessToken(token);
        Optional<User> optionalUser = getUserDao().get(username);
        User user = null;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            // This code is not that good but according to specification an admin is someone with the token
            // admin-mtcgToken
            if (Objects.equals(username, "admin")) {
                user.setAdmin(true);
            }
        }
        return user;
    }

    @Override
    public String parseAccessToken(String token) {
        return token == null ? "" : token.split("-")[0];
    }

    @Override
    public String generateAccessToken(String username) {
        return username + TOKEN_SUFFIX;
    }
}
