package app.dao;

import app.exceptions.DBErrorException;
import app.models.Package;
import db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class PackageDao implements Dao<Package> {
    @Override
    public Optional<Package> get(String id) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, price
                FROM "Package"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Package(
                        resultSet.getString(1),
                        resultSet.getInt(2)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Package> getAll() {
        return null;
    }

    @Override
    public int save(Package aPackage) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Package"
                (id, price)
                VALUES (?, ?)
                """)) {

            // Create Empty Package
            statement.setString(1, aPackage.getId());
            statement.setInt(2, aPackage.getPrice());

            return  statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    @Override
    public int update(String packageId, Package updatedPackage) {
        return 0;
    }

    @Override
    public int delete(Package aPackage) throws DBErrorException {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                DELETE FROM "Package"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, aPackage.getId());
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DBErrorException(e.getMessage());
        }
    }

    public Optional<Package> getFirst() {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, price
                FROM "Package"
                LIMIT 1;
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Package(
                        resultSet.getString(1),
                        resultSet.getInt(2)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
