package app.dao;

import java.util.Collection;
import java.util.Optional;

// TODO: REFACTOR THIS INTERFACE RETURN ALL THE TIME
// TODO: HANDLE DAO ERRORS
public interface Dao<T> {
    // READ
    Optional<T> get(String id);

    Collection<T> getAll();

    // CREATE
    void save(T t);

    // UPDATE
    void update(String d, T t);

    // DELETE
    void delete(T t);
}
