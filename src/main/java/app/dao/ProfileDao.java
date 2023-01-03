package app.dao;

import app.models.Profile;
import db.DBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class ProfileDao implements Dao<Profile> {

    // TODO: IF THERE IS A FAILED SQL STATMENT THEN STIL GETING 200 RESPONSE FOR EXAMPLE RENAMING USERID TO USER_ID
    @Override
    public Optional<Profile> get(String userId) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, bio, image, userid
                FROM "Profile"
                WHERE userid = ?;
                """)
        ) {
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(createProfileFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }


    @Override
    public Collection<Profile> getAll() {
        ArrayList<Profile> result = new ArrayList<>();
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                SELECT id, bio, image, userid
                FROM "Profile";
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(createProfileFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(Profile profile) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                INSERT INTO "Profile"
                (id, bio, image, userid)
                VALUES (?, ?, ?, ?);
                """)) {

            // Create Empty Package
            statement.setString(1, profile.getId());
            statement.setString(2, profile.getBio());
            statement.setString(3, profile.getImage());
            statement.setString(4, profile.getUserId());

            // TODO: HANDLE AFFECTED
            int affectedColumns = statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Profile profile, Profile updatedProfile) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                UPDATE "Profile"
                SET bio = ?, image = ?
                WHERE id = ?
                """)
        ) {
            // UPDATE WITH NEW PROFILE DATA
            statement.setString(1, updatedProfile.getBio());
            statement.setString(2, updatedProfile.getImage());

            // USE CURRENT ID
            statement.setString(3, profile.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Profile profile) {
        try (PreparedStatement statement = DBConnection.getInstance().prepareStatement("""
                DELETE FROM "Profile"
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, profile.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Profile createProfileFromResultSet(ResultSet resultSet) throws SQLException {
        return new Profile(
                resultSet.getString(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4)
        );
    }
}
