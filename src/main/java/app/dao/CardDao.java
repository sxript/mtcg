package app.dao;

import app.models.*;
import app.models.Package;
import db.DBConnection;
import enums.Element;
import enums.Type;
import factories.CardFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CardDao implements Dao<Card> {
    private final CardFactory cardFactory = new CardFactory();

    @Override
    public Optional<Card> get(String id) {

        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, name, damage, element, package_id, user_id, deck_id
                FROM "Card"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createCardFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Card> getAll() {
        return null;
    }

    public Collection<Card> getAllByPackageUserDeckId(String packageId, String userId, String deckId) {
        ArrayList<Card> result = new ArrayList<>();
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, name, damage, element, package_id, user_id, deck_id
                FROM "Card"
                WHERE package_id = ? OR user_id = ? OR deck_id = ?;
                """)
        ) {
            statement.setString(1, packageId);
            statement.setString(2, userId);
            statement.setString(3, deckId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Card card = createCardFromResultSet(resultSet);
                result.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Card createCardFromResultSet(ResultSet resultSet) throws SQLException {
        Card card = cardFactory.create(resultSet.getString(2).toLowerCase(Locale.ROOT).contains("spell") ? Type.SPELL : Type.MONSTER);
        card.setId(resultSet.getString(1));
        card.setName(resultSet.getString(2));
        card.setDamage(resultSet.getFloat(3));
        card.setElementType(Element.valueOf(resultSet.getString(4)));
        card.setPackageId(resultSet.getString(5));
        card.setUserId(resultSet.getString(6));
        card.setDeckId(resultSet.getString(7));
        return card;
    }

    @Override
    public void save(Card card) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Card"
                (id, name, damage, element, package_id)
                VALUES (?, ?, ?, ?::"Element", ?)
                """)) {
            // Insert Into User
            statement.setString(1, card.getId());
            statement.setString(2, card.getName());
            statement.setFloat(3, card.getDamage());
            statement.setString(4, card.getElementType().name());
            statement.setString(5, card.getPackageId());

            // Execute Query
            int affectedColumns = statement.executeUpdate();
            System.out.println("AFFECTED: " + affectedColumns);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    // TODO: UPDATE most of the time i call it with the same instance twice ?!?
    public void update(Card card, Card updatedCard) {
        try ( PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                UPDATE "Card"
                SET name = ?, damage = ?, element = ?::"Element", package_id = ?, user_id = ?, deck_id = ?
                WHERE id = ?
                """)
        ) {
            // UPDATE WITH NEW CARD DATA
            statement.setString(1, updatedCard.getName());
            statement.setFloat(2, updatedCard.getDamage());
            statement.setString(3, updatedCard.getElementType().name());
            statement.setString(4, updatedCard.getPackageId());
            statement.setString(5, updatedCard.getUserId());
            statement.setString(6, updatedCard.getDeckId());

            // USE CURRENT ID
            statement.setString(7, card.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Card card) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                DELETE FROM "Card"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, card.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
