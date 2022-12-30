package app.service;

import app.models.User;


public interface TokenService {
    User authenticateToken(String token);
    String parseAccessToken(String token);
    String generateAccessToken(String username);
}
