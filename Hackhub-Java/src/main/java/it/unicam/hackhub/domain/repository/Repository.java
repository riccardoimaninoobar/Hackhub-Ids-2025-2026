package it.unicam.hackhub.domain.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T, ID> {

    void save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    boolean existsById(ID id);
}