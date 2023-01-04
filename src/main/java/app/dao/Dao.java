package app.dao;

import app.exceptions.DBErrorException;

import java.util.Collection;
import java.util.Optional;

public interface Dao<T> {
    // READ
    Optional<T> get(String id);

    Collection<T> getAll();

    // CREATE
    int save(T t) throws DBErrorException;

    // UPDATE
    int update(String d, T t) throws DBErrorException;

    // DELETE
    int delete(T t) throws DBErrorException;
}
