package app.dao;

import app.exceptions.DBErrorException;
import app.models.Stats;
import db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class StatsDao implements Dao<Stats> {
    @Override
    public Optional<Stats> get(String userId) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, elo, wins, losses, draws, userid
                FROM Stats
                WHERE userid = ?;
                """)
        ) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createStatsFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Stats> getAll() {
        ArrayList<Stats> result = new ArrayList<>();
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, elo, wins, losses, draws, userid
                FROM Stats
                ORDER BY elo DESC;
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createStatsFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int save(Stats stats) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO Stats
                (id, elo, wins, losses, draws, userid)
                VALUES (?, ?, ?, ?, ?, ?);
                """)) {

            // Create Empty Package
            statement.setString(1, stats.getId());
            statement.setInt(2, stats.getElo());
            statement.setInt(3, stats.getWins());
            statement.setInt(4, stats.getLosses());
            statement.setInt(5, stats.getDraws());
            statement.setString(6, stats.getUserId());

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    @Override
    public int update(String userId, Stats updatedStats) throws DBErrorException {
        try ( PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                UPDATE Stats
                SET elo = ?, wins = ?, losses = ?, draws = ?
                WHERE userid = ?
                """)
        ) {
            // UPDATE WITH NEW STATS DATA
            statement.setInt(1, updatedStats.getElo());
            statement.setInt(2, updatedStats.getWins());
            statement.setInt(3, updatedStats.getLosses());
            statement.setInt(4, updatedStats.getDraws());

            // USE CURRENT ID
            statement.setString(5, userId);

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    @Override
    public int delete(Stats stats) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                DELETE FROM Stats
                WHERE userid = ?;
                """)
        ) {
            statement.setString(1, stats.getUserId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    private Stats createStatsFromResultSet(ResultSet resultSet) throws SQLException {
        return new Stats(
                resultSet.getString(1),
                resultSet.getInt(2),
                resultSet.getInt(3),
                resultSet.getInt(4),
                resultSet.getInt(5),
                resultSet.getString(6)
        );
    }
}
