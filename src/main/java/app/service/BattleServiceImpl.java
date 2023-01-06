package app.service;

import app.dao.*;
import app.exceptions.DBErrorException;
import app.models.BattleLog;

import java.util.Optional;

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
        return battleLogDao.get(battleLogId);
    }

    @Override
    public int createBattleLog(BattleLog battleLog) {
        try {
            return battleLogDao.save(battleLog);
        } catch (DBErrorException e) {
            return 0;
        }
    }
}
