package app.service;

import app.dao.TradeDao;
import app.models.Trade;
import lombok.AccessLevel;
import lombok.Getter;

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
    public void createTrade(Trade trade) {
        tradeDao.save(trade);
    }

    @Override
    public void deleteTrade(Trade trade) {
        tradeDao.delete(trade);
    }
}
