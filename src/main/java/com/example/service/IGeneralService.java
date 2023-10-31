package com.example.service;

import java.util.List;
import java.util.Optional;

public interface IGeneralService<E, T> {

    List<E> findAll(boolean deleted);

    Optional<E> findById(T t);

    void create(E e);

    void update(T t, E e);

    void removeById(T t);


}
