package app.dao;

import db.DBConnection;
import game.Profile;
import game.Stats;
import game.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class UserDao implements Dao<User> {
    @Override
    public Optional<User> get(String username) {
        try ( PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT name, username, password, coins, elo, wins, losses, bio, image
                FROM "User"
                LEFT JOIN "Stats"
                ON "User".id = "Stats".userid
                LEFT JOIN "Profile"
                ON "User".id = "Profile".userid
                WHERE "User".username=?
                """)
        ) {
            statement.setString( 1, username );
            ResultSet resultSet = statement.executeQuery();
            if( resultSet.next() ) {
                System.out.println("INSIDE");
                return Optional.of(new User(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        new Stats(
                                resultSet.getInt(5),
                                resultSet.getInt(6),
                                resultSet.getInt(7)
                        ),
                        new Profile(
                                resultSet.getString(8),
                                resultSet.getString(9)
                        )

                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> getAll() {
        ArrayList<User> result = new ArrayList<>();
        try ( PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT name, username, password, coins, elo, wins, losses, bio, image
                FROM "User"
                LEFT JOIN "Stats"
                ON "User".id = "Stats".userid
                LEFT JOIN "Profile"
                ON "User".id = "Profile".userid
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while( resultSet.next() ) {
                result.add(new User(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        new Stats(
                                resultSet.getInt(5),
                                resultSet.getInt(6),
                                resultSet.getInt(7)
                        ),
                        new Profile(
                                resultSet.getString(8),
                                resultSet.getString(9)
                        )));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(User user) {
        String userId = UUID.randomUUID().toString();
        try ( PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "User"
                (id, username, password, coins)
                VALUES (?, ?, ?, ?);
                """ )
        ) {
            statement.setString(1, userId);
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setInt(4, user.getCoins());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Stats userStats = user.getStats();
        try ( PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Stats"
                (id, elo, wins, losses, userid)
                VALUES (?, ?, ?, ?, ?);
                """ )
        ) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setInt(2, userStats.getElo());
            statement.setInt(3, userStats.getWins());
            statement.setInt(4, userStats.getLosses());
            statement.setString(5, userId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Profile profile = user.getProfile();
        try ( PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Profile"
                (id, bio, image, userid)
                VALUES (?, ?, ?, ?);
                """ )
        ) {
            statement.setString(1, UUID.randomUUID().toString());
            statement.setString(2, profile.getBio());
            statement.setString(3, profile.getImage());
            statement.setString(4, userId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User user, String[] params) {

    }

    @Override
    public void delete(User user) {

    }
}
