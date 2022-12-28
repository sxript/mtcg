package app.dao;

import app.models.Card;
import app.models.MonsterCard;
import db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import app.models.Package;

public class PackageDao implements Dao<Package> {
    @Override
    public Optional<Package> get(String id) {
        return Optional.empty();
    }

    @Override
    public Collection<Package> getAll() {
        return null;
    }

    @Override
    public void save(Package aPackage) {
        // TODO: MAYBE CREATE PACKAGE IDS IN CLASS
        String packageId = UUID.randomUUID().toString();
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Package"
                (id)
                VALUES (?)
                """)) {

            // Create Empty Package
            statement.setString(1, packageId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Package aPackage, Package d) {

    }

    @Override
    public void delete(Package aPackage) {

    }
}
