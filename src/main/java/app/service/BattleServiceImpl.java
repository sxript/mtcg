package app.service;

import app.dao.*;
import app.exceptions.DBErrorException;
import app.models.BattleLog;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Optional;

@Getter(AccessLevel.PRIVATE)
public class BattleServiceImpl implements BattleService {
    private final BattleLogDao battleLogDao;

    public BattleServiceImpl(BattleLogDao battleLogDao) {
        this.battleLogDao = battleLogDao;
    }

    public BattleServiceImpl() {
        this(new BattleLogDao());
    }

    @Override
    public Optional<BattleLog> findBattleLogById(String battleLogId) {
        return getBattleLogDao().get(battleLogId);
    }

    @Override
    public int createBattleLog(BattleLog battleLog) {
        try {
            return getBattleLogDao().save(battleLog);
        } catch (DBErrorException e) {
            return 0;
        }
    }
}
