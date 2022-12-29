package app.dao;

import java.util.Collection;
import java.util.Optional;

public interface ExtendedAccessDao<T> {
    Collection<T> getAllById(T t);

    Optional<T> getFirst();


}
