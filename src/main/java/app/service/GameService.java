package app.service;

import app.dto.UserStatsDTO;
import app.exceptions.InvalidDeckException;
import app.models.User;

import java.util.Collection;
import java.util.Optional;

public interface GameService {
    Collection<UserStatsDTO> getAllStatsSorted();

    Optional<UserStatsDTO> getStatsByUserId(String userId);

    void battlePreCheck(User user) throws InvalidDeckException;
}
