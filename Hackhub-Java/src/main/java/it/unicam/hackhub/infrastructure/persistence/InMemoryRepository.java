package it.unicam.hackhub.infrastructure.persistence;

import it.unicam.hackhub.domain.repository.Repository;

import java.util.*;

public abstract class InMemoryRepository<T, ID>
        implements Repository<T, ID> {

    protected final Map<ID, T> store = new HashMap<>();

    protected abstract ID getId(T entity);

    @Override
    public void save(T entity) {
        if (entity == null) return;
        store.put(getId(entity), entity);
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean existsById(ID id) {
        return store.containsKey(id);
    }
}