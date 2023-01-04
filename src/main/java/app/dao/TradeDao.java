package app.dao;

import app.exceptions.DBErrorException;
import app.models.Trade;
import db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class TradeDao implements Dao<Trade> {
    @Override
    public Optional<Trade> get(String id) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, card_id, type, min_damage
                FROM "Trade"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(createTradeFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Trade> getByCardId(String cardId) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, card_id, type, min_damage
                FROM "Trade"
                WHERE card_id = ?;
                """)
        ) {
            statement.setString(1, cardId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return Optional.of(createTradeFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Trade> getAll() {
        ArrayList<Trade> result = new ArrayList<>();
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, card_id, type, min_damage
                FROM "Trade";
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createTradeFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int save(Trade trade) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Trade"
                (id, card_id, type, min_damage)
                VALUES (?, ?, ?, ?);
                """)) {

            statement.setString(1, trade.getId());
            statement.setString(2, trade.getCardId());
            statement.setString(3, trade.getCardType());
            statement.setInt(4, trade.getMinimumDamage());

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    @Override
    public int update(String tradeId, Trade updatedTrade) throws DBErrorException {
        try ( PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                UPDATE "Trade"
                SET card_id = ?, type = ?, min_damage = ?
                WHERE id = ?
                """)
        ) {
            // UPDATE WITH NEW TRADE DATA
            statement.setString(1, updatedTrade.getCardId());
            statement.setString(2, updatedTrade.getCardType());
            statement.setInt(3, updatedTrade.getMinimumDamage());

            // USE CURRENT ID
            statement.setString(4, tradeId);

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    @Override
    public int delete(Trade trade) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                DELETE FROM "Trade"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, trade.getId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    private Trade createTradeFromResultSet(ResultSet resultSet) throws SQLException {
        return new Trade(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getInt(4)
        );
    }
}
