package app.service;

import app.models.BattleLog;

import java.util.Optional;

public interface BattleService {
    Optional<BattleLog> findBattleLogById(String battleLogId);

    int createBattleLog(BattleLog battleLog);
}
