package app.dao;

import app.models.Card;
import app.models.Profile;
import app.models.Stats;
import db.DBConnection;
import enums.Element;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public class CardDao implements Dao<Card> {
    @Override
    public Optional<Card> get(String id) {
        return Optional.empty();
    }

    @Override
    public Collection<Card> getAll() {
        return null;
    }

    @Override
    public void save(Card card) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Card"
                (id, name, damage, element, package_id)
                VALUES (?, ?, ?, ?, ?)
                """)) {
            // Insert Into User
            statement.setString(1, card.getId());
            statement.setString(2, card.getName());
            statement.setString(3, card.getElementType().name());
            statement.setString(4, card.getPackageId());
            statement.setString(5, card.getPackageId());

            // Execute Query
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Card card, Card d) {

    }

    @Override
    public void delete(Card card) {

    }
}
