package app.dao;

import db.DBConnection;
import app.models.Profile;
import app.models.Stats;
import app.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class UserDao implements Dao<User> {
    @Override
    public Optional<User> get(String username) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT "User".id, name, username, password, coins, "Stats".id, elo, wins, losses, draws, "Profile".id, bio, image
                FROM "User"
                LEFT JOIN "Stats"
                ON "User".id = "Stats".userid
                LEFT JOIN "Profile"
                ON "User".id = "Profile".userid
                WHERE "User".username=?
                """)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createUserWithResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<User> getById(String id) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT "User".id, name, username, password, coins, "Stats".id, elo, wins, losses, draws, "Profile".id, bio, image
                FROM "User"
                LEFT JOIN "Stats"
                ON "User".id = "Stats".userid
                LEFT JOIN "Profile"
                ON "User".id = "Profile".userid
                WHERE "User".id=?
                """)
        ) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createUserWithResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> getAll() {
        ArrayList<User> result = new ArrayList<>();
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT "User".id, name, username, password, coins, "Stats".id, elo, wins, losses, draws, "Profile".id, bio, image
                FROM "User"
                LEFT JOIN "Stats"
                ON "User".id = "Stats".userid
                LEFT JOIN "Profile"
                ON "User".id = "Profile".userid
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createUserWithResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(User user) {
        String userId = user.getId();
        Stats userStats = user.getStats();
        Profile profile = user.getProfile();
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                WITH user_insert AS (
                    INSERT INTO "User"
                    (id, username, password, coins)
                    VALUES (?, ?, ?, ?)
                    RETURNING id as userid
                ), stats_insert AS (
                    INSERT INTO "Stats"
                    (id, elo, wins, losses, draws, userid)
                    VALUES (?, ?, ?, ?, ?, (select * from "user_insert"))
                    RETURNING userid
                )
                INSERT INTO "Profile"
                (id, bio, image, userid)
                VALUES (?, ?, ?, (select * from "user_insert"))
                RETURNING userid;
                """)) {
            // Insert Into User
            statement.setString(1, userId);
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setInt(4, user.getCoins());

            // Insert Into Stats
            statement.setString(5, userStats.getId());
            statement.setInt(6, userStats.getElo());
            statement.setInt(7, userStats.getWins());
            statement.setInt(8, userStats.getLosses());
            statement.setInt(9, userStats.getDraws());

            // Insert Into Profile
            statement.setString(10, profile.getId());
            statement.setString(11, profile.getBio());
            statement.setString(12, profile.getImage());

            // Execute Query
            // TODO: EXECUTE UPDATE
            statement.execute();
            try (ResultSet rs = statement.getResultSet()) {
                if (rs.next()) {
                    System.out.println(rs.getString(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user, User updatedUser) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                WITH update_user as (
                    UPDATE "User"
                    SET name = ?, username = ?, password = ?, coins = ?
                    WHERE username = ?
                    RETURNING id as userid
                ), update_stats as (
                    UPDATE "Stats"
                    SET elo = ?, wins = ?, losses = ?, draws = ?
                    WHERE userid = (select * from "update_user")
                    RETURNING userid
                )
                UPDATE "Profile"
                SET bio = ?, image = ?
                WHERE userid = (select * from "update_stats");
                """)
        ) {
            // UPDATE WITH NEW USER DATA
            statement.setString(1, updatedUser.getName());
            statement.setString(2, updatedUser.getUsername());
            statement.setString(3, updatedUser.getPassword());
            statement.setInt(4, updatedUser.getCoins());

            // USE CURRENT USERNAME
            statement.setString(5, user.getUsername());

            // UPDATE STATS
            Stats updatedStats = updatedUser.getStats();
            statement.setInt(6, updatedStats.getElo());
            statement.setInt(7, updatedStats.getWins());
            statement.setInt(8, updatedStats.getLosses());
            statement.setInt(9, updatedStats.getDraws());

            // UPDATE PROFILE
            Profile updatedProfile = updatedUser.getProfile();
            statement.setString(10, updatedProfile.getBio());
            statement.setString(11, updatedProfile.getImage());

            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(User user) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                DELETE FROM "User"
                WHERE username = ?;
                """)
        ) {
            statement.setString(1, user.getUsername());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private User createUserWithResultSet(ResultSet resultSet) throws SQLException {
        return new User(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getInt(5),
                new Stats(
                        resultSet.getString(6),
                        resultSet.getInt(7),
                        resultSet.getInt(8),
                        resultSet.getInt(9),
                        resultSet.getInt(10),
                        resultSet.getString(1)
                ),
                new Profile(
                        resultSet.getString(11),
                        resultSet.getString(12),
                        resultSet.getString(13),
                        resultSet.getString(1)
                )
        );
    }
}
