package app.service;

import app.dao.TradeDao;
import app.exceptions.DBErrorException;
import app.models.Trade;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collection;
import java.util.Optional;

@Getter(AccessLevel.PRIVATE)
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
        return getTradeDao().get(id);
    }

    @Override
    public Collection<Trade> findAllTrades() {
        return getTradeDao().getAll();
    }

    @Override
    public int createTrade(Trade trade) throws DBErrorException {
            return getTradeDao().save(trade);
    }

    @Override
    public int deleteTrade(Trade trade) {
        try {
            return getTradeDao().delete(trade);
        } catch (DBErrorException e) {
            return 0;
        }
    }
}
