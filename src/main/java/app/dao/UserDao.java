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
                SELECT id, name, username, password, coins
                FROM "User"
                WHERE username=?
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
                SELECT id, name, username, password, coins
                FROM "User"
                WHERE id=?
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
                SELECT id, name, username, password, coins
                FROM "User"
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
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "User"
                (id, name, username, password, coins)
                VALUES (?, ?, ?, ?, ?)
                """)) {
            // Insert Into User
            statement.setString(1, user.getId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getUsername());
            statement.setString(4, user.getPassword());
            statement.setInt(5, user.getCoins());

            // Execute Query
            // TODO: EXECUTE UPDATE
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(String username, User updatedUser) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                UPDATE "User"
                SET name = ?, username = ?, password = ?, coins = ?
                WHERE username = ?
                """)
        ) {
            // UPDATE WITH NEW USER DATA
            statement.setString(1, updatedUser.getName());
            statement.setString(2, updatedUser.getUsername());
            statement.setString(3, updatedUser.getPassword());
            statement.setInt(4, updatedUser.getCoins());

            // USE CURRENT USERNAME
            statement.setString(5, username);

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
                resultSet.getInt(5)
        );
    }
}
