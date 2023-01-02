package app.service;

import app.models.Trade;

import java.util.Collection;
import java.util.Optional;

public interface TradingService {
    Optional<Trade> findTradeById(String id);

    Collection<Trade> findAllTrades();

    void createTrade(Trade trade);

    void deleteTrade(Trade trade);
}
