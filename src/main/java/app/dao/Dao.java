package app.dao;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T> {
    // READ
    Optional<T> get(String id);

    Collection<T> getAll();

    // CREATE
    void save(T t);

    // UPDATE
    void update(T t, T d);

    // DELETE
    void delete(T t);
}
