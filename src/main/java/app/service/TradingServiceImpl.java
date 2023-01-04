package app.service;

import app.dao.TradeDao;
import app.exceptions.DBErrorException;
import app.models.Trade;

import java.util.Collection;
import java.util.Optional;

public class TradingServiceImpl implements TradingService {
    private final TradeDao tradeDao;

    public TradingServiceImpl(TradeDao tradeDao) {
        this.tradeDao = tradeDao;
    }

    public TradingServiceImpl() {
        this(new TradeDao());
    }

    @Override
    public Optional<Trade> findTradeById(String id) {
        return tradeDao.get(id);
    }

    @Override
    public Collection<Trade> findAllTrades() {
        return tradeDao.getAll();
    }

    @Override
    public int createTrade(Trade trade) throws DBErrorException {
            return tradeDao.save(trade);
    }

    @Override
    public int deleteTrade(Trade trade) {
        try {
            return tradeDao.delete(trade);
        } catch (DBErrorException e) {
            return 0;
        }
    }
}
