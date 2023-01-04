package app.service;

import app.exceptions.DBErrorException;
import app.models.Trade;

import java.util.Collection;
import java.util.Optional;

public interface TradingService {
    Optional<Trade> findTradeById(String id);

    Collection<Trade> findAllTrades();

    int createTrade(Trade trade) throws DBErrorException;

    int deleteTrade(Trade trade);
}
