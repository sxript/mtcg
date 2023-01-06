package app.dao;

import app.exceptions.DBErrorException;
import app.models.BattleLog;
import db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class BattleLogDao implements Dao<BattleLog> {
    @Override
    public Optional<BattleLog> get(String id) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, log, message
                FROM battlelog
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(
                        new BattleLog(
                                resultSet.getString(1),
                                resultSet.getString(2),
                                resultSet.getString(3)
                        )
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<BattleLog> getAll() {
        return null;
    }

    @Override
    public int save(BattleLog battleLog) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO battlelog
                (id, log, message)
                VALUES (?, ?::jsonb, ?)
                """)) {
            // Insert Into User
            statement.setString(1, battleLog.getId());
            statement.setString(2, battleLog.getJson());
            statement.setString(3, battleLog.getMessage());

            // Execute Query
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    @Override
    public int update(String d, BattleLog battleLog) throws DBErrorException {
        return 0;
    }

    @Override
    public int delete(BattleLog battleLog) throws DBErrorException {
        return 0;
    }
}
