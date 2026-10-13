package org.example.namia.supportdeskspringboot.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IBaseRepository<T> {

    T Create(T entity);
    Optional<T> Get(UUID Id);
    List<T> GetAll();
    T Update(T entity);
    void Delete(UUID Id);
}
