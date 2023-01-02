package app.service;

import app.dto.UserStatsDTO;

import java.util.Collection;
import java.util.Optional;

public interface GameService {
    Collection<UserStatsDTO> getAllStatsSorted();

    Optional<UserStatsDTO> getStatsByUserId(String userId);
}
