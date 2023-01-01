package app.dao;

import app.models.Trade;
import db.DBConnection;
import enums.CardType;

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
    public void save(Trade trade) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Trade"
                (id, card_id, type, min_damage)
                VALUES (?, ?, ?, ?);
                """)) {

            statement.setString(1, trade.getId());
            statement.setString(2, trade.getCardId());
            statement.setString(3, trade.getCardType());
            statement.setInt(4, trade.getMinimumDamage());

            // TODO: HANDLE AFFECTED
            int affectedColumns = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Trade trade, Trade updatedTrade) {
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
            statement.setString(4, trade.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Trade trade) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                DELETE FROM "Trade"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, trade.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
