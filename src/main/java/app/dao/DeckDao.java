package app.dao;

import app.exceptions.DBErrorException;
import app.models.Deck;
import db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class DeckDao implements Dao<Deck> {
    @Override
    public Optional<Deck> get(String id) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, user_id
                FROM "Deck"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Deck(
                        resultSet.getString(1),
                        resultSet.getString(2)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Deck> getByUserId(String userId) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, user_id
                FROM "Deck"
                WHERE user_id = ?;
                """)
        ) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Deck(
                        resultSet.getString(1),
                        resultSet.getString(2)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Deck> getAll() {
        return null;
    }

    @Override
    public int save(Deck deck) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Deck"
                (id, user_id)
                VALUES (?, ?)
                """)) {

            // Create Empty Package
            statement.setString(1, deck.getId());
            statement.setString(2, deck.getUserId());

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    @Override
    public int update(String deckId, Deck updatedDeck) {
        return 0;
    }

    @Override
    public int delete(Deck deck) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                DELETE FROM "Deck"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, deck.getId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }
}
