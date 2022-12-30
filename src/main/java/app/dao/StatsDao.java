package app.dao;

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
                FROM "Stats"
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
//        SELECT "Stats".id, "User".name, elo, wins, losses, "Stats".userid
//        FROM "Stats"
//        JOIN "User"
//        ON "Stats".userid = "User".id
//        ORDER BY elo DESC;
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, elo, wins, losses, draws, userid
                FROM "Stats"
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
    public void save(Stats stats) {

    }

    @Override
    public void update(Stats stats, Stats d) {

    }

    @Override
    public void delete(Stats stats) {

    }

    private Stats createStatsFromResultSet(ResultSet resultSet) throws SQLException {
        return new Stats(
                resultSet.getString(1),
                resultSet.getInt(2),
                resultSet.getInt(3),
                resultSet.getInt(4),
                resultSet.getInt(5),
                resultSet.getString(6)
        ));
    }
}
